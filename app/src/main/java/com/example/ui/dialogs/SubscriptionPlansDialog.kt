package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.BusinessProfileEntity
import com.example.domain.subscription.PlanDefinition
import com.example.domain.subscription.SubscriptionCatalog
import com.example.domain.subscription.SubscriptionManager
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*

@Composable
fun SubscriptionPlansDialog(
    profile: BusinessProfileEntity?,
    onDismiss: () -> Unit,
    onSelectPlan: (planId: String) -> Unit,
    onRequestPilotActivationWhatsApp: (plan: PlanDefinition, shopName: String?) -> Unit
) {
    val strings = LocalAppStrings.current
    val now = System.currentTimeMillis()
    val remainingDays = SubscriptionManager.calculateDaysRemaining(profile?.trialEndDate ?: (now + SubscriptionManager.TRIAL_DURATION_MS), now)
    var selectedPlanId by remember { mutableStateOf(profile?.selectedPlan?.takeIf { it != "FREE_TRIAL" } ?: SubscriptionCatalog.STARTER.id) }
    var showActivationRequestedNotice by remember { mutableStateOf(false) }

    val currentSelectedPlan = SubscriptionCatalog.getPlanById(selectedPlanId)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("subscription_plans_dialog"),
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            border = BorderStroke(1.2.dp, LavenderCardBorder),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = LavenderPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.plansModalTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SalesPilotTextPrimary
                            )
                        }
                        Text(
                            text = strings.plansModalSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = SalesPilotTextMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = SalesPilotTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Separation & Zero Auto-Billing Disclaimers
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = LavenderSurfaceLight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LavenderCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = LavenderPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.merchantPaymentSeparationNotice,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = SalesPilotTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Plan Cards List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(SubscriptionCatalog.ALL_PLANS) { plan ->
                        PlanCard(
                            plan = plan,
                            isSelected = selectedPlanId == plan.id,
                            isCurrentlyActive = (profile?.selectedPlan == plan.id),
                            remainingTrialDays = remainingDays,
                            onSelect = { selectedPlanId = plan.id }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Plan Summary & Manual Payment Verification Note
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = LavenderSurfaceLight
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, LavenderCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Selected: ${currentSelectedPlan.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SalesPilotTextPrimary
                            )
                            Text(
                                text = if (currentSelectedPlan.priceInr == 0) "Free" else "₹${currentSelectedPlan.priceInr}/month",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = LavenderPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Explicit verification note required by user
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = LavenderPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.paymentNoteVerification,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = SalesPilotTextSecondary
                            )
                        }
                    }
                }

                if (showActivationRequestedNotice) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LavenderPrimaryContainer),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, LavenderCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✓ Status: Payment Pending Verification. WhatsApp opened for ${currentSelectedPlan.name}. Access will be activated upon verification.",
                            fontSize = 11.sp,
                            color = LavenderOnPrimaryContainer,
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSelectPlan(selectedPlanId)
                            showActivationRequestedNotice = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, LavenderCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary)
                    ) {
                        Text("Save Selection", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            onSelectPlan(selectedPlanId)
                            onRequestPilotActivationWhatsApp(
                                currentSelectedPlan,
                                profile?.businessName
                            )
                            showActivationRequestedNotice = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("subscribe_whatsapp_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.btnSubscribeViaWhatsApp,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlanCard(
    plan: PlanDefinition,
    isSelected: Boolean,
    isCurrentlyActive: Boolean,
    remainingTrialDays: Int,
    onSelect: () -> Unit
) {
    val strings = LocalAppStrings.current
    val borderColor = if (isSelected) LavenderPrimary else LavenderCardBorder

    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LavenderSurfaceLight else Color.White
        ),
        border = BorderStroke(if (isSelected) 2.dp else 1.2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Plan Title & Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SalesPilotTextPrimary
                        )
                        if (isCurrentlyActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(containerColor = LavenderPrimary) {
                                Text("Current", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                    if (plan.isProposedLaunchPrice) {
                        Text(
                            text = strings.planProposedNotice,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = LavenderPrimary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (plan.priceInr == 0) "Free" else "₹${plan.priceInr}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = LavenderPrimary
                    )
                    Text(
                        text = plan.billingCycle,
                        fontSize = 10.sp,
                        color = SalesPilotTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = plan.subtitle,
                fontSize = 12.sp,
                color = SalesPilotTextSecondary,
                lineHeight = 16.sp
            )

            if (plan.id == "FREE_TRIAL" && remainingTrialDays > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⏳ $remainingTrialDays ${strings.subscriptionDaysRemaining}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LavenderPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = LavenderCardBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(8.dp))

            // Features Checklist
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                plan.features.forEach { feature ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feature,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = SalesPilotTextPrimary
                        )
                    }
                }
            }
        }
    }
}
