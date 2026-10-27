package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SalesPilotDatabase
import com.example.data.local.entity.*
import com.example.data.repository.SalesPilotRepository
import com.example.domain.ai.AiSalesCopilot
import com.example.domain.ai.CopilotDraftResult
import com.example.domain.ai.ReplyIntent
import com.example.domain.subscription.SubscriptionManager
import com.example.domain.subscription.SubscriptionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.UUID

enum class NavigationTab(val title: String) {
    DASHBOARD("Dashboard"),
    PIPELINE("Pipeline"),
    CATALOG("Catalog"),
    COPILOT("AI Copilot"),
    ORDERS("Orders"),
    FOLLOW_UPS("Follow-Ups"),
    SETTINGS("Settings")
}

data class DashboardMetrics(
    val totalLeads: Int = 0,
    val newEnquiries: Int = 0,
    val followUpsDue: Int = 0,
    val followUpsOverdue: Int = 0,
    val convertedOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalOutstanding: Double = 0.0,
    val pipelineValue: Double = 0.0,
    val conversionRatePercent: Int = 0
)

class SalesPilotViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SalesPilotRepository
    private val sessionPrefs = application.getSharedPreferences("salespilot_session_prefs", Context.MODE_PRIVATE)

    // Defaults to false so the Welcome / Landing Screen is shown on startup or when signed out
    val isUserSignedIn = MutableStateFlow(
        sessionPrefs.getBoolean("user_authenticated_session", false)
    )

    init {
        val database = SalesPilotDatabase.getDatabase(application)
        repository = SalesPilotRepository(database)
        // Ensure database seeder runs if fresh install and sync default language
        viewModelScope.launch(Dispatchers.IO) {
            val existingProfile = repository.getProfile()
            if (existingProfile == null) {
                SalesPilotDatabase.seedInitialData(database)
            } else {
                selectedAppLanguage.value = existingProfile.defaultLanguage
            }
        }
    }

    fun signIn() {
        sessionPrefs.edit().putBoolean("user_authenticated_session", true).apply()
        isUserSignedIn.value = true
    }

    fun signOut() {
        sessionPrefs.edit().putBoolean("user_authenticated_session", false).apply()
        isUserSignedIn.value = false
        currentTab.value = NavigationTab.DASHBOARD
        showMessage("Signed out. Welcome to SalesPilot AI.")
    }

    val currentTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val selectedAppLanguage = MutableStateFlow("en") // "en", "hi", "kn"
    val userSnackbarMessage = MutableStateFlow<String?>(null)

    // Data streams
    val profile: StateFlow<BusinessProfileEntity?> = repository.getProfileFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val products: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leads: StateFlow<List<LeadEntity>> = repository.getAllLeads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> = repository.getAllCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingFollowUps: StateFlow<List<LeadEntity>> = repository.getPendingFollowUps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Copilot active drafting state
    val copilotTargetLead = MutableStateFlow<LeadEntity?>(null)
    val copilotSelectedProduct = MutableStateFlow<ProductEntity?>(null)
    val copilotInquiryText = MutableStateFlow("")
    val copilotCustomerName = MutableStateFlow("")
    val copilotDraftResult = MutableStateFlow<CopilotDraftResult?>(null)
    val isCopilotGenerating = MutableStateFlow(false)

    // Derived Dashboard Metrics
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(leads, orders, pendingFollowUps) { leadList, orderList, followUpList ->
        val total = leadList.size
        val newEnq = leadList.count { it.stage == LeadStage.NEW_ENQUIRY }
        val now = System.currentTimeMillis()
        val overdue = followUpList.count { it.followUpDate != null && it.followUpDate < now && !it.isFollowUpCompleted }
        val dueCount = followUpList.count { !it.isFollowUpCompleted }
        val activeOrders = orderList.filter { it.status != OrderStatus.CANCELLED }
        val converted = activeOrders.size
        val rev = activeOrders.sumOf { it.totalAmount }
        val outstanding = activeOrders.sumOf { it.balanceDue }
        val pipelineVal = leadList.filter { it.stage != LeadStage.CLOSED_LOST && it.stage != LeadStage.ORDER_PLACED }.sumOf { it.estimatedValue }
        val convRate = if (total > 0) ((leadList.count { it.stage == LeadStage.ORDER_PLACED }.toDouble() / total) * 100).toInt() else 0

        DashboardMetrics(
            totalLeads = total,
            newEnquiries = newEnq,
            followUpsDue = dueCount,
            followUpsOverdue = overdue,
            convertedOrders = converted,
            totalRevenue = rev,
            totalOutstanding = outstanding,
            pipelineValue = pipelineVal,
            conversionRatePercent = convRate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    fun selectTab(tab: NavigationTab) {
        currentTab.value = tab
    }

    fun setAppLanguage(lang: String) {
        selectedAppLanguage.value = lang
        viewModelScope.launch(Dispatchers.IO) {
            profile.value?.let { currentProf ->
                repository.updateProfile(currentProf.copy(defaultLanguage = lang))
            }
        }
    }

    fun clearSnackbar() {
        userSnackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        userSnackbarMessage.value = msg
    }

    // --- AI COPILOT ACTIONS ---
    fun prepareCopilotForLead(lead: LeadEntity) {
        copilotTargetLead.value = lead
        copilotCustomerName.value = lead.customerName
        copilotInquiryText.value = lead.inquiryText
        val matchedProd = products.value.find { it.id == lead.productId }
            ?: products.value.firstOrNull()
        copilotSelectedProduct.value = matchedProd
        currentTab.value = NavigationTab.COPILOT

        val detectedLang = if (lead.lastDraftLanguage.isNotBlank() && lead.lastDraftLanguage != "en") {
            lead.lastDraftLanguage
        } else {
            AiSalesCopilot.detectInquiryLanguage(lead.inquiryText)
        }
        generateCopilotReply(lang = detectedLang)
    }

    fun generateCopilotReply(
        manualIntent: ReplyIntent? = null,
        lang: String = selectedAppLanguage.value
    ) {
        val currentProfile = profile.value ?: BusinessProfileEntity()
        val customerName = copilotCustomerName.value.ifBlank { "Valued Customer" }
        val text = copilotInquiryText.value
        val product = copilotSelectedProduct.value

        isCopilotGenerating.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val draft = AiSalesCopilot.draftResponseAsync(
                customerName = customerName,
                inquiryText = text,
                product = product,
                profile = currentProfile,
                languageOverride = lang,
                manualIntentOverride = manualIntent
            )
            copilotDraftResult.value = draft
            isCopilotGenerating.value = false

            // Update lead in Room if attached
            copilotTargetLead.value?.let { lead ->
                repository.updateLead(
                    lead.copy(
                        lastAiDraftReply = draft.draftReply,
                        lastDraftLanguage = lang,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun openInWhatsApp(context: Context, phone: String, message: String) {
        try {
            val cleanPhone = phone.replace("+", "").replace(" ", "").replace("-", "")
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            showMessage("Opening WhatsApp with drafted message...")
        } catch (e: Exception) {
            // Fallback: Generic Share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share reply via...").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            showMessage("Shared reply via message sheet")
        }
    }

    // --- PRODUCT ACTIONS ---
    fun addProduct(
        name: String,
        sku: String,
        category: String,
        price: Double,
        salePrice: Double?,
        fabricOrMaterial: String,
        availableSizes: String,
        stockQuantity: Int,
        description: String,
        tags: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newProduct = ProductEntity(
                id = "PROD-${UUID.randomUUID().toString().take(6).uppercase()}",
                name = name,
                sku = sku.ifBlank { "SKU-${(100..999).random()}" },
                category = category,
                price = price,
                salePrice = salePrice,
                fabricOrMaterial = fabricOrMaterial,
                availableSizes = availableSizes,
                stockQuantity = stockQuantity,
                description = description,
                tags = tags,
                isAvailable = stockQuantity > 0
            )
            repository.insertProduct(newProduct)
            showMessage("Product '${name}' added to catalog!")
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
            showMessage("Product '${product.name}' updated!")
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProduct(product)
            showMessage("Product removed from catalog")
        }
    }

    // --- LEAD ACTIONS ---
    fun addLead(
        customerName: String,
        customerPhone: String,
        productId: String?,
        inquiryText: String,
        preferredLanguage: String = "en",
        source: String = "WhatsApp",
        estimatedValue: Double = 0.0
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Find or create customer
            var customer = repository.getCustomerByPhone(customerPhone)
            if (customer == null) {
                customer = CustomerEntity(
                    id = "CUST-${UUID.randomUUID().toString().take(6).uppercase()}",
                    name = customerName,
                    phone = customerPhone,
                    preferredLanguage = preferredLanguage,
                    source = source
                )
                repository.insertCustomer(customer)
            }

            val matchedProd = products.value.find { it.id == productId }
            val estVal = if (estimatedValue > 0) estimatedValue else (matchedProd?.salePrice ?: matchedProd?.price ?: 2500.0)

            val lead = LeadEntity(
                id = "LEAD-${UUID.randomUUID().toString().take(6).uppercase()}",
                customerId = customer.id,
                customerName = customerName,
                customerPhone = customerPhone,
                stage = LeadStage.NEW_ENQUIRY,
                productId = matchedProd?.id,
                productName = matchedProd?.name,
                inquiryText = inquiryText,
                lastDraftLanguage = preferredLanguage,
                estimatedValue = estVal,
                source = source,
                followUpDate = System.currentTimeMillis() + 86400000L // default 24h follow-up
            )
            repository.insertLead(lead)
            showMessage("New lead created for $customerName")
        }
    }

    fun updateLeadStage(leadId: String, newStage: LeadStage) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLeadStage(leadId, newStage)
            showMessage("Lead moved to ${newStage.displayName}")
        }
    }

    fun completeFollowUp(leadId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markFollowUpComplete(leadId)
            showMessage("Follow-up marked as completed!")
        }
    }

    fun scheduleFollowUp(leadId: String, timestamp: Long, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lead = repository.getLeadById(leadId)
            if (lead != null) {
                repository.updateLead(
                    lead.copy(
                        followUpDate = timestamp,
                        followUpNote = note,
                        isFollowUpCompleted = false,
                        stage = LeadStage.FOLLOW_UP_DUE,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                showMessage("Follow-up scheduled!")
            }
        }
    }

    // --- ORDER ACTIONS ---
    fun convertLeadToOrder(
        lead: LeadEntity,
        deliveryAddress: String,
        paymentMethod: String,
        quantity: Int = 1
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val matchedProduct = products.value.find { it.id == lead.productId }
            val unitPrice = matchedProduct?.salePrice ?: matchedProduct?.price ?: lead.estimatedValue
            val currentProfile = profile.value ?: BusinessProfileEntity()
            val shippingFee = if (unitPrice * quantity >= 1499.0) 0.0 else 80.0
            val total = (unitPrice * quantity) + shippingFee
            val isPrepaidUpi = paymentMethod.contains("UPI", ignoreCase = true)

            val newOrder = OrderEntity(
                id = "ORD-${(1000..9999).random()}",
                businessId = currentProfile.id,
                customerId = lead.customerId,
                customerName = lead.customerName,
                customerPhone = lead.customerPhone,
                productId = matchedProduct?.id ?: "CUSTOM-ITEM",
                productName = matchedProduct?.name ?: lead.productName ?: "Boutique Item",
                quantity = quantity,
                unitPrice = unitPrice,
                shippingFee = shippingFee,
                discountAmount = 0.0,
                totalAmount = total,
                status = OrderStatus.CONFIRMED,
                deliveryAddress = deliveryAddress.ifBlank { "Address provided via WhatsApp" },
                paymentMethod = paymentMethod,
                paymentStatus = if (isPrepaidUpi) OrderPaymentStatus.PAID else OrderPaymentStatus.PENDING,
                amountPaid = if (isPrepaidUpi) total else 0.0,
                isPaid = isPrepaidUpi
            )

            repository.createOrder(newOrder, autoDeductStock = matchedProduct != null)
            repository.updateLeadStage(lead.id, LeadStage.ORDER_PLACED)
            showMessage("Order ${newOrder.id} confirmed! Stock updated.")
        }
    }

    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateOrderStatus(orderId, status)
            showMessage("Order status updated to ${status.displayName}")
        }
    }

    fun updatePaymentStatus(orderId: String, isPaid: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val order = repository.getOrderById(orderId)
            val total = order?.totalAmount ?: 0.0
            val status = if (isPaid) OrderPaymentStatus.PAID else OrderPaymentStatus.PENDING
            val amount = if (isPaid) total else 0.0
            repository.updateOrderPaymentDetails(
                orderId = orderId,
                paymentStatus = status,
                amountPaid = amount,
                reference = order?.paymentReference ?: "",
                notes = order?.paymentNotes ?: ""
            )
            showMessage("Payment marked as ${if (isPaid) "Received (₹${total.toInt()})" else "Pending"}")
        }
    }

    fun recordOrderPayment(
        orderId: String,
        amountReceived: Double,
        paymentMethod: String,
        reference: String,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val order = repository.getOrderById(orderId) ?: return@launch
            val newAmountPaid = (order.amountPaid + amountReceived).coerceAtMost(order.totalAmount)
            val newStatus = when {
                newAmountPaid >= order.totalAmount -> OrderPaymentStatus.PAID
                newAmountPaid > 0.0 -> OrderPaymentStatus.PARTIALLY_PAID
                else -> OrderPaymentStatus.PENDING
            }
            repository.updateOrderPaymentDetails(
                orderId = orderId,
                paymentStatus = newStatus,
                amountPaid = newAmountPaid,
                reference = reference.ifBlank { order.paymentReference },
                notes = if (notes.isNotBlank()) notes else order.paymentNotes
            )
            showMessage("Recorded ₹${amountReceived.toInt()} for Order $orderId (Status: ${newStatus.displayName})")
        }
    }

    fun generateOrderUpiDeepLink(order: OrderEntity): String {
        val merchant = profile.value ?: BusinessProfileEntity()
        val upiVpa = merchant.upiId.trim()
        val payeeName = merchant.businessName.ifBlank { merchant.ownerName }.trim()
        val amount = String.format(java.util.Locale.US, "%.2f", order.balanceDue)
        val note = "Order ${order.id}".replace(" ", "%20")
        val encodedPayee = try {
            URLEncoder.encode(payeeName, "UTF-8")
        } catch (e: Exception) {
            "Merchant"
        }
        return "upi://pay?pa=$upiVpa&pn=$encodedPayee&am=$amount&cu=INR&tn=$note"
    }

    fun generateOrderUpiShareMessage(order: OrderEntity): String {
        val merchant = profile.value ?: BusinessProfileEntity()
        val link = generateOrderUpiDeepLink(order)
        return """
            *Invoice & Payment Details - ${merchant.businessName}*
            Order ID: #${order.id}
            Customer: ${order.customerName}
            Item: ${order.productName} (Qty: ${order.quantity})
            Total Amount: ₹${order.totalAmount.toInt()}
            Amount Paid: ₹${order.amountPaid.toInt()}
            *Balance Due: ₹${order.balanceDue.toInt()}*

            *Pay via UPI:*
            UPI ID: ${merchant.upiId}
            UPI Payment Link: $link

            After payment, please share your UPI Reference / UTR screenshot.
        """.trimIndent()
    }

    fun completeOnboarding(
        businessName: String,
        ownerName: String,
        category: String,
        phone: String,
        email: String,
        shopAddress: String,
        language: String,
        upiId: String,
        accountType: String = "LOCAL_DEVICE"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = profile.value ?: repository.getProfile() ?: BusinessProfileEntity()
            val updated = current.copy(
                businessName = businessName,
                ownerName = ownerName,
                category = category,
                contactPhone = phone,
                ownerEmail = email,
                shopAddress = shopAddress,
                defaultLanguage = language,
                upiId = upiId,
                accountType = accountType,
                isOnboardingComplete = true,
                updatedTimestamp = System.currentTimeMillis()
            )
            repository.updateProfile(updated)
            selectedAppLanguage.value = language
            sessionPrefs.edit().putBoolean("user_authenticated_session", true).apply()
            isUserSignedIn.value = true
            showMessage("Welcome to SalesPilot AI! Shop configured successfully.")
        }
    }

    fun deleteCurrentBusinessData() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = profile.value ?: repository.getProfile() ?: return@launch
            repository.deleteBusinessData(current.id)
            // Re-seed fresh clean onboarding state
            val freshProfile = BusinessProfileEntity(
                id = current.id,
                businessName = "My Boutique Shop",
                ownerName = "Shop Owner",
                contactPhone = "",
                isOnboardingComplete = false
            )
            repository.updateProfile(freshProfile)
            sessionPrefs.edit().putBoolean("user_authenticated_session", false).apply()
            isUserSignedIn.value = false
            currentTab.value = NavigationTab.DASHBOARD
            showMessage("All store records safely deleted and reset.")
        }
    }

    // --- SETTINGS ACTIONS ---
    fun updateBusinessProfile(updated: BusinessProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfile(updated.copy(updatedTimestamp = System.currentTimeMillis()))
            showMessage("Shop policies and settings saved!")
        }
    }

    // --- SIMULATED INCOMING ENQUIRY PLAYGROUND ---
    fun injectSimulatedEnquiry(type: Int) {
        when (type) {
            1 -> {
                // Saree Kannada inquiry
                addLead(
                    customerName = "Divya Ramesh",
                    customerPhone = "+91 94812 55678",
                    productId = "PROD-KJV-01",
                    inquiryText = "ನಮಸ್ಕಾರ, Crimson Maroon Kanjivaram saree ಇವಾಗ ಸಿಗುತ್ತಾ? ಮೈಸೂರಿಗೆ ಎಷ್ಟು ದಿನದಲ್ಲಿ ಡೆಲಿವರಿ ಆಗುತ್ತೆ?",
                    preferredLanguage = "kn",
                    source = "WhatsApp",
                    estimatedValue = 3999.0
                )
            }
            2 -> {
                // Jewellery Hindi inquiry with COD query
                addLead(
                    customerName = "Sneha Verma",
                    customerPhone = "+91 97110 44556",
                    productId = "PROD-JWL-02",
                    inquiryText = "Hi, Kundan choker set Jaipur me deliver ho jayega kya? Aur COD available hai?",
                    preferredLanguage = "hi",
                    source = "Instagram DM",
                    estimatedValue = 1599.0
                )
            }
            3 -> {
                // Festive Suit English inquiry
                addLead(
                    customerName = "Kavitha Natarajan",
                    customerPhone = "+91 98401 77889",
                    productId = "PROD-KUR-03",
                    inquiryText = "Can I get this Chanderi Anarkali in size L for Saturday function in Chennai? Is there any discount if I buy 2 sets?",
                    preferredLanguage = "en",
                    source = "WhatsApp",
                    estimatedValue = 2499.0
                )
            }
        }
    }

    // --- SUBSCRIPTION & FREE TRIAL ACTIONS ---
    fun selectSubscriptionPlan(planId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = profile.value ?: repository.getProfile() ?: BusinessProfileEntity()
            val updated = current.copy(
                selectedPlan = planId,
                subscriptionStatus = if (current.isPilotGranted) "PILOT_ACTIVE" else "PENDING_PAYMENT",
                updatedTimestamp = System.currentTimeMillis()
            )
            repository.updateProfile(updated)
            showMessage("Plan recorded: $planId (Payment Pending Verification)")
        }
    }

    fun extendTrial(days: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = profile.value ?: repository.getProfile() ?: BusinessProfileEntity()
            val baseEnd = if (current.trialEndDate > System.currentTimeMillis()) current.trialEndDate else System.currentTimeMillis()
            val newEnd = baseEnd + (days.toLong() * SubscriptionManager.MS_PER_DAY)
            val updated = current.copy(
                trialEndDate = newEnd,
                subscriptionStatus = "TRIAL_ACTIVE",
                updatedTimestamp = System.currentTimeMillis()
            )
            repository.updateProfile(updated)
            showMessage("Free trial extended by $days days!")
        }
    }

    fun grantPilotAccess(planId: String = "PRO", notes: String = "Granted by founder during early beta validation") {
        viewModelScope.launch(Dispatchers.IO) {
            val current = profile.value ?: repository.getProfile() ?: BusinessProfileEntity()
            val updated = current.copy(
                isPilotGranted = true,
                selectedPlan = planId,
                subscriptionStatus = "PILOT_ACTIVE",
                pilotNotes = notes,
                updatedTimestamp = System.currentTimeMillis()
            )
            repository.updateProfile(updated)
            showMessage("Pilot $planId access granted!")
        }
    }

    fun simulateTrialExpiry() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = profile.value ?: repository.getProfile() ?: BusinessProfileEntity()
            val updated = current.copy(
                isPilotGranted = false,
                trialEndDate = System.currentTimeMillis() - 1000L,
                subscriptionStatus = "TRIAL_EXPIRED",
                updatedTimestamp = System.currentTimeMillis()
            )
            repository.updateProfile(updated)
            showMessage("Simulated trial expiry: status set to Expired")
        }
    }

    fun resetToFreshTrial() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = profile.value ?: repository.getProfile() ?: BusinessProfileEntity()
            val now = System.currentTimeMillis()
            val updated = current.copy(
                isPilotGranted = false,
                trialStartDate = now,
                trialEndDate = now + SubscriptionManager.TRIAL_DURATION_MS,
                subscriptionStatus = "TRIAL_ACTIVE",
                selectedPlan = "FREE_TRIAL",
                updatedTimestamp = now
            )
            repository.updateProfile(updated)
            showMessage("Reset to fresh 14-day free trial!")
        }
    }
}
