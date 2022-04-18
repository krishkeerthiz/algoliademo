package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.data.source.local.entity.Order

interface OrdersDataSource {

    suspend fun addNewOrder(
        userId: String,
        orderId: String,
        addressId: String,
        items: List<ItemCount>,
        total: Float
    )

    suspend fun getOrder(orderId: String): Order

    suspend fun getOrderItems(orderId: String): List<ItemCount>

    suspend fun getOrders(userId: String): List<String>

    suspend fun getOrderItemQuantity(orderId: String, productId: String): Int

    suspend fun getOrderItemsIds(orderId: String): List<String>

}