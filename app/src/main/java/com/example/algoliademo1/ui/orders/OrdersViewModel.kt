package com.example.algoliademo1.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.algoliademo1.model.OrdersModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.firestore.ktx.toObject

class OrdersViewModel :ViewModel() {

    private val _ordersModel = MutableLiveData<OrdersModel>()
    val ordersModel: LiveData<OrdersModel>
    get() = _ordersModel

    fun getOrders() {
        val ordersDocumentReference = FirebaseService.getOrdersDocumentReference()

        ordersDocumentReference.get().addOnSuccessListener {
            val model = it.toObject<OrdersModel>()

            if(model != null)
                _ordersModel.value = model
            else
                _ordersModel.value = null
        }
    }

}
