package com.example.algoliademo1.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.model.OrderAddressModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {

    // Repositories
    private val ordersRepository = OrdersRepository //.getRepository()
    private val addressRepository = AddressRepository //.getRepository()

    // Live data
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>>
        get() = _orders

    fun getOrders() {
        viewModelScope.launch {
            val orders = ordersRepository.getOrders()

            _orders.value = orders
        }
    }

    private suspend fun getAddress(addressId: String): String{
        return addressRepository.getAddress(addressId, FirebaseService.userId).toString()
    }

    suspend fun getAddresses(orders: List<Order>): List<OrderAddressModel>{
        val orderAddresses = mutableListOf<OrderAddressModel>()

        viewModelScope.launch(Dispatchers.IO) {
            for(order in orders)
                orderAddresses.add(OrderAddressModel(order, getAddress(order.addressId)))
        }.join()

        return orderAddresses
    }


}
