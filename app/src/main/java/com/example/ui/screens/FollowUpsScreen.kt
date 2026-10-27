package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LeadEntity
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FollowUpsScreen(
    followUps: List<LeadEntity>,
    onCompleteFollowUp: (leadId: String) -> Unit,
    onSendWhatsAppReminder: (lead: LeadEntity) -> Unit,
    onOpenInCopilot: (LeadEntity) -> Unit,
    onConvertToOrder: (LeadEntity) -> Unit
) {
    val strings = LocalAppStrings.current
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Overdue", "Today", "Upcoming"
    val now = System.currentTimeMillis()

    val filteredList = remember(followUps, selectedFilter) {
        when (selectedFilter) {
            "Overdue" -> followUps.filter { it.followUpDate != null && it.followUpDate < now && !it.isFollowUpCompleted }
            "Today" -> followUps.filter {
                val date = it.followUpDate ?: 0L
                date >= now && date < (now + 86400000L) && !it.isFollowUpCompleted
            }
            "Upcoming" -> followUps.filter {
                val date = it.followUpDate ?: 0L
                date >= (now + 86400000L) && !it.isFollowUpCompleted
            }
            else -> followUps.filter { !it.isFollowUpCompleted }
        }
    }

    val overdueCount = remember(followUps) {
        followUps.count { it.followUpDate != null && it.followUpDate < now && !it.isFollowUpCompleted }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Overdue warning banner
        if (overdueCount > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AlarmOff,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "$overdueCount ${strings.followUpsOverdueAlert}",
                        color = Color(0xFF991B1B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val filters = listOf(
                "All" to "${strings.allFilter} (${followUps.count { !it.isFollowUpCompleted }})",
                "Overdue" to "${strings.filterOverdue} ($overdueCount)",
                "Today" to strings.filterToday,
                "Upcoming" to strings.filterUpcoming
            )
            items(filters) { (key, label) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key },
                    label = { Text(label, fontSize = 12.sp, fontWeight = if (selectedFilter == key) FontWeight.Bold else FontWeight.Medium) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LavenderPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = SalesPilotTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == key,
                        borderColor = LavenderCardBorder,
                        selectedBorderColor = LavenderPrimary,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.2.dp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.allFollowUpsDoneTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.allFollowUpsDoneSubtitle,
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
                items(filteredList, key = { it.id }) { lead ->
                    FollowUpCardItem(
                        lead = lead,
                        onComplete = { onCompleteFollowUp(lead.id) },
                        onSendReminder = { onSendWhatsAppReminder(lead) },
                        onOpenCopilot = { onOpenInCopilot(lead) },
                        onConvertToOrder = { onConvertToOrder(lead) }
                    )
                }
            }
        }
    }
}

@Composable
fun FollowUpCardItem(
    lead: LeadEntity,
    onComplete: () -> Unit,
    onSendReminder: () -> Unit,
    onOpenCopilot: () -> Unit,
    onConvertToOrder: () -> Unit
) {
    val strings = LocalAppStrings.current
    val now = System.currentTimeMillis()
    val isOverdue = lead.followUpDate != null && lead.followUpDate < now
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("follow_up_card_${lead.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.2.dp, LavenderCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Customer & Due status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = lead.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SalesPilotTextPrimary
                    )
                    Text(
                        text = lead.customerPhone,
                        fontSize = 12.sp,
                        color = SalesPilotTextMuted
                    )
                }

                Surface(
                    color = if (isOverdue) Color(0xFFFEF2F2) else LavenderPrimaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isOverdue) Color(0xFFFECACA) else LavenderCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isOverdue) Icons.Default.ErrorOutline else Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (isOverdue) Color(0xFFDC2626) else LavenderPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isOverdue) strings.filterOverdue else "Scheduled",
                            color = if (isOverdue) Color(0xFFDC2626) else LavenderPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Item inquired
            if (!lead.productName.isNullOrBlank()) {
                Text(
                    text = "🛍️ ${lead.productName} (Est. ₹${lead.estimatedValue.toInt()})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Note
            if (!lead.followUpNote.isNullOrBlank()) {
                Surface(
                    color = LavenderSurfaceLight,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, LavenderCardBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 ${lead.followUpNote}",
                        fontSize = 11.sp,
                        color = SalesPilotTextSecondary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            lead.followUpDate?.let { date ->
                Text(
                    text = "Target: ${dateFormat.format(Date(date))}",
                    fontSize = 11.sp,
                    color = if (isOverdue) Color(0xFFDC2626) else SalesPilotTextMuted,
                    fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSendReminder,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.btnWhatsAppReminder, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onComplete,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.2.dp, LavenderCardBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(0.8f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.btnDone, fontSize = 11.sp, color = LavenderPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
