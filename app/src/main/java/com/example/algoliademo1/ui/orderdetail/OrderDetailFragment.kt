package com.example.algoliademo1.ui.orderdetail

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentOrderDetailBinding
import com.example.algoliademo1.util.formatDate
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private val viewModel: OrderDetailViewModel by viewModels {
        OrderDetailViewModelFactory(requireContext())
    }

    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        // Adapter
        val orderItemsAdapter = OrderedItemsAdapter(
            OrderedItemOnClickListener { productId ->
                showRatingDialog(productId)
            }
        )

        binding = FragmentOrderDetailBinding.bind(view)
        // viewModel = ViewModelProvider(requireActivity())[OrderDetailViewModel::class.java]

        viewModel.orderId = order.orderId
        viewModel.getAddress()
        viewModel.getOrderItems()

        // Updating UI
        val totalPrice = getString(R.string.currency) + String.format("%.2f", order.total)
        binding.totalPrice.text = totalPrice
        binding.orderDateTextView.text = formatDate(order.date)

        // Recyclerview
        binding.orderItemsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderItemsAdapter
        }

        // Updating Address
        viewModel.addressFlag.observe(viewLifecycleOwner) { flag ->
            if (flag == true) {
                viewModel.address.observe(viewLifecycleOwner) { address ->
                    binding.addressTextView.text = address
                }
                viewModel.addressFlag.value = false
            }
        }

        // Live Data // correct it
        viewModel.ordersFlag.observe(viewLifecycleOwner) { flag ->
            if (flag == true) {
                viewModel.orders.observe(viewLifecycleOwner) { productIds ->

                    lifecycleScope.launch {
                        val productsQuantity =
                            viewModel.getProductsQuantity(productIds, order.orderId)

                        orderItemsAdapter.addProductQuantityModels(productsQuantity)
                        viewModel.ordersFlag.value = false

                    }
                }
            }
        }

    }

    private fun showRatingDialog(productId: String) {

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Product Rating")

        val ratingDialog = layoutInflater.inflate(R.layout.rating_dialog, null)
        builder.setView(ratingDialog)

        val ratingItemImage = ratingDialog.findViewById<ImageView>(R.id.ratingItemImage)
        val ratingItemName = ratingDialog.findViewById<TextView>(R.id.ratingItemName)
        val ratingBar = ratingDialog.findViewById<RatingBar>(R.id.productRatingBar)

        // Displaying dialog views
        lifecycleScope.launch {
            val product = viewModel.getProduct(productId)

            if (product != null) {
                ratingItemName.text = product.name

                Glide.with(ratingItemImage.context)
                    .load(product.image)
                    .placeholder(R.drawable.spinner1)
                    .into(ratingItemImage)
            }

        }

        builder.setPositiveButton("Rate") { _, _ ->
            viewModel.addRating(productId, (ratingBar.rating * 2).toInt())
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(Dialog.BUTTON_POSITIVE).isVisible = false

        // Change listener
        ratingBar.setOnRatingBarChangeListener { _, value, _ ->
            dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = value != 0.0f
            dialog.getButton(Dialog.BUTTON_POSITIVE).isVisible = true
        }

        // Showing previous rating
        lifecycleScope.launch {
            val userRating = viewModel.getUserRating(productId)

            if (userRating != null) {
                ratingBar.rating = userRating.toFloat() / 2
                dialog.getButton(Dialog.BUTTON_POSITIVE).isVisible = false
            }
        }
    }
}
