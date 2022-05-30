package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Stores cart items ids and quantities
@Entity(tableName = "cart_items")
data class CartItems(
    @ColumnInfo(name = "user_id") val userId: String,  // could have used cart id also but see // CartLocalDataSource line 78
                                                       // also only one cart for each user, unlike several orders for each user
    @ColumnInfo(name = "product_id") val productId: String,
    val quantity: Int

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cart_items_id")
    var cartItemsId: Int = 0

}
