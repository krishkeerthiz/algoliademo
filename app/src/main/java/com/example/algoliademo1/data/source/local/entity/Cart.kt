package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Contains cart total, cart items are kept in separate table(CartItems)
@Entity
data class Cart(
    @ColumnInfo(name = "user_id") val userId: String,
    val total: Float
){
    @PrimaryKey(autoGenerate = true)  // auto generate primary key field should be here, when in constructor we need to pass cart id while creating cart instance
    @ColumnInfo(name = "cart_id")     // No error when place in cart constructor, But it should be here.
    var cartId: Int = 0
}