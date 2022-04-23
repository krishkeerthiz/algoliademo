package com.example.algoliademo1.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.data.source.repository.OrdersRepository
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {

    private val ordersRepository = OrdersRepository.getRepository()
    private val addressRepository = AddressRepository.getRepository()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>>
        get() = _orders

    fun getOrders() {
        viewModelScope.launch {
            val orders = ordersRepository.getOrders()

            _orders.value = orders
        }

    }

    suspend fun getAddress(addressId: String): String{
        return addressRepository.getAddress(addressId, FirebaseService.userId).toString()
    }

}
