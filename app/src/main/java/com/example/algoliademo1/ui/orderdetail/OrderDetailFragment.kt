package com.example.algoliademo1.ui.orderdetail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.OrderedItemsAdapter
import com.example.algoliademo1.OrdersAdapter
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentOrderDetailBinding
import com.example.algoliademo1.model.AddressModel
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.OrderModel
import com.example.algoliademo1.util.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import java.text.SimpleDateFormat
import java.util.*


class OrderDetailFragment(val order: Order) : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: keerthi")

        binding = FragmentOrderDetailBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[OrderDetailViewModel::class.java]

//        viewModel = ViewModelProvider(requireActivity(),
//            OrderDetailViewModelFactory(order.orderId))[OrderDetailViewModel::class.java]

        binding.totalPrice.text = order.total.toString()
        binding.orderDateTextView.text = formatDate(order.date)

        viewModel.orderId = order.orderId
        viewModel.getAddress()
        viewModel.getOrderItems()

        viewModel.addressFlag.observe(viewLifecycleOwner){ flag ->
            if(flag == true){
                viewModel.address.observe(viewLifecycleOwner){ address ->
                    binding.addressTextView.text = address
                }
                viewModel.addressFlag.value = false
            }
        }

        val orderItemsAdapter = OrderedItemsAdapter(order.orderId)

        viewModel.ordersFlag.observe(viewLifecycleOwner){ flag ->
            if(flag == true){
                viewModel.orders.observe(viewLifecycleOwner){ productIds ->
                    Log.d(TAG, "before submit list ${productIds.toString()}")
                    orderItemsAdapter.submitList(productIds)
                    Log.d(TAG, "after submit list ${productIds.toString()}")

                    viewModel.ordersFlag.value = false
                }
            }
        }


        binding.orderItemsList.let {
            it.itemAnimator = null
            it.adapter = orderItemsAdapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

    }

//    private fun loadUI(){
//        loadOrderedItemsAndPrice()
//        loadAddressAndDate()
//    }

//    private fun loadAddressAndDate(){
//        binding.addressTextView.text = viewModel.address
//        binding.orderDateTextView.text = formatDate(order.date)
//
//    }

//    private fun loadAddressAndDate(){
//        orderDocumentReference.get().addOnSuccessListener {
//            val orderModel = it.toObject<OrderModel>()
//
//            if(orderModel != null){
//                binding.orderDateTextView.text = getDate(orderModel.date!!)
//
//                orderModel.address!!.get().addOnSuccessListener { addressDocSnap ->
//                    val addressModel = addressDocSnap.toObject<AddressModel>()
//
//                    binding.addressTextView.text = addressModel.toString()
//                }
//                    .addOnFailureListener {
//                        Log.d("Order Detail Fragment", "Failed to load address model")
//                    }
//            }
//        }
//            .addOnFailureListener {
//                Log.d("Order Detail Fragment", "Failed to load order model")
//            }
//    }


//    private fun loadOrderedItemsAndPrice(){
//        val orderedItemsAdapter = OrderedItemsAdapter()
//
//        orderDocumentReference.get().addOnSuccessListener {
//            val orderModel = it.toObject<OrderModel>()
//
//            if(orderModel != null){
//
//                orderModel.orderItems!!.get().addOnSuccessListener { orderItemsDocSnap ->
//                    val orderItemsModel = orderItemsDocSnap.toObject<CartModel>()
//
//                    binding.totalPrice.text =  "₹" + String.format("%.2f", orderItemsModel?.total)
//
//                    orderedItemsAdapter.addCountValues(orderItemsModel?.products?.values?.toList())
//                    orderedItemsAdapter.submitList(orderItemsModel?.products?.keys?.toList())
//
//                    binding.orderItemsList.let { recyclerView ->
//                        recyclerView.adapter = orderedItemsAdapter
//                        recyclerView.itemAnimator = null
//                        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//                    }
//                }
//                    .addOnFailureListener {
//                        Log.d("Order Detail Fragment", "Failed to load ordered items model")
//                    }
//            }
//
//        }
//            .addOnFailureListener {
//                Log.d("Order Detail Fragment", "Failed to load order model")
//            }
//    }


}
