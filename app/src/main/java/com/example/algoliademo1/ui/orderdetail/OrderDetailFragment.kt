package com.example.algoliademo1.ui.orderdetail

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.algoliademo1.OrderedItemOnClickListener
import com.example.algoliademo1.OrderedItemsAdapter
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.databinding.FragmentOrderDetailBinding
import com.example.algoliademo1.util.formatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel
    private val productsRepository = ProductsRepository.getRepository()

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

        val orderItemsAdapter = OrderedItemsAdapter(order.orderId,
            OrderedItemOnClickListener { productId ->
                showRatingDialog(productId)
            }
        )

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

    private fun showRatingDialog(productId: String){
        //Toast.makeText(requireContext(), "Rating text clicked",Toast.LENGTH_SHORT).show()
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Product Rating")

        val ratingDialog = layoutInflater.inflate(R.layout.rating_dialog, null)
        builder.setView(ratingDialog)

        val ratingItemImage = ratingDialog.findViewById<ImageView>(R.id.ratingItemImage)
        val ratingItemName = ratingDialog.findViewById<TextView>(R.id.ratingItemName)
        val ratingBar = ratingDialog.findViewById<RatingBar>(R.id.productRatingBar)

        CoroutineScope(Dispatchers.Main).launch {
            val productModel = withContext(Dispatchers.IO) {
                productsRepository.getProduct(productId)
            }

            ratingItemName.text = productModel?.name

            Glide.with(ratingItemImage.context)
                .load(productModel.image)
                .placeholder(R.drawable.spinner1)
                .into(ratingItemImage)
        }

        builder.setPositiveButton("Rate"){ dialogInterface, _ ->
            //Toast.makeText(requireContext(), "Rating updated ${ratingBar.rating.toInt()}", Toast.LENGTH_SHORT).show()
            viewModel.addRating(productId, (ratingBar.rating * 2).toInt())
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(Dialog.BUTTON_POSITIVE).isVisible = false

        ratingBar.setOnRatingBarChangeListener { ratingBar, value, b ->
            dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = value != 0.0f
            dialog.getButton(Dialog.BUTTON_POSITIVE).isVisible = true
        }

        CoroutineScope(Dispatchers.Main).launch {
            val userRating = withContext(Dispatchers.IO){
                productsRepository.getUserRating(productId)
            }

            if(userRating != null){
                //Toast.makeText(requireContext(), "${userRating!!.toFloat()}", Toast.LENGTH_SHORT).show()
                ratingBar.rating = userRating!!.toFloat() / 2
                dialog.getButton(Dialog.BUTTON_POSITIVE).isVisible = false
            }
        }
    }
}
