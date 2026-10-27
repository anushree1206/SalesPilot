package com.example

import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.ProductEntity
import com.example.domain.ai.AiSalesCopilot
import com.example.domain.ai.ReplyIntent
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AiSalesCopilotTest {

    private lateinit var profile: BusinessProfileEntity
    private lateinit var inStockProduct: ProductEntity
    private lateinit var outOfStockProduct: ProductEntity

    @Before
    fun setup() {
        profile = BusinessProfileEntity(
            businessName = "Meera's Handcrafted Silk & Jewellery",
            ownerName = "Meera Sundaram",
            contactPhone = "+91 98450 12345",
            upiId = "meerasilk@okhdfcbank",
            standardShippingDays = "3-5 business days across India",
            shippingChargePolicy = "Free shipping above ₹1,499. Flat ₹80 for orders below.",
            isCodEnabled = true,
            codPolicy = "COD available with ₹100 advance deposit via UPI.",
            maxAllowedDiscountPercent = 10,
            aiBackendMode = "LOCAL_RULE_ENGINE",
            proxyEndpointUrl = "",
            customApiKey = ""
        )

        inStockProduct = ProductEntity(
            id = "PROD-KJV-01",
            name = "Pure Kanjivaram Silk Saree - Crimson Maroon",
            sku = "KJV-01",
            category = "Sarees",
            price = 4499.00,
            salePrice = 3999.00,
            description = "Handwoven pure silk Kanjivaram saree with rich gold zari pallu.",
            fabricOrMaterial = "Pure Mulberry Silk with High-Grade Gold Zari",
            availableSizes = "Free Size",
            stockQuantity = 5,
            isAvailable = true
        )

        outOfStockProduct = ProductEntity(
            id = "PROD-BAN-04",
            name = "Banarasi Georgette Bandhani Saree - Royal Blue",
            sku = "BAN-04",
            category = "Sarees",
            price = 3299.00,
            salePrice = null,
            description = "Traditional Kutchi Bandhej.",
            fabricOrMaterial = "Pure Georgette",
            availableSizes = "Free Size",
            stockQuantity = 0,
            isAvailable = false
        )
    }

    @Test
    fun testCatalogGroundedPriceAndAvailability_English() {
        val result = AiSalesCopilot.draftResponse(
            customerName = "Ananya Sharma",
            inquiryText = "Is this saree available? What is the price?",
            product = inStockProduct,
            profile = profile,
            language = "en"
        )

        assertTrue(result.draftReply.contains("in stock"))
        assertTrue(result.draftReply.contains("₹3999"))
        assertTrue(result.draftReply.contains("KJV-01"))
        assertTrue(result.draftReply.contains("Pure Mulberry Silk"))
        assertEquals("en", result.targetLanguage)
        assertTrue(result.groundedFactsUsed.isNotEmpty())
    }

    @Test
    fun testOutOfStockSafeguard_DoesNotHallucinateFabricatedRestockDate() {
        val result = AiSalesCopilot.draftResponse(
            customerName = "Pooja Hegde",
            inquiryText = "Can I order 2 pieces of this Bandhani saree?",
            product = outOfStockProduct,
            profile = profile,
            language = "en"
        )

        assertTrue(
            "AI must explicitly state out of stock if stockQuantity is 0",
            result.draftReply.contains("sold out") || result.draftReply.contains("out of stock")
        )
        assertFalse(
            "AI must not claim the product is ready to ship when out of stock",
            result.draftReply.contains("ready to ship")
        )
        assertFalse(
            "AI must not hardcode fabricated weaver batch timelines",
            result.draftReply.contains("artisan weavers")
        )
    }

    @Test
    fun testUnconfiguredShipping_AsksCustomerForPincode() {
        // Business profile where shipping days policy has not been configured
        val unconfiguredProfile = profile.copy(standardShippingDays = "", shippingChargePolicy = "")

        val result = AiSalesCopilot.draftResponse(
            customerName = "Kavitha",
            inquiryText = "When will I get delivery for this saree?",
            product = inStockProduct,
            profile = unconfiguredProfile,
            language = "en",
            manualIntentOverride = ReplyIntent.SHIPPING_AND_COD
        )

        assertTrue(
            "AI must ask customer for pincode when shipping days are not configured",
            result.draftReply.contains("pincode") || result.draftReply.contains("location")
        )
        assertFalse(
            "AI must not invent blanket 3-5 days delivery when unconfigured",
            result.draftReply.contains("3-5 business days")
        )
    }

    @Test
    fun testDisabledCodPolicy_InformsPrepaidOnly() {
        // Business profile where COD is disabled
        val noCodProfile = profile.copy(isCodEnabled = false, codPolicy = "")

        val result = AiSalesCopilot.draftResponse(
            customerName = "Rahul",
            inquiryText = "Is COD available for this piece?",
            product = inStockProduct,
            profile = noCodProfile,
            language = "en",
            manualIntentOverride = ReplyIntent.SHIPPING_AND_COD
        )

        assertTrue(
            "AI must state COD is not available when disabled",
            result.draftReply.contains("COD is not available") || result.draftReply.contains("secure advance payment")
        )
        assertFalse(
            "AI must not claim COD is available with advance deposit when disabled",
            result.draftReply.contains("COD available with ₹100")
        )
    }

    @Test
    fun testFixedPricingPolicy_RejectsDiscountWhenCapIsZero() {
        // Business profile with strictly fixed pricing (maxAllowedDiscountPercent = 0)
        val fixedPriceProfile = profile.copy(maxAllowedDiscountPercent = 0)

        val result = AiSalesCopilot.draftResponse(
            customerName = "Simran",
            inquiryText = "Can I get some discount or best rate on this piece?",
            product = inStockProduct,
            profile = fixedPriceProfile,
            language = "en",
            manualIntentOverride = ReplyIntent.DISCOUNT_INQUIRY
        )

        assertTrue(
            "AI must explain prices are fixed when discount cap is 0",
            result.draftReply.contains("strictly fixed") || result.draftReply.contains("fixed with minimal margins")
        )
        assertTrue(
            "Must quote full price of ₹3999 without deducting discount",
            result.draftReply.contains("₹3999")
        )
    }

    @Test
    fun testMultilingualSupport_Kannada() {
        val result = AiSalesCopilot.draftResponse(
            customerName = "Divya Ramesh",
            inquiryText = "Crimson Maroon ಸೀರೆ ಲಭ್ಯವಿದೆಯಾ?",
            product = inStockProduct,
            profile = profile,
            language = "kn"
        )

        assertEquals("kn", result.targetLanguage)
        assertTrue("Must include authentic Kannada greeting", result.draftReply.contains("ನಮಸ್ಕಾರ"))
        assertTrue("Must quote verified price in Kannada draft", result.draftReply.contains("₹3999"))
        assertTrue("Must quote in-stock status in Kannada", result.draftReply.contains("ಸ್ಟಾಕ್‌ನಲ್ಲಿದೆ"))
    }

    @Test
    fun testMultilingualSupport_Hindi() {
        val result = AiSalesCopilot.draftResponse(
            customerName = "Sneha Verma",
            inquiryText = "Kya ye saree available hai aur COD ho jayega?",
            product = inStockProduct,
            profile = profile,
            language = "hi"
        )

        assertEquals("hi", result.targetLanguage)
        assertTrue("Must include respectful Hindi greeting", result.draftReply.contains("नमस्ते"))
        assertTrue("Must quote accurate price in Hindi", result.draftReply.contains("₹3999"))
    }

    @Test
    fun testDiscountNegotiation_StrictlyEnforcesMaxCap() {
        val result = AiSalesCopilot.draftResponse(
            customerName = "Kavitha",
            inquiryText = "Can you give 30% discount on this saree?",
            product = inStockProduct,
            profile = profile,
            language = "en",
            manualIntentOverride = ReplyIntent.DISCOUNT_INQUIRY
        )

        val maxDiscountVal = (3999.0 * (profile.maxAllowedDiscountPercent / 100.0)).toInt()
        val discountedPrice = (3999.0 - maxDiscountVal).toInt()
        assertTrue("AI must enforce 10% maximum discount cap", result.draftReply.contains("₹$discountedPrice"))
        assertFalse("AI must never offer 30% discount", result.draftReply.contains("₹2799"))
    }

    @Test
    fun testLanguageDetection_KannadaScript() {
        val text = "ಈ ಸೀರೆ ಕೆಂಪು ಬಣ್ಣದಲ್ಲಿ ಇದೆಯಾ?"
        val detected = AiSalesCopilot.detectInquiryLanguage(text)
        assertEquals("kn", detected)
    }

    @Test
    fun testLanguageDetection_RomanizedKannada() {
        val text = "Red colour alli ee saree available ideya? Delivery yavaga agatte?"
        val detected = AiSalesCopilot.detectInquiryLanguage(text)
        assertEquals("kn", detected)
    }

    @Test
    fun testLanguageDetection_HindiScript() {
        val text = "क्या यह साड़ी लाल रंग में उपलब्ध है?"
        val detected = AiSalesCopilot.detectInquiryLanguage(text)
        assertEquals("hi", detected)
    }

    @Test
    fun testLanguageDetection_Hinglish() {
        val text = "Bhaiya ye red saree Delhi me kitne din me deliver hoga?"
        val detected = AiSalesCopilot.detectInquiryLanguage(text)
        assertEquals("hi", detected)
    }

    @Test
    fun testLanguageDetection_CodeMixedKannadaEnglish() {
        val text = "Hello madam, is this saree in stock? Bengaluru ge deliver madtira?"
        val detected = AiSalesCopilot.detectInquiryLanguage(text)
        assertEquals("kn", detected)
    }

    @Test
    fun testLanguageDetection_EnglishDefault() {
        val text = "Is this saree available in red color for immediate shipping?"
        val detected = AiSalesCopilot.detectInquiryLanguage(text)
        assertEquals("en", detected)
    }

    @Test
    fun testMultilingualStringsProvider_HasNonEmptyTranslations() {
        val en = com.example.ui.i18n.StringsProvider.getStrings("en")
        val hi = com.example.ui.i18n.StringsProvider.getStrings("hi")
        val kn = com.example.ui.i18n.StringsProvider.getStrings("kn")

        assertTrue(en.navDashboard.isNotBlank())
        assertTrue(hi.navDashboard.isNotBlank())
        assertTrue(kn.navDashboard.isNotBlank())

        assertTrue(en.dashboardTitle.isNotBlank())
        assertTrue(hi.dashboardTitle.isNotBlank())
        assertTrue(kn.dashboardTitle.isNotBlank())

        assertTrue(en.btnGenerateGroundedReply.isNotBlank())
        assertTrue(hi.btnGenerateGroundedReply.isNotBlank())
        assertTrue(kn.btnGenerateGroundedReply.isNotBlank())

        assertTrue(en.privacyDialogTitle.isNotBlank())
        assertTrue(hi.privacyDialogTitle.isNotBlank())
        assertTrue(kn.privacyDialogTitle.isNotBlank())
    }

    @Test
    fun testDraftResponseAsync_FallsBackGracefullyWhenKeyAbsent() = runBlocking {
        // Profile with local rule engine and no external proxy or API key
        val localProfile = profile.copy(aiBackendMode = "LOCAL_RULE_ENGINE", proxyEndpointUrl = "", customApiKey = "")
        val result = AiSalesCopilot.draftResponseAsync(
            customerName = "Pooja Hegde",
            inquiryText = "Chanderi suit size M ideya?",
            product = inStockProduct,
            profile = localProfile
        )
        assertNotNull(result)
        assertTrue(result.draftReply.isNotBlank())
        assertTrue(result.groundedFactsUsed.isNotEmpty())
        assertEquals("Grounded Business Rules Engine", result.aiProviderUsed)
    }
}
