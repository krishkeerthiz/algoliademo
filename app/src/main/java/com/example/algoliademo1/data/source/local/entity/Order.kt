package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

// Order specific details
@Entity
data class Order(
    @PrimaryKey @ColumnInfo(name = "order_id") val orderId: String,
    @ColumnInfo(name = "address_id") val addressId: String,
    val date: Date,
    val total: Float
) : Serializable  // we don't want to send order reference, instead we send byte stream of order data.
