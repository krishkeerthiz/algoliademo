package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Cart

@Dao
interface CartDao {

    @Query("SELECT total FROM cart WHERE user_id = :userId")
    fun getCartTotal(userId: String) : Float

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)
}