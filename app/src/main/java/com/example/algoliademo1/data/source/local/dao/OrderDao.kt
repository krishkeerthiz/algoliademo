package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Order

@Dao
interface OrderDao {
    @Query("SELECT * from `order` WHERE order_id = :orderId")
    suspend fun getOrder(orderId: String): Order

    @Query("SELECT address_id from `order` WHERE order_id = :orderId")
    suspend fun getOrderAddress(orderId: String): String

    @Query("SELECT total from `order` WHERE order_id = :orderId")
    suspend fun getOrderTotal(orderId: String): Float

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)
}