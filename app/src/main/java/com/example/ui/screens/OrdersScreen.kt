package com.example.ui.screens

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
import androidx.compose.foundation.background
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.OrderStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    orders: List<OrderEntity>,
    onUpdateOrderStatus: (orderId: String, status: OrderStatus) -> Unit,
    onUpdatePaymentStatus: (orderId: String, isPaid: Boolean) -> Unit,
    onRecordOrderPayment: (orderId: String, amountReceived: Double, method: String, reference: String, notes: String) -> Unit = { _, _, _, _, _ -> },
    onCollectUpiPayment: (OrderEntity) -> Unit = {},
    onShareInvoiceOnWhatsApp: (OrderEntity) -> Unit
) {
    val strings = LocalAppStrings.current
    var selectedFilterStatus by remember { mutableStateOf<OrderStatus?>(null) }
    var recordingPaymentOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val filteredOrders = remember(orders, selectedFilterStatus) {
        if (selectedFilterStatus == null) orders else orders.filter { it.status == selectedFilterStatus }
    }

    val totalRevenue = remember(orders) {
        orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalAmount }
    }

    val totalOutstanding = remember(orders) {
        orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.balanceDue }
    }

    if (recordingPaymentOrder != null) {
        com.example.ui.dialogs.RecordPaymentDialog(
            order = recordingPaymentOrder!!,
            onDismiss = { recordingPaymentOrder = null },
            onConfirm = { amount, method, ref, notes ->
                onRecordOrderPayment(recordingPaymentOrder!!.id, amount, method, ref, notes)
                recordingPaymentOrder = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Summary Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(LavenderSurfaceLight, Color.White)
                        )
                    )
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.ordersHeaderTotal, fontSize = 11.sp, color = SalesPilotTextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${orders.size} Orders", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SalesPilotTextPrimary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Revenue", fontSize = 11.sp, color = SalesPilotTextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("₹${totalRevenue.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = LavenderPrimary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Pending Dues", fontSize = 11.sp, color = SalesPilotTextSecondary, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("₹${totalOutstanding.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = if (totalOutstanding > 0) Color(0xFFDC2626) else EmeraldPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val isAllSelected = selectedFilterStatus == null
                FilterChip(
                    selected = isAllSelected,
                    onClick = { selectedFilterStatus = null },
                    label = { Text("${strings.allFilter} (${orders.size})", fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium) },
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
            items(OrderStatus.values()) { status ->
                val count = orders.count { it.status == status }
                val isStatusSelected = selectedFilterStatus == status
                FilterChip(
                    selected = isStatusSelected,
                    onClick = { selectedFilterStatus = status },
                    label = { Text("${status.displayName} ($count)", fontWeight = if (isStatusSelected) FontWeight.Bold else FontWeight.Medium) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LavenderPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = SalesPilotTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isStatusSelected,
                        borderColor = if (isStatusSelected) LavenderPrimary else LavenderCardBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.noOrdersTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.noOrdersSubtitle,
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
                items(filteredOrders, key = { it.id }) { order ->
                    OrderCardItem(
                        order = order,
                        onStatusChange = { newStatus -> onUpdateOrderStatus(order.id, newStatus) },
                        onRecordPaymentClick = { recordingPaymentOrder = order },
                        onCollectUpiClick = { onCollectUpiPayment(order) },
                        onShareInvoice = { onShareInvoiceOnWhatsApp(order) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(
    order: OrderEntity,
    onStatusChange: (OrderStatus) -> Unit,
    onRecordPaymentClick: () -> Unit,
    onCollectUpiClick: () -> Unit,
    onShareInvoice: () -> Unit
) {
    val strings = LocalAppStrings.current
    var showStatusDropdown by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.id,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SalesPilotTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${dateFormat.format(Date(order.createdAt))}",
                        fontSize = 11.sp,
                        color = SalesPilotTextMuted
                    )
                }

                Box {
                    AssistChip(
                        onClick = { showStatusDropdown = true },
                        label = { Text(order.status.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LavenderPrimary) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = LavenderPrimary) },
                        shape = RoundedCornerShape(10.dp),
                        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = LavenderCardBorder)
                    )

                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        OrderStatus.values().forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.displayName) },
                                onClick = {
                                    onStatusChange(st)
                                    showStatusDropdown = false
                                },
                                leadingIcon = {
                                    if (order.status == st) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = LavenderPrimary)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Customer details
            Text(
                text = "${order.customerName} (${order.customerPhone})",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = SalesPilotTextPrimary
            )

            if (order.deliveryAddress.isNotBlank()) {
                Text(
                    text = "📍 ${order.deliveryAddress}",
                    fontSize = 11.sp,
                    color = SalesPilotTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product & Calculation Breakdown
            Surface(
                color = LavenderSurfaceLight,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${order.quantity}x ${order.productName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SalesPilotTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "₹${(order.unitPrice * order.quantity).toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SalesPilotTextPrimary
                        )
                    }

                    if (order.shippingFee > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shipping Charge", fontSize = 11.sp, color = SalesPilotTextMuted)
                            Text("+₹${order.shippingFee.toInt()}", fontSize = 11.sp, color = SalesPilotTextMuted)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = LavenderCardBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SalesPilotTextPrimary)
                        Text(
                            "₹${order.totalAmount.toInt()}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = LavenderPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Payment Breakdown Card
            Surface(
                color = when (order.paymentStatus) {
                    com.example.data.local.entity.OrderPaymentStatus.PAID -> Color(0xFFF0FDF4)
                    com.example.data.local.entity.OrderPaymentStatus.PARTIALLY_PAID -> Color(0xFFFFFBEB)
                    else -> Color(0xFFFEF2F2)
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (order.paymentStatus) {
                                    com.example.data.local.entity.OrderPaymentStatus.PAID -> Icons.Default.CheckCircle
                                    com.example.data.local.entity.OrderPaymentStatus.PARTIALLY_PAID -> Icons.Default.HourglassBottom
                                    else -> Icons.Default.ErrorOutline
                                },
                                contentDescription = null,
                                tint = when (order.paymentStatus) {
                                    com.example.data.local.entity.OrderPaymentStatus.PAID -> EmeraldPrimary
                                    com.example.data.local.entity.OrderPaymentStatus.PARTIALLY_PAID -> Color(0xFFD97706)
                                    else -> Color(0xFFDC2626)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Payment: ${order.paymentStatus.displayName} (${order.paymentMethod})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = when (order.paymentStatus) {
                                    com.example.data.local.entity.OrderPaymentStatus.PAID -> Color(0xFF166534)
                                    com.example.data.local.entity.OrderPaymentStatus.PARTIALLY_PAID -> Color(0xFF92400E)
                                    else -> Color(0xFF991B1B)
                                }
                            )
                        }

                        Text(
                            text = if (order.balanceDue > 0) "Due: ₹${order.balanceDue.toInt()}" else "Settled",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (order.balanceDue > 0) Color(0xFFDC2626) else EmeraldPrimary
                        )
                    }

                    if (order.amountPaid > 0 && order.paymentStatus != com.example.data.local.entity.OrderPaymentStatus.PAID) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Received: ₹${order.amountPaid.toInt()} • Remaining: ₹${order.balanceDue.toInt()}",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }

                    if (order.paymentReference.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ref: ${order.paymentReference}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRecordPaymentClick,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, LavenderCardBorder),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Record Pay", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LavenderPrimary)
                }

                if (order.balanceDue > 0) {
                    Button(
                        onClick = onCollectUpiClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Collect UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                FilledTonalButton(
                    onClick = onShareInvoice,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = WhatsAppGreen.copy(alpha = 0.15f),
                        contentColor = Color(0xFF0F5132)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invoice", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

