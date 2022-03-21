package com.example.algoliademo1

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.algoliademo1.databinding.OrderCardBinding
import com.example.algoliademo1.model.AddressModel
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.OrderModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(val onClickListener: OrdersOnClickListener) : ListAdapter<DocumentReference, OrdersViewHolder>(OrdersAdapter) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderCardBinding.inflate(view, parent, false)
        return OrdersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val docRef = getItem(position)
        holder.bind(docRef)

        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(docRef)
        }
    }

    companion object : DiffUtil.ItemCallback<DocumentReference>() {

        override fun areItemsTheSame(
            oldItem: DocumentReference,
            newItem: DocumentReference
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DocumentReference,
            newItem: DocumentReference
        ): Boolean {
            return oldItem == newItem
        }

    }
}

class OrdersViewHolder(val binding: OrderCardBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(docRef: DocumentReference){
        docRef.get().addOnSuccessListener {
            val orderModel = it.toObject<OrderModel>()

            if(orderModel != null){
                orderModel.address!!.get().addOnSuccessListener { addressDocRef ->// Address may be null
                    val address = addressDocRef.toObject<AddressModel>()

                    binding.orderAddress.text = address?.doorNumber + " " + address?.address

                }.addOnFailureListener {
                    Log.d("Orders fragment", " Failed to load order items detail")
                }

                val date = getDate(orderModel.date!!)
                binding.orderDate.text = date

                orderModel.orderItems!!.get().addOnSuccessListener { orderItemsDocRef ->
                    val orderItems = orderItemsDocRef.toObject<CartModel>()

                    binding.orderTotalPrice.text = "₹" + String.format("%.2f", orderItems?.total)
                }.addOnFailureListener {
                    Log.d("Orders fragment", " Failed to load order items detail")
                }
            }


        }
    }

    fun getDate(timeStamp: Timestamp): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val milliSeconds = timeStamp.seconds * 1000 + timeStamp.nanoseconds/1000000
        val netDate = Date(milliSeconds)
        return sdf.format(netDate).toString()
    }

}

class OrdersOnClickListener(
    val itemClickListener : (orderDocumentReference: DocumentReference) -> Unit){

    fun onItemClick(orderDocumentReference: DocumentReference) = itemClickListener(orderDocumentReference)
}