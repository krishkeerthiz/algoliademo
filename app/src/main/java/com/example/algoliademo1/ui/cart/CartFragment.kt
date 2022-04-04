package com.example.algoliademo1.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.algoliademo1.*
import com.example.algoliademo1.databinding.FragmentCartBinding
import com.example.algoliademo1.ui.MainActivity

class CartFragment : Fragment() {
    private lateinit var viewModel: CartViewModel
    private lateinit var binding: FragmentCartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]

        viewModel.getCartItems()
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartBinding.bind(view)

       // setTotalPriceView()

        val cartAdapter = CartAdapter( CartOnClickListener(
            { productId -> (requireActivity() as MainActivity).showProductDetailFragment(productId)},
            { productId, price ->  viewModel.removeItemAndUpdate(productId, price)
            Toast.makeText(requireContext(), "trash clicked", Toast.LENGTH_SHORT).show()}
        ))

        viewModel.cartModel.observe(viewLifecycleOwner){
            if(it != null) {
                cartAdapter.submitList(it.products?.keys?.toList())
                binding.totalPrice.text = "₹" + it?.total?.toString()
                //Toast.makeText(requireContext(), it.products?.entries.toString(), Toast.LENGTH_LONG).show()
            }
         }

        binding.cartItemsList.let{
            it.itemAnimator = null
            it.adapter = cartAdapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.buyButton.setOnClickListener {
            (requireActivity() as MainActivity).showAddressFragment()
        }

    }

    private fun setTotalPriceView(){
        viewModel.cartModel.observe(viewLifecycleOwner){ cartModel ->
            binding.totalPrice.text = "₹" + cartModel?.total?.toString()
         }
    }

}