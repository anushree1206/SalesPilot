package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.LeadEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.ui.NavigationTab
import com.example.ui.SalesPilotViewModel
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.ui.components.SalesPilotBottomBar
import com.example.ui.components.SalesPilotTopBar
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.SalesPilotTheme
import com.example.ui.theme.SalesPilotThemeGradients

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesPilotTheme {
                SalesPilotApp()
            }
        }
    }
}

@Composable
fun SalesPilotApp(viewModel: SalesPilotViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // State collections
    val isUserSignedIn by viewModel.isUserSignedIn.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val appLanguage by viewModel.selectedAppLanguage.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val leads by viewModel.leads.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val pendingFollowUps by viewModel.pendingFollowUps.collectAsStateWithLifecycle()
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()

    val targetLead by viewModel.copilotTargetLead.collectAsStateWithLifecycle()
    val copilotProduct by viewModel.copilotSelectedProduct.collectAsStateWithLifecycle()
    val copilotInquiry by viewModel.copilotInquiryText.collectAsStateWithLifecycle()
    val copilotCustomerName by viewModel.copilotCustomerName.collectAsStateWithLifecycle()
    val copilotDraftResult by viewModel.copilotDraftResult.collectAsStateWithLifecycle()
    val isCopilotGenerating by viewModel.isCopilotGenerating.collectAsStateWithLifecycle()

    val snackbarMsg by viewModel.userSnackbarMessage.collectAsStateWithLifecycle()

    // Dialog states
    var showAddLeadDialog by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showSimulateDialog by remember { mutableStateOf(false) }
    var showSubscriptionPlansDialog by remember { mutableStateOf(false) }
    var showOnboardingDialog by remember { mutableStateOf(false) }
    var collectUpiOrderTarget by remember { mutableStateOf<OrderEntity?>(null) }
    var orderConversionTarget by remember { mutableStateOf<Pair<LeadEntity, ProductEntity?>?>(null) }

    // Handle snackbar messages
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Handle Android system back button
    BackHandler(enabled = isUserSignedIn && currentTab != NavigationTab.DASHBOARD) {
        viewModel.selectTab(NavigationTab.DASHBOARD)
    }

    val currentStrings = com.example.ui.i18n.StringsProvider.getStrings(appLanguage)

    CompositionLocalProvider(com.example.ui.i18n.LocalAppStrings provides currentStrings) {
        if (!isUserSignedIn) {
            WelcomeScreen(
                profile = profile,
                onGetStarted = { showOnboardingDialog = true },
                onSignInSuccess = { viewModel.signIn() }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    SalesPilotTopBar(
                        profile = profile,
                        currentLanguage = appLanguage,
                        onLanguageChange = { viewModel.setAppLanguage(it) },
                        onSimulateClick = { showSimulateDialog = true },
                        onSettingsClick = { viewModel.selectTab(NavigationTab.SETTINGS) },
                        onSignOutClick = { viewModel.signOut() }
                    )
                },
                bottomBar = {
                    SalesPilotBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        pendingFollowUpsCount = pendingFollowUps.count { !it.isFollowUpCompleted },
                        newEnquiriesCount = metrics.newEnquiries
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(com.example.ui.theme.SalesPilotThemeGradients.screenBackground)
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                NavigationTab.DASHBOARD -> {
                    DashboardScreen(
                        metrics = metrics,
                        recentLeads = leads,
                        pendingFollowUps = pendingFollowUps,
                        profile = profile,
                        onNavigateTab = { viewModel.selectTab(it) },
                        onLeadClick = { lead -> viewModel.prepareCopilotForLead(lead) },
                        onSimulateClick = { showSimulateDialog = true },
                        onAddNewLeadClick = { showAddLeadDialog = true },
                        onAddNewProductClick = { showAddProductDialog = true },
                        onViewPlansClick = { showSubscriptionPlansDialog = true }
                    )
                }

                NavigationTab.PIPELINE -> {
                    PipelineScreen(
                        leads = leads,
                        onLeadSelectedForCopilot = { lead -> viewModel.prepareCopilotForLead(lead) },
                        onUpdateStage = { id, stage -> viewModel.updateLeadStage(id, stage) },
                        onConvertToOrderClick = { lead ->
                            val prod = products.find { it.id == lead.productId }
                            orderConversionTarget = lead to prod
                        },
                        onAddNewLeadClick = { showAddLeadDialog = true },
                        onDirectWhatsAppClick = { phone, msg ->
                            viewModel.openInWhatsApp(context, phone, msg)
                        }
                    )
                }

                NavigationTab.CATALOG -> {
                    CatalogScreen(
                        products = products,
                        onAddProductClick = { showAddProductDialog = true },
                        onUpdateProduct = { viewModel.updateProduct(it) },
                        onDeleteProduct = { viewModel.deleteProduct(it) },
                        onShareProductOnWhatsApp = { prod ->
                            val shopName = profile?.businessName ?: "Our Boutique"
                            val curr = profile?.currencySymbol ?: "₹"
                            val priceStr = if (prod.salePrice != null) {
                                "$curr${prod.salePrice.toInt()} (Special Offer! Orig: $curr${prod.price.toInt()})"
                            } else {
                                "$curr${prod.price.toInt()}"
                            }
                            val shareMessage = """
                                🌸 *${prod.name.uppercase()}*
                                Brand: $shopName
                                
                                📌 *Details:*
                                • Price: $priceStr
                                • Material: ${prod.fabricOrMaterial}
                                • Available Sizes: ${prod.availableSizes}
                                • Description: ${prod.description}
                                
                                🚚 *Delivery:* ${profile?.standardShippingDays ?: "3-5 days"}
                                💵 *COD Policy:* ${profile?.codPolicy ?: "Available with advance"}
                                
                                Reply to this message to book yours before it sells out! ✨
                            """.trimIndent()
                            viewModel.openInWhatsApp(context, "", shareMessage)
                        }
                    )
                }

                NavigationTab.COPILOT -> {
                    AiCopilotScreen(
                        profile = profile,
                        products = products,
                        targetLead = targetLead,
                        initialProduct = copilotProduct,
                        initialInquiry = copilotInquiry,
                        initialCustomerName = copilotCustomerName,
                        draftResult = copilotDraftResult,
                        isGenerating = isCopilotGenerating,
                        currentLanguage = appLanguage,
                        onGenerateClick = { intent, lang ->
                            viewModel.generateCopilotReply(manualIntent = intent, lang = lang)
                        },
                        onProductSelected = { viewModel.copilotSelectedProduct.value = it },
                        onInquiryTextChanged = { viewModel.copilotInquiryText.value = it },
                        onCustomerNameChanged = { viewModel.copilotCustomerName.value = it },
                        onSendWhatsApp = { phone, text ->
                            viewModel.openInWhatsApp(context, phone, text)
                        },
                        onConvertToOrderClick = {
                            targetLead?.let { l ->
                                orderConversionTarget = l to copilotProduct
                            }
                        },
                        onShowMessage = { viewModel.showMessage(it) }
                    )
                }

                NavigationTab.ORDERS -> {
                    OrdersScreen(
                        orders = orders,
                        onUpdateOrderStatus = { id, st -> viewModel.updateOrderStatus(id, st) },
                        onUpdatePaymentStatus = { id, paid -> viewModel.updatePaymentStatus(id, paid) },
                        onRecordOrderPayment = { id, amt, method, ref, notes ->
                            viewModel.recordOrderPayment(id, amt, method, ref, notes)
                        },
                        onCollectUpiPayment = { order ->
                            collectUpiOrderTarget = order
                        },
                        onShareInvoiceOnWhatsApp = { order ->
                            val invoiceText = viewModel.generateOrderUpiShareMessage(order)
                            viewModel.openInWhatsApp(context, order.customerPhone, invoiceText)
                        }
                    )
                }

                NavigationTab.FOLLOW_UPS -> {
                    FollowUpsScreen(
                        followUps = pendingFollowUps,
                        onCompleteFollowUp = { viewModel.completeFollowUp(it) },
                        onSendWhatsAppReminder = { lead ->
                            val matchedProd = products.find { it.id == lead.productId }
                            val shopName = profile?.businessName ?: "Our Boutique"
                            val reminderText = """
                                Hello ${lead.customerName}! 🌸
                                Gentle reminder from $shopName.
                                
                                We have *${matchedProd?.name ?: lead.productName ?: "your selected design"}* temporarily reserved for you today.
                                Since festive stock is moving fast, please let us know if you would like us to pack this order for you! ✨
                            """.trimIndent()
                            viewModel.openInWhatsApp(context, lead.customerPhone, reminderText)
                        },
                        onOpenInCopilot = { lead -> viewModel.prepareCopilotForLead(lead) },
                        onConvertToOrder = { lead ->
                            val prod = products.find { it.id == lead.productId }
                            orderConversionTarget = lead to prod
                        }
                    )
                }

                NavigationTab.SETTINGS -> {
                    SettingsScreen(
                        profile = profile,
                        onSaveProfile = { viewModel.updateBusinessProfile(it) },
                        onViewPlansClick = { showSubscriptionPlansDialog = true },
                        onExtendTrial = { days -> viewModel.extendTrial(days) },
                        onGrantPilotPro = { viewModel.grantPilotAccess() },
                        onSimulateExpiry = { viewModel.simulateTrialExpiry() },
                        onResetTrial = { viewModel.resetToFreshTrial() },
                        onOpenOnboarding = { showOnboardingDialog = true },
                        onDeleteData = { viewModel.deleteCurrentBusinessData() },
                        onSignOut = { viewModel.signOut() }
                    )
                }
            }
        }
    }
}

    // Modal Dialogs
    if (showSubscriptionPlansDialog) {
        SubscriptionPlansDialog(
            profile = profile,
            onDismiss = { showSubscriptionPlansDialog = false },
            onSelectPlan = { planId ->
                viewModel.selectSubscriptionPlan(planId)
            },
            onRequestPilotActivationWhatsApp = { plan, shopName ->
                viewModel.selectSubscriptionPlan(plan.id)
                val success = com.example.domain.subscription.SubscriptionManager.launchWhatsAppContact(
                    context = context,
                    plan = plan,
                    businessName = shopName
                )
                if (!success) {
                    viewModel.showMessage("Could not open WhatsApp or browser. Founder contact: +91 8217018706")
                }
            }
        )
    }

    if (showAddLeadDialog) {
        AddLeadDialog(
            products = products,
            onDismiss = { showAddLeadDialog = false },
            onConfirm = { name, phone, prodId, inq, lang, src ->
                viewModel.addLead(
                    customerName = name,
                    customerPhone = phone,
                    productId = prodId,
                    inquiryText = inq,
                    preferredLanguage = lang,
                    source = src
                )
                showAddLeadDialog = false
            }
        )
    }

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onConfirm = { name, sku, cat, pr, spr, fab, sz, qty, desc ->
                viewModel.addProduct(
                    name = name,
                    sku = sku,
                    category = cat,
                    price = pr,
                    salePrice = spr,
                    fabricOrMaterial = fab,
                    availableSizes = sz,
                    stockQuantity = qty,
                    description = desc,
                    tags = ""
                )
                showAddProductDialog = false
            }
        )
    }

    if (showSimulateDialog) {
        SimulateEnquiryDialog(
            onDismiss = { showSimulateDialog = false },
            onSelectScenario = { scenarioId ->
                viewModel.injectSimulatedEnquiry(scenarioId)
            }
        )
    }

    orderConversionTarget?.let { (lead, product) ->
        ConvertToOrderDialog(
            lead = lead,
            product = product,
            onDismiss = { orderConversionTarget = null },
            onConfirm = { addr, payMethod, qty ->
                viewModel.convertLeadToOrder(
                    lead = lead,
                    deliveryAddress = addr,
                    paymentMethod = payMethod,
                    quantity = qty
                )
                orderConversionTarget = null
            }
        )
    }

    collectUpiOrderTarget?.let { order ->
        profile?.let { prof ->
            CollectUpiDialog(
                order = order,
                merchantProfile = prof,
                upiDeepLink = viewModel.generateOrderUpiDeepLink(order),
                onDismiss = { collectUpiOrderTarget = null },
                onCopyLink = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("UPI Payment Link", viewModel.generateOrderUpiDeepLink(order))
                    clipboard.setPrimaryClip(clip)
                    viewModel.showMessage("UPI payment link copied to clipboard!")
                },
                onShareOnWhatsApp = {
                    val shareMsg = viewModel.generateOrderUpiShareMessage(order)
                    viewModel.openInWhatsApp(context, order.customerPhone, shareMsg)
                    collectUpiOrderTarget = null
                }
            )
        }
    }

    if (showOnboardingDialog) {
        OnboardingDialog(
            initialProfile = profile,
            onDismiss = { showOnboardingDialog = false },
            onComplete = { bName, oName, cat, ph, em, addr, lang, upi, accType ->
                viewModel.completeOnboarding(bName, oName, cat, ph, em, addr, lang, upi, accType)
                showOnboardingDialog = false
            }
        )
    }
    }
}
