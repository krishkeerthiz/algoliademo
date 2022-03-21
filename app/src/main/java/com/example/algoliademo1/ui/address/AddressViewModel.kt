package com.example.algoliademo1.ui.address

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.algoliademo1.model.AddressModel
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.OrderModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

class AddressViewModel : ViewModel() {

    private val _address = MutableLiveData<String>()

    val address: LiveData<String>
        get() = _address

    private val _addresses = MutableLiveData<MutableMap<DocumentReference, String>>()

    val addresses: LiveData<MutableMap<DocumentReference, String>>
    get() = _addresses

    var addressReference: DocumentReference? = null

    init{
        val addressDocumentRef = FirebaseService.getAddressesReference()

        addressDocumentRef.get().addOnSuccessListener { documentContainingReference ->
            val data = documentContainingReference.data

            if (data != null) {
                val addressField = data.get("address") as List<DocumentReference>

                val addressRefMap: MutableMap<DocumentReference, String> = mutableMapOf()

                for(address in addressField){
                    address.get().addOnSuccessListener { addressDocSnap ->
                        val addressModel = addressDocSnap.toObject<AddressModel>()

                        if(addressModel != null)
                            addressRefMap[address] = addressModel.toString()
                    }
                        .addOnFailureListener {
                            Log.d("Address fragment", "unable to get address from reference")
                        }
                }
                _addresses.value = addressRefMap

            } else {
                Log.d("Address fragment", "data not found")
            }
        }
            .addOnFailureListener {
                Log.d("Address fragment", "Failed to load addresses")
            }
    }

    fun getAddress(){
        val addressDocumentRef = FirebaseService.getAddressesReference()

        addressDocumentRef.get().addOnSuccessListener { documentContainingReference ->
            val data = documentContainingReference.data

            if(data != null){
                val addressField = data.get("address") as List<DocumentReference>
               // _address.value = addressField[0].toString()
                val docRef = addressField[0]
                addressReference = docRef
            docRef.get().addOnSuccessListener {
                val addressModel = it.toObject<AddressModel>()

                _address.value = addressModel?.doorNumber + "\n" + addressModel?.address + "\n" + addressModel?.city +
                 "\n" + addressModel?.state + "\n" + addressModel?.pincode
            }
                .addOnFailureListener {
                    Log.d("Address fragment", "unable to get document from path")
                }
            }
            else{
                Log.d("Address fragment", "data not found")
            }

        }
            .addOnFailureListener {
                Log.d("Address fragment", "unable to read document from map")
            }
    }

    fun placeOrder(){
        if(addressReference != null){
            val cartReference = FirebaseService.getCartReference()
            cartReference.get().addOnSuccessListener {
                val cartModel = it.toObject<CartModel>()

                val orderItemsCollectionReference = FirebaseService.getOrderItemsReference()
                orderItemsCollectionReference.add(cartModel!!)
                    .addOnSuccessListener { orderItemsDocumentReference ->

                        val order = OrderModel(addressReference!!, Timestamp.now(), orderItemsDocumentReference!!)

                        val orderCollectionReference = FirebaseService.getOrderReference()
                        orderCollectionReference.add(order)
                            .addOnSuccessListener { orderDocumentReference ->

                                val ordersDocumentReference = FirebaseService.getOrdersDocumentReference()
                                ordersDocumentReference.set(
                                    hashMapOf(
                                        "order" to FieldValue.arrayUnion(orderDocumentReference)
                                    )
                                        , SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("Address Fragment", "Order placed successfully")
                                        cartReference.delete()
                                    }
                                    .addOnFailureListener {
                                        Log.d("Address Fragment", "Error placing order")
                                    }
                            }
                            .addOnFailureListener {
                                Log.d("Address Fragment", "Error processing order collection")
                            }
                    }
                    .addOnFailureListener {
                        Log.d("Address Fragment", "Error processing order items collection")
                    }
            }
                .addOnFailureListener {
                    Log.d("Address Fragment", "Error processing cart collection")
                }
        }
        else
            Log.d("Address Fragment", "Select Address to proceed")
    }

    fun addAddress(doorNumber: String, address: String, city: String, pincode: Int, state: String){
        val addressModel = AddressModel(address, city, doorNumber, pincode, state)

        val addressReference = FirebaseService.getAddressReference()

        addressReference.add(addressModel).addOnSuccessListener { addressDocumentReference ->
            val addressesReference = FirebaseService.getAddressesReference()
            addressesReference.set(
                hashMapOf(
                    "address" to FieldValue.arrayUnion(addressDocumentReference)
                ), SetOptions.merge()
            ).addOnSuccessListener {
                Log.d("Address Fragment", "Address added successfully")
            }
                .addOnFailureListener {
                    Log.d("Address Fragment", "Failed to add address to array")
                }
        }
            .addOnFailureListener {
                Log.d("Address Fragment", "Failed to add address")
            }
    }
}