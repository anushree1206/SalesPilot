package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val displayName: String) {
    DRAFT("Draft"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

enum class OrderPaymentStatus(val displayName: String) {
    PENDING("Pending"),
    PARTIALLY_PAID("Partially Paid"),
    PAID("Paid"),
    REFUNDED("Refunded")
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String, // e.g. "ORD-1042"
    val businessId: String = "primary_business_profile",
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val productId: String,
    val productName: String,
    val quantity: Int = 1,
    val unitPrice: Double,
    val shippingFee: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.CONFIRMED,
    val deliveryAddress: String = "",
    val paymentMethod: String = "UPI (GPay / PhonePe)",
    val paymentStatus: OrderPaymentStatus = OrderPaymentStatus.PENDING,
    val amountPaid: Double = 0.0,
    val paymentReference: String = "",
    val paymentNotes: String = "",
    val isPaid: Boolean = false,
    val trackingNumber: String? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val balanceDue: Double
        get() = (totalAmount - amountPaid).coerceAtLeast(0.0)
}
