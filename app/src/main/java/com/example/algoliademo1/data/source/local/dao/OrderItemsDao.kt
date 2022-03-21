package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.OrderItems
import com.example.algoliademo1.data.source.local.entity.ProductQuantityTuple

@Dao
interface OrderItemsDao {

    @Query("SELECT product_id, quantity FROM order_items WHERE order_id = :orderId")
    fun getProducts(orderId: String) : List<ProductQuantityTuple>

    @Query("SELECT quantity FROM order_items WHERE order_id = :orderId AND product_id = :productId")
    fun getProductQuantity(orderId: String, productId: String) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderItem : OrderItems)
}