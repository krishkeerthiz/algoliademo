package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Categories

@Dao
interface CategoriesDao {

    @Query("SELECT category FROM categories WHERE product_id= :productId")
    suspend fun getCategories(productId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Categories)
}