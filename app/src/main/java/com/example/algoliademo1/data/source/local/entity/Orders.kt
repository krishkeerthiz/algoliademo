package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// Stores all orders placed by users
@Entity
data class Orders(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "order_id") val orderId: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orders_id")
    var ordersId: Int = 0
}
