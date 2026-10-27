package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.LeadEntity
import com.example.data.local.entity.ProductEntity
import com.example.domain.ai.CopilotDraftResult
import com.example.domain.ai.ReplyIntent
import com.example.ui.components.StockBadge
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*

@Composable
fun AiCopilotScreen(
    profile: BusinessProfileEntity?,
    products: List<ProductEntity>,
    targetLead: LeadEntity?,
    initialProduct: ProductEntity?,
    initialInquiry: String,
    initialCustomerName: String,
    draftResult: CopilotDraftResult?,
    isGenerating: Boolean,
    currentLanguage: String,
    onGenerateClick: (intent: ReplyIntent?, lang: String) -> Unit,
    onProductSelected: (ProductEntity?) -> Unit,
    onInquiryTextChanged: (String) -> Unit,
    onCustomerNameChanged: (String) -> Unit,
    onSendWhatsApp: (phone: String, text: String) -> Unit,
    onConvertToOrderClick: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current

    var selectedLanguage by remember(currentLanguage) { mutableStateOf(currentLanguage) }
    var selectedIntent by remember { mutableStateOf<ReplyIntent?>(null) }
    var showProductDropdown by remember { mutableStateOf(false) }
    var editableDraftText by remember(draftResult?.draftReply) {
        mutableStateOf(draftResult?.draftReply ?: "")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // AI Guardrail Assurance Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(LavenderSurfaceLight, Color.White)
                            )
                        )
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(LavenderPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = LavenderPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.copilotGuardrailBanner,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SalesPilotTextPrimary
                    )
                }
            }
        }

        // Customer & Context Inputs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = strings.stepCustomerContext,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SalesPilotTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = initialCustomerName,
                        onValueChange = onCustomerNameChanged,
                        label = { Text(strings.labelCustomerName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copilot_customer_name"),
                        colors = salesPilotTextFieldColors(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Product Selector
                    Text(
                        text = strings.labelReferencedProduct,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SalesPilotTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box {
                        OutlinedCard(
                            onClick = { showProductDropdown = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("copilot_product_picker"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder),
                            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = initialProduct?.name ?: strings.selectProductPrompt,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (initialProduct != null) {
                                        Text(
                                            text = "₹${(initialProduct.salePrice ?: initialProduct.price).toInt()} • ${initialProduct.fabricOrMaterial}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                if (initialProduct != null) {
                                    StockBadge(quantity = initialProduct.stockQuantity, isAvailable = initialProduct.isAvailable)
                                } else {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showProductDropdown,
                            onDismissRequest = { showProductDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            products.forEach { prod ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(prod.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text(
                                                "₹${(prod.salePrice ?: prod.price).toInt()} • ${prod.stockQuantity} in stock",
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    },
                                    onClick = {
                                        onProductSelected(prod)
                                        showProductDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Raw Incoming Message Text
                    OutlinedTextField(
                        value = initialInquiry,
                        onValueChange = onInquiryTextChanged,
                        label = { Text(strings.labelInquiryMessage) },
                        placeholder = { Text(strings.inquiryPlaceholder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("copilot_inquiry_input"),
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Language & Intent Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = strings.stepGoalAndLanguage,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Language Chips
                    Text(
                        text = strings.labelTargetLanguage,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "en" to "English",
                            "hi" to "हिंदी (Hindi)",
                            "kn" to "ಕನ್ನಡ (Kannada)"
                        ).forEach { (code, label) ->
                            val isLangSelected = selectedLanguage == code
                            FilterChip(
                                selected = isLangSelected,
                                onClick = {
                                    selectedLanguage = code
                                    onGenerateClick(selectedIntent, code)
                                },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (isLangSelected) FontWeight.Bold else FontWeight.Medium) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LavenderPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = SalesPilotTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isLangSelected,
                                    borderColor = if (isLangSelected) LavenderPrimary else LavenderCardBorder
                                ),
                                modifier = Modifier.testTag("lang_chip_$code")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Intent Chips
                    Text(
                        text = strings.labelReplyFocus,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SalesPilotTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ReplyIntent.values()) { intent ->
                            val isIntentSelected = selectedIntent == intent
                            FilterChip(
                                selected = isIntentSelected,
                                onClick = {
                                    selectedIntent = if (selectedIntent == intent) null else intent
                                    onGenerateClick(selectedIntent, selectedLanguage)
                                },
                                label = { Text(intent.label, fontSize = 11.sp, fontWeight = if (isIntentSelected) FontWeight.Bold else FontWeight.Medium) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LavenderPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = SalesPilotTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isIntentSelected,
                                    borderColor = if (isIntentSelected) LavenderPrimary else LavenderCardBorder
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onGenerateClick(selectedIntent, selectedLanguage) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("generate_ai_reply_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LavenderPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.btnGenerating, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.btnGenerateGroundedReply, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Grounded Audit & Draft Output
        if (draftResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder.copy(alpha = 0.8f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = LavenderPrimaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (draftResult.aiProviderUsed.contains("Gemini")) "✨ ${draftResult.aiProviderUsed.uppercase()}" else strings.badgeAiDrafted,
                                        color = LavenderPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Goal: ${draftResult.detectedIntent.label}",
                                    fontSize = 11.sp,
                                    color = SalesPilotTextMuted
                                )
                            }

                            Text(
                                text = "Editable",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LavenderPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Verified Facts Card
                        Surface(
                            color = LavenderSurfaceLight,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = strings.verifiedGroundingTitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SalesPilotTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                draftResult.groundedFactsUsed.take(3).forEach { fact ->
                                    Text(
                                        text = "• $fact",
                                        fontSize = 11.sp,
                                        color = SalesPilotTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Editable Draft Box
                        OutlinedTextField(
                            value = editableDraftText,
                            onValueChange = { editableDraftText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .testTag("copilot_draft_text_field"),
                            colors = salesPilotTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${strings.nextActionLabel} ${draftResult.suggestedAction}",
                            fontSize = 11.sp,
                            color = AmberSecondary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: WhatsApp & Copy & Convert
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val phone = targetLead?.customerPhone ?: "+919845012345"
                                    onSendWhatsApp(phone, editableDraftText)
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp)
                                    .testTag("copilot_send_whatsapp_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.btnSendOnWhatsApp, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Copilot Reply", editableDraftText))
                                    onShowMessage("Reply copied to clipboard!")
                                },
                                modifier = Modifier
                                    .weight(0.7f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, LavenderCardBorder)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.btnCopy, fontSize = 12.sp)
                            }
                        }

                        if (targetLead != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            FilledTonalButton(
                                onClick = onConvertToOrderClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("copilot_convert_order_btn"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.btnConvertLeadToOrder, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
