package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.CartItems
import com.example.algoliademo1.data.source.local.entity.ItemCount

@Dao
interface CartItemsDao {

    @Query("SELECT product_id, quantity FROM cart_items WHERE user_id = :userId ")
    fun getProductsAndQuantities(userId: String): List<ItemCount>

    @Query("SELECT quantity FROM cart_items WHERE user_id = :userId AND product_id = :productId")
    fun getProductQuantity(userId: String, productId: String): Int

    @Query("SELECT product_id FROM cart_items WHERE user_id = :userId ")
    fun getProducts(userId: String): List<String>

    @Query("UPDATE cart_items SET quantity = :quantity WHERE user_id = :userId AND product_id = :productId")
    fun updateProductQuantity(userId: String, productId: String, quantity: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cartItems: CartItems): Long

    @Query("DELETE FROM cart_items WHERE user_id = :userId AND product_id = :productId")
    fun deleteProduct(userId: String, productId: String)

    @Query("DELETE FROM cart_items WHERE user_id = :userId")
    fun deleteProducts(userId: String)

    @Query("SELECT quantity FROM cart_items WHERE user_id= :userId AND product_id= :productId")
    fun isProductInCart(userId: String, productId: String): Int?
}