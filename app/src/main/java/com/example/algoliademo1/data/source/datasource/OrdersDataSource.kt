package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.model.ItemCountModel
import com.example.algoliademo1.data.source.local.entity.Order

interface OrdersDataSource {

    suspend fun addNewOrder(
        userId: String,
        orderId: String,
        addressId: String,
        items: List<ItemCountModel>,
        total: Float
    )

    suspend fun getOrder(orderId: String): Order

    suspend fun getOrderItems(orderId: String): List<ItemCountModel>

    suspend fun getOrders(userId: String): List<String> // Order ids of that user

    suspend fun getOrderItemQuantity(orderId: String, productId: String): Int

    suspend fun getOrderItemsIds(orderId: String): List<String>  // Items product ids of that order

}