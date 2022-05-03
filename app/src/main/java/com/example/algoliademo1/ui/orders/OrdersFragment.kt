package com.example.algoliademo1.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentOrdersBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: OrdersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrdersBinding.bind(view)

        // Adapter
        val orderAdapter = OrdersAdapter(
            OrdersOnClickListener { order ->
                gotoOrderDetailsFragment(order)
            }
        )



        // Getting orders
        viewModel.getOrders()

        // Recycler view
        binding.ordersList.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                reverseLayout = true
                stackFromEnd = true
            }
        }

        // Live data
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders != null) {
                if (orders.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.ordersList.visibility = View.INVISIBLE
                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.ordersList.visibility = View.VISIBLE
                }
                lifecycleScope.launch(Dispatchers.IO){
                    val orderAddresses = viewModel.getAddresses(orders)

                    withContext(Dispatchers.Main){
                        orderAdapter.addOrderAddresses(orderAddresses)
                    }
                }
            }
        }

    }

    private fun gotoOrderDetailsFragment(order: Order) {
        val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailFragment(order)
        view?.findNavController()?.navigate(action)
    }

}