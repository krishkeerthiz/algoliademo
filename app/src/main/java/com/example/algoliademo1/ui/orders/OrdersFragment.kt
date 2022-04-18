package com.example.algoliademo1.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.OrdersAdapter
import com.example.algoliademo1.OrdersOnClickListener
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentOrdersBinding


class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var viewModel: OrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrdersBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[OrdersViewModel::class.java]

        viewModel.getOrders()

        val adapter = OrdersAdapter(
            OrdersOnClickListener { order ->
                gotoOrderDetailsFragment(order)
            }
        )

        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders != null) {
                if (orders.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.ordersList.visibility = View.INVISIBLE
                } else {
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.ordersList.visibility = View.VISIBLE
                }
                adapter.submitList(orders)
            }
        }

        binding.ordersList.let {
            it.itemAnimator = null
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun gotoOrderDetailsFragment(order: Order) {
        val action = OrdersFragmentDirections.actionOrdersFragmentToOrderDetailFragment(order)
        view?.findNavController()?.navigate(action)
    }

}