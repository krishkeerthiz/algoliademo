package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.OrderItems
import com.example.algoliademo1.data.source.local.entity.ItemCount

@Dao
interface OrderItemsDao {

    @Query("SELECT product_id, quantity FROM order_items WHERE order_id = :orderId ")
    suspend fun getProductsAndQuantities(orderId: String) : List<ItemCount>

    @Query("SELECT quantity FROM order_items WHERE order_id = :orderId AND product_id = :productId")
    suspend fun getProductQuantity(orderId: String, productId: String) : Int

    @Query("SELECT product_id FROM order_items  WHERE order_id = :orderId ")
    suspend fun getProducts(orderId: String) : List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderItem : OrderItems)
}