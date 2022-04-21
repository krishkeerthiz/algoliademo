package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.ProductRatings

@Dao
interface ProductRatingsDao {

    @Query("SELECT rating FROM product_ratings WHERE user_id = :userId AND product_id = :productId")
    suspend fun getUserRating(userId: String, productId: String): Int?

    @Query("SELECT rating FROM product_ratings WHERE product_id = :productId")
    suspend fun getOverallRating(productId: String): List<Int>

    @Query("UPDATE product_ratings SET rating = :rating WHERE user_id = :userId AND product_id = :productId")
    suspend fun updateRating(userId: String, productId: String, rating: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(productRating: ProductRatings)
}