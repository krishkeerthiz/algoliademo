package com.example.algoliademo1.data.source.repository

import android.content.Context
import com.example.algoliademo1.data.source.datasource.OrdersDataSource
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.example.algoliademo1.model.ItemCountModel
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.local.localdatasource.OrdersLocalDataSource
import com.example.algoliademo1.data.source.remote.FirebaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersRepository(context: Context) {

    private var dataSource: OrdersDataSource

    init {
        val dbInstance = ShoppingRoomDatabase.getDatabase(context, CoroutineScope(IO))
        dataSource = OrdersLocalDataSource(
            dbInstance.orderDao(),
            dbInstance.ordersDao(),
            dbInstance.orderItemsDao()
        )
    }

    suspend fun placeOrder(
        userId: String, orderId: String,
        addressId: String,
        items: List<ItemCountModel>,
        total: Float
    ) {
        CoroutineScope(IO).launch {
            dataSource.addNewOrder(userId, orderId, addressId, items, total)
        }.join()
    }

    suspend fun getOrders(): List<Order> {
        return withContext(IO) {
            val orderIds = dataSource.getOrders(FirebaseService.userId)

            val orders = mutableListOf<Order>()
            for (orderId in orderIds) {
                orders.add(dataSource.getOrder(orderId))
            }

            orders
        }
    }

    suspend fun getOrder(orderId: String): Order = withContext(IO) {
        dataSource.getOrder(orderId)
    }

    suspend fun getOrderItemsId(orderId: String): List<String> = withContext(IO) {
        dataSource.getOrderItemsIds(orderId)
    }

    suspend fun getOrderItemQuantity(orderId: String, productId: String): Int = withContext(IO) {
        dataSource.getOrderItemQuantity(orderId, productId)
    }

    companion object {
        @Volatile
        private var Instance: OrdersRepository? = null

        fun getRepository(context: Context): OrdersRepository {
            return Instance ?: synchronized(this) {
                OrdersRepository(context).also {
                    Instance = it
                }
            }
        }
    }
}