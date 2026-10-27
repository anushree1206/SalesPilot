package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val businessId: String = "primary_business_profile",
    val name: String,
    val sku: String,
    val category: String, // "Sarees", "Jewellery", "Kurtas", "Lehengas", "Dupattas"
    val price: Double,
    val salePrice: Double? = null,
    val description: String,
    val fabricOrMaterial: String,
    val availableSizes: String, // e.g. "Free Size", "S, M, L, XL"
    val stockQuantity: Int,
    val isAvailable: Boolean = true,
    val tags: String = "", // e.g. "Bridal, Festive, Pure Silk"
    val careInstructions: String = "Dry clean only. Store wrapped in muslin cloth.",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
