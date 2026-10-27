package com.example

import com.example.data.local.entity.BusinessProfileEntity
import com.example.domain.subscription.FOUNDER_PHONE
import com.example.domain.subscription.FOUNDER_UNLOCK_PIN
import com.example.domain.subscription.FOUNDER_WA_BASE_URL
import com.example.domain.subscription.SubscriptionCatalog
import com.example.domain.subscription.SubscriptionManager
import com.example.domain.subscription.SubscriptionStatus
import org.junit.Assert.*
import org.junit.Test
import java.net.URLDecoder

class SubscriptionManagerTest {

    @Test
    fun `test founder WhatsApp number and base contact URL formatting`() {
        // Must be international-format number without spaces or symbols
        assertEquals("918217018706", FOUNDER_PHONE)
        assertEquals("https://wa.me/918217018706", FOUNDER_WA_BASE_URL)
        assertEquals("8217", FOUNDER_UNLOCK_PIN)
    }

    @Test
    fun `test 14-day free trial default initial duration`() {
        val now = 1700000000000L
        val trialEnd = now + (14L * 86400000L)
        val daysRemaining = SubscriptionManager.calculateDaysRemaining(trialEnd, now)
        assertEquals(14, daysRemaining)
    }

    @Test
    fun `test trial remaining days midway through trial`() {
        val now = 1700000000000L
        val sevenDaysAhead = now + (7L * 86400000L)
        val remaining = SubscriptionManager.calculateDaysRemaining(sevenDaysAhead, now)
        assertEquals(7, remaining)
    }

    @Test
    fun `test trial expired returns 0 days remaining and resolved status EXPIRED`() {
        val now = 1700000000000L
        val pastEnd = now - 1000L // 1 second ago

        val remaining = SubscriptionManager.calculateDaysRemaining(pastEnd, now)
        assertEquals(0, remaining)

        val expiredProfile = BusinessProfileEntity(
            trialStartDate = now - (15L * 86400000L),
            trialEndDate = pastEnd,
            isPilotGranted = false,
            subscriptionStatus = "TRIAL_ACTIVE"
        )

        assertFalse("Trial should be inactive when expired without pilot grant", SubscriptionManager.isAccessActive(expiredProfile, now))
        assertEquals(SubscriptionStatus.TRIAL_EXPIRED, SubscriptionManager.getResolvedStatus(expiredProfile, now))
    }

    @Test
    fun `test manual pilot grant allows active access even when trial date has passed`() {
        val now = 1700000000000L
        val pastEnd = now - (30L * 86400000L) // expired 30 days ago

        val pilotProfile = BusinessProfileEntity(
            trialStartDate = now - (44L * 86400000L),
            trialEndDate = pastEnd,
            isPilotGranted = true,
            selectedPlan = "PRO",
            pilotNotes = "Bangalore boutique early validation cohort",
            subscriptionStatus = "PILOT_ACTIVE"
        )

        assertTrue("Access must be active when pilot is explicitly granted by founder", SubscriptionManager.isAccessActive(pilotProfile, now))
        assertEquals(SubscriptionStatus.PILOT_ACTIVE, SubscriptionManager.getResolvedStatus(pilotProfile, now))
    }

    @Test
    fun `test configurable plan definitions for proposed launch prices`() {
        val freeTrial = SubscriptionCatalog.FREE_TRIAL
        val starter = SubscriptionCatalog.STARTER
        val pro = SubscriptionCatalog.PRO

        // 1. Free Trial
        assertEquals(0, freeTrial.priceInr)
        assertEquals("for 14 days", freeTrial.billingCycle)
        assertFalse(freeTrial.isProposedLaunchPrice)

        // 2. Starter: ₹299/mo proposed
        assertEquals(299, starter.priceInr)
        assertEquals("/ month", starter.billingCycle)
        assertTrue(starter.isProposedLaunchPrice)
        assertEquals("Proposed Launch Price", starter.badge)

        // 3. Pro: ₹599/mo proposed
        assertEquals(599, pro.priceInr)
        assertEquals("/ month", pro.billingCycle)
        assertTrue(pro.isProposedLaunchPrice)
        assertTrue(pro.isPopular)

        // Verify all plans are indexed
        assertEquals(3, SubscriptionCatalog.ALL_PLANS.size)
    }

    @Test
    fun `test Starter plan pre-filled WhatsApp message with business name`() {
        val starter = SubscriptionCatalog.STARTER
        val businessName = "Sri Lakshmi Silks"
        val message = SubscriptionManager.buildSubscriptionContactMessage(starter, businessName)

        val expected = """
            Hi! I would like to subscribe to SalesPilot AI.
            Plan: Starter
            Price: ₹299/month
            Business name: Sri Lakshmi Silks
            Please share the payment instructions.
        """.trimIndent()

        assertEquals(expected, message)
        assertTrue("Message must contain Starter plan", message.contains("Plan: Starter"))
        assertTrue("Message must contain ₹299/month", message.contains("Price: ₹299/month"))
        assertTrue("Message must contain business name", message.contains("Business name: Sri Lakshmi Silks"))
        assertTrue("Message must ask for payment instructions", message.contains("Please share the payment instructions."))
    }

    @Test
    fun `test Pro plan pre-filled WhatsApp message without business name`() {
        val pro = SubscriptionCatalog.PRO
        val message = SubscriptionManager.buildSubscriptionContactMessage(pro, null)

        val expected = """
            Hi! I would like to subscribe to SalesPilot AI.
            Plan: Pro
            Price: ₹599/month
            Please share the payment instructions.
        """.trimIndent()

        assertEquals(expected, message)
        assertTrue("Message must contain Pro plan", message.contains("Plan: Pro"))
        assertTrue("Message must contain ₹599/month", message.contains("Price: ₹599/month"))
        assertFalse("Message must omit business name when null or empty", message.contains("Business name:"))
    }

    @Test
    fun `test WhatsApp URL encoding and privacy safety`() {
        val starter = SubscriptionCatalog.STARTER
        val businessName = "Aadhya Boutique"
        val url = SubscriptionManager.buildSubscriptionWhatsAppUrl(starter, businessName)

        assertTrue("URL must use founder WhatsApp base URL", url.startsWith("https://wa.me/918217018706?text="))
        assertFalse("URL must not contain raw spaces", url.contains(" "))

        // Extract query parameter and decode
        val queryText = url.substringAfter("text=")
        val decoded = URLDecoder.decode(queryText, "UTF-8")

        val original = SubscriptionManager.buildSubscriptionContactMessage(starter, businessName)
        assertEquals("Decoded URL text must perfectly match original message", original, decoded)

        // Privacy check: verify no customer sensitive records are leaked
        assertFalse(decoded.contains("+919"))
        assertFalse(decoded.contains("customerPhone"))
        assertFalse(decoded.contains("orderHistory"))
    }

    @Test
    fun `test manual payment verification keeps access inactive after trial until verified`() {
        val now = 1700000000000L
        val pastEnd = now - 5000L // trial expired 5 seconds ago

        val pendingProfile = BusinessProfileEntity(
            trialStartDate = now - (15L * 86400000L),
            trialEndDate = pastEnd,
            selectedPlan = "STARTER",
            subscriptionStatus = "PENDING_PAYMENT",
            isPilotGranted = false
        )

        // Status must resolve to PENDING_PAYMENT
        assertEquals(SubscriptionStatus.PENDING_PAYMENT, SubscriptionManager.getResolvedStatus(pendingProfile, now))

        // Access must NOT be automatically activated simply because merchant tapped subscribe
        assertFalse(
            "Access must remain inactive when trial expired and payment is pending manual founder verification",
            SubscriptionManager.isAccessActive(pendingProfile, now)
        )

        // Once founder manually verifies and grants access:
        val verifiedProfile = pendingProfile.copy(
            isPilotGranted = true,
            subscriptionStatus = "PILOT_ACTIVE",
            pilotNotes = "Verified UPI ₹299 on 25 Oct"
        )

        assertEquals(SubscriptionStatus.PILOT_ACTIVE, SubscriptionManager.getResolvedStatus(verifiedProfile, now))
        assertTrue("Access must be active after founder manually verifies payment", SubscriptionManager.isAccessActive(verifiedProfile, now))
    }

    @Test
    fun `test founder unlock PIN authorization logic`() {
        val correctPin = "8217"
        val wrongPin = "1234"

        assertEquals(FOUNDER_UNLOCK_PIN, correctPin)
        assertNotEquals(FOUNDER_UNLOCK_PIN, wrongPin)
    }
}
