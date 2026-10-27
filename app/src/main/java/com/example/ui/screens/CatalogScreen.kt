package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.local.entity.ProductEntity
import com.example.ui.components.StockBadge
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.*

@Composable
fun CatalogScreen(
    products: List<ProductEntity>,
    onAddProductClick: () -> Unit,
    onUpdateProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onShareProductOnWhatsApp: (ProductEntity) -> Unit
) {
    val strings = LocalAppStrings.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    val categories = remember(products, strings.allFilter) {
        listOf(strings.allFilter) + products.map { it.category }.distinct()
    }

    val filteredProducts = remember(products, searchQuery, selectedCategory, strings.allFilter) {
        products.filter { p ->
            val matchesCategory = selectedCategory == null || selectedCategory == strings.allFilter || p.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    p.name.contains(searchQuery, ignoreCase = true) ||
                    p.sku.contains(searchQuery, ignoreCase = true) ||
                    p.fabricOrMaterial.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProductClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("catalog_fab_add_product")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_field"),
                placeholder = { Text(strings.searchCatalogPlaceholder, color = SalesPilotTextMuted) },
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

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = (cat == strings.allFilter && selectedCategory == null) || selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedCategory = if (cat == strings.allFilter) null else cat
                        },
                        label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LavenderPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = SalesPilotTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) LavenderPrimary else LavenderCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = strings.noProductsTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.noProductsSubtitle,
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
                    items(filteredProducts, key = { it.id }) { product ->
                        ProductCardItem(
                            product = product,
                            onStockIncrement = {
                                onUpdateProduct(product.copy(stockQuantity = product.stockQuantity + 1, isAvailable = true))
                            },
                            onStockDecrement = {
                                val newQty = maxOf(0, product.stockQuantity - 1)
                                onUpdateProduct(product.copy(stockQuantity = newQty, isAvailable = newQty > 0))
                            },
                            onShare = { onShareProductOnWhatsApp(product) },
                            onDelete = { productToDelete = product }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    productToDelete?.let { prod ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text(strings.deleteProductDialogTitle) },
            text = { Text("\"${prod.name}\"\n\n${strings.deleteProductDialogDesc}") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(prod)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.btnConfirmDelete)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text(strings.btnCancel)
                }
            }
        )
    }
}

@Composable
fun ProductCardItem(
    product: ProductEntity,
    onStockIncrement: () -> Unit,
    onStockDecrement: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderCardBorder.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Category, SKU, and Menu
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
                            text = product.category,
                            color = LavenderOnPrimaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SKU: ${product.sku}",
                        fontSize = 11.sp,
                        color = SalesPilotTextMuted
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = SalesPilotTextMuted)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(strings.btnWhatsAppCard) },
                            onClick = {
                                onShare()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = WhatsAppGreen) }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.btnDelete, color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = SalesPilotTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Fabric & Sizes
            Text(
                text = "✨ ${product.fabricOrMaterial} • Sizes: ${product.availableSizes}",
                fontSize = 12.sp,
                color = SalesPilotTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pricing & Stock row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.salePrice != null) {
                        Text(
                            text = "₹${product.salePrice.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = LavenderPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontSize = 13.sp,
                            color = SalesPilotTextMuted,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    } else {
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = LavenderPrimary
                        )
                    }
                }

                StockBadge(quantity = product.stockQuantity, isAvailable = product.isAvailable)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock Quick Toggler & Share Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock counter controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(LavenderSurfaceLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = strings.stockQtyLabel,
                        fontSize = 11.sp,
                        color = SalesPilotTextSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    IconButton(
                        onClick = onStockDecrement,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = LavenderPrimary, modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = "${product.stockQuantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SalesPilotTextPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(
                        onClick = onStockIncrement,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = LavenderPrimary, modifier = Modifier.size(14.dp))
                    }
                }

                Button(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.btnWhatsAppCard, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
