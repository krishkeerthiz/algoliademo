package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address(
    @PrimaryKey @ColumnInfo(name = "address_id") val addressId: String,
    val address: String,
    val city: String,
    @ColumnInfo(name= "door_number") val doorNumber: String,
    val pincode: Int,
    val state: String
)