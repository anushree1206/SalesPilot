package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BusinessProfileEntity
import com.example.domain.subscription.SubscriptionCatalog
import com.example.domain.subscription.SubscriptionManager
import com.example.domain.subscription.SubscriptionStatus
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    profile: BusinessProfileEntity?,
    onSaveProfile: (BusinessProfileEntity) -> Unit,
    onViewPlansClick: () -> Unit = {},
    onExtendTrial: (days: Int) -> Unit = {},
    onGrantPilotPro: () -> Unit = {},
    onSimulateExpiry: () -> Unit = {},
    onResetTrial: () -> Unit = {},
    onOpenOnboarding: () -> Unit = {},
    onDeleteData: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val strings = LocalAppStrings.current

    var businessName by remember(profile) { mutableStateOf(profile?.businessName ?: "") }
    var ownerName by remember(profile) { mutableStateOf(profile?.ownerName ?: "") }
    var contactPhone by remember(profile) { mutableStateOf(profile?.contactPhone ?: "") }
    var upiId by remember(profile) { mutableStateOf(profile?.upiId ?: "") }
    var shippingDays by remember(profile) { mutableStateOf(profile?.standardShippingDays ?: "") }
    var shippingPolicy by remember(profile) { mutableStateOf(profile?.shippingChargePolicy ?: "") }
    var returnPolicy by remember(profile) { mutableStateOf(profile?.returnExchangePolicy ?: "") }
    var isCodEnabled by remember(profile) { mutableStateOf(profile?.isCodEnabled ?: true) }
    var codPolicy by remember(profile) { mutableStateOf(profile?.codPolicy ?: "") }
    var maxDiscount by remember(profile) { mutableStateOf((profile?.maxAllowedDiscountPercent ?: 0).toString()) }
    var currentLang by remember(profile) { mutableStateOf(profile?.defaultLanguage ?: "en") }

    // AI & Security Settings
    var aiMode by remember(profile) { mutableStateOf(profile?.aiBackendMode ?: "LOCAL_RULE_ENGINE") }
    var proxyUrl by remember(profile) { mutableStateOf(profile?.proxyEndpointUrl ?: "") }
    var customApiKey by remember(profile) { mutableStateOf(profile?.customApiKey ?: "") }

    // Pilot & Subscription Notes
    var pilotNotes by remember(profile) { mutableStateOf(profile?.pilotNotes ?: "") }

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Shop Data & Reset", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete all local products, customers, leads, and orders for this business? This will reset the application to a fresh onboarding state. This action cannot be undone.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = LavenderPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.privacyDialogTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = strings.privacyDialogContent,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.btnClose)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // App Language Preference Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = LavenderPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.settingsLanguageTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SalesPilotTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "en" to "English",
                            "hi" to "हिंदी (Hindi)",
                            "kn" to "ಕನ್ನಡ (Kannada)"
                        ).forEach { (code, label) ->
                            FilterChip(
                                selected = currentLang == code,
                                onClick = {
                                    currentLang = code
                                    profile?.let { p ->
                                        onSaveProfile(p.copy(defaultLanguage = code))
                                    }
                                },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (currentLang == code) FontWeight.Bold else FontWeight.Medium) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LavenderPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = SalesPilotTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = currentLang == code,
                                    borderColor = LavenderCardBorder,
                                    selectedBorderColor = LavenderPrimary,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.2.dp
                                ),
                                modifier = Modifier.testTag("settings_lang_$code")
                            )
                        }
                    }
                }
            }
        }

        // Business Identity Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = LavenderPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.settingsBusinessIdentity, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SalesPilotTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text(strings.labelShopName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_shop_name"),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text(strings.labelOwnerName) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text(strings.labelWhatsAppNumber) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = salesPilotTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = upiId,
                            onValueChange = { upiId = it },
                            label = { Text(strings.labelUpiId) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = salesPilotTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // AI Architecture & Security Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = LavenderPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.aiEngineSettingsTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SalesPilotTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings.aiEngineSubtitle,
                        fontSize = 11.sp,
                        color = SalesPilotTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mode Selection Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "LOCAL_RULE_ENGINE" to strings.aiModeLocal,
                            "CUSTOM_PROXY" to strings.aiModeProxy,
                            "BYOK_KEY" to strings.aiModeByok
                        ).forEach { (mode, label) ->
                            FilterChip(
                                selected = aiMode == mode,
                                onClick = { aiMode = mode },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (aiMode == mode) FontWeight.Bold else FontWeight.Medium) },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LavenderPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = SalesPilotTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = aiMode == mode,
                                    borderColor = LavenderCardBorder,
                                    selectedBorderColor = LavenderPrimary,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 1.2.dp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (aiMode == "CUSTOM_PROXY") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = proxyUrl,
                            onValueChange = { proxyUrl = it },
                            label = { Text(strings.labelProxyUrl) },
                            placeholder = { Text("https://my-boutique-proxy.run.app/api/reply") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = salesPilotTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else if (aiMode == "BYOK_KEY") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customApiKey,
                            onValueChange = { customApiKey = it },
                            label = { Text(strings.labelCustomApiKey) },
                            placeholder = { Text("AIzaSy...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = salesPilotTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Verified Policies Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = LavenderPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.settingsPoliciesTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SalesPilotTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings.settingsPoliciesSubtitle,
                        fontSize = 11.sp,
                        color = SalesPilotTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = shippingDays,
                        onValueChange = { shippingDays = it },
                        label = { Text(strings.labelShippingDays) },
                        placeholder = { Text("Leave blank to ask customer for delivery pincode") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = shippingPolicy,
                        onValueChange = { shippingPolicy = it },
                        label = { Text(strings.labelShippingCharges) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // COD Toggle Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(strings.labelCodEnabled, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = SalesPilotTextPrimary)
                        Switch(
                            checked = isCodEnabled,
                            onCheckedChange = { isCodEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = LavenderPrimary
                            )
                        )
                    }

                    if (isCodEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = codPolicy,
                            onValueChange = { codPolicy = it },
                            label = { Text(strings.labelCodTerms) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = salesPilotTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = returnPolicy,
                        onValueChange = { returnPolicy = it },
                        label = { Text(strings.labelReturnPolicy) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = maxDiscount,
                        onValueChange = { maxDiscount = it },
                        label = { Text(strings.labelMaxDiscount) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Subscription & Free Trial Status
        item {
            val now = System.currentTimeMillis()
            val remainingDays = SubscriptionManager.calculateDaysRemaining(profile?.trialEndDate ?: (now + SubscriptionManager.TRIAL_DURATION_MS), now)
            val resolvedStatus = SubscriptionManager.getResolvedStatus(profile, now)

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = LavenderPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.subscriptionTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SalesPilotTextPrimary)
                        }

                        AssistChip(
                            onClick = onViewPlansClick,
                            label = {
                                Text(
                                    when (resolvedStatus) {
                                        SubscriptionStatus.TRIAL_ACTIVE -> "$remainingDays ${strings.subscriptionDaysRemaining}"
                                        SubscriptionStatus.PENDING_PAYMENT,
                                        SubscriptionStatus.PLAN_REQUESTED -> "Pending Verification"
                                        SubscriptionStatus.PILOT_ACTIVE -> "Active Subscription"
                                        SubscriptionStatus.TRIAL_EXPIRED -> "Trial Expired"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = LavenderPrimaryContainer,
                                labelColor = LavenderPrimary
                            ),
                            border = BorderStroke(1.dp, LavenderCardBorder)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current Plan: ${profile?.selectedPlan ?: "14-Day Free Trial"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SalesPilotTextPrimary
                    )

                    if (resolvedStatus == SubscriptionStatus.PENDING_PAYMENT || resolvedStatus == SubscriptionStatus.PLAN_REQUESTED) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⏳ Status: Payment Pending Verification. Paid access will be activated once the founder manually verifies your payment.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = LavenderPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = strings.merchantPaymentSeparationNotice,
                        fontSize = 11.sp,
                        color = SalesPilotTextMuted,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onViewPlansClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_view_plans_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, LavenderCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary)
                    ) {
                        Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.btnViewPlans, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Founder & Early Pilot Testing Controls (Protected: Founder passcode required to prevent ordinary merchants self-granting)
        item {
            var isFounderUnlocked by remember { mutableStateOf(false) }
            var showFounderPinDialog by remember { mutableStateOf(false) }
            var pinInput by remember { mutableStateOf("") }
            var pinError by remember { mutableStateOf(false) }

            if (showFounderPinDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showFounderPinDialog = false
                        pinInput = ""
                        pinError = false
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.founderUnlockTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = strings.founderUnlockPrompt,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = {
                                    pinInput = it
                                    pinError = false
                                },
                                label = { Text("Founder PIN") },
                                singleLine = true,
                                isError = pinError,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("founder_pin_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            if (pinError) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = strings.founderPinIncorrect,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (pinInput.trim() == com.example.domain.subscription.FOUNDER_UNLOCK_PIN) {
                                    isFounderUnlocked = true
                                    showFounderPinDialog = false
                                    pinInput = ""
                                    pinError = false
                                } else {
                                    pinError = true
                                }
                            },
                            modifier = Modifier.testTag("founder_pin_submit_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Unlock")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showFounderPinDialog = false
                            pinInput = ""
                            pinError = false
                        }) {
                            Text(strings.btnClose)
                        }
                    }
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isFounderUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isFounderUnlocked) Icons.Default.AdminPanelSettings else Icons.Default.Lock,
                                contentDescription = null,
                                tint = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.pilotAdminTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        if (isFounderUnlocked) {
                            TextButton(onClick = { isFounderUnlocked = false }) {
                                Text(strings.btnLockFounder, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (!isFounderUnlocked) {
                        // Protected state for ordinary merchants
                        Text(
                            text = strings.founderControlsLocked,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { showFounderPinDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("unlock_founder_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.btnUnlockFounder, fontSize = 12.sp)
                        }
                    } else {
                        // Unlocked state for authorized founder
                        Text(
                            text = strings.pilotAdminSubtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = pilotNotes,
                            onValueChange = { pilotNotes = it },
                            label = { Text(strings.pilotNotesLabel) },
                            placeholder = { Text("e.g. Verified UPI payment for Pro plan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = salesPilotTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Manual Grant Paid Subscription Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onGrantPilotPro,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("founder_grant_pro_btn"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text("Grant Pro Access", fontSize = 11.sp)
                            }

                            FilledTonalButton(
                                onClick = onGrantPilotPro,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text("Grant Starter Access", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Trial Extension buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onExtendTrial(7) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(strings.btnExtendTrial7Days, fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { onExtendTrial(14) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(strings.btnExtendTrial14Days, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onSimulateExpiry,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(strings.btnSimulateExpiry, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }

                            OutlinedButton(
                                onClick = onResetTrial,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(strings.btnResetTrial, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Privacy & Data Safety Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = LavenderPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.privacyTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SalesPilotTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = { showPrivacyDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, LavenderCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.privacyViewDetails, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Account & Multi-Tenant Data Management Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = LavenderPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Account & Shop Data Management", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SalesPilotTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tenant Account Mode: ${if (profile?.accountType == "VERIFIED_ONLINE") "Verified Online Account" else "Private Local Device Account"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LavenderPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Data Storage: On-device encrypted SQLite/Room database. Tenant records are strictly partitioned by business ID.",
                        fontSize = 11.sp,
                        color = SalesPilotTextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenOnboarding,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, LavenderCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_sign_out_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, LavenderCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sign Out", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.2.dp, Color(0xFFFECACA))
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Data", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = (profile ?: BusinessProfileEntity()).copy(
                        businessName = businessName,
                        ownerName = ownerName,
                        contactPhone = contactPhone,
                        upiId = upiId,
                        standardShippingDays = shippingDays,
                        shippingChargePolicy = shippingPolicy,
                        isCodEnabled = isCodEnabled,
                        codPolicy = codPolicy,
                        returnExchangePolicy = returnPolicy,
                        maxAllowedDiscountPercent = maxDiscount.toIntOrNull() ?: 0,
                        defaultLanguage = currentLang,
                        aiBackendMode = aiMode,
                        proxyEndpointUrl = proxyUrl,
                        customApiKey = customApiKey,
                        pilotNotes = pilotNotes
                    )
                    onSaveProfile(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("settings_save_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.btnSaveConfiguration, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
