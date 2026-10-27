package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessProfileDao {
    @Query("SELECT * FROM business_profile WHERE id = :businessId LIMIT 1")
    fun getProfileFlow(businessId: String = "primary_business_profile"): Flow<BusinessProfileEntity?>

    @Query("SELECT * FROM business_profile WHERE id = :businessId LIMIT 1")
    suspend fun getProfile(businessId: String = "primary_business_profile"): BusinessProfileEntity?

    @Query("SELECT * FROM business_profile ORDER BY updatedTimestamp DESC")
    fun getAllProfiles(): Flow<List<BusinessProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: BusinessProfileEntity)

    @Query("DELETE FROM business_profile WHERE id = :businessId")
    suspend fun deleteProfile(businessId: String)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getProductsForBusiness(businessId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE businessId = :businessId AND isAvailable = 1 ORDER BY name ASC")
    fun getAvailableProductsForBusiness(businessId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAvailableProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE businessId = :businessId AND (name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%')")
    fun searchProductsForBusiness(businessId: String, query: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE businessId = :businessId")
    suspend fun deleteProductsForBusiness(businessId: String)

    @Query("UPDATE products SET stockQuantity = MAX(0, stockQuantity - :quantity) WHERE id = :productId")
    suspend fun decrementStock(productId: String, quantity: Int)
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getCustomersForBusiness(businessId: String): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE businessId = :businessId AND phone = :phone LIMIT 1")
    suspend fun getCustomerByPhoneForBusiness(businessId: String, phone: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE phone = :phone LIMIT 1")
    suspend fun getCustomerByPhone(phone: String): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("DELETE FROM customers WHERE businessId = :businessId")
    suspend fun deleteCustomersForBusiness(businessId: String)

    @Query("UPDATE customers SET totalOrdersCount = totalOrdersCount + 1, totalSpend = totalSpend + :amount WHERE id = :customerId")
    suspend fun incrementCustomerSpend(customerId: String, amount: Double)
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads WHERE businessId = :businessId ORDER BY updatedAt DESC")
    fun getLeadsForBusiness(businessId: String): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads ORDER BY updatedAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE businessId = :businessId AND stage = :stage ORDER BY updatedAt DESC")
    fun getLeadsByStageForBusiness(businessId: String, stage: LeadStage): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE stage = :stage ORDER BY updatedAt DESC")
    fun getLeadsByStage(stage: LeadStage): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE businessId = :businessId AND followUpDate IS NOT NULL AND isFollowUpCompleted = 0 ORDER BY followUpDate ASC")
    fun getPendingFollowUpsForBusiness(businessId: String): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE followUpDate IS NOT NULL AND isFollowUpCompleted = 0 ORDER BY followUpDate ASC")
    fun getPendingFollowUps(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE id = :id LIMIT 1")
    suspend fun getLeadById(id: String): LeadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity)

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Query("UPDATE leads SET stage = :newStage, updatedAt = :timestamp WHERE id = :leadId")
    suspend fun updateLeadStage(leadId: String, newStage: LeadStage, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE leads SET isFollowUpCompleted = 1, updatedAt = :timestamp WHERE id = :leadId")
    suspend fun markFollowUpComplete(leadId: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE businessId = :businessId")
    suspend fun deleteLeadsForBusiness(businessId: String)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getOrdersForBusiness(businessId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE businessId = :businessId AND status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatusForBusiness(businessId: String, status: OrderStatus): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET isPaid = :isPaid, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: String, isPaid: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET paymentStatus = :paymentStatus, amountPaid = :amountPaid, paymentReference = :reference, paymentNotes = :notes, isPaid = :isPaid, updatedAt = :timestamp WHERE id = :orderId")
    suspend fun updateOrderPaymentDetails(
        orderId: String,
        paymentStatus: OrderPaymentStatus,
        amountPaid: Double,
        reference: String,
        notes: String,
        isPaid: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("DELETE FROM orders WHERE businessId = :businessId")
    suspend fun deleteOrdersForBusiness(businessId: String)
}
