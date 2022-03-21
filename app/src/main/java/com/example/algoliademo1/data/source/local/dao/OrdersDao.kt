package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Orders
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {
    @Query("SELECT order_id from Orders WHERE user_id = :userId ")
    fun getUserOrders(userId: String) : Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orders: Orders)
}