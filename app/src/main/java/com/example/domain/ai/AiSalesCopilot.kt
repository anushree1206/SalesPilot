package com.example.domain.ai

import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.remote.GeminiClient

enum class ReplyIntent(val label: String) {
    PRICE_AND_AVAILABILITY("Price & Stock Check"),
    SHIPPING_AND_COD("Shipping & COD Terms"),
    SIZING_AND_MATERIAL("Sizing & Fabric Care"),
    GENTLE_FOLLOW_UP("Follow-Up Reminder"),
    DISCOUNT_INQUIRY("Discount Negotiation")
}

data class CopilotDraftResult(
    val draftReply: String,
    val detectedIntent: ReplyIntent,
    val targetLanguage: String, // "en", "hi", "kn"
    val groundedFactsUsed: List<String>,
    val requiresSellerReview: Boolean = true,
    val suggestedAction: String,
    val aiProviderUsed: String // e.g. "Gemini 3.8 Flash (Secure Proxy)" or "Grounded Engine (Local)"
)

object AiSalesCopilot {

    /**
     * Detects customer inquiry language, including Devanagari Hindi, Kannada script,
     * Romanized Kannada ("ideya", "beku", "alli", "madtira"), and Hinglish ("bhaiya", "milega", "kitna").
     */
    fun detectInquiryLanguage(text: String): String {
        // 1. Kannada script check (Unicode 0C80 - 0CFF)
        if (text.any { it.code in 0x0C80..0x0CFF }) return "kn"

        // 2. Devanagari Hindi script check (Unicode 0900 - 097F)
        if (text.any { it.code in 0x0900..0x097F }) return "hi"

        // Tokenize text into lowercase words for accurate boundary matching
        val words = text.lowercase().split(Regex("[^\\p{L}0-9]+")).filter { it.isNotBlank() }.toSet()

        // 3. Romanized Kannada keywords (distinctive marker words)
        val kannadaRomanKeywords = setOf(
            "namaskara", "namaskaram", "ideya", "beku", "yavaga", "eshtu",
            "kodi", "madtira", "kodtira", "chennagide", "illi", "alli", "seere",
            "hegide", "banni", "haudu", "illa", "tumba", "dhanyavada", "kannada"
        )
        if (words.any { kannadaRomanKeywords.contains(it) }) return "kn"

        // 4. Hinglish keywords (distinctive marker words)
        val hindiRomanKeywords = setOf(
            "bhaiya", "didi", "milega", "milenge", "hoga", "honge", "kitna", "kitne", "kitni",
            "chahiye", "kahan", "bhejo", "karein", "kijiye", "namaste", "batao", "bataiye",
            "paisa", "paise", "kaise", "kya", "kab", "kisko", "achha", "bahut"
        )
        if (words.any { hindiRomanKeywords.contains(it) }) return "hi"

        return "en"
    }

    /**
     * Core hybrid engine: Attempts secure AI proxy or Gemini 3.8 Flash with strict ground-truth injection,
     * seamlessly falling back to local deterministic grounded templates if offline or unconfigured.
     */
    suspend fun draftResponseAsync(
        customerName: String,
        inquiryText: String,
        product: ProductEntity?,
        profile: BusinessProfileEntity,
        languageOverride: String? = null,
        manualIntentOverride: ReplyIntent? = null
    ): CopilotDraftResult {
        val detectedLang = languageOverride ?: detectInquiryLanguage(inquiryText)
        val detectedIntent = manualIntentOverride ?: detectIntent(inquiryText)
        val factsUsed = mutableListOf<String>()

        val firstName = customerName.split(" ").firstOrNull()?.trim() ?: "there"
        val curr = profile.currencySymbol

        val price = product?.let { it.salePrice ?: it.price } ?: 0.0
        val isSale = product?.salePrice != null
        val inStock = (product?.stockQuantity ?: 0) > 0 && (product?.isAvailable ?: false)

        factsUsed.add("Shop: ${profile.businessName} (Owner: ${profile.ownerName})")
        if (product != null) {
            factsUsed.add("Catalog Item: ${product.name} [SKU: ${product.sku}]")
            factsUsed.add("Stock: ${product.stockQuantity} units (${if (inStock) "In Stock" else "Out of Stock"})")
            factsUsed.add("Price: $curr${product.price}${if (isSale) " (Sale Offer: $curr${product.salePrice})" else ""}")
            factsUsed.add("Material: ${product.fabricOrMaterial}")
            factsUsed.add("Sizes: ${product.availableSizes}")
        } else {
            factsUsed.add("No specific catalog item identified yet — require clarification from customer")
        }

        // Verified seller policies
        if (profile.standardShippingDays.isNotBlank()) {
            factsUsed.add("Shipping Timeline: ${profile.standardShippingDays}")
        } else {
            factsUsed.add("Shipping Timeline: Not globally configured — must ask customer for delivery pincode/city")
        }

        if (profile.shippingChargePolicy.isNotBlank()) {
            factsUsed.add("Shipping Charges: ${profile.shippingChargePolicy}")
        }

        if (profile.isCodEnabled && profile.codPolicy.isNotBlank()) {
            factsUsed.add("COD Policy: ${profile.codPolicy}")
        } else {
            factsUsed.add("COD Policy: COD not available. Prepaid digital payment only.")
        }

        if (profile.returnExchangePolicy.isNotBlank()) {
            factsUsed.add("Return Policy: ${profile.returnExchangePolicy}")
        }

        if (profile.maxAllowedDiscountPercent > 0) {
            factsUsed.add("Max Allowed Discount: Up to ${profile.maxAllowedDiscountPercent}%")
        } else {
            factsUsed.add("Max Allowed Discount: 0% (Strictly fixed prices — no discounts authorized)")
        }

        var aiText: String? = null
        var provider = "Grounded Engine (Local)"

        // 1. Try AI service (Secure Backend Proxy or Gemini 3.8 Flash)
        if (GeminiClient.isServiceConfigured(profile.customApiKey, profile.proxyEndpointUrl)) {
            val systemInstruction = """
                You are SalesPilot AI, a polite, professional sales assistant for an Indian boutique or small business.
                
                CRITICAL ZERO-HALLUCINATION RULES:
                1. Strictly quote ONLY the verified store facts provided. Never invent or hallucinate information.
                2. Price rule: Use only the verified price ($curr${price.toInt()}).
                3. Stock rule: Stock is ${product?.stockQuantity ?: 0}. If out of stock, explicitly say it is sold out. Never invent a restock date unless explicitly provided in facts. Offer a waitlist or similar in-stock items.
                4. Shipping rule: If shipping timeline is not provided in facts, ask the customer for their delivery pincode/city.
                5. COD rule: If COD is not available in facts, politely state that orders are prepaid via UPI/online only. Never invent advance token amounts.
                6. Discount rule: If max discount is 0%, politely explain that prices are fixed. Never offer more than ${profile.maxAllowedDiscountPercent}%.
                7. Clarification rule: If no product is selected, politely ask which design, SKU, or photo they would like details on.
                8. Language rule: Compose the reply strictly in $detectedLang ('kn'=Kannada, 'hi'=Hindi, 'en'=English).
                9. Format cleanly for WhatsApp: polite greeting, bold product titles, bullet points, and clear call-to-action.
            """.trimIndent()

            val userPrompt = """
                Customer First Name: $firstName
                Customer Inquiry: "$inquiryText"
                Detected Goal: ${detectedIntent.label}
                Target Language: $detectedLang
                
                Verified Store Facts:
                ${factsUsed.joinToString("\n") { "• $it" }}
                
                Generate a ready-to-send WhatsApp reply adhering strictly to these rules:
            """.trimIndent()

            val response = GeminiClient.generateGroundedReply(
                systemInstructionText = systemInstruction,
                promptText = userPrompt,
                proxyUrl = profile.proxyEndpointUrl.ifBlank { null },
                merchantApiKey = profile.customApiKey.ifBlank { null }
            )

            if (!response.isNullOrBlank()) {
                aiText = response.trim()
                provider = when {
                    profile.proxyEndpointUrl.isNotBlank() -> "Gemini 3.8 Flash (Secure Proxy)"
                    profile.customApiKey.isNotBlank() -> "Gemini 3.8 Flash (BYOK Key)"
                    else -> "Gemini 3.8 Flash"
                }
            }
        }

        // 2. Deterministic Grounded Fallback if AI service is offline or unconfigured
        if (aiText.isNullOrBlank()) {
            aiText = when (detectedLang) {
                "kn" -> buildKannadaDraft(firstName, detectedIntent, product, price, inStock, profile)
                "hi" -> buildHindiDraft(firstName, detectedIntent, product, price, inStock, profile)
                else -> buildEnglishDraft(firstName, detectedIntent, product, price, inStock, profile)
            }
            provider = "Grounded Business Rules Engine"
        }

        val suggestedAction = when {
            product == null -> "Ask customer for photo or SKU of the requested item."
            !inStock -> "Offer similar in-stock collection or note customer on waitlist."
            detectedIntent == ReplyIntent.PRICE_AND_AVAILABILITY -> "Send WhatsApp quote and ask for preferred delivery address."
            detectedIntent == ReplyIntent.SHIPPING_AND_COD -> {
                if (profile.isCodEnabled && profile.codPolicy.isNotBlank()) {
                    "Share UPI ID (${profile.upiId}) for COD advance confirmation."
                } else {
                    "Share UPI ID (${profile.upiId}) for secure prepaid order confirmation."
                }
            }
            detectedIntent == ReplyIntent.GENTLE_FOLLOW_UP -> "Hold product for 12 hours and schedule follow-up check."
            else -> "Review drafted reply, copy or send directly to WhatsApp."
        }

        return CopilotDraftResult(
            draftReply = aiText.trimIndent(),
            detectedIntent = detectedIntent,
            targetLanguage = detectedLang,
            groundedFactsUsed = factsUsed,
            requiresSellerReview = true,
            suggestedAction = suggestedAction,
            aiProviderUsed = provider
        )
    }

    // Synchronous signature for backward-compatibility & testing
    fun draftResponse(
        customerName: String,
        inquiryText: String,
        product: ProductEntity?,
        profile: BusinessProfileEntity,
        language: String,
        manualIntentOverride: ReplyIntent? = null
    ): CopilotDraftResult {
        val detectedIntent = manualIntentOverride ?: detectIntent(inquiryText)
        val factsUsed = mutableListOf<String>()

        val firstName = customerName.split(" ").firstOrNull()?.trim() ?: "there"
        val curr = profile.currencySymbol

        val price = product?.let { it.salePrice ?: it.price } ?: 0.0
        val isSale = product?.salePrice != null
        val inStock = (product?.stockQuantity ?: 0) > 0 && (product?.isAvailable ?: false)

        factsUsed.add("Shop: ${profile.businessName} (Owner: ${profile.ownerName})")
        if (product != null) {
            factsUsed.add("Catalog Item: ${product.name} [SKU: ${product.sku}]")
            factsUsed.add("Stock: ${product.stockQuantity} units (${if (inStock) "In Stock" else "Out of Stock"})")
            factsUsed.add("Price: $curr${product.price}${if (isSale) " (Sale: $curr${product.salePrice})" else ""}")
            factsUsed.add("Material: ${product.fabricOrMaterial}")
        }
        if (profile.standardShippingDays.isNotBlank()) factsUsed.add("Shipping: ${profile.standardShippingDays}")
        if (profile.isCodEnabled && profile.codPolicy.isNotBlank()) factsUsed.add("COD: ${profile.codPolicy}")
        if (profile.returnExchangePolicy.isNotBlank()) factsUsed.add("Return: ${profile.returnExchangePolicy}")

        val draftText = when (language) {
            "kn" -> buildKannadaDraft(firstName, detectedIntent, product, price, inStock, profile)
            "hi" -> buildHindiDraft(firstName, detectedIntent, product, price, inStock, profile)
            else -> buildEnglishDraft(firstName, detectedIntent, product, price, inStock, profile)
        }

        val suggestedAction = when {
            product == null -> "Ask customer for photo or SKU of the requested item."
            !inStock -> "Offer similar in-stock collection or note customer on waitlist."
            detectedIntent == ReplyIntent.PRICE_AND_AVAILABILITY -> "Send WhatsApp quote and ask for delivery address."
            detectedIntent == ReplyIntent.SHIPPING_AND_COD -> "Share payment details for order confirmation."
            else -> "Review drafted reply, copy or send directly to WhatsApp."
        }

        return CopilotDraftResult(
            draftReply = draftText.trimIndent(),
            detectedIntent = detectedIntent,
            targetLanguage = language,
            groundedFactsUsed = factsUsed,
            requiresSellerReview = true,
            suggestedAction = suggestedAction,
            aiProviderUsed = "Grounded Business Rules Engine"
        )
    }

    private fun detectIntent(text: String): ReplyIntent {
        val lower = text.lowercase()
        return when {
            lower.contains("cod") || lower.contains("deliver") || lower.contains("shipping") ||
                    lower.contains("kahan") || lower.contains("charges") || lower.contains("courier") ||
                    lower.contains("ಡೆಲಿವರಿ") || lower.contains("ತಲುಪಿಸ") || lower.contains("distpatch") -> {
                ReplyIntent.SHIPPING_AND_COD
            }
            lower.contains("size") || lower.contains("fabric") || lower.contains("material") ||
                    lower.contains("blouse") || lower.contains("pure") || lower.contains("ಅಳತೆ") ||
                    lower.contains("ಬಟ್ಟೆ") || lower.contains("kapda") || lower.contains("color") -> {
                ReplyIntent.SIZING_AND_MATERIAL
            }
            lower.contains("discount") || lower.contains("kam") || lower.contains("offer") ||
                    lower.contains("less") || lower.contains("best price") || lower.contains("ರಿಯಾಯಿತಿ") ||
                    lower.contains("chhoot") || lower.contains("bargain") -> {
                ReplyIntent.DISCOUNT_INQUIRY
            }
            lower.contains("hold") || lower.contains("mom") || lower.contains("later") ||
                    lower.contains("reminder") || lower.contains("ನಾಳೆ") || lower.contains("kal") -> {
                ReplyIntent.GENTLE_FOLLOW_UP
            }
            else -> ReplyIntent.PRICE_AND_AVAILABILITY
        }
    }

    private fun buildEnglishDraft(
        name: String,
        intent: ReplyIntent,
        product: ProductEntity?,
        price: Double,
        inStock: Boolean,
        profile: BusinessProfileEntity
    ): String {
        val curr = profile.currencySymbol

        if (product == null) {
            return """
                Hello $name! ✨
                Thank you for reaching out to ${profile.businessName}.

                Could you please share the photo or design name you are interested in? We will promptly check availability and share pricing and details!

                Warm regards,
                ${profile.ownerName}
            """
        }

        if (!inStock) {
            return """
                Hello $name! ✨
                Thank you for inquiring about *${product.name}* (SKU: ${product.sku}).

                This piece is currently *temporarily sold out*. We do not have a confirmed restocking date at this time.
                • Would you like us to notify you if it becomes available again?
                • Alternatively, we would be delighted to share similar in-stock designs from our collection!

                Best regards,
                ${profile.businessName}
            """
        }

        // Shipping policy formatting
        val shippingText = when {
            profile.standardShippingDays.isNotBlank() && profile.shippingChargePolicy.isNotBlank() ->
                "📦 *Shipping:* ${profile.standardShippingDays} (${profile.shippingChargePolicy})"
            profile.standardShippingDays.isNotBlank() ->
                "📦 *Shipping:* ${profile.standardShippingDays}"
            else ->
                "📦 *Shipping:* Timeline and charges depend on your location. Please share your delivery pincode so we can check serviceability."
        }

        return when (intent) {
            ReplyIntent.PRICE_AND_AVAILABILITY -> """
                Hello $name! ✨
                Thank you for contacting ${profile.businessName}!

                Yes, our *${product.name}* is *in stock* and ready to dispatch!

                📌 *Product Details:*
                • SKU: ${product.sku}
                • Price: $curr${price.toInt()} ${if (product.salePrice != null) "(Special Offer!)" else ""}
                • Fabric/Material: ${product.fabricOrMaterial}
                • Available Sizes: ${product.availableSizes}

                $shippingText

                Would you like us to reserve this piece for you? Please share your delivery address to proceed!
            """

            ReplyIntent.SHIPPING_AND_COD -> {
                val codText = when {
                    profile.isCodEnabled && profile.codPolicy.isNotBlank() ->
                        "💵 *COD Terms:* ${profile.codPolicy}"
                    else ->
                        "💵 *Payment Method:* 100% secure advance payment via UPI or net banking. COD is not available for this collection."
                }
                """
                Hello $name! ✨
                Regarding your delivery inquiry for *${product.name}*:

                $shippingText
                $codText

                Total amount: *$curr${price.toInt()}*
                UPI ID: *${profile.upiId}*

                Please share your full pincode and city so we can schedule the quickest dispatch for you!
                """
            }

            ReplyIntent.SIZING_AND_MATERIAL -> """
                Hello $name! ✨
                Here are the craftsmanship and sizing details for *${product.name}*:

                🧵 *Material:* ${product.fabricOrMaterial}
                📏 *Sizes:* ${product.availableSizes}
                ✨ *Care Guide:* ${product.careInstructions}
                💰 *Price:* $curr${price.toInt()}

                Every piece is inspected for quality prior to dispatch. Please let us know if you have any questions regarding fit or styling!
            """

            ReplyIntent.DISCOUNT_INQUIRY -> {
                if (profile.maxAllowedDiscountPercent > 0) {
                    val maxDiscountVal = (price * (profile.maxAllowedDiscountPercent / 100.0)).toInt()
                    val discountedPrice = (price - maxDiscountVal).toInt()
                    """
                    Hello $name! ✨
                    Thank you for your interest in *${product.name}*.

                    As our collections are handcrafted with genuine ${product.fabricOrMaterial}, our regular pricing is already set with minimal margins.

                    However, as a special welcome gesture for you:
                    • Original Price: $curr${price.toInt()}
                    • Special Offer: *$curr$discountedPrice*
                    
                    This offer is valid for the next 24 hours. May we prepare your order confirmation?
                    """
                } else {
                    """
                    Hello $name! ✨
                    Thank you for your interest in *${product.name}*.

                    As each piece is carefully handcrafted with genuine ${product.fabricOrMaterial}, our prices are strictly fixed with minimal margins to guarantee authentic quality.

                    Total: *$curr${price.toInt()}*
                    We would be delighted to pack this beautiful piece for you. Would you like to confirm the order?
                    """
                }
            }

            ReplyIntent.GENTLE_FOLLOW_UP -> """
                Hi $name! 🌸
                Gentle check-in from ${profile.businessName}.

                We currently have *${product.name}* on temporary hold for you. Since we have only ${product.stockQuantity} piece(s) remaining in stock, please let us know if you would like us to confirm your order before releasing it.

                Let us know if you need any assistance!
            """
        }
    }

    private fun buildHindiDraft(
        name: String,
        intent: ReplyIntent,
        product: ProductEntity?,
        price: Double,
        inStock: Boolean,
        profile: BusinessProfileEntity
    ): String {
        val curr = profile.currencySymbol

        if (product == null) {
            return """
                नमस्ते $name जी! 🙏
                ${profile.businessName} में आपका स्वागत है।

                कृपया हमें बताएं कि आप किस डिज़ाइन या फोटो के बारे में जानकारी चाहते हैं? हम तुरंत फोटो और विवरण साझा करेंगे।

                धन्यवाद,
                ${profile.ownerName}
            """
        }

        if (!inStock) {
            return """
                नमस्ते $name जी! 🙏
                *${product.name}* की जानकारी के लिए धन्यवाद।

                यह पीस फिलहाल *स्टॉक में समाप्त* हो गया है। फिलहाल इसकी रीस्टॉक तारीख निश्चित नहीं है।
                • क्या आप चाहते हैं कि जब यह उपलब्ध हो तब हम आपको सूचित करें?
                • या हम इससे मिलते-जुलते अन्य खूबसूरत डिज़ाइन्स दिखाएं?

                सादर,
                ${profile.businessName}
            """
        }

        val shippingText = when {
            profile.standardShippingDays.isNotBlank() && profile.shippingChargePolicy.isNotBlank() ->
                "📦 *डिलीवरी:* ${profile.standardShippingDays} (${profile.shippingChargePolicy})"
            profile.standardShippingDays.isNotBlank() ->
                "📦 *डिलीवरी समय:* ${profile.standardShippingDays}"
            else ->
                "📦 *डिलीवरी:* डिलीवरी समय आपके पिनकोड पर निर्भर करता है। कृपया अपना पिनकोड बताएं ताकि हम सही समय बता सकें।"
        }

        return when (intent) {
            ReplyIntent.PRICE_AND_AVAILABILITY -> """
                नमस्ते $name जी! 🙏
                ${profile.businessName} से संपर्क करने के लिए धन्यवाद।

                जी हाँ, *${product.name}* बिल्कुल *स्टॉक में उपलब्ध* है!

                📌 *प्रोडक्ट विवरण:*
                • कीमत: $curr${price.toInt()} ${if (product.salePrice != null) "(विशेष ऑफर)" else ""}
                • कपड़ा/मटीरियल: ${product.fabricOrMaterial}
                • साइज़: ${product.availableSizes}

                $shippingText

                क्या हम यह पीस आपके लिए बुक करें? कृपया अपना पता साझा करें!
            """

            ReplyIntent.SHIPPING_AND_COD -> {
                val codText = when {
                    profile.isCodEnabled && profile.codPolicy.isNotBlank() ->
                        "💵 *COD नीति:* ${profile.codPolicy}"
                    else ->
                        "💵 *पेमेंट माध्यम:* UPI या ऑनलाइन ट्रांसफर द्वारा सुरक्षित प्रीपेड भुगतान। COD उपलब्ध नहीं है।"
                }
                """
                नमस्ते $name जी! 🙏
                *${product.name}* की डिलीवरी और पेमेंट विवरण:

                $shippingText
                $codText

                कुल देय राशि: *$curr${price.toInt()}*
                UPI ID: *${profile.upiId}*

                कृपया अपना पिनकोड भेजें ताकि हम सबसे तेज़ कूरियर बुक कर सकें!
                """
            }

            ReplyIntent.SIZING_AND_MATERIAL -> """
                नमस्ते $name जी! 🙏
                *${product.name}* की बनावट और साइज़ की जानकारी:

                🧵 *कपड़ा/मटीरियल:* ${product.fabricOrMaterial}
                📏 *उपलब्ध साइज़:* ${product.availableSizes}
                ✨ *रखरखाव:* ${product.careInstructions}
                💰 *कीमत:* $curr${price.toInt()}

                हर पीस की क्वालिटी जांच के बाद ही डिस्पैच किया जाता है। क्या आपको कोई अन्य जानकारी चाहिए?
            """

            ReplyIntent.DISCOUNT_INQUIRY -> {
                if (profile.maxAllowedDiscountPercent > 0) {
                    val maxDiscountVal = (price * (profile.maxAllowedDiscountPercent / 100.0)).toInt()
                    val discountedPrice = (price - maxDiscountVal).toInt()
                    """
                    नमस्ते $name जी! 🙏
                    *${product.name}* में रुचि के लिए धन्यवाद।

                    हमारा प्रत्येक पीस प्रीमियम क्वालिटी का है, फिर भी आपके लिए विशेष रियायत:
                    • सामान्य कीमत: $curr${price.toInt()}
                    • बेस्ट स्पेशल ऑफर: *$curr$discountedPrice*
                    
                    यह खास ऑफर अगले 24 घंटे के लिए मान्य है। क्या हम ऑर्डर कन्फर्म करें?
                    """
                } else {
                    """
                    नमस्ते $name जी! 🙏
                    *${product.name}* में रुचि के लिए धन्यवाद।

                    हमारा प्रत्येक पीस उच्च गुणवत्ता और न्यूनतम मार्जिन पर तैयार किया गया है, इसलिए कीमतें पूरी तरह निश्चित हैं।

                    कीमत: *$curr${price.toInt()}*
                    हम आपका ऑर्डर बुक करने में प्रसन्न होंगे। कृपया बताएं कि क्या हम इसे आपके लिए पैक करें?
                    """
                }
            }

            ReplyIntent.GENTLE_FOLLOW_UP -> """
                नमस्ते $name जी! 🌸
                ${profile.businessName} से एक विनम्र स्मरण।

                हमने आपके लिए *${product.name}* रिज़र्व रखा हुआ है। स्टॉक में सीमित पीस होने के कारण, कृपया बताएं कि क्या हम इसे आपके नाम पर कन्फर्म करें?

                हम आपकी सहायता के लिए उपलब्ध हैं!
            """
        }
    }

    private fun buildKannadaDraft(
        name: String,
        intent: ReplyIntent,
        product: ProductEntity?,
        price: Double,
        inStock: Boolean,
        profile: BusinessProfileEntity
    ): String {
        val curr = profile.currencySymbol

        if (product == null) {
            return """
                ನಮಸ್ಕಾರ $name ಅವರೇ! 🙏
                ${profile.businessName} ಗೆ ಸುಸ್ವಾಗತ.

                ತಾವು ಯಾವ ವಿನ್ಯಾಸ ಅಥವಾ ಪ್ರಾಡಕ್ಟ್ ಬಗ್ಗೆ ವಿಚಾರಿಸುತ್ತಿದ್ದೀರಿ ಎಂದು ತಿಳಿಸಿದರೆ ತಕ್ಷಣ ಮಾಹಿತಿ ನೀಡುತ್ತೇವೆ!

                ಧನ್ಯವಾದಗಳು,
                ${profile.ownerName}
            """
        }

        if (!inStock) {
            return """
                ನಮಸ್ಕಾರ $name ಅವರೇ! 🙏
                *${product.name}* ಬಗ್ಗೆ ವಿಚಾರಿಸಿದ್ದಕ್ಕಾಗಿ ಧನ್ಯವಾದಗಳು.

                ಈ ವಿನ್ಯಾಸ ಸದ್ಯಕ್ಕೆ *ಸ್ಟಾಕ್ ಮುಗಿದಿದೆ*. ಮರು-ಸ್ಟಾಕ್ ಆಗುವ ನಿಖರ ದಿನಾಂಕ ಸದ್ಯಕ್ಕೆ ಲಭ್ಯವಿಲ್ಲ.
                • ಸ್ಟಾಕ್ ಬಂದಾಗ ನಿಮಗೆ ಮಾಹಿತಿ ತಿಳಿಸಬೇಕೇ?
                • ಅಥವಾ ಇದೇ ತರಹದ ಬೇರೆ ಸುಂದರ ವಿನ್ಯಾಸಗಳನ್ನು ನೋಡಲು ಇಷ್ಟಪಡುವಿರಾ?

                ಇಂತಿ,
                ${profile.businessName}
            """
        }

        val shippingText = when {
            profile.standardShippingDays.isNotBlank() && profile.shippingChargePolicy.isNotBlank() ->
                "📦 *ಶಿಪ್ಪಿಂಗ್:* ${profile.standardShippingDays} (${profile.shippingChargePolicy})"
            profile.standardShippingDays.isNotBlank() ->
                "📦 *ಶಿಪ್ಪಿಂಗ್:* ${profile.standardShippingDays}"
            else ->
                "📦 *ಶಿಪ್ಪಿಂಗ್:* ಡೆಲಿವರಿ ಸಮಯವು ನಿಮ್ಮ ವಿಳಾಸದ ಪಿನ್‌ಕೋಡ್ ಮೇಲೆ ಅವಲಂಬಿತವಾಗಿರುತ್ತದೆ. ದಯವಿಟ್ಟು ನಿಮ್ಮ ಪಿನ್‌ಕೋಡ್ ತಿಳಿಸಿ."
        }

        return when (intent) {
            ReplyIntent.PRICE_AND_AVAILABILITY -> """
                ನಮಸ್ಕಾರ $name ಅವರೇ! 🙏
                ${profile.businessName} ಸಂಪರ್ಕಿಸಿದ್ದಕ್ಕಾಗಿ ಧನ್ಯವಾದಗಳು.

                ಖಂಡಿತ, ನೀವು ವಿಚಾರಿಸಿದ *${product.name}* ಪ್ರಸ್ತುತ *ಸ್ಟಾಕ್‌ನಲ್ಲಿದೆ*!

                📌 *ವಿವರಗಳು:*
                • ಬೆಲೆ: $curr${price.toInt()} ${if (product.salePrice != null) "(ವಿಶೇಷ ಆಫರ್!)" else ""}
                • ಬಟ್ಟೆ / ಮಟೀರಿಯಲ್: ${product.fabricOrMaterial}
                • ಅಳತೆಗಳು: ${product.availableSizes}

                $shippingText

                ನಿಮಗೆ ಈ ಪೀಸ್ ಪ್ಯಾಕ್ ಮಾಡಬೇಕೇ? ದಯವಿಟ್ಟು ನಿಮ್ಮ ಡೆಲಿವರಿ ವಿಳಾಸ ಕಳುಹಿಸಿ!
            """

            ReplyIntent.SHIPPING_AND_COD -> {
                val codText = when {
                    profile.isCodEnabled && profile.codPolicy.isNotBlank() ->
                        "💵 *COD ನಿಯಮ:* ${profile.codPolicy}"
                    else ->
                        "💵 *ಪಾವತಿ ವಿಧಾನ:* UPI ಮೂಲಕ ಸುರಕ್ಷಿತ ಮುಂಗಡ ಪಾವತಿ. ಈ ಕಲೆಕ್ಷನ್‌ಗೆ COD ಸೌಲಭ್ಯವಿರುವುದಿಲ್ಲ."
                }
                """
                ನಮಸ್ಕಾರ $name ಅವರೇ! 🙏
                *${product.name}* ಡೆಲಿವರಿ ಮತ್ತು ಪಾವತಿ ವಿವರಗಳು:

                $shippingText
                $codText

                ಒಟ್ಟು ಪಾವತಿಸಬೇಕಾದ ಮೊತ್ತ: *$curr${price.toInt()}*
                UPI ID: *${profile.upiId}*

                ದಯವಿಟ್ಟು ನಿಮ್ಮ ಪಿನ್‌ಕೋಡ್ ತಿಳಿಸಿದರೆ ಶೀಘ್ರ ಡೆಲಿವರಿ ನಿಗದಿಪಡಿಸುತ್ತೇವೆ!
                """
            }

            ReplyIntent.SIZING_AND_MATERIAL -> """
                ನಮಸ್ಕಾರ $name ಅವರೇ! 🙏
                *${product.name}* ಕ್ವಾಲಿಟಿ ಮತ್ತು ಸೈಜ್ ವಿವರಗಳು:

                🧵 *ಮಟೀರಿಯಲ್:* ${product.fabricOrMaterial}
                📏 *ಅಳತೆಗಳು:* ${product.availableSizes}
                ✨ *ಆರೈಕೆ:* ${product.careInstructions}
                💰 *ಬೆಲೆ:* $curr${price.toInt()}

                ಪ್ರತಿಯೊಂದು ಪೀಸ್ ಅನ್ನು ಸ್ವತಃ ಪರಿಶೀಲಿಸಿ ಕಳುಹಿಸುತ್ತೇವೆ. ಹೆಚ್ಚಿನ ಮಾಹಿತಿ ಬೇಕಿದ್ದಲ್ಲಿ ತಿಳಿಸಿ!
            """

            ReplyIntent.DISCOUNT_INQUIRY -> {
                if (profile.maxAllowedDiscountPercent > 0) {
                    val maxDiscountVal = (price * (profile.maxAllowedDiscountPercent / 100.0)).toInt()
                    val discountedPrice = (price - maxDiscountVal).toInt()
                    """
                    ನಮಸ್ಕಾರ $name ಅವರೇ! 🙏
                    *${product.name}* ನಲ್ಲಿ ಆಸಕ್ತಿ ತೋರಿದ್ದಕ್ಕಾಗಿ ಧನ್ಯವಾದಗಳು.

                    ನಮ್ಮ ಪ್ರಾಡಕ್ಟ್‌ಗಳು ಅತ್ಯುನ್ನತ ಗುಣಮಟ್ಟದ್ದಾಗಿದ್ದು, ನಿಮಗಾಗಿ ವಿಶೇಷ ಆಫರ್ ಇಲ್ಲಿದೆ:
                    • ಸಾಮಾನ್ಯ ಬೆಲೆ: $curr${price.toInt()}
                    • ಬೆಸ್ಟ್ ಆಫರ್ ದರ: *$curr$discountedPrice*
                    
                    ಈ ಆಫರ್ 24 ಗಂಟೆಗಳವರೆಗೆ ಮಾತ್ರ ಲಭ್ಯವಿರುತ್ತದೆ. ಆರ್ಡರ್ ಕನ್ಫರ್ಮ್ ಮಾಡಬೇಕೇ?
                    """
                } else {
                    """
                    ನಮಸ್ಕಾರ $name ಅವರೇ! 🙏
                    *${product.name}* ನಲ್ಲಿ ಆಸಕ್ತಿ ತೋರಿದ್ದಕ್ಕಾಗಿ ಧನ್ಯವಾದಗಳು.

                    ನಮ್ಮ ಪ್ರಾಡಕ್ಟ್‌ಗಳು ಉತ್ತಮ ಗುಣಮಟ್ಟದ್ದಾಗಿದ್ದು, ನಿಶ್ಚಿತ ದರವನ್ನು ಹೊಂದಿರುತ್ತವೆ.

                    ಬೆಲೆ: *$curr${price.toInt()}*
                    ನಿಮಗಾಗಿ ಈ ಪೀಸ್ ಪ್ಯಾಕ್ ಮಾಡಲು ಸಂತೋಷಪಡುತ್ತೇವೆ. ಆರ್ಡರ್ ಕನ್ಫರ್ಮ್ ಮಾಡಬೇಕೇ?
                    """
                }
            }

            ReplyIntent.GENTLE_FOLLOW_UP -> """
                ನಮಸ್ಕಾರ $name ಅವರೇ! 🌸
                ${profile.businessName} ಇಂದ ವಿನಮ್ರ ನೆನಪಿಸುವಿಕೆ.

                ನಾವು *${product.name}* ಅನ್ನು ನಿಮಗಾಗಿ ಕಾಯ್ದಿರಿಸಿದ್ದೇವೆ. ಸ್ಟಾಕ್‌ನಲ್ಲಿ ಸೀಮಿತ ಪೀಸ್‌ಗಳು ಮಾತ್ರ ಇರುವುದರಿಂದ, ಆರ್ಡರ್ ಕನ್ಫರ್ಮ್ ಮಾಡಲು ಇಷ್ಟಪಡುವಿರಾ?

                ನಿಮ್ಮ ಪ್ರತಿಕ್ರಿಯೆಗಾಗಿ ಕಾಯುತ್ತಿದ್ದೇವೆ!
            """
        }
    }
}
