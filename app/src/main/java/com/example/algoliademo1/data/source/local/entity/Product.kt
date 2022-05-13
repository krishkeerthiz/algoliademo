package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_table")
data class Product(
    @PrimaryKey @ColumnInfo(name = "product_id") val productId: String,
    val brand: String,
    val description: String,
    @ColumnInfo(name = "free_shipping") val freeShipping: Boolean,
    val image: String,
    val name: String,
    val objectId: String,
    val popularity: Int,
    val price: Float,
    @ColumnInfo(name = "price_range") val priceRange: String,
    val rating: Int,
    val type: String,
    val url: String
)
