package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BusinessProfileEntity
import com.example.data.local.entity.LeadEntity
import com.example.ui.DashboardMetrics
import com.example.ui.NavigationTab
import com.example.ui.components.SourceBadge
import com.example.ui.components.StageBadge
import com.example.ui.components.SubscriptionBanner
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    metrics: DashboardMetrics,
    recentLeads: List<LeadEntity>,
    pendingFollowUps: List<LeadEntity>,
    profile: BusinessProfileEntity? = null,
    onNavigateTab: (NavigationTab) -> Unit,
    onLeadClick: (LeadEntity) -> Unit,
    onSimulateClick: () -> Unit,
    onAddNewLeadClick: () -> Unit,
    onAddNewProductClick: () -> Unit,
    onViewPlansClick: () -> Unit = {}
) {
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // Subscription & Free Trial Status Banner
        item {
            SubscriptionBanner(
                profile = profile,
                onViewPlansClick = onViewPlansClick
            )
        }

        // Quick Action Ribbon
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(LavenderSurfaceLight, Color.White)
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(LavenderPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = LavenderPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.quickActions,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SalesPilotTextPrimary
                            )
                        }

                        TextButton(
                            onClick = onSimulateClick,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = strings.btnTestSampleDm,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = LavenderPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onAddNewLeadClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("dashboard_add_lead_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LavenderPrimary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.btnNewEnquiry, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = onAddNewProductClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("dashboard_add_product_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, LavenderCardBorder),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.AddBox, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.btnAddProduct, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LavenderPrimary)
                        }
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Text(
                text = strings.dashboardTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = strings.confirmedSales,
                    value = "₹${metrics.totalRevenue.toInt()}",
                    subtitle = "${metrics.convertedOrders} ${strings.ordersFulfilled}",
                    icon = Icons.Default.Payments,
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = strings.pipelineValue,
                    value = "₹${metrics.pipelineValue.toInt()}",
                    subtitle = "${metrics.totalLeads} Total Enquiries",
                    icon = Icons.Default.TrendingUp,
                    color = AmberSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = strings.conversionRate,
                    value = "${metrics.conversionRatePercent}%",
                    subtitle = strings.conversionRateExplanation,
                    icon = Icons.Default.Percent,
                    color = StatusNew,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = strings.followUpsDue,
                    value = "${metrics.followUpsDue}",
                    subtitle = if (metrics.followUpsOverdue > 0) "${metrics.followUpsOverdue} Overdue!" else strings.allOnTrack,
                    icon = Icons.Default.AccessTime,
                    color = if (metrics.followUpsOverdue > 0) Color(0xFFDC2626) else Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Pending Dues",
                    value = "₹${metrics.totalOutstanding.toInt()}",
                    subtitle = if (metrics.totalOutstanding > 0) "Awaiting payment" else "All orders cleared",
                    icon = Icons.Default.PendingActions,
                    color = if (metrics.totalOutstanding > 0) Color(0xFFD97706) else EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "New Enquiries",
                    value = "${metrics.newEnquiries}",
                    subtitle = "Needs seller contact",
                    icon = Icons.Default.ChatBubbleOutline,
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Follow-ups Warning Banner if overdue
        if (metrics.followUpsOverdue > 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateTab(NavigationTab.FOLLOW_UPS) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${metrics.followUpsOverdue} ${strings.overdueWarningTitle}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B),
                                fontSize = 14.sp
                            )
                            Text(
                                text = strings.overdueWarningDesc,
                                color = Color(0xFFB91C1C),
                                fontSize = 12.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }

        // Recent Inquiries & Draft Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.activeEnquiries,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { onNavigateTab(NavigationTab.PIPELINE) }) {
                    Text(strings.viewFullPipeline, fontSize = 12.sp)
                }
            }
        }

        if (recentLeads.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(strings.emptyEnquiriesTitle, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            strings.emptyEnquiriesSubtitle,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(recentLeads.take(4)) { lead ->
                LeadCardItem(
                    lead = lead,
                    onOpenCopilot = { onLeadClick(lead) }
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1))
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = SalesPilotTextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = SalesPilotTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LeadCardItem(
    lead: LeadEntity,
    onOpenCopilot: () -> Unit
) {
    val strings = LocalAppStrings.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenCopilot() }
            .testTag("lead_item_${lead.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lead.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SalesPilotTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SourceBadge(source = lead.source)
                }

                StageBadge(stage = lead.stage)
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!lead.productName.isNullOrBlank()) {
                Surface(
                    color = LavenderPrimaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🛍️ ${lead.productName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LavenderOnPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = "\"${lead.inquiryText}\"",
                fontSize = 12.sp,
                color = SalesPilotTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${strings.estValueLabel}: ₹${lead.estimatedValue.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LavenderPrimary
                )

                Button(
                    onClick = onOpenCopilot,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LavenderPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (lead.lastAiDraftReply != null) "View Draft" else strings.btnAiCopilot,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
