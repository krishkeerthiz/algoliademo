package com.example.algoliademo1.ui.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.*
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.FragmentOrdersBinding
import com.example.algoliademo1.ui.MainActivity


class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var viewModel: OrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrdersBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[OrdersViewModel::class.java]

        viewModel.getOrders()

        val adapter = OrdersAdapter(
            OrdersOnClickListener {
                order -> gotoOrderDetailsFragment(order)
            //(requireActivity() as MainActivity).showOrderDetailFragment(order)  // Its incorrect to document reference as parameter
            }
        )

        viewModel.orders.observe(viewLifecycleOwner){ orders ->
            if(orders != null){
                if(orders.isEmpty()){
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.ordersList.visibility = View.INVISIBLE
                }
                else{
                    binding.emptyLayout.visibility = View.INVISIBLE
                    binding.ordersList.visibility = View.VISIBLE
                }
                adapter.submitList(orders)
            }
        }

        binding.ordersList.let{
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