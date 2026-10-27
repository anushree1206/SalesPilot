package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.LeadStage
import com.example.data.local.entity.OrderStatus
import com.example.ui.theme.*

@Composable
fun StageBadge(stage: LeadStage, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (stage) {
        LeadStage.NEW_ENQUIRY -> StatusNew.copy(alpha = 0.15f) to StatusNew
        LeadStage.CONTACTED -> StatusContacted.copy(alpha = 0.15f) to StatusContacted
        LeadStage.INTERESTED -> StatusInterested.copy(alpha = 0.15f) to StatusInterested
        LeadStage.FOLLOW_UP_DUE -> StatusFollowUp.copy(alpha = 0.15f) to StatusFollowUp
        LeadStage.ORDER_PLACED -> StatusConverted.copy(alpha = 0.15f) to StatusConverted
        LeadStage.CLOSED_LOST -> StatusClosed.copy(alpha = 0.15f) to StatusClosed
    }

    Surface(
        modifier = modifier.testTag("stage_badge_${stage.name}"),
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = stage.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.DRAFT -> Color.Gray.copy(alpha = 0.15f) to Color.DarkGray
        OrderStatus.CONFIRMED -> StatusNew.copy(alpha = 0.15f) to StatusNew
        OrderStatus.PROCESSING -> StatusInterested.copy(alpha = 0.15f) to StatusInterested
        OrderStatus.SHIPPED -> StatusContacted.copy(alpha = 0.15f) to StatusContacted
        OrderStatus.DELIVERED -> StatusConverted.copy(alpha = 0.15f) to StatusConverted
        OrderStatus.CANCELLED -> StatusClosed.copy(alpha = 0.15f) to StatusClosed
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = status.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SourceBadge(source: String, modifier: Modifier = Modifier) {
    val isWhatsApp = source.contains("WhatsApp", ignoreCase = true)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isWhatsApp) WhatsAppGreen.copy(alpha = 0.15f) else Color(0xFFE1306C).copy(alpha = 0.15f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = if (isWhatsApp) Icons.Default.Chat else Icons.Default.CameraAlt,
                contentDescription = source,
                tint = if (isWhatsApp) WhatsAppGreen else Color(0xFFE1306C),
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = source,
                color = if (isWhatsApp) Color(0xFF0F5132) else Color(0xFF8B004F),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StockBadge(quantity: Int, isAvailable: Boolean, modifier: Modifier = Modifier) {
    val inStock = isAvailable && quantity > 0
    val bgColor = if (inStock) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
    val textColor = if (inStock) Color(0xFF065F46) else Color(0xFF991B1B)
    val text = if (inStock) "$quantity in stock" else "Out of Stock"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
