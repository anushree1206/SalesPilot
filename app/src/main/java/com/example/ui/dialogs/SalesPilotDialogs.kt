package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.LeadEntity
import com.example.data.local.entity.ProductEntity
import com.example.ui.theme.*

@Composable
fun AddLeadDialog(
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, productId: String?, inquiry: String, lang: String, source: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var inquiry by remember { mutableStateOf("") }
    var selectedProductId by remember { mutableStateOf(products.firstOrNull()?.id) }
    var selectedLang by remember { mutableStateOf("en") }
    var selectedSource by remember { mutableStateOf("WhatsApp") }
    var showProductMenu by remember { mutableStateOf(false) }

    val selectedProduct = products.find { it.id == selectedProductId }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = LavenderPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Record Customer Enquiry", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SalesPilotTextPrimary)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Buyer Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_lead_name"),
                    singleLine = true,
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp Phone (+91) *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_lead_phone"),
                    singleLine = true,
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Product Picker
                Text("Inquired Item:", fontSize = 12.sp, color = SalesPilotTextMuted)
                Box {
                    OutlinedCard(
                        onClick = { showProductMenu = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color(0xFFCBD5E1))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedProduct?.name ?: "Select from catalog...",
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                                color = SalesPilotTextPrimary
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = LavenderPrimary)
                        }
                    }

                    DropdownMenu(
                        expanded = showProductMenu,
                        onDismissRequest = { showProductMenu = false }
                    ) {
                        products.forEach { prod ->
                            DropdownMenuItem(
                                text = { Text("${prod.name} (₹${prod.price.toInt()})", fontSize = 12.sp) },
                                onClick = {
                                    selectedProductId = prod.id
                                    showProductMenu = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = inquiry,
                    onValueChange = { inquiry = it },
                    label = { Text("Customer Message / Query *") },
                    placeholder = { Text("e.g. Size M available? Delivery time?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("dialog_lead_inquiry"),
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Source selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("WhatsApp", "Instagram DM", "Exhibition").forEach { src ->
                        FilterChip(
                            selected = selectedSource == src,
                            onClick = { selectedSource = src },
                            label = { Text(src, fontSize = 11.sp, fontWeight = if (selectedSource == src) FontWeight.Bold else FontWeight.Medium) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = SalesPilotTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedSource == src,
                                borderColor = LavenderCardBorder,
                                selectedBorderColor = LavenderPrimary
                            )
                        )
                    }
                }

                // Language
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("en" to "English", "hi" to "Hindi", "kn" to "Kannada").forEach { (code, label) ->
                        FilterChip(
                            selected = selectedLang == code,
                            onClick = { selectedLang = code },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (selectedLang == code) FontWeight.Bold else FontWeight.Medium) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = SalesPilotTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedLang == code,
                                borderColor = LavenderCardBorder,
                                selectedBorderColor = LavenderPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = SalesPilotTextSecondary) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                onConfirm(name, phone, selectedProductId, inquiry, selectedLang, selectedSource)
                            }
                        },
                        enabled = name.isNotBlank() && phone.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add Lead", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        sku: String,
        category: String,
        price: Double,
        salePrice: Double?,
        fabric: String,
        sizes: String,
        stock: Int,
        desc: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Sarees") }
    var priceText by remember { mutableStateOf("") }
    var salePriceText by remember { mutableStateOf("") }
    var fabric by remember { mutableStateOf("") }
    var sizes by remember { mutableStateOf("Free Size") }
    var colorVariant by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("5") }
    var desc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add Catalog Product", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SalesPilotTextPrimary)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    placeholder = { Text("e.g. Pure Mysore Silk Saree - Ruby") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU Code") },
                        placeholder = { Text("e.g. MYS-01") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price (₹) *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = salePriceText,
                        onValueChange = { salePriceText = it },
                        label = { Text("Sale Price (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fabric,
                        onValueChange = { fabric = it },
                        label = { Text("Fabric / Material *") },
                        placeholder = { Text("e.g. Pure Silk, Zari") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock Qty") },
                        modifier = Modifier.weight(0.8f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = sizes,
                        onValueChange = { sizes = it },
                        label = { Text("Sizes") },
                        placeholder = { Text("e.g. Free Size, S, M, L") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = colorVariant,
                        onValueChange = { colorVariant = it },
                        label = { Text("Color / Variant") },
                        placeholder = { Text("e.g. Crimson Maroon") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Product Description & Features") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = SalesPilotTextSecondary) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val pr = priceText.toDoubleOrNull() ?: 0.0
                            val spr = salePriceText.toDoubleOrNull()
                            val qty = stockText.toIntOrNull() ?: 0
                            if (name.isNotBlank() && pr > 0) {
                                val fullSizes = if (colorVariant.isNotBlank()) "$sizes ($colorVariant)" else sizes
                                onConfirm(name, sku, category, pr, spr, fabric, fullSizes, qty, desc)
                            }
                        },
                        enabled = name.isNotBlank() && priceText.toDoubleOrNull() != null,
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add to Catalog", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ConvertToOrderDialog(
    lead: LeadEntity,
    product: ProductEntity?,
    onDismiss: () -> Unit,
    onConfirm: (address: String, paymentMethod: String, quantity: Int) -> Unit
) {
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("UPI (GPay / PhonePe)") }
    var quantityText by remember { mutableStateOf("1") }

    val qty = quantityText.toIntOrNull() ?: 1
    val unitPrice = product?.salePrice ?: product?.price ?: lead.estimatedValue
    val shipping = if (unitPrice * qty >= 1499.0) 0.0 else 80.0
    val total = (unitPrice * qty) + shipping

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Confirm & Record Order", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SalesPilotTextPrimary)

                Surface(
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Customer: ${lead.customerName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = SalesPilotTextPrimary)
                        Text("Phone: ${lead.customerPhone}", fontSize = 12.sp, color = SalesPilotTextSecondary)
                        Text("Item: ${product?.name ?: lead.productName ?: "Boutique Item"}", fontSize = 12.sp, color = SalesPilotTextSecondary)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Order Total", fontSize = 11.sp, color = SalesPilotTextMuted)
                        Text("₹${total.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EmeraldPrimary)
                        Text(if (shipping == 0.0) "Free Shipping" else "+₹80 Shipping", fontSize = 10.sp, color = SalesPilotTextMuted)
                    }
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Delivery Address & Pincode *") },
                    placeholder = { Text("e.g. #24, 2nd Main, Indiranagar, Bangalore - 560038") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Payment Method:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SalesPilotTextPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("UPI (GPay / PhonePe)", "Advance COD", "Bank Transfer").forEach { pm ->
                        FilterChip(
                            selected = paymentMethod == pm,
                            onClick = { paymentMethod = pm },
                            label = { Text(pm, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LavenderPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = SalesPilotTextSecondary) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(address, paymentMethod, qty) },
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Generate Order", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SimulateEnquiryDialog(
    onDismiss: () -> Unit,
    onSelectScenario: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simulate Real Customer Enquiry", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Text(
                    text = "Select a realistic WhatsApp/Instagram inquiry from Indian buyers to immediately test the AI grounding, vernacular translation, and conversion flow:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ScenarioCard(
                    title = "1. Kannada Festive Saree Inquiry",
                    desc = "Divya Ramesh asking in Kannada about Crimson Kanjivaram silk stock and delivery to Mysore.",
                    onClick = {
                        onSelectScenario(1)
                        onDismiss()
                    }
                )

                ScenarioCard(
                    title = "2. Hindi Jewellery COD Inquiry",
                    desc = "Sneha Verma asking in Hindi about Kundan choker set shipping to Jaipur and COD rules.",
                    onClick = {
                        onSelectScenario(2)
                        onDismiss()
                    }
                )

                ScenarioCard(
                    title = "3. English Suit Discount Negotiation",
                    desc = "Kavitha asking in English for Chanderi Anarkali size L and discount on 2 sets for Saturday function.",
                    onClick = {
                        onSelectScenario(3)
                        onDismiss()
                    }
                )

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun ScenarioCard(title: String, desc: String, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontSize = 11.sp, color = Color.Gray, lineHeight = 15.sp)
        }
    }
}

@Composable
fun RecordPaymentDialog(
    order: com.example.data.local.entity.OrderEntity,
    onDismiss: () -> Unit,
    onConfirm: (amountReceived: Double, method: String, reference: String, notes: String) -> Unit
) {
    val remainingDue = order.balanceDue
    var amountText by remember { mutableStateOf(if (remainingDue > 0) remainingDue.toInt().toString() else "0") }
    var selectedMethod by remember { mutableStateOf(order.paymentMethod) }
    var referenceText by remember { mutableStateOf(order.paymentReference) }
    var notesText by remember { mutableStateOf(order.paymentNotes) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val methods = listOf(
        "UPI (GPay / PhonePe / Paytm)",
        "Cash on Delivery (COD Collection)",
        "Cash (In Store)",
        "Bank Transfer (NEFT/IMPS)"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, LavenderCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = LavenderPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Record Customer Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SalesPilotTextPrimary)
                }

                Text(
                    text = "Order #${order.id} • ${order.customerName}",
                    fontSize = 12.sp,
                    color = SalesPilotTextMuted
                )

                Surface(
                    color = LavenderSurfaceLight,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LavenderCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Order", fontSize = 11.sp, color = SalesPilotTextMuted)
                            Text("₹${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SalesPilotTextPrimary)
                        }
                        Column {
                            Text("Already Paid", fontSize = 11.sp, color = SalesPilotTextMuted)
                            Text("₹${order.amountPaid.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Balance Due", fontSize = 11.sp, color = SalesPilotTextMuted)
                            Text("₹${order.balanceDue.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (order.balanceDue > 0) Color(0xFFDC2626) else EmeraldPrimary)
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        validationError = null
                    },
                    label = { Text("Amount Received (₹) *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_payment_amount"),
                    singleLine = true,
                    isError = validationError != null,
                    supportingText = validationError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Payment Method:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SalesPilotTextPrimary)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    methods.forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedMethod == method,
                                onClick = { selectedMethod = method },
                                colors = RadioButtonDefaults.colors(selectedColor = LavenderPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(method, fontSize = 13.sp, color = SalesPilotTextPrimary)
                        }
                    }
                }

                OutlinedTextField(
                    value = referenceText,
                    onValueChange = { referenceText = it },
                    label = { Text("UTR / Transaction Reference (Optional)") },
                    placeholder = { Text("e.g. 428910482019 or Cash Receipt #12") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Internal Payment Notes (Optional)") },
                    placeholder = { Text("e.g. Advance deposit before dispatch") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = salesPilotTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = SalesPilotTextSecondary) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val parsed = amountText.toDoubleOrNull()
                            if (parsed == null || parsed <= 0.0) {
                                validationError = "Please enter a valid positive amount"
                            } else {
                                onConfirm(parsed, selectedMethod, referenceText.trim(), notesText.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Payment", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CollectUpiDialog(
    order: com.example.data.local.entity.OrderEntity,
    merchantProfile: com.example.data.local.entity.BusinessProfileEntity,
    upiDeepLink: String,
    onDismiss: () -> Unit,
    onCopyLink: () -> Unit,
    onShareOnWhatsApp: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.2.dp, LavenderCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Collect UPI Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SalesPilotTextPrimary)
                }

                Surface(
                    color = LavenderSurfaceLight,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LavenderCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Order #${order.id} • ${order.customerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SalesPilotTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Amount Due:", fontSize = 12.sp, color = SalesPilotTextMuted)
                            Text("₹${order.balanceDue.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = LavenderPrimary)
                        }
                    }
                }

                OutlinedCard(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LavenderCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Merchant Payee VPA:", fontSize = 11.sp, color = SalesPilotTextMuted)
                        Text(merchantProfile.upiId.ifBlank { "Not configured" }, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SalesPilotTextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Business Name:", fontSize = 11.sp, color = SalesPilotTextMuted)
                        Text(merchantProfile.businessName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = SalesPilotTextPrimary)
                    }
                }

                Text(
                    text = "Share the official payment link with the buyer. Once they transfer, manually verify the UTR transaction reference before dispatching.",
                    fontSize = 11.sp,
                    color = SalesPilotTextMuted,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCopyLink,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, LavenderCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderPrimary)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy URI", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onShareOnWhatsApp,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Done", color = SalesPilotTextSecondary)
                }
            }
        }
    }
}

@Composable
fun OnboardingDialog(
    initialProfile: com.example.data.local.entity.BusinessProfileEntity?,
    onDismiss: () -> Unit,
    onComplete: (
        businessName: String,
        ownerName: String,
        category: String,
        phone: String,
        email: String,
        shopAddress: String,
        language: String,
        upiId: String,
        accountType: String
    ) -> Unit
) {
    var businessName by remember { mutableStateOf(initialProfile?.businessName ?: "") }
    var ownerName by remember { mutableStateOf(initialProfile?.ownerName ?: "") }
    var category by remember { mutableStateOf(initialProfile?.category ?: "Ethnic Wear & Handlooms") }
    var phone by remember { mutableStateOf(initialProfile?.contactPhone ?: "") }
    var email by remember { mutableStateOf(initialProfile?.ownerEmail ?: "") }
    var shopAddress by remember { mutableStateOf(initialProfile?.shopAddress ?: "") }
    var language by remember { mutableStateOf(initialProfile?.defaultLanguage ?: "en") }
    var upiId by remember { mutableStateOf(initialProfile?.upiId ?: "") }
    var accountType by remember { mutableStateOf("LOCAL_DEVICE") }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    val categories = listOf(
        "Ethnic Wear & Handlooms",
        "Handcrafted Jewellery",
        "Kurtas & Anarkalis",
        "Footwear & Accessories",
        "Bridal Lehengas",
        "Other Retail"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFFC4B5FD)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Pinned Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Setup Your Business Profile", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = SalesPilotTextPrimary)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Configure your boutique profile to ground AI responses, generate branded invoices, and manage orders.",
                    fontSize = 12.sp,
                    color = SalesPilotTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Account Architecture Explainer
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, Color(0xFFC4B5FD))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = LavenderPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Account Architecture", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF5B21B6))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Local Device Account: Zero latency, offline-ready, 100% private data stored in your phone's Room database.\n• Free 14-day trial included without card details or auto-charges.",
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = SalesPilotTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = {
                            businessName = it
                            nameError = false
                        },
                        label = { Text("Shop / Business Name *") },
                        isError = nameError,
                        supportingText = if (nameError) { { Text("Shop name is required", color = MaterialTheme.colorScheme.error) } } else null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = ownerName,
                        onValueChange = { ownerName = it },
                        label = { Text("Owner Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            phoneError = false
                        },
                        label = { Text("WhatsApp Phone (+91) *") },
                        placeholder = { Text("+91 98450 12345") },
                        isError = phoneError,
                        supportingText = if (phoneError) { { Text("Please enter a valid phone number", color = MaterialTheme.colorScheme.error) } } else null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Business Email (Optional)") },
                        placeholder = { Text("orders@myboutique.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text("Merchant UPI ID (For Customer Payments)") },
                        placeholder = { Text("shopname@okhdfcbank") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = shopAddress,
                        onValueChange = { shopAddress = it },
                        label = { Text("Shop City & Address (Optional)") },
                        placeholder = { Text("Malleshwaram, Bangalore - 560003") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = salesPilotTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pinned Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = SalesPilotTextSecondary, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (businessName.isBlank()) {
                                nameError = true
                                return@Button
                            }
                            if (phone.isBlank()) {
                                phoneError = true
                                return@Button
                            }
                            onComplete(
                                businessName.trim(),
                                ownerName.trim().ifBlank { "Owner" },
                                category,
                                phone.trim(),
                                email.trim(),
                                shopAddress.trim(),
                                language,
                                upiId.trim(),
                                accountType
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderPrimary, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("Save & Start Trial", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
