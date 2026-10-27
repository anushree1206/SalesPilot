package com.example.domain.subscription

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.local.entity.BusinessProfileEntity
import java.net.URLEncoder

const val FOUNDER_PHONE = "918217018706"
const val FOUNDER_WA_BASE_URL = "https://wa.me/918217018706"
const val FOUNDER_UNLOCK_PIN = "8217"

enum class SubscriptionStatus(val displayName: String) {
    TRIAL_ACTIVE("14-Day Free Trial"),
    PENDING_PAYMENT("Payment Pending Verification"),
    PILOT_ACTIVE("Active Subscription"),
    TRIAL_EXPIRED("Free Trial Expired"),
    PLAN_REQUESTED("Payment Pending Verification")
}

data class PlanDefinition(
    val id: String,
    val name: String,
    val priceInr: Int,
    val billingCycle: String = "/ month",
    val isProposedLaunchPrice: Boolean = true,
    val badge: String,
    val subtitle: String,
    val features: List<String>,
    val isPopular: Boolean = false
)

object SubscriptionCatalog {
    val FREE_TRIAL = PlanDefinition(
        id = "FREE_TRIAL",
        name = "14-Day Free Trial",
        priceInr = 0,
        billingCycle = "for 14 days",
        isProposedLaunchPrice = false,
        badge = "No Card Required",
        subtitle = "Zero payment details required and zero automatic charges.",
        features = listOf(
            "14-day full access to SalesPilot AI",
            "Multilingual AI Copilot (Kannada, Hindi, English)",
            "WhatsApp order confirmations and receipts",
            "Customer lead pipeline & follow-up tracker",
            "Boutique product catalog & stock manager"
        )
    )

    val STARTER = PlanDefinition(
        id = "STARTER",
        name = "Starter Plan",
        priceInr = 299,
        billingCycle = "/ month",
        isProposedLaunchPrice = true,
        badge = "Proposed Launch Price",
        subtitle = "Ideal for growing individual boutiques, home-based saree sellers, and jewellery creators.",
        features = listOf(
            "Up to 150 customer enquiries per month",
            "AI Copilot replies in Kannada, Hindi, English, and Hinglish",
            "Customer pipeline and overdue follow-up reminders",
            "Custom store policies (COD advance, shipping rules)",
            "Formatted WhatsApp bill and invoice generator",
            "100% on-device private data storage"
        )
    )

    val PRO = PlanDefinition(
        id = "PRO",
        name = "Pro Plan",
        priceInr = 599,
        billingCycle = "/ month",
        isProposedLaunchPrice = true,
        badge = "Proposed Launch Price • Most Popular",
        subtitle = "For active boutiques and established sellers managing high-volume festive orders.",
        features = listOf(
            "Unlimited customer enquiries and follow-ups",
            "Unlimited AI grounded replies and objection handling",
            "Multi-language code-mixed NLP support",
            "WhatsApp catalog product showcase cards",
            "Priority early pilot updates & direct founder support",
            "Custom backend proxy support for high-volume teams"
        ),
        isPopular = true
    )

    val ALL_PLANS = listOf(FREE_TRIAL, STARTER, PRO)

    fun getPlanById(id: String?): PlanDefinition {
        return ALL_PLANS.find { it.id.equals(id, ignoreCase = true) } ?: STARTER
    }
}

object SubscriptionManager {
    const val TRIAL_DURATION_DAYS = 14
    const val MS_PER_DAY = 86400000L
    const val TRIAL_DURATION_MS = TRIAL_DURATION_DAYS * MS_PER_DAY

    /**
     * Calculates remaining trial days based on end timestamp.
     * Returns 0 if expired. Uses ceil so partial days count as 1 day remaining.
     */
    fun calculateDaysRemaining(trialEndDate: Long, currentTime: Long = System.currentTimeMillis()): Int {
        val diff = trialEndDate - currentTime
        return if (diff <= 0L) 0 else kotlin.math.ceil(diff.toDouble() / MS_PER_DAY).toInt()
    }

    /**
     * Checks if the merchant currently has active access to the application.
     * True if pilot access has been manually granted, or if the 14-day trial has not elapsed.
     */
    fun isAccessActive(profile: BusinessProfileEntity?, currentTime: Long = System.currentTimeMillis()): Boolean {
        if (profile == null) return true // Graceful fallback
        if (profile.isPilotGranted) return true
        if (profile.subscriptionStatus == SubscriptionStatus.PILOT_ACTIVE.name) return true
        return currentTime < profile.trialEndDate
    }

    /**
     * Computes the current subscription status based on profile and time.
     */
    fun getResolvedStatus(profile: BusinessProfileEntity?, currentTime: Long = System.currentTimeMillis()): SubscriptionStatus {
        if (profile == null) return SubscriptionStatus.TRIAL_ACTIVE
        if (profile.isPilotGranted || profile.subscriptionStatus == SubscriptionStatus.PILOT_ACTIVE.name) {
            return SubscriptionStatus.PILOT_ACTIVE
        }
        if (profile.subscriptionStatus == SubscriptionStatus.PENDING_PAYMENT.name ||
            profile.subscriptionStatus == SubscriptionStatus.PLAN_REQUESTED.name) {
            return SubscriptionStatus.PENDING_PAYMENT
        }
        return if (currentTime < profile.trialEndDate) {
            SubscriptionStatus.TRIAL_ACTIVE
        } else {
            SubscriptionStatus.TRIAL_EXPIRED
        }
    }

    /**
     * Generates a pre-filled WhatsApp contact message dynamically based on selected plan.
     * Includes business name only if available.
     * Strictly avoids customer phone numbers, addresses, order histories, or personal customer data.
     */
    fun buildSubscriptionContactMessage(plan: PlanDefinition, businessName: String? = null): String {
        val planDisplayName = if (plan.id == "PRO") "Pro" else if (plan.id == "STARTER") "Starter" else plan.name
        val priceStr = if (plan.priceInr == 0) "Free" else "₹${plan.priceInr}/month"
        val cleanBusinessName = businessName?.trim()
        val businessLine = if (!cleanBusinessName.isNullOrBlank()) "\nBusiness name: $cleanBusinessName" else ""

        return "Hi! I would like to subscribe to SalesPilot AI.\n" +
                "Plan: $planDisplayName\n" +
                "Price: $priceStr" +
                businessLine + "\n" +
                "Please share the payment instructions."
    }

    /**
     * Builds the complete URL-encoded WhatsApp contact link using the founder international-format number.
     * Uses %20 for spaces to ensure compatibility with both WhatsApp client and browser fallbacks.
     */
    fun buildSubscriptionWhatsAppUrl(plan: PlanDefinition, businessName: String? = null): String {
        val message = buildSubscriptionContactMessage(plan, businessName)
        val encodedMessage = URLEncoder.encode(message, "UTF-8").replace("+", "%20")
        return "$FOUNDER_WA_BASE_URL?text=$encodedMessage"
    }

    /**
     * Attempts to open WhatsApp directly via ACTION_VIEW intent.
     * If WhatsApp is not installed or unavailable, gracefully falls back to opening the link in a browser.
     */
    fun launchWhatsAppContact(context: Context, plan: PlanDefinition, businessName: String? = null): Boolean {
        val url = buildSubscriptionWhatsAppUrl(plan, businessName)
        val uri = Uri.parse(url)

        val whatsappIntent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        return try {
            context.startActivity(whatsappIntent)
            true
        } catch (e: Exception) {
            // WhatsApp package not installed; fallback to browser
            val browserIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(browserIntent)
                true
            } catch (e2: Exception) {
                false
            }
        }
    }
}
