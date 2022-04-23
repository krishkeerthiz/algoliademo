package com.example.algoliademo1.model

import com.example.algoliademo1.data.source.local.entity.Product

data class ProductQuantityModel(
    val product: Product,
    val quantity: Int
)
