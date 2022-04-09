package com.example.algoliademo1.data.source.datasource

import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.data.source.local.entity.Order

interface OrdersDataSource {

    suspend fun addNewOrder(userId: String, orderId: String, addressId: String, items: List<ItemCount>, total: Float)  // create order object and update orders

    suspend fun getOrder(orderId: String): Order // returns order - room <string, item count>  - firestore <reference, reference> ,  parameter reference or id

    suspend fun getOrderItems(orderId: String): List<ItemCount>

    suspend fun getOrders(userId: String): List<String> // Order ids // returns list of order - room list of order ids string, firestore list of order reference reference

    suspend fun getOrderItemQuantity(orderId: String, productId: String): Int

    suspend fun getOrderItemsIds(orderId: String): List<String>

}