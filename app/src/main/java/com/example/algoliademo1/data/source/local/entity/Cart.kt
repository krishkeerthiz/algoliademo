package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    @PrimaryKey @ColumnInfo(name = "user_id") val userId: String,
    val total: Float
)
