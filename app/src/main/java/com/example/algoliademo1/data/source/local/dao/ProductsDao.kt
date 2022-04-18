package com.example.algoliademo1.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoliademo1.data.source.local.entity.Product

@Dao
interface ProductsDao {

    @Query("SELECT * FROM products_table WHERE product_id = :productId")
    suspend fun getProduct(productId: String): Product //Product//

    @Query("SELECT price FROM products_table WHERE product_id = :productId")
    fun getProductPrice(productId: String): Float

    @Query("SELECT * FROM products_table")
    fun getProducts(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)
}