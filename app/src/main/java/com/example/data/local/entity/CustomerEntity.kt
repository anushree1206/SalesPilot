package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey
    val id: String,
    val businessId: String = "primary_business_profile",
    val name: String,
    val phone: String,
    val preferredLanguage: String = "en", // "en", "hi", "kn"
    val source: String = "WhatsApp", // "WhatsApp", "Instagram DM", "Exhibition", "Referral"
    val city: String = "Bangalore",
    val notes: String = "",
    val totalOrdersCount: Int = 0,
    val totalSpend: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
