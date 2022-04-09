package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo

data class ItemCount(
    @ColumnInfo(name = "product_id") val productId: String,
    val quantity: Int
)
