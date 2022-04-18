package com.example.algoliademo1.data.source.repository

import com.example.algoliademo1.ShoppingApplication
import com.example.algoliademo1.data.source.datasource.OrdersDataSource
import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.local.localdatasource.OrdersLocalDataSource
import com.example.algoliademo1.data.source.remote.FirebaseService

class OrdersRepository {

    private var dataSource: OrdersDataSource

    init {
        val dbInstance = ShoppingApplication.instance!!.database
        dataSource = OrdersLocalDataSource(
            dbInstance.orderDao(),
            dbInstance.ordersDao(),
            dbInstance.orderItemsDao(),
            dbInstance.cartItemsDao()
        )
    }

    suspend fun placeOrder(
        userId: String, orderId: String,
        addressId: String,
        items: List<ItemCount>,
        total: Float
    ) {
        dataSource.addNewOrder(userId, orderId, addressId, items, total)
    }

    suspend fun getOrders(): List<Order> {
        val orderIds = dataSource.getOrders(FirebaseService.userId)

        val orders = mutableListOf<Order>()
        for (orderId in orderIds) {
            orders.add(dataSource.getOrder(orderId))
        }

        return orders
    }

    suspend fun getOrder(orderId: String): Order {
        return dataSource.getOrder(orderId)
    }

    suspend fun getOrderItemsId(orderId: String): List<String> {
        return dataSource.getOrderItemsIds(orderId)
    }

    suspend fun getOrderItemQuantity(orderId: String, productId: String): Int {
        return dataSource.getOrderItemQuantity(orderId, productId)
    }

    companion object {
        @Volatile
        private var Instance: OrdersRepository? = null

        fun getRepository(): OrdersRepository {
            return Instance ?: synchronized(this) {
                OrdersRepository().also {
                    Instance = it
                }
            }
        }


    }
}