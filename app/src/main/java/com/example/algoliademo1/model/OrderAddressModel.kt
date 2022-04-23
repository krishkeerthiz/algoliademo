package com.example.algoliademo1.model

import com.example.algoliademo1.data.source.local.entity.Order

data class OrderAddressModel(
    val order: Order,
    val address: String,
)
