package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfileEntity(
    @PrimaryKey
    val id: String = "primary_business_profile",
    val businessName: String = "Meera's Handcrafted Silk & Jewellery",
    val ownerName: String = "Meera Sundaram",
    val category: String = "Ethnic Wear & Handlooms", // Ethnic Wear, Handcrafted Jewellery, Footwear & Accessories, Custom Tailoring
    val contactPhone: String = "+91 98450 12345",
    val upiId: String = "meerasilk@okhdfcbank",
    val defaultLanguage: String = "en", // "en", "hi", "kn"
    val standardShippingDays: String = "3-5 business days across India",
    val shippingChargePolicy: String = "Free shipping above ₹1,499. Flat ₹80 for orders below.",
    val returnExchangePolicy: String = "7-day replacement for damaged items with mandatory unboxing video. No cash refunds.",
    val isCodEnabled: Boolean = true,
    val codPolicy: String = "COD available with ₹100 advance deposit via UPI to verify address.",
    val maxAllowedDiscountPercent: Int = 10, // Max discount seller authorizes; 0 means fixed prices
    val isOnboardingComplete: Boolean = true,
    val currencySymbol: String = "₹",
    val aiBackendMode: String = "LOCAL_RULE_ENGINE", // "LOCAL_RULE_ENGINE", "CUSTOM_PROXY", "BYOK_KEY"
    val proxyEndpointUrl: String = "", // Optional backend proxy endpoint (e.g. Cloud Run / Cloud Functions)
    val customApiKey: String = "", // On-device private key if merchant opts for BYOK
    val subscriptionStatus: String = "TRIAL_ACTIVE", // "TRIAL_ACTIVE", "TRIAL_EXPIRED", "PILOT_ACTIVE", "PLAN_REQUESTED"
    val selectedPlan: String = "FREE_TRIAL", // "FREE_TRIAL", "STARTER", "PRO"
    val trialStartDate: Long = System.currentTimeMillis(),
    val trialEndDate: Long = System.currentTimeMillis() + (14L * 86400000L), // 14-day default free trial
    val isPilotGranted: Boolean = false, // Allows founder to manually grant pilot access during validation
    val pilotNotes: String = "", // Internal notes for pilot partners
    val ownerEmail: String = "",
    val shopAddress: String = "",
    val isOnlineAccountVerified: Boolean = false,
    val authUserId: String = "",
    val accountType: String = "LOCAL_DEVICE", // "LOCAL_DEVICE" vs "VERIFIED_ONLINE"
    val updatedTimestamp: Long = System.currentTimeMillis()
)

