package com.example.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    // Navigation
    val navDashboard: String
    val navPipeline: String
    val navCatalog: String
    val navCopilot: String
    val navOrders: String
    val navFollowUps: String
    val navSettings: String

    // Dashboard
    val dashboardTitle: String
    val confirmedSales: String
    val pipelineValue: String
    val conversionRate: String
    val conversionRateExplanation: String
    val followUpsDue: String
    val overdueWarningTitle: String
    val overdueWarningDesc: String
    val quickActions: String
    val btnNewEnquiry: String
    val btnAddProduct: String
    val btnTestSampleDm: String
    val activeEnquiries: String
    val viewFullPipeline: String
    val emptyEnquiriesTitle: String
    val emptyEnquiriesSubtitle: String
    val ordersFulfilled: String
    val allOnTrack: String

    // Pipeline
    val searchLeadsPlaceholder: String
    val allFilter: String
    val noLeadsInStage: String
    val noLeadsSubtitle: String
    val btnAiCopilot: String
    val btnChat: String
    val btnOrder: String
    val stageNewEnquiry: String
    val stageContacted: String
    val stageInterested: String
    val stageFollowUpDue: String
    val stageOrderPlaced: String
    val stageClosed: String

    // Catalog
    val searchCatalogPlaceholder: String
    val noProductsTitle: String
    val noProductsSubtitle: String
    val inStock: String
    val outOfStock: String
    val stockQtyLabel: String
    val btnWhatsAppCard: String
    val btnDelete: String
    val deleteProductDialogTitle: String
    val deleteProductDialogDesc: String
    val btnConfirmDelete: String
    val btnCancel: String

    // AI Copilot
    val copilotGuardrailBanner: String
    val stepCustomerContext: String
    val labelCustomerName: String
    val labelReferencedProduct: String
    val selectProductPrompt: String
    val labelInquiryMessage: String
    val inquiryPlaceholder: String
    val stepGoalAndLanguage: String
    val labelTargetLanguage: String
    val labelReplyFocus: String
    val btnGenerateGroundedReply: String
    val btnGenerating: String
    val badgeAiDrafted: String
    val verifiedGroundingTitle: String
    val nextActionLabel: String
    val btnSendOnWhatsApp: String
    val btnCopy: String
    val btnConvertLeadToOrder: String

    // Orders
    val ordersHeaderTotal: String
    val ordersHeaderRevenue: String
    val noOrdersTitle: String
    val noOrdersSubtitle: String
    val paymentPaid: String
    val paymentPending: String
    val btnWhatsAppBill: String
    val statusConfirmed: String
    val statusProcessing: String
    val statusShipped: String
    val statusDelivered: String
    val statusCancelled: String

    // Follow-Ups
    val followUpsOverdueAlert: String
    val filterOverdue: String
    val filterToday: String
    val filterUpcoming: String
    val allFollowUpsDoneTitle: String
    val allFollowUpsDoneSubtitle: String
    val btnWhatsAppReminder: String
    val btnDone: String

    // Settings
    val settingsBusinessIdentity: String
    val labelShopName: String
    val labelOwnerName: String
    val labelWhatsAppNumber: String
    val labelUpiId: String
    val settingsPoliciesTitle: String
    val settingsPoliciesSubtitle: String
    val labelShippingDays: String
    val labelShippingCharges: String
    val labelCodEnabled: String
    val labelCodTerms: String
    val labelReturnPolicy: String
    val labelMaxDiscount: String
    val btnSaveConfiguration: String
    val settingsLanguageTitle: String

    // AI Engine & Security Settings
    val aiEngineSettingsTitle: String
    val aiEngineSubtitle: String
    val aiModeLocal: String
    val aiModeProxy: String
    val aiModeByok: String
    val labelProxyUrl: String
    val labelCustomApiKey: String

    // Privacy & Data Safety
    val privacyTitle: String
    val privacyViewDetails: String
    val privacyDialogTitle: String
    val privacyDialogContent: String
    val btnClose: String

    // Subscription & Free Trial
    val subscriptionTitle: String
    val subscriptionStatusTrial: String
    val subscriptionStatusExpired: String
    val subscriptionStatusPilot: String
    val subscriptionDaysRemaining: String
    val subscriptionExpiredBanner: String
    val btnViewPlans: String
    val btnUpgradeNow: String
    val plansModalTitle: String
    val plansModalSubtitle: String
    val planProposedNotice: String
    val merchantPaymentSeparationNotice: String
    val planFreeTrialName: String
    val planFreeTrialBadge: String
    val planStarterName: String
    val planStarterPrice: String
    val planProName: String
    val planProPrice: String
    val planProBadge: String
    val btnSelectPlan: String
    val btnRequestActivationWhatsApp: String
    val pilotAdminTitle: String
    val pilotAdminSubtitle: String
    val btnExtendTrial7Days: String
    val btnExtendTrial14Days: String
    val btnGrantPilotPro: String
    val btnSimulateExpiry: String
    val btnResetTrial: String
    val pilotNotesLabel: String
    val btnSubscribeViaWhatsApp: String
    val paymentNoteVerification: String
    val subscriptionStatusPending: String
    val founderUnlockTitle: String
    val founderUnlockPrompt: String
    val founderControlsLocked: String
    val btnUnlockFounder: String
    val btnLockFounder: String
    val founderPinIncorrect: String

    // Common
    val estValueLabel: String
    val priceLabel: String
}

object EnglishStrings : AppStrings {
    override val navDashboard = "Dashboard"
    override val navPipeline = "Pipeline"
    override val navCatalog = "Catalog"
    override val navCopilot = "AI Copilot"
    override val navOrders = "Orders"
    override val navFollowUps = "Follow-Ups"
    override val navSettings = "Settings"

    override val dashboardTitle = "Sales Performance Snapshot"
    override val confirmedSales = "Confirmed Sales"
    override val pipelineValue = "Pipeline Value"
    override val conversionRate = "Conversion Rate"
    override val conversionRateExplanation = "Formula: (Confirmed Orders ÷ Total Leads) × 100"
    override val followUpsDue = "Follow-Ups Due"
    override val overdueWarningTitle = "Customer Follow-up(s) Overdue"
    override val overdueWarningDesc = "Buyers awaiting product reservation or size hold confirmation."
    override val quickActions = "Quick Actions"
    override val btnNewEnquiry = "New Enquiry"
    override val btnAddProduct = "Add Product"
    override val btnTestSampleDm = "⚡ Test Sample DM"
    override val activeEnquiries = "Active Customer Enquiries"
    override val viewFullPipeline = "View Full Pipeline →"
    override val emptyEnquiriesTitle = "No customer enquiries logged yet"
    override val emptyEnquiriesSubtitle = "Tap '⚡ Test Sample DM' to test an instant WhatsApp buyer query!"
    override val ordersFulfilled = "Orders Fulfilled"
    override val allOnTrack = "All on track"

    override val searchLeadsPlaceholder = "Search by buyer name, phone, or item..."
    override val allFilter = "All"
    override val noLeadsInStage = "No leads in this stage"
    override val noLeadsSubtitle = "Tap + to add a lead or choose another stage"
    override val btnAiCopilot = "AI Copilot"
    override val btnChat = "Chat"
    override val btnOrder = "Order"
    override val stageNewEnquiry = "New Enquiry"
    override val stageContacted = "Contacted"
    override val stageInterested = "Interested"
    override val stageFollowUpDue = "Follow-Up Due"
    override val stageOrderPlaced = "Order Placed"
    override val stageClosed = "Closed"

    override val searchCatalogPlaceholder = "Search catalog by name, fabric, or SKU..."
    override val noProductsTitle = "No products found"
    override val noProductsSubtitle = "Tap + to add a new boutique item"
    override val inStock = "in stock"
    override val outOfStock = "Out of Stock"
    override val stockQtyLabel = "Stock: "
    override val btnWhatsAppCard = "WhatsApp Card"
    override val btnDelete = "Delete"
    override val deleteProductDialogTitle = "Delete Catalog Item?"
    override val deleteProductDialogDesc = "Are you sure you want to remove this product from your catalog? This action cannot be undone."
    override val btnConfirmDelete = "Delete Item"
    override val btnCancel = "Cancel"

    override val copilotGuardrailBanner = "100% Catalog-Grounded • Zero Price & Stock Hallucinations"
    override val stepCustomerContext = "1. Customer Context"
    override val labelCustomerName = "Customer Name / WhatsApp Contact"
    override val labelReferencedProduct = "Referenced Catalog Item:"
    override val selectProductPrompt = "Select product from catalog..."
    override val labelInquiryMessage = "Customer Enquiry Message (WhatsApp / DM)"
    override val inquiryPlaceholder = "Paste buyer message here, e.g. 'Can I get COD to Bangalore for Friday?'"
    override val stepGoalAndLanguage = "2. Response Goal & Language"
    override val labelTargetLanguage = "Target Reply Language:"
    override val labelReplyFocus = "Reply Focus (Auto-detected if unselected):"
    override val btnGenerateGroundedReply = "Generate Grounded Reply"
    override val btnGenerating = "Grounding with Catalog Policies..."
    override val badgeAiDrafted = "🤖 AI-DRAFTED REPLY"
    override val verifiedGroundingTitle = "✓ Verified Business Grounding:"
    override val nextActionLabel = "💡 Next Action:"
    override val btnSendOnWhatsApp = "Send on WhatsApp"
    override val btnCopy = "Copy"
    override val btnConvertLeadToOrder = "Convert This Lead to Confirmed Order →"

    override val ordersHeaderTotal = "Total Recorded Orders"
    override val ordersHeaderRevenue = "Gross Fulfilled Value"
    override val noOrdersTitle = "No orders in this status"
    override val noOrdersSubtitle = "Convert active leads in the Pipeline tab to log orders"
    override val paymentPaid = "✓ Paid"
    override val paymentPending = "⏳ Payment Pending"
    override val btnWhatsAppBill = "WhatsApp Bill"
    override val statusConfirmed = "Confirmed"
    override val statusProcessing = "Processing"
    override val statusShipped = "Shipped"
    override val statusDelivered = "Delivered"
    override val statusCancelled = "Cancelled"

    override val followUpsOverdueAlert = "inquiry follow-up(s) need immediate attention!"
    override val filterOverdue = "Overdue"
    override val filterToday = "Due Today"
    override val filterUpcoming = "Upcoming"
    override val allFollowUpsDoneTitle = "All customer follow-ups completed!"
    override val allFollowUpsDoneSubtitle = "Great job! No abandoned customer enquiries right now."
    override val btnWhatsAppReminder = "WhatsApp Reminder"
    override val btnDone = "Done"

    override val settingsBusinessIdentity = "Business Identity"
    override val labelShopName = "Shop / Boutique Name"
    override val labelOwnerName = "Owner / Contact Name"
    override val labelWhatsAppNumber = "WhatsApp Number"
    override val labelUpiId = "Shop UPI ID (GPay)"
    override val settingsPoliciesTitle = "Verified Policies (Used by AI Copilot)"
    override val settingsPoliciesSubtitle = "The AI strictly quotes these terms and will NEVER fabricate unauthorized terms."
    override val labelShippingDays = "Standard Delivery Timeline"
    override val labelShippingCharges = "Shipping Charges & Free Delivery Threshold"
    override val labelCodEnabled = "Enable Cash on Delivery (COD)"
    override val labelCodTerms = "Cash on Delivery (COD) Terms"
    override val labelReturnPolicy = "Return & Exchange Policy"
    override val labelMaxDiscount = "Max Permitted Negotiation Discount (%) - (0 = Fixed Prices)"
    override val btnSaveConfiguration = "Save Shop Configuration"
    override val settingsLanguageTitle = "Preferred Language"

    override val aiEngineSettingsTitle = "AI Architecture & Security"
    override val aiEngineSubtitle = "Configure how AI replies are generated. Local engine runs 100% on-device with zero costs."
    override val aiModeLocal = "Local Rule Engine (Offline & Private)"
    override val aiModeProxy = "Secure Backend Proxy (Recommended for Play Store)"
    override val aiModeByok = "Private Merchant API Key (BYOK)"
    override val labelProxyUrl = "Backend Proxy Endpoint URL (e.g. Cloud Run / Cloud Functions)"
    override val labelCustomApiKey = "Private Gemini API Key (Stored on this device only)"

    override val privacyTitle = "Privacy & Data Safety"
    override val privacyViewDetails = "View Data Safety & Privacy Disclosures"
    override val privacyDialogTitle = "Data Safety & Privacy Information"
    override val privacyDialogContent = "• On-Device Storage: All customer details, catalog items, and orders are stored locally in your device's private SQLite database.\n\n• Zero Tracking: No third-party ad networks, telemetry trackers, or marketing SDKs are embedded.\n\n• AI Generation: When using AI drafting, only the customer's first name and query text are processed to compose replies. Phone numbers, addresses, and financial info are never shared with AI models.\n\n• Data Ownership: You own 100% of your business data. Uninstalling the app completely removes all local database records."
    override val btnClose = "Close"

    // Subscription & Free Trial
    override val subscriptionTitle = "Subscription & Free Trial"
    override val subscriptionStatusTrial = "14-Day Free Trial"
    override val subscriptionStatusExpired = "Free Trial Expired"
    override val subscriptionStatusPilot = "Early Pilot Active"
    override val subscriptionDaysRemaining = "days remaining"
    override val subscriptionExpiredBanner = "Your 14-day free trial has expired. No automatic charges have occurred."
    override val btnViewPlans = "View Plans"
    override val btnUpgradeNow = "Upgrade Plan"
    override val plansModalTitle = "SalesPilot AI Boutique Subscription"
    override val plansModalSubtitle = "Boost your boutique sales with AI. 14-day trial has zero auto-billing."
    override val planProposedNotice = "Proposed Launch Pricing (Subject to pilot feedback)"
    override val merchantPaymentSeparationNotice = "Merchant Software Separation: This subscription covers your SalesPilot AI assistant. It is strictly separate from customer payments and UPI receipts for your sarees, jewellery, and clothing."
    override val planFreeTrialName = "14-Day Free Trial"
    override val planFreeTrialBadge = "No Payment Details Required"
    override val planStarterName = "Starter Plan"
    override val planStarterPrice = "₹299"
    override val planProName = "Pro Plan"
    override val planProPrice = "₹599"
    override val planProBadge = "Proposed Launch Price • Most Popular"
    override val btnSelectPlan = "Select Plan"
    override val btnRequestActivationWhatsApp = "Request Pilot Activation via WhatsApp"
    override val pilotAdminTitle = "Founder & Early Validation Admin"
    override val pilotAdminSubtitle = "Controls for extending trials and granting pilot access during early beta testing."
    override val btnExtendTrial7Days = "+7 Days Trial"
    override val btnExtendTrial14Days = "+14 Days Trial"
    override val btnGrantPilotPro = "Grant Pilot Pro Access"
    override val btnSimulateExpiry = "Simulate Trial Expiry"
    override val btnResetTrial = "Reset to 14-Day Trial"
    override val pilotNotesLabel = "Pilot Partner Notes"
    override val btnSubscribeViaWhatsApp = "Subscribe via WhatsApp"
    override val paymentNoteVerification = "Payment is completed separately and paid access is activated only after payment is verified by the founder. Zero auto-charges."
    override val subscriptionStatusPending = "Payment Pending Verification"
    override val founderUnlockTitle = "Founder Access Verification"
    override val founderUnlockPrompt = "Enter Founder PIN to unlock manual verification & pilot controls:"
    override val founderControlsLocked = "Founder controls are protected to prevent unauthorized self-activation. Authorized administrators only."
    override val btnUnlockFounder = "Unlock Founder Controls"
    override val btnLockFounder = "Lock Founder Controls"
    override val founderPinIncorrect = "Incorrect PIN. Founder controls remain locked."

    override val estValueLabel = "Est. Value"
    override val priceLabel = "Price"
}

object HindiStrings : AppStrings {
    override val navDashboard = "डैशबोर्ड"
    override val navPipeline = "पाइपलाइन"
    override val navCatalog = "कैटलॉग"
    override val navCopilot = "AI पायलट"
    override val navOrders = "ऑर्डर्स"
    override val navFollowUps = "फॉलो-अप्स"
    override val navSettings = "सेटिंग्स"

    override val dashboardTitle = "बिक्री प्रदर्शन सारांश"
    override val confirmedSales = "कन्फर्म बिक्री"
    override val pipelineValue = "पाइपलाइन मूल्य"
    override val conversionRate = "कन्वर्ज़न दर"
    override val conversionRateExplanation = "फॉर्मूला: (कन्फर्म ऑर्डर्स ÷ कुल इन्क्वायरी) × 100"
    override val followUpsDue = "फॉलो-अप्स बाकी"
    override val overdueWarningTitle = "फॉलो-अप्स समय सीमा समाप्त"
    override val overdueWarningDesc = "ग्राहक जो प्रोडक्ट होल्ड या साइज़ कन्फर्मेशन का इंतज़ार कर रहे हैं।"
    override val quickActions = "त्वरित कार्य"
    override val btnNewEnquiry = "नई पूछताछ"
    override val btnAddProduct = "प्रोडक्ट जोड़ें"
    override val btnTestSampleDm = "⚡ टेस्ट मैसेज"
    override val activeEnquiries = "सक्रिय ग्राहक पूछताछ"
    override val viewFullPipeline = "पूरी पाइपलाइन देखें →"
    override val emptyEnquiriesTitle = "कोई पूछताछ दर्ज नहीं है"
    override val emptyEnquiriesSubtitle = "'⚡ टेस्ट मैसेज' दबाकर तुरंत डेमो व्हाट्सएप चैट आज़माएँ!"
    override val ordersFulfilled = "ऑर्डर्स पूरे हुए"
    override val allOnTrack = "सब समय पर है"

    override val searchLeadsPlaceholder = "ग्राहक का नाम, फोन या साड़ी खोजें..."
    override val allFilter = "सभी"
    override val noLeadsInStage = "इस स्टेज में कोई ग्राहक नहीं है"
    override val noLeadsSubtitle = "+ दबाकर नया लीड जोड़ें या अन्य स्टेज चुनें"
    override val btnAiCopilot = "AI रिप्लाई"
    override val btnChat = "चैट"
    override val btnOrder = "ऑर्डर"
    override val stageNewEnquiry = "नई पूछताछ"
    override val stageContacted = "संपर्क किया"
    override val stageInterested = "रुचि है"
    override val stageFollowUpDue = "फॉलो-अप बाकी"
    override val stageOrderPlaced = "ऑर्डर कन्फर्म"
    override val stageClosed = "बंद हुआ"

    override val searchCatalogPlaceholder = "नाम, कपड़ा या SKU से कैटलॉग खोजें..."
    override val noProductsTitle = "कोई प्रोडक्ट नहीं मिला"
    override val noProductsSubtitle = "+ दबाकर नया बुटीक आइटम जोड़ें"
    override val inStock = "स्टॉक में है"
    override val outOfStock = "स्टॉक खत्म"
    override val stockQtyLabel = "स्टॉक: "
    override val btnWhatsAppCard = "व्हाट्सएप कार्ड"
    override val btnDelete = "हटाएं"
    override val deleteProductDialogTitle = "कैटलॉग से हटाएं?"
    override val deleteProductDialogDesc = "क्या आप वाकई इस प्रोडक्ट को कैटलॉग से हटाना चाहते हैं? यह क्रिया वापस नहीं ली जा सकती।"
    override val btnConfirmDelete = "हटाएं"
    override val btnCancel = "रद्द करें"

    override val copilotGuardrailBanner = "100% कैटलॉग-सत्यापित • बिना किसी गलत कीमत या स्टॉक के"
    override val stepCustomerContext = "1. ग्राहक संदर्भ"
    override val labelCustomerName = "ग्राहक का नाम / व्हाट्सएप नंबर"
    override val labelReferencedProduct = "कैटलॉग आइटम चुनें:"
    override val selectProductPrompt = "कैटलॉग से प्रोडक्ट चुनें..."
    override val labelInquiryMessage = "ग्राहक का संदेश (व्हाट्सएप / डीएम)"
    override val inquiryPlaceholder = "यहाँ ग्राहक का सवाल पेस्ट करें, जैसे 'क्या दिल्ली में शुक्रवार तक COD मिल जाएगा?'"
    override val stepGoalAndLanguage = "2. उत्तर का उद्देश्य और भाषा"
    override val labelTargetLanguage = "उत्तर की भाषा:"
    override val labelReplyFocus = "जवाब का विषय:"
    override val btnGenerateGroundedReply = "सत्यापित AI उत्तर बनाएं"
    override val btnGenerating = "कैटलॉग नीतियों से सत्यापित हो रहा है..."
    override val badgeAiDrafted = "🤖 AI द्वारा तैयार उत्तर"
    override val verifiedGroundingTitle = "✓ सत्यापित व्यावसायिक शर्तें:"
    override val nextActionLabel = "💡 अगला कदम:"
    override val btnSendOnWhatsApp = "व्हाट्सएप पर भेजें"
    override val btnCopy = "कॉपी करें"
    override val btnConvertLeadToOrder = "इसे कन्फर्म ऑर्डर में बदलें →"

    override val ordersHeaderTotal = "कुल दर्ज ऑर्डर्स"
    override val ordersHeaderRevenue = "कुल पूरा हुआ मूल्य"
    override val noOrdersTitle = "इस स्थिति में कोई ऑर्डर नहीं है"
    override val noOrdersSubtitle = "पाइपलाइन टैब से लीड्स को ऑर्डर में बदलें"
    override val paymentPaid = "✓ भुगतान प्राप्त"
    override val paymentPending = "⏳ भुगतान बाकी"
    override val btnWhatsAppBill = "व्हाट्सएप बिल"
    override val statusConfirmed = "कन्फर्म"
    override val statusProcessing = "प्रोसेसिंग"
    override val statusShipped = "भेज दिया गया"
    override val statusDelivered = "डिलीवर हो गया"
    override val statusCancelled = "रद्द हुआ"

    override val followUpsOverdueAlert = "फॉलो-अप तुरंत ध्यान देने योग्य हैं!"
    override val filterOverdue = "समय सीमा समाप्त"
    override val filterToday = "आज के"
    override val filterUpcoming = "आने वाले"
    override val allFollowUpsDoneTitle = "सभी फॉलो-अप्स पूरे हुए!"
    override val allFollowUpsDoneSubtitle = "बहुत बढ़िया! फिलहाल कोई छूटा हुआ ग्राहक नहीं है।"
    override val btnWhatsAppReminder = "व्हाट्सएप स्मरण"
    override val btnDone = "पूरा हुआ"

    override val settingsBusinessIdentity = "व्यापार पहचान"
    override val labelShopName = "दुकान / बुटीक का नाम"
    override val labelOwnerName = "मालिक का नाम"
    override val labelWhatsAppNumber = "व्हाट्सएप नंबर"
    override val labelUpiId = "दुकान की UPI ID (GPay)"
    override val settingsPoliciesTitle = "सत्यापित नीतियां (AI द्वारा उपयोग हेतु)"
    override val settingsPoliciesSubtitle = "AI केवल इन नियमों का पालन करेगा और कभी भी गलत नियम नहीं बनाएगा।"
    override val labelShippingDays = "सामान्य डिलीवरी समय"
    override val labelShippingCharges = "शिपिंग शुल्क और फ्री डिलीवरी सीमा"
    override val labelCodEnabled = "कैश ऑन डिलीवरी (COD) सक्षम करें"
    override val labelCodTerms = "कैश ऑन डिलीवरी (COD) नियम"
    override val labelReturnPolicy = "वापसी और बदलाव नीति"
    override val labelMaxDiscount = "अधिकतम बातचीत छूट सीमा (%) - (0 = निश्चित मूल्य)"
    override val btnSaveConfiguration = "सेटिंग्स सेव करें"
    override val settingsLanguageTitle = "पसंदीदा भाषा"

    override val aiEngineSettingsTitle = "AI आर्किटेक्चर एवं सुरक्षा"
    override val aiEngineSubtitle = "AI उत्तर कैसे तैयार किए जाते हैं। स्थानीय इंजन 100% ऑन-डिवाइस बिना किसी सर्वर लागत के चलता है।"
    override val aiModeLocal = "लोकल रूल इंजन (ऑफलाइन और निजी)"
    override val aiModeProxy = "सुरक्षित बैकएंड प्रॉक्सी (Google Play के लिए अनुशंसित)"
    override val aiModeByok = "व्यक्तिगत मर्चेंट API की (BYOK)"
    override val labelProxyUrl = "बैकएंड प्रॉक्सी एंडपॉइंट URL (उदा. Cloud Run / Cloud Functions)"
    override val labelCustomApiKey = "व्यक्तिगत Gemini API Key (केवल इसी फोन में सुरक्षित)"

    override val privacyTitle = "गोपनीयता और डेटा सुरक्षा"
    override val privacyViewDetails = "डेटा सुरक्षा और प्राइवेसी विवरण देखें"
    override val privacyDialogTitle = "डेटा सुरक्षा और प्राइवेसी नीति"
    override val privacyDialogContent = "• ऑन-डिवाइस स्टोरेज: आपके सभी ग्राहक, उत्पाद और ऑर्डर्स आपके डिवाइस के निजी SQLite डेटाबेस में सुरक्षित रहते हैं।\n\n• शून्य ट्रैकिंग: कोई तृतीय-पक्ष विज्ञापन या ट्रैकिंग SDK शामिल नहीं है।\n\n• AI उत्तर: AI ड्राफ्टिंग के दौरान केवल ग्राहक का पहला नाम और प्रश्न ही प्रोसेस होता है। फोन नंबर, पता या वित्तीय जानकारी कभी AI को नहीं भेजी जाती।\n\n• डेटा नियंत्रण: आपका डेटा पूरी तरह आपका है। ऐप अनइंस्टॉल करने पर सारा डेटा समाप्त हो जाता है।"
    override val btnClose = "बंद करें"

    // Subscription & Free Trial
    override val subscriptionTitle = "सदस्यता और मुफ्त ट्रायल"
    override val subscriptionStatusTrial = "14 दिन का मुफ्त ट्रायल"
    override val subscriptionStatusExpired = "मुफ्त ट्रायल समाप्त"
    override val subscriptionStatusPilot = "प्रारंभिक पायलट सक्रिय"
    override val subscriptionDaysRemaining = "दिन शेष"
    override val subscriptionExpiredBanner = "आपका 14 दिनों का मुफ्त ट्रायल समाप्त हो गया है। कोई स्वतः शुल्क नहीं काटा गया है।"
    override val btnViewPlans = "प्लान देखें"
    override val btnUpgradeNow = "प्लान अपग्रेड करें"
    override val plansModalTitle = "SalesPilot AI बुटीक सदस्यता"
    override val plansModalSubtitle = "AI के साथ अपनी बिक्री बढ़ाएं। 14 दिनों के ट्रायल में कोई स्वतः भुगतान नहीं है।"
    override val planProposedNotice = "प्रस्तावित लॉन्च मूल्य (पायलट प्रतिक्रिया पर आधारित)"
    override val merchantPaymentSeparationNotice = "मर्चेंट सॉफ्टवेयर पृथक्करण: यह सदस्यता SalesPilot AI असिस्टेंट के लिए है। यह आपकी साड़ियों, आभूषणों और कपड़ों के लिए ग्राहकों द्वारा किए जाने वाले भुगतानों से पूरी तरह अलग है।"
    override val planFreeTrialName = "14 दिन का मुफ्त ट्रायल"
    override val planFreeTrialBadge = "कार्ड या भुगतान विवरण आवश्यक नहीं"
    override val planStarterName = "स्टार्टर प्लान"
    override val planStarterPrice = "₹299"
    override val planProName = "प्रो प्लान"
    override val planProPrice = "₹599"
    override val planProBadge = "प्रस्तावित लॉन्च मूल्य • सबसे लोकप्रिय"
    override val btnSelectPlan = "प्लान चुनें"
    override val btnRequestActivationWhatsApp = "व्हाट्सएप पर पायलट एक्टिवेशन का अनुरोध करें"
    override val pilotAdminTitle = "संस्थापक एवं प्रारंभिक सत्यापन नियंत्रण"
    override val pilotAdminSubtitle = "बीटा परीक्षण के दौरान ट्रायल बढ़ाने और पायलट एक्सेस प्रदान करने के विकल्प।"
    override val btnExtendTrial7Days = "+7 दिन ट्रायल"
    override val btnExtendTrial14Days = "+14 दिन ट्रायल"
    override val btnGrantPilotPro = "पायलट प्रो एक्सेस दें"
    override val btnSimulateExpiry = "ट्रायल समाप्ति टेस्ट करें"
    override val btnResetTrial = "14 दिन का ट्रायल रीसेट करें"
    override val pilotNotesLabel = "पायलट पार्टनर नोट्स"
    override val btnSubscribeViaWhatsApp = "व्हाट्सएप द्वारा सदस्यता लें"
    override val paymentNoteVerification = "भुगतान अलग से पूरा किया जाता है और संस्थापक द्वारा सत्यापन के बाद ही पेड एक्सेस सक्रिय होता है। कोई स्वतः शुल्क नहीं।"
    override val subscriptionStatusPending = "भुगतान सत्यापन प्रक्रिया में है"
    override val founderUnlockTitle = "संस्थापक सत्यापन"
    override val founderUnlockPrompt = "मैनुअल भुगतान सत्यापन और पायलट नियंत्रण अनलॉक करने के लिए संस्थापक पिन दर्ज करें:"
    override val founderControlsLocked = "अनधिकृत स्वतः-सक्रियण को रोकने के लिए संस्थापक नियंत्रण सुरक्षित हैं।"
    override val btnUnlockFounder = "संस्थापक नियंत्रण अनलॉक करें"
    override val btnLockFounder = "संस्थापक नियंत्रण लॉक करें"
    override val founderPinIncorrect = "गलत पिन। संस्थापक नियंत्रण लॉक हैं।"

    override val estValueLabel = "अनुमानित मूल्य"
    override val priceLabel = "कीमत"
}

object KannadaStrings : AppStrings {
    override val navDashboard = "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್"
    override val navPipeline = "ಪೈಪ್‌ಲೈನ್"
    override val navCatalog = "ಕ್ಯಾಟಲಾಗ್"
    override val navCopilot = "AI ಕೋಪೈಲಟ್"
    override val navOrders = "ಆರ್ಡರ್‌ಗಳು"
    override val navFollowUps = "ಫಾಲೋ-ಅಪ್‌ಗಳು"
    override val navSettings = "ಸೆಟ್ಟಿಂಗ್ಸ್"

    override val dashboardTitle = "ಮಾರಾಟ ಸಾಧನೆಯ ನೋಟ"
    override val confirmedSales = "ಖಚಿತ ಮಾರಾಟ"
    override val pipelineValue = "ಪೈಪ್‌ಲೈನ್ ಮೌಲ್ಯ"
    override val conversionRate = "ಕನ್ವರ್ಷನ್ ದರ"
    override val conversionRateExplanation = "ಸೂತ್ರ: (ಖಚಿತ ಆರ್ಡರ್‌ಗಳು ÷ ಒಟ್ಟು ವಿಚಾರಣೆಗಳು) × 100"
    override val followUpsDue = "ಫಾಲೋ-ಅಪ್ ಬಾಕಿ"
    override val overdueWarningTitle = "ಸಮಯ ಮೀರಿದ ಫಾಲೋ-ಅಪ್‌ಗಳು"
    override val overdueWarningDesc = "ಸೀರೆ ಅಥವಾ ಅಳತೆ ಕಾಯ್ದಿರಿಸುವಿಕೆಯ ದೃಢೀಕರಣಕ್ಕಾಗಿ ಕಾಯುತ್ತಿರುವ ಗ್ರಾಹಕರು."
    override val quickActions = "ತ್ವರಿತ ಕ್ರಿಯೆಗಳು"
    override val btnNewEnquiry = "ಹೊಸ ವಿಚಾರಣೆ"
    override val btnAddProduct = "ಪ್ರಾಡಕ್ಟ್ ಸೇರಿಸಿ"
    override val btnTestSampleDm = "⚡ ಡೆಮೊ ವಿಚಾರಣೆ"
    override val activeEnquiries = "ಸಕ್ರಿಯ ಗ್ರಾಹಕ ವಿಚಾರಣೆಗಳು"
    override val viewFullPipeline = "ಸಂಪೂರ್ಣ ಪೈಪ್‌ಲೈನ್ ನೋಡಿ →"
    override val emptyEnquiriesTitle = "ಇನ್ನೂ ಯಾವುದೇ ವಿಚಾರಣೆ ದಾಖಲಾಗಿಲ್ಲ"
    override val emptyEnquiriesSubtitle = "'⚡ ಡೆಮೊ ವಿಚಾರಣೆ' ಒತ್ತಿ ತ್ವರಿತ ಕನ್ನಡ ವಾಟ್ಸಾಪ್ ವಿಚಾರಣೆ ಪರೀಕ್ಷಿಸಿ!"
    override val ordersFulfilled = "ಆರ್ಡರ್‌ಗಳು ಪೂರ್ಣಗೊಂಡಿವೆ"
    override val allOnTrack = "ಎಲ್ಲವೂ ಸರಿಯಾಗಿದೆ"

    override val searchLeadsPlaceholder = "ಗ್ರಾಹಕರ ಹೆಸರು, ಫೋನ್ ಅಥವಾ ಸೀರೆ ಹುಡುಕಿ..."
    override val allFilter = "ಎಲ್ಲವೂ"
    override val noLeadsInStage = "ಈ ಹಂತದಲ್ಲಿ ಯಾವುದೇ ಗ್ರಾಹಕರಿಲ್ಲ"
    override val noLeadsSubtitle = "+ ಒತ್ತಿ ಹೊಸ ಲೀಡ್ ಸೇರಿಸಿ ಅಥವಾ ಬೇರೆ ಹಂತ ಆಯ್ಕೆಮಾಡಿ"
    override val btnAiCopilot = "AI ಉತ್ತರ"
    override val btnChat = "ಚಾಟ್"
    override val btnOrder = "ಆರ್ಡರ್"
    override val stageNewEnquiry = "ಹೊಸ ವಿಚಾರಣೆ"
    override val stageContacted = "ಸಂಪರ್ಕಿಸಲಾಗಿದೆ"
    override val stageInterested = "ಆಸಕ್ತಿ ಇದೆ"
    override val stageFollowUpDue = "ಫಾಲೋ-ಅಪ್ ಬಾಕಿ"
    override val stageOrderPlaced = "ಆರ್ಡರ್ ದೃಢಪಟ್ಟಿದೆ"
    override val stageClosed = "ಮುಕ್ತಾಯ"

    override val searchCatalogPlaceholder = "ಹೆಸರು, ಬಟ್ಟೆ ಅಥವಾ SKU ಮೂಲಕ ಹುಡುಕಿ..."
    override val noProductsTitle = "ಯಾವುದೇ ಪ್ರಾಡಕ್ಟ್ ಕಂಡುಬಂದಿಲ್ಲ"
    override val noProductsSubtitle = "+ ಒತ್ತಿ ಹೊಸ ಬುಟೀಕ್ ಐಟಂ ಸೇರಿಸಿ"
    override val inStock = "ಸ್ಟಾಕ್‌ನಲ್ಲಿದೆ"
    override val outOfStock = "ಸ್ಟಾಕ್ ಮುಗಿದಿದೆ"
    override val stockQtyLabel = "ಸ್ಟಾಕ್: "
    override val btnWhatsAppCard = "ವಾಟ್ಸಾಪ್ ಕಾರ್ಡ್"
    override val btnDelete = "ತೆಗೆದುಹಾಕಿ"
    override val deleteProductDialogTitle = "ಕ್ಯಾಟಲಾಗ್‌ನಿಂದ ತೆಗೆದುಹಾಕಬೇಕೇ?"
    override val deleteProductDialogDesc = "ಈ ಪ್ರಾಡಕ್ಟ್ ಅನ್ನು ನಿಮ್ಮ ಕ್ಯಾಟಲಾಗ್‌ನಿಂದ ಖಚಿತವಾಗಿ ತೆಗೆದುಹಾಕಲು ಬಯಸುವಿರಾ? ಈ ಕ್ರಿಯೆಯನ್ನು ರದ್ದುಗೊಳಿಸಲಾಗುವುದಿಲ್ಲ."
    override val btnConfirmDelete = "ತೆಗೆದುಹಾಕಿ"
    override val btnCancel = "ರದ್ದುಮಾಡಿ"

    override val copilotGuardrailBanner = "100% ಕ್ಯಾಟಲಾಗ್ ದೃಢೀಕೃತ • ಯಾವುದೇ ತಪ್ಪು ಬೆಲೆ ಅಥವಾ ಸ್ಟಾಕ್ ಇಲ್ಲ"
    override val stepCustomerContext = "1. ಗ್ರಾಹಕರ ವಿವರ"
    override val labelCustomerName = "ಗ್ರಾಹಕರ ಹೆಸರು / ವಾಟ್ಸಾಪ್ ನಂಬರ್"
    override val labelReferencedProduct = "ಆಯ್ಕೆಮಾಡಿದ ಪ್ರಾಡಕ್ಟ್:"
    override val selectProductPrompt = "ಕ್ಯಾಟಲಾಗ್‌ನಿಂದ ಪ್ರಾಡಕ್ಟ್ ಆಯ್ಕೆಮಾಡಿ..."
    override val labelInquiryMessage = "ಗ್ರಾಹಕರ ಸಂದೇಶ (ವಾಟ್ಸಾಪ್ / ಇನ್‌ಸ್ಟಾ)"
    override val inquiryPlaceholder = "ಗ್ರಾಹಕರ ಪ್ರಶ್ನೆಯನ್ನು ಇಲ್ಲಿ ಪೇಸ್ಟ್ ಮಾಡಿ, ಉದಾ: 'ಬೆಂಗಳೂರಿಗೆ ಶುಕ್ರವಾರದೊಳಗೆ ಡೆಲಿವರಿ ಆಗುತ್ತಾ?'"
    override val stepGoalAndLanguage = "2. ಉತ್ತರದ ಉದ್ದೇಶ ಮತ್ತು ಭಾಷೆ"
    override val labelTargetLanguage = "ಉತ್ತರದ ಭಾಷೆ:"
    override val labelReplyFocus = "ಉತ್ತರದ ವಿಷಯ:"
    override val btnGenerateGroundedReply = "ದೃಢೀಕೃತ AI ಉತ್ತರ ಸಿದ್ಧಪಡಿಸಿ"
    override val btnGenerating = "ಕ್ಯಾಟಲಾಗ್ ನಿಯಮಗಳೊಂದಿಗೆ ಪರಿಶೀಲಿಸಲಾಗುತ್ತಿದೆ..."
    override val badgeAiDrafted = "🤖 AI ಸಿದ್ಧಪಡಿಸಿದ ಉತ್ತರ"
    override val verifiedGroundingTitle = "✓ ಪರಿಶೀಲಿಸಿದ ವ್ಯಾಪಾರ ನಿಯಮಗಳು:"
    override val nextActionLabel = "💡 ಮುಂದಿನ ಹೆಜ್ಜೆ:"
    override val btnSendOnWhatsApp = "ವಾಟ್ಸಾಪ್‌ನಲ್ಲಿ ಕಳುಹಿಸಿ"
    override val btnCopy = "ಕಾಪಿ ಮಾಡಿ"
    override val btnConvertLeadToOrder = "ಖಚಿತ ಆರ್ಡರ್ ಆಗಿ ಪರಿವರ್ತಿಸಿ →"

    override val ordersHeaderTotal = "ಒಟ್ಟು ದಾಖಲಾದ ಆರ್ಡರ್‌ಗಳು"
    override val ordersHeaderRevenue = "ಪೂರ್ಣಗೊಂಡ ಒಟ್ಟು ಮೌಲ್ಯ"
    override val noOrdersTitle = "ಯಾವುದೇ ಆರ್ಡರ್‌ಗಳಿಲ್ಲ"
    override val noOrdersSubtitle = "ಪೈಪ್‌ಲೈನ್ ಟ್ಯಾಬ್‌ನಲ್ಲಿ ಲೀಡ್‌ಗಳನ್ನು ಆರ್ಡರ್ ಆಗಿ ಪರಿವರ್ತಿಸಿ"
    override val paymentPaid = "✓ ಪಾವತಿಸಲಾಗಿದೆ"
    override val paymentPending = "⏳ ಪಾವತಿ ಬಾಕಿ"
    override val btnWhatsAppBill = "ವಾಟ್ಸಾಪ್ ರಸೀದಿ"
    override val statusConfirmed = "ದೃಢಪಟ್ಟಿದೆ"
    override val statusProcessing = "ಸಿದ್ಧತೆ ಹಂತದಲ್ಲಿದೆ"
    override val statusShipped = "ರವಾನಿಸಲಾಗಿದೆ"
    override val statusDelivered = "ತಲುಪಿದೆ"
    override val statusCancelled = "ರದ್ದುಗೊಂಡಿದೆ"

    override val followUpsOverdueAlert = "ಫಾಲೋ-ಅಪ್‌ಗಳು ತಕ್ಷಣದ ಗಮನ ಬಯಸುತ್ತವೆ!"
    override val filterOverdue = "ಸಮಯ ಮೀರಿದೆ"
    override val filterToday = "ಇಂದಿನದು"
    override val filterUpcoming = "ಮುಂದಿನದು"
    override val allFollowUpsDoneTitle = "ಎಲ್ಲಾ ಫಾಲೋ-ಅಪ್‌ಗಳು ಪೂರ್ಣಗೊಂಡಿವೆ!"
    override val allFollowUpsDoneSubtitle = "ಅತ್ಯುತ್ತಮ! ಯಾವುದೇ ಬಾಕಿ ಉಳಿದ ವಿಚಾರಣೆಗಳಿಲ್ಲ."
    override val btnWhatsAppReminder = "ವಾಟ್ಸಾಪ್ ನೆನಪಿಸುವಿಕೆ"
    override val btnDone = "ಆಯಿತು"

    override val settingsBusinessIdentity = "ವ್ಯಾಪಾರದ ವಿವರ"
    override val labelShopName = "ಅಂಗಡಿ / ಬುಟೀಕ್ ಹೆಸರು"
    override val labelOwnerName = "ಮಾಲೀಕರ ಹೆಸರು"
    override val labelWhatsAppNumber = "ವಾಟ್ಸಾಪ್ ನಂಬರ್"
    override val labelUpiId = "ಅಂಗಡಿಯ UPI ID (GPay)"
    override val settingsPoliciesTitle = "ದೃಢೀಕೃತ ನೀತಿಗಳು (AI ಗಾಗಿ)"
    override val settingsPoliciesSubtitle = "AI ಈ ನಿಖರ ನಿಯಮಗಳನ್ನು ಮಾತ್ರ ಬಳಸುತ್ತದೆ ಮತ್ತು ಯಾವುದೇ ಅನಧಿಕೃತ ನಿಯಮಗಳನ್ನು ಸೃಷ್ಟಿಸುವುದಿಲ್ಲ."
    override val labelShippingDays = "ಸಾಮಾನ್ಯ ಡೆಲಿವರಿ ಅವಧಿ"
    override val labelShippingCharges = "ಶಿಪ್ಪಿಂಗ್ ದರ ಮತ್ತು ಉಚಿತ ಡೆಲಿವರಿ ಮಿತಿ"
    override val labelCodEnabled = "ಕ್ಯಾಶ್ ಆನ್ ಡೆಲಿವರಿ (COD) ಸಕ್ರಿಯಗೊಳಿಸಿ"
    override val labelCodTerms = "ಕ್ಯಾಶ್ ಆನ್ ಡೆಲಿವರಿ (COD) ನಿಯಮಗಳು"
    override val labelReturnPolicy = "ಹಿಂತಿರುಗಿಸುವ ಮತ್ತು ವಿನಿಮಯ ನೀತಿ"
    override val labelMaxDiscount = "ಗರಿಷ್ಠ ರಿಯಾಯಿತಿ ಮಿತಿ (%) - (0 = ನಿಶ್ಚಿತ ದರ)"
    override val btnSaveConfiguration = "ಸೆಟ್ಟಿಂಗ್ಸ್ ಉಳಿಸಿ"
    override val settingsLanguageTitle = "ಆದ್ಯತೆಯ ಭಾಷೆ"

    override val aiEngineSettingsTitle = "AI ಆರ್ಕಿಟೆಕ್ಚರ್ ಮತ್ತು ಭದ್ರತೆ"
    override val aiEngineSubtitle = "AI ಉತ್ತರಗಳನ್ನು ಹೇಗೆ ಸಿದ್ಧಪಡಿಸಲಾಗುತ್ತದೆ. ಲೋಕಲ್ ಇಂಜಿನ್ 100% ಡಿವೈಸ್‌ನಲ್ಲೇ ರನ್ ಆಗುತ್ತದೆ (ಉಚಿತ & ಖಾಸಗಿ)."
    override val aiModeLocal = "ಲೋಕಲ್ ರೂಲ್ ಇಂಜಿನ್ (ಆಫ್‌ಲೈನ್ ಮತ್ತು ಖಾಸಗಿ)"
    override val aiModeProxy = "ಸುರಕ್ಷಿತ ಬ್ಯಾಕೆಂಡ್ ಪ್ರಾಕ್ಸಿ (Google Play ಗೆ ಶಿಫಾರಸು ಮಾಡಲಾಗಿದೆ)"
    override val aiModeByok = "ವೈಯಕ್ತಿಕ ಮರ್ಚೆಂಟ್ API ಕೀ (BYOK)"
    override val labelProxyUrl = "ಬ್ಯಾಕೆಂಡ್ ಪ್ರಾಕ್ಸಿ URL (ಉದಾ. Cloud Run / Cloud Functions)"
    override val labelCustomApiKey = "ವೈಯಕ್ತಿಕ Gemini API Key (ಈ ಫೋನ್‌ನಲ್ಲಿ ಮಾತ್ರ ಸಂಗ್ರಹವಾಗುತ್ತದೆ)"

    override val privacyTitle = "ಗೌಪ್ಯತೆ ಮತ್ತು ಡೇಟಾ ಸುರಕ್ಷತೆ"
    override val privacyViewDetails = "ಡೇಟಾ ಸುರಕ್ಷತೆ ವಿವರಗಳನ್ನು ವೀಕ್ಷಿಸಿ"
    override val privacyDialogTitle = "ಡೇಟಾ ಸುರಕ್ಷತೆ ಮತ್ತು ಗೌಪ್ಯತೆ ನೀತಿ"
    override val privacyDialogContent = "• ಆನ್-ಡಿವೈಸ್ ಸಂಗ್ರಹಣೆ: ನಿಮ್ಮ ಎಲ್ಲಾ ಗ್ರಾಹಕರು, ಪ್ರಾಡಕ್ಟ್‌ಗಳು ಮತ್ತು ಆರ್ಡರ್‌ಗಳು ನಿಮ್ಮ ಫೋನ್‌ನ ಖಾಸಗಿ SQLite ಡೇಟಾಬೇಸ್‌ನಲ್ಲಿ ಮಾತ್ರ ಇರುತ್ತವೆ.\n\n• ಯಾವುದೇ ಟ್ರ್ಯಾಕಿಂಗ್ ಇಲ್ಲ: ಯಾವುದೇ ಜಾಹೀರಾತು ಅಥವಾ ಟ್ರ್ಯಾಕಿಂಗ್ SDK ಗಳನ್ನು ಬಳಸಲಾಗಿಲ್ಲ.\n\n• AI ಉತ್ಪಾದನೆ: AI ಉತ್ತರ ಸಿದ್ಧಪಡಿಸುವಾಗ ಗ್ರಾಹಕರ ಹೆಸರು ಮತ್ತು ಸಂದೇಶವನ್ನು ಮಾತ್ರ ಪರಿಶೀಲಿಸಲಾಗುತ್ತದೆ. ಫೋನ್ ನಂಬರ್, ವಿಳಾಸ ಅಥವಾ ಹಣಕಾಸಿನ ವಿವರಗಳನ್ನು AI ಗೆ ಕಳುಹಿಸಲಾಗುವುದಿಲ್ಲ.\n\n• ಡೇಟಾ ನಿಯಂತ್ರಣ: ನಿಮ್ಮ ಡೇಟಾ ಸಂಪೂರ್ಣವಾಗಿ ನಿಮ್ಮದೇ ಆಗಿರುತ್ತದೆ. ಆ್ಯಪ್ ಅನ್‌ಇನ್‌ಸ್ಟಾಲ್ ಮಾಡಿದರೆ ಎಲ್ಲಾ ಡೇಟಾ ಅಳಿಸಿಹೋಗುತ್ತದೆ."
    override val btnClose = "ಮುಚ್ಚಿ"

    // Subscription & Free Trial
    override val subscriptionTitle = "ಚಂದಾದಾರಿಕೆ ಮತ್ತು ಉಚಿತ ಟ್ರಯಲ್"
    override val subscriptionStatusTrial = "14 ದಿನಗಳ ಉಚಿತ ಟ್ರಯಲ್"
    override val subscriptionStatusExpired = "ಉಚಿತ ಟ್ರಯಲ್ ಮುಕ್ತಾಯವಾಗಿದೆ"
    override val subscriptionStatusPilot = "ಆರಂಭಿಕ ಪೈಲಟ್ ಸಕ್ರಿಯವಾಗಿದೆ"
    override val subscriptionDaysRemaining = "ದಿನಗಳು ಬಾಕಿ"
    override val subscriptionExpiredBanner = "ನಿಮ್ಮ 14 ದಿನಗಳ ಉಚಿತ ಟ್ರಯಲ್ ಮುಕ್ತಾಯವಾಗಿದೆ. ಯಾವುದೇ ಸ್ವಯಂಚಾಲಿತ ಶುಲ್ಕ ಕಡಿತವಾಗಿಲ್ಲ."
    override val btnViewPlans = "ಪ್ಲಾನ್‌ಗಳನ್ನು ನೋಡಿ"
    override val btnUpgradeNow = "ಪ್ಲಾನ್ ಅಪ್‌ಗ್ರೇಡ್ ಮಾಡಿ"
    override val plansModalTitle = "SalesPilot AI ಬುಟೀಕ್ ಚಂದಾದಾರಿಕೆ"
    override val plansModalSubtitle = "AI ಸಹಾಯದೊಂದಿಗೆ ಮಾರಾಟ ಹೆಚ್ಚಿಸಿ. 14 ದಿನಗಳ ಟ್ರಯಲ್‌ನಲ್ಲಿ ಯಾವುದೇ ಆಟೋ-ಚಾರ್ಜ್ ಇಲ್ಲ."
    override val planProposedNotice = "ಪ್ರಸ್ತಾವಿತ ಬಿಡುಗಡೆ ದರ (ಪೈಲಟ್ ಪ್ರತಿಕ್ರಿಯೆ ಆಧಾರಿತ)"
    override val merchantPaymentSeparationNotice = "ಮರ್ಚೆಂಟ್ ಸಾಫ್ಟ್‌ವೇರ್ ಪ್ರತ್ಯೇಕತೆ: ಈ ಚಂದಾದಾರಿಕೆಯು SalesPilot AI ಗಾಗಿ ಮಾತ್ರ. ಇದು ನಿಮ್ಮ ಸೀರೆ, ಆಭರಣಗಳಿಗಾಗಿ ಗ್ರಾಹಕರು ಅಂಗಡಿಗೆ ನೀಡುವ UPI ಪಾವತಿಗಳಿಂದ ಸಂಪೂರ್ಣ ಪ್ರತ್ಯೇಕವಾಗಿದೆ."
    override val planFreeTrialName = "14 ದಿನಗಳ ಉಚಿತ ಟ್ರಯಲ್"
    override val planFreeTrialBadge = "ಯಾವುದೇ ಕಾರ್ಡ್ ವಿವರಗಳ ಅಗತ್ಯವಿಲ್ಲ"
    override val planStarterName = "ಸ್ಟಾರ್ಟರ್ ಪ್ಲಾನ್"
    override val planStarterPrice = "₹299"
    override val planProName = "ಪ್ರೊ ಪ್ಲಾನ್"
    override val planProPrice = "₹599"
    override val planProBadge = "ಪ್ರಸ್ತಾವಿತ ದರ • ಹೆಚ್ಚು ಜನಪ್ರಿಯ"
    override val btnSelectPlan = "ಪ್ಲಾನ್ ಆಯ್ಕೆಮಾಡಿ"
    override val btnRequestActivationWhatsApp = "ವಾಟ್ಸಾಪ್ ಮೂಲಕ ಪೈಲಟ್ ಸಕ್ರಿಯಗೊಳಿಸಲು ವಿನಂತಿಸಿ"
    override val pilotAdminTitle = "ಸ್ಥಾಪಕ ಮತ್ತು ಆರಂಭಿಕ ಮೌಲ್ಯಮಾಪನ ನಿರ್ವಹಣೆ"
    override val pilotAdminSubtitle = "ಬೀಟಾ ಪರೀಕ್ಷೆಯ ಸಮಯದಲ್ಲಿ ಟ್ರಯಲ್ ವಿಸ್ತರಿಸಲು ಮತ್ತು ಪ್ರವೇಶ ನೀಡಲು ನಿಯಂತ್ರಣಗಳು."
    override val btnExtendTrial7Days = "+7 ದಿನ ಟ್ರಯಲ್"
    override val btnExtendTrial14Days = "+14 ದಿನ ಟ್ರಯಲ್"
    override val btnGrantPilotPro = "ಪೈಲಟ್ ಪ್ರೊ ಪ್ರವೇಶ ನೀಡಿ"
    override val btnSimulateExpiry = "ಟ್ರಯಲ್ ಮುಕ್ತಾಯ ಪರೀಕ್ಷಿಸಿ"
    override val btnResetTrial = "14 ದಿನದ ಟ್ರಯಲ್ ರೀಸೆಟ್ ಮಾಡಿ"
    override val pilotNotesLabel = "ಪೈಲಟ್ ಪಾರ್ಟ್ನರ್ ಟಿಪ್ಪಣಿಗಳು"
    override val btnSubscribeViaWhatsApp = "ವಾಟ್ಸಾಪ್ ಮೂಲಕ ಚಂದಾದಾರರಾಗಿ"
    override val paymentNoteVerification = "ಪಾವತಿಯನ್ನು ಪ್ರತ್ಯೇಕವಾಗಿ ಪೂರ್ಣಗೊಳಿಸಲಾಗುತ್ತದೆ ಮತ್ತು ಸ್ಥಾಪಕರು ಪರಿಶೀಲಿಸಿದ ನಂತರವೇ ಪ್ರವೇಶ ಸಕ್ರಿಯವಾಗುತ್ತದೆ. ಯಾವುದೇ ಆಟೋ-ಚಾರ್ಜ್ ಇಲ್ಲ."
    override val subscriptionStatusPending = "ಪಾವತಿ ಪರಿಶೀಲನೆ ಬಾಕಿ ಇದೆ"
    override val founderUnlockTitle = "ಸ್ಥಾಪಕರ ಪ್ರವೇಶ ಪರಿಶೀಲನೆ"
    override val founderUnlockPrompt = "ಹಸ್ತಚಾಲಿತ ಪಾವತಿ ಪರಿಶೀಲನೆ ಮತ್ತು ನಿಯಂತ್ರಣಗಳನ್ನು ಅನ್‌ಲಾಕ್ ಮಾಡಲು ಸ್ಥಾಪಕರ ಪಿನ್ ನಮೂದಿಸಿ:"
    override val founderControlsLocked = "ಅನಧಿಕೃತ ಸ್ವಯಂ-ಸಕ್ರಿಯಗೊಳಿಸುವಿಕೆಯನ್ನು ತಡೆಯಲು ಸ್ಥಾಪಕರ ನಿಯಂತ್ರಣಗಳನ್ನು ರಕ್ಷಿಸಲಾಗಿದೆ."
    override val btnUnlockFounder = "ಸ್ಥಾಪಕರ ನಿಯಂತ್ರಣ ಅನ್‌ಲಾಕ್ ಮಾಡಿ"
    override val btnLockFounder = "ಸ್ಥಾಪಕರ ನಿಯಂತ್ರಣ ಲಾಕ್ ಮಾಡಿ"
    override val founderPinIncorrect = "ತಪ್ಪಾದ ಪಿನ್. ನಿಯಂತ್ರಣಗಳು ಲಾಕ್ ಆಗಿವೆ."

    override val estValueLabel = "ಅಂದಾಜು ಮೌಲ್ಯ"
    override val priceLabel = "ಬೆಲೆ"
}

object StringsProvider {
    fun getStrings(langCode: String): AppStrings {
        return when (langCode.lowercase()) {
            "kn" -> KannadaStrings
            "hi" -> HindiStrings
            else -> EnglishStrings
        }
    }
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnglishStrings }
