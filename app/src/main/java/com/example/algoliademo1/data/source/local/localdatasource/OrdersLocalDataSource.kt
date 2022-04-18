package com.example.algoliademo1.data.source.local.localdatasource

import com.example.algoliademo1.data.source.datasource.OrdersDataSource
import com.example.algoliademo1.data.source.local.dao.CartItemsDao
import com.example.algoliademo1.data.source.local.dao.OrderDao
import com.example.algoliademo1.data.source.local.dao.OrderItemsDao
import com.example.algoliademo1.data.source.local.dao.OrdersDao
import com.example.algoliademo1.data.source.local.entity.ItemCount
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.local.entity.OrderItems
import com.example.algoliademo1.data.source.local.entity.Orders
import java.util.*

class OrdersLocalDataSource(
    val orderDao: OrderDao,
    val ordersDao: OrdersDao,
    val orderItemsDao: OrderItemsDao,
    val cartItemsDao: CartItemsDao
) : OrdersDataSource {

    override suspend fun addNewOrder(
        userId: String,
        orderId: String,
        addressId: String,
        items: List<ItemCount>,
        total: Float
    ) {
        //Create new order
        val order = Order(orderId, addressId, Date(), total)
        orderDao.insert(order)

        //Add to orders
        ordersDao.insert(Orders(userId, orderId))

        //Add items
        for (item in items)
            orderItemsDao.insert(OrderItems(orderId, item.productId, item.quantity))
    }

    override suspend fun getOrderItems(orderId: String): List<ItemCount> {
        return orderItemsDao.getProductsAndQuantities(orderId)
    }

    override suspend fun getOrder(orderId: String): Order {
        return orderDao.getOrder(orderId)
    }

    override suspend fun getOrders(userId: String): List<String> {
        return ordersDao.getUserOrders(userId)
    }

    override suspend fun getOrderItemQuantity(orderId: String, productId: String): Int {
        return orderItemsDao.getProductQuantity(orderId, productId)
    }

    override suspend fun getOrderItemsIds(orderId: String): List<String> {
        return orderItemsDao.getProducts(orderId)
    }
}