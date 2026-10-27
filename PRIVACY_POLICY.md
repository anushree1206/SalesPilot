# Privacy Policy for SalesPilot AI

*Effective Date: October 26, 2026*  
*Application: SalesPilot AI (`com.aistudio.salespilot.inapp`)*  
*Developer Contact: anu740560@gmail.com*

---

## 1. Overview & Privacy Commitment
SalesPilot AI is an on-device sales management and customer enquiry conversion tool designed specifically for small business owners and boutique merchants. We respect your privacy and that of your customers.

**Our Core Privacy Principle:**  
SalesPilot AI uses a **Local-First Architecture**. Your core business data (catalog, pricing, customers, leads, and orders) remains stored exclusively in your device's private SQLite database.

---

## 2. Information We Collect and Store

### A. Information Stored Locally on Your Device (Not Transmitted to Us)
The following information is authored by you and stored strictly in your device's local database:
- **Product Catalog Data:** Product names, SKUs, categories, retail prices, sale prices, fabric/materials, sizing, and stock levels.
- **Customer Contact Information:** Customer names, phone numbers, delivery addresses, and customer notes.
- **Sales & Order Records:** Order numbers, order stage, payment status, tracking numbers, and order totals.
- **Store Policies & Settings:** Shop name, owner name, WhatsApp number, UPI ID, return policies, and language preferences.

*None of this customer or transactional data is ever uploaded to our servers, sold, or shared with third-party advertising networks.*

### B. Information Processed for AI-Assisted Reply Drafting
When you tap **"Generate Grounded Reply"** in the AI Copilot:
- **Grounded Engine (Local Mode):** When operating in local mode, generation is 100% on-device. Zero bytes leave your device.
- **AI Cloud Service / Proxy Mode:** If you enable a backend proxy or supply a Gemini AI key, only the customer's *first name*, *enquiry message*, and *relevant product specifications* are transmitted ephemerally to generate the reply draft. Full customer phone numbers, complete residential addresses, and financial transaction histories are never sent to the AI service.

---

## 3. Third-Party Services & APIs
- **Google AI Studio (Gemini 3.8 Flash):** When the AI cloud feature is invoked, queries are processed pursuant to Google AI's data terms (ephemeral generation without using data to train models on paid/API tiers).
- **WhatsApp:** When you choose to dispatch a drafted quote, receipt, or reminder, SalesPilot AI triggers a standard Android Intent opening WhatsApp. You retain full control to review and edit any message before tapping send in WhatsApp.

---

## 4. Google Play Data Safety Disclosures
In accordance with Google Play Developer Policies:
| Data Type | Collected | Shared with 3rd Parties | Purpose | Storage & Security |
| :--- | :--- | :--- | :--- | :--- |
| **Personal Info (Name, Phone)** | Yes (locally on device) | No | App functionality (lead & customer records) | Stored in app's private sandbox SQLite database. Not shared. |
| **User Content (Enquiry text)** | Ephemeral (during AI generation only) | Yes (to Gemini API / Proxy) | Generating sales replies | Encrypted in transit via HTTPS/TLS. Not retained by app servers. |
| **Financial Info** | None | None | None | No bank account numbers or credit cards are collected or processed. |
| **Location Data** | None | None | None | No GPS or location tracking permissions are requested. |
| **Device or other IDs** | None | None | None | No advertising IDs (AD_ID) or persistent trackers used. |

---

## 5. Data Retention & Deletion
Because all data is stored on your device:
- You may delete any product, lead, or order directly within the application at any time.
- Uninstalling the application or clearing the application data from Android Settings immediately and permanently deletes all database records.

---

## 6. Security
All local SQLite databases are protected by the Android Linux kernel application sandbox. Network communication with AI endpoints or your backend proxy is strictly enforced over encrypted Transport Layer Security (TLS 1.3 / HTTPS).

---

## 7. Contact Us
If you have any questions or concerns regarding this Privacy Policy, please contact:  
**Email:** anu740560@gmail.com  
**Application ID:** `com.aistudio.salespilot.inapp`
