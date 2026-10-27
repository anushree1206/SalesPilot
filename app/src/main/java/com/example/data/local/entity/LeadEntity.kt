package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LeadStage(val displayName: String) {
    NEW_ENQUIRY("New Enquiry"),
    CONTACTED("Contacted"),
    INTERESTED("Interested"),
    FOLLOW_UP_DUE("Follow-Up Due"),
    ORDER_PLACED("Order Placed"),
    CLOSED_LOST("Closed")
}

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey
    val id: String,
    val businessId: String = "primary_business_profile",
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val stage: LeadStage = LeadStage.NEW_ENQUIRY,
    val productId: String? = null,
    val productName: String? = null,
    val inquiryText: String,
    val lastAiDraftReply: String? = null,
    val lastDraftLanguage: String = "en", // "en", "hi", "kn"
    val followUpDate: Long? = null,
    val followUpNote: String? = null,
    val isFollowUpCompleted: Boolean = false,
    val estimatedValue: Double = 0.0,
    val source: String = "WhatsApp",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
