package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.CartItems
import com.example.algoliademo1.data.source.local.entity.ProductQuantityTuple

@Dao
interface CartItemsDao {

    @Query("SELECT product_id, quantity FROM cart_items WHERE user_id = :userId")
    fun getProducts(userId: String) : List<ProductQuantityTuple>

    @Query("SELECT quantity FROM cart_items WHERE user_id = :userId AND product_id = :productId")
    fun getProductQuantity(userId: String, productId: String) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItems : CartItems)
}