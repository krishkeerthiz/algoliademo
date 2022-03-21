package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Order

@Dao
interface OrderDao {
    @Query("SELECT * from `order` WHERE order_id = :orderId")
    fun getOrder(orderId: String) : Order

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)
}