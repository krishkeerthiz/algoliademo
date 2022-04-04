package com.example.algoliademo1.data.source.local.dao

import androidx.room.*
import com.example.algoliademo1.data.source.local.entity.Wishlist
import com.example.algoliademo1.data.source.local.entity.ItemCount

@Dao
interface WishlistDao {

    @Query("SELECT product_id FROM wishlist WHERE user_id = :userId")
    suspend fun getItems(userId: String) : List<String>

//    @Query("SELECT quantity FROM wishlist WHERE user_id = :userId AND product_id = :productId")
//    fun getProductQuantity(userId: String, productId: String) : Int
//
//    @Query("UPDATE wishlist SET product_id= :productIds WHERE user_id = :userId")
//    fun updateItems(userId: String, productIds: List<String>)

    @Query("DELETE FROM wishlist WHERE user_id = :userId AND product_id = :productId")
    fun deleteProduct(userId: String, productId: String)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun updateItems(userId: String, productIds: List<String>)


    @Insert
    suspend fun insert(wishlist : Wishlist)
}