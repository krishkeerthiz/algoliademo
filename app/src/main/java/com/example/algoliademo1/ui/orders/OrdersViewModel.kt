package com.example.algoliademo1.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.model.OrdersModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch

class OrdersViewModel :ViewModel() {

    // New code

    private val ordersRepository = OrdersRepository.getRepository()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>>
        get() = _orders

    fun getOrders() {
        viewModelScope.launch {
            val orders = ordersRepository.getOrders()

            _orders.value = orders
        }

    }

    // Old code
//    private val _ordersModel = MutableLiveData<OrdersModel>()
//    val ordersModel: LiveData<OrdersModel>
//    get() = _ordersModel
//
//    fun getOrders() {
//        val ordersDocumentReference = FirebaseService.getOrdersDocumentReference()
//
//        ordersDocumentReference.get().addOnSuccessListener {
//            val model = it.toObject<OrdersModel>()
//
//            if(model != null)
//                _ordersModel.value = model
//            else
//                _ordersModel.value = null
//        }
//    }

}
