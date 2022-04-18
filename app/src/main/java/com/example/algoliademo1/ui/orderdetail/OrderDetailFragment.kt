package com.example.algoliademo1.ui.orderdetail

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.OrderedItemsAdapter
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentOrderDetailBinding
import com.example.algoliademo1.util.formatDate

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel

    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order: Order = args.order

        Log.d(TAG, "order total: ${order.total}")

        binding = FragmentOrderDetailBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[OrderDetailViewModel::class.java]

        binding.totalPrice.text = getString(R.string.currency) + String.format("%.2f", order.total)
        binding.orderDateTextView.text = formatDate(order.date)

        viewModel.orderId = order.orderId
        viewModel.getAddress()
        viewModel.getOrderItems()

        viewModel.addressFlag.observe(viewLifecycleOwner) { flag ->
            if (flag == true) {
                viewModel.address.observe(viewLifecycleOwner) { address ->
                    binding.addressTextView.text = address
                }
                viewModel.addressFlag.value = false
            }
        }

        val orderItemsAdapter = OrderedItemsAdapter(order.orderId)

        viewModel.ordersFlag.observe(viewLifecycleOwner) { flag ->
            if (flag == true) {
                viewModel.orders.observe(viewLifecycleOwner) { productIds ->
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
}
