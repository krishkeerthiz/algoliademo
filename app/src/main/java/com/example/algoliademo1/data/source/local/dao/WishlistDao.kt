package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Wishlist

@Dao
interface WishlistDao {

    @Query("SELECT product_id FROM wishlist WHERE user_id = :userId")
    suspend fun getItems(userId: String): List<String>

    @Query("SELECT product_id FROM wishlist WHERE user_id = :userId AND product_id= :productId")
    suspend fun getItem(userId: String, productId: String): String?

    @Query("DELETE FROM wishlist WHERE user_id = :userId AND product_id = :productId")
    suspend fun deleteProduct(userId: String, productId: String)

    @Insert
    suspend fun insert(wishlist: Wishlist)
}