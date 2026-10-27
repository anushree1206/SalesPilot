package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.data.local.entity.LeadEntity
import com.example.data.local.entity.LeadStage
import com.example.ui.components.SourceBadge
import com.example.ui.components.StageBadge
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*

@Composable
fun PipelineScreen(
    leads: List<LeadEntity>,
    onLeadSelectedForCopilot: (LeadEntity) -> Unit,
    onUpdateStage: (String, LeadStage) -> Unit,
    onConvertToOrderClick: (LeadEntity) -> Unit,
    onAddNewLeadClick: () -> Unit,
    onDirectWhatsAppClick: (phone: String, message: String) -> Unit
) {
    val strings = LocalAppStrings.current
    var selectedFilterStage by remember { mutableStateOf<LeadStage?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLeads = remember(leads, selectedFilterStage, searchQuery) {
        leads.filter { lead ->
            val matchesStage = selectedFilterStage == null || lead.stage == selectedFilterStage
            val matchesSearch = searchQuery.isBlank() ||
                    lead.customerName.contains(searchQuery, ignoreCase = true) ||
                    lead.customerPhone.contains(searchQuery) ||
                    (lead.productName?.contains(searchQuery, ignoreCase = true) == true)
            matchesStage && matchesSearch
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewLeadClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("pipeline_fab_add_lead")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Lead")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pipeline_search_field"),
                placeholder = { Text(strings.searchLeadsPlaceholder, color = SalesPilotTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LavenderPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SalesPilotTextMuted)
                        }
                    }
                },
                colors = salesPilotTextFieldColors(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Stage Filters Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isAllSelected = selectedFilterStage == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { selectedFilterStage = null },
                        label = { Text("${strings.allFilter} (${leads.size})", fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LavenderPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = SalesPilotTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isAllSelected,
                            borderColor = if (isAllSelected) LavenderPrimary else LavenderCardBorder
                        )
                    )
                }
                items(LeadStage.values()) { stage ->
                    val count = leads.count { it.stage == stage }
                    val isStageSelected = selectedFilterStage == stage
                    FilterChip(
                        selected = isStageSelected,
                        onClick = { selectedFilterStage = stage },
                        label = { Text("${stage.displayName} ($count)", fontWeight = if (isStageSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LavenderPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = SalesPilotTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isStageSelected,
                            borderColor = if (isStageSelected) LavenderPrimary else LavenderCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredLeads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FilterListOff,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = strings.noLeadsInStage,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.noLeadsSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredLeads, key = { it.id }) { lead ->
                        PipelineLeadCard(
                            lead = lead,
                            onOpenCopilot = { onLeadSelectedForCopilot(lead) },
                            onStageChange = { newStage -> onUpdateStage(lead.id, newStage) },
                            onConvertToOrder = { onConvertToOrderClick(lead) },
                            onSendWhatsApp = {
                                val replyText = lead.lastAiDraftReply
                                    ?: "Hello ${lead.customerName}! Thank you for reaching out regarding ${lead.productName ?: "our boutique collection"}. How can we assist you today?"
                                onDirectWhatsAppClick(lead.customerPhone, replyText)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PipelineLeadCard(
    lead: LeadEntity,
    onOpenCopilot: () -> Unit,
    onStageChange: (LeadStage) -> Unit,
    onConvertToOrder: () -> Unit,
    onSendWhatsApp: () -> Unit
) {
    val strings = LocalAppStrings.current
    var showStageMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pipeline_card_${lead.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = lead.customerName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SalesPilotTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        SourceBadge(source = lead.source)
                    }
                    Text(
                        text = lead.customerPhone,
                        fontSize = 12.sp,
                        color = SalesPilotTextMuted
                    )
                }

                // Stage Changer Dropdown
                Box {
                    AssistChip(
                        onClick = { showStageMenu = true },
                        label = { Text(lead.stage.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LavenderPrimary) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = LavenderPrimary) },
                        shape = RoundedCornerShape(10.dp),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = LavenderCardBorder)
                    )

                    DropdownMenu(
                        expanded = showStageMenu,
                        onDismissRequest = { showStageMenu = false }
                    ) {
                        LeadStage.values().forEach { stage ->
                            DropdownMenuItem(
                                text = { Text(stage.displayName) },
                                onClick = {
                                    onStageChange(stage)
                                    showStageMenu = false
                                },
                                leadingIcon = {
                                    if (lead.stage == stage) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = LavenderPrimary)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Inquired Item
            if (!lead.productName.isNullOrBlank()) {
                Surface(
                    color = LavenderPrimaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛍️ ${lead.productName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LavenderOnPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Est. ₹${lead.estimatedValue.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LavenderPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Customer Inquiry Bubble
            Surface(
                color = LavenderSurfaceLight,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${lead.inquiryText}\"",
                    fontSize = 12.sp,
                    color = SalesPilotTextSecondary,
                    modifier = Modifier.padding(10.dp),
                    lineHeight = 16.sp
                )
            }

            // Follow-up indicator if set
            if (lead.followUpDate != null && !lead.isFollowUpCompleted) {
                Spacer(modifier = Modifier.height(6.dp))
                val isOverdue = lead.followUpDate < System.currentTimeMillis()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = if (isOverdue) Color(0xFFDC2626) else LavenderPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOverdue) "Overdue follow-up!" else "Follow-up scheduled",
                        fontSize = 11.sp,
                        color = if (isOverdue) Color(0xFFDC2626) else LavenderPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    if (!lead.followUpNote.isNullOrBlank()) {
                        Text(
                            text = " • ${lead.followUpNote}",
                            fontSize = 11.sp,
                            color = SalesPilotTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Ribbon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenCopilot,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderPrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.btnAiCopilot, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onSendWhatsApp,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = WhatsAppGreen.copy(alpha = 0.15f),
                        contentColor = Color(0xFF0F5132)
                    )
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.btnChat, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                if (lead.stage != LeadStage.ORDER_PLACED) {
                    OutlinedButton(
                        onClick = onConvertToOrder,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, LavenderCardBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.btnOrder, fontSize = 12.sp, color = LavenderPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
