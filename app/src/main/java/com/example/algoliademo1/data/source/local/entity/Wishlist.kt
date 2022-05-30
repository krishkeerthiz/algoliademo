package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// User's wishlist products are stored here
@Entity
data class Wishlist(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "product_id") val productIds: String

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "wishlist_id")
    var wishlistId: Int = 0
}
