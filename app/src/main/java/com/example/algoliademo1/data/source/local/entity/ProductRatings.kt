package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Ratings given by each user to the products
@Entity(tableName = "product_ratings")
data class ProductRatings(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "product_id") val productId: String,
    val rating: Int
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_ratings_id")
    var productRatingsId: Int = 0

}
