package com.example.algoliademo1.model

import androidx.room.ColumnInfo


// used to hold productId, quantity from cart items, order items
data class ItemCountModel(
    @ColumnInfo(name = "product_id") val productId: String,
    val quantity: Int
)
