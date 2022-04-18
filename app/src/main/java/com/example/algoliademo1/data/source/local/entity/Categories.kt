package com.example.algoliademo1.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Categories(
    @ColumnInfo(name = "product_id") val productId: String,
    val category: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categories_id")
    var categoriesId: Int = 0
}
