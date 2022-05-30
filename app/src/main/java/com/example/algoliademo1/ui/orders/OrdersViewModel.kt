package com.example.algoliademo1.ui.orders

import android.content.Context
import androidx.lifecycle.*
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.AddressRepository
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.model.OrderAddressModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersViewModel(context: Context) : ViewModel() {

    // Repositories
    private val ordersRepository = OrdersRepository.getRepository(context)
    private val addressRepository = AddressRepository.getRepository(context)

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
        return withContext(Dispatchers.Default){
            val orderAddresses = mutableListOf<OrderAddressModel>()

            for(order in orders)
                orderAddresses.add(OrderAddressModel(order, getAddress(order.addressId)))

            orderAddresses
        }
    }

//    suspend fun getAddresses(orders: List<Order>): List<OrderAddressModel>{
//        val orderAddresses = mutableListOf<OrderAddressModel>()
//
//        viewModelScope.launch(Dispatchers.IO) {
//            for(order in orders)
//                orderAddresses.add(OrderAddressModel(order, getAddress(order.addressId)))
//        }.join()
//
//        return orderAddresses
//    }
}


class OrdersViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrdersViewModel(context) as T
    }
}
