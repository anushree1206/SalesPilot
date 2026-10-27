package com.example.data.repository

import com.example.data.local.SalesPilotDatabase
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SalesPilotRepository(private val database: SalesPilotDatabase) {
    private val profileDao = database.businessProfileDao()
    private val productDao = database.productDao()
    private val customerDao = database.customerDao()
    private val leadDao = database.leadDao()
    private val orderDao = database.orderDao()

    // Profile & Multi-tenant accounts
    fun getProfileFlow(businessId: String = "primary_business_profile"): Flow<BusinessProfileEntity?> =
        profileDao.getProfileFlow(businessId)

    suspend fun getProfile(businessId: String = "primary_business_profile"): BusinessProfileEntity? =
        profileDao.getProfile(businessId)

    fun getAllProfiles(): Flow<List<BusinessProfileEntity>> = profileDao.getAllProfiles()

    suspend fun updateProfile(profile: BusinessProfileEntity) = profileDao.insertOrUpdate(profile)

    suspend fun deleteBusinessData(businessId: String) {
        orderDao.deleteOrdersForBusiness(businessId)
        leadDao.deleteLeadsForBusiness(businessId)
        customerDao.deleteCustomersForBusiness(businessId)
        productDao.deleteProductsForBusiness(businessId)
        profileDao.deleteProfile(businessId)
    }

    // Products
    fun getProductsForBusiness(businessId: String): Flow<List<ProductEntity>> =
        productDao.getProductsForBusiness(businessId)

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getAvailableProducts(): Flow<List<ProductEntity>> = productDao.getAvailableProducts()
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)
    suspend fun getProductById(id: String): ProductEntity? = productDao.getProductById(id)
    suspend fun insertProduct(product: ProductEntity) = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)
    suspend fun decrementStock(productId: String, quantity: Int) = productDao.decrementStock(productId, quantity)

    // Customers
    fun getCustomersForBusiness(businessId: String): Flow<List<CustomerEntity>> =
        customerDao.getCustomersForBusiness(businessId)

    fun getAllCustomers(): Flow<List<CustomerEntity>> = customerDao.getAllCustomers()
    suspend fun getCustomerById(id: String): CustomerEntity? = customerDao.getCustomerById(id)
    suspend fun getCustomerByPhone(phone: String): CustomerEntity? = customerDao.getCustomerByPhone(phone)
    suspend fun getCustomerByPhoneForBusiness(businessId: String, phone: String): CustomerEntity? =
        customerDao.getCustomerByPhoneForBusiness(businessId, phone)

    suspend fun insertCustomer(customer: CustomerEntity) = customerDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: CustomerEntity) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: CustomerEntity) = customerDao.deleteCustomer(customer)

    // Leads
    fun getLeadsForBusiness(businessId: String): Flow<List<LeadEntity>> =
        leadDao.getLeadsForBusiness(businessId)

    fun getAllLeads(): Flow<List<LeadEntity>> = leadDao.getAllLeads()
    fun getLeadsByStage(stage: LeadStage): Flow<List<LeadEntity>> = leadDao.getLeadsByStage(stage)
    fun getPendingFollowUps(): Flow<List<LeadEntity>> = leadDao.getPendingFollowUps()
    fun getPendingFollowUpsForBusiness(businessId: String): Flow<List<LeadEntity>> =
        leadDao.getPendingFollowUpsForBusiness(businessId)

    suspend fun getLeadById(id: String): LeadEntity? = leadDao.getLeadById(id)
    suspend fun insertLead(lead: LeadEntity) = leadDao.insertLead(lead)
    suspend fun updateLead(lead: LeadEntity) = leadDao.updateLead(lead)
    suspend fun updateLeadStage(leadId: String, newStage: LeadStage) = leadDao.updateLeadStage(leadId, newStage)
    suspend fun markFollowUpComplete(leadId: String) = leadDao.markFollowUpComplete(leadId)
    suspend fun deleteLead(lead: LeadEntity) = leadDao.deleteLead(lead)

    // Orders
    fun getOrdersForBusiness(businessId: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersForBusiness(businessId)

    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    suspend fun getOrderById(id: String): OrderEntity? = orderDao.getOrderById(id)
    suspend fun createOrder(order: OrderEntity, autoDeductStock: Boolean = true) {
        orderDao.insertOrder(order)
        if (autoDeductStock) {
            productDao.decrementStock(order.productId, order.quantity)
        }
        customerDao.incrementCustomerSpend(order.customerId, order.totalAmount)
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) = orderDao.updateOrderStatus(orderId, status)
    suspend fun updatePaymentStatus(orderId: String, isPaid: Boolean) = orderDao.updatePaymentStatus(orderId, isPaid)

    suspend fun updateOrderPaymentDetails(
        orderId: String,
        paymentStatus: OrderPaymentStatus,
        amountPaid: Double,
        reference: String,
        notes: String
    ) {
        val isPaid = paymentStatus == OrderPaymentStatus.PAID
        orderDao.updateOrderPaymentDetails(
            orderId = orderId,
            paymentStatus = paymentStatus,
            amountPaid = amountPaid,
            reference = reference,
            notes = notes,
            isPaid = isPaid
        )
    }

    suspend fun deleteOrder(order: OrderEntity) = orderDao.deleteOrder(order)
}
