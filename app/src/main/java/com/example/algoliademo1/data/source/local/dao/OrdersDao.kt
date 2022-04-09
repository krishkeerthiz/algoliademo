package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.data.source.local.entity.Orders
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {
    @Query("SELECT order_id from Orders WHERE user_id = :userId ")
    suspend fun getUserOrders(userId: String) : List<String>
//
//    @Query("UPDATE orders SET order_id = :orderIds WHERE user_id = :userId")
//    fun updateOrders(userId: String, orderIds: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orders: Orders)
}