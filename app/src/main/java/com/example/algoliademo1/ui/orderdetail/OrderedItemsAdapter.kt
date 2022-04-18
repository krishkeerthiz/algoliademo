package com.example.algoliademo1

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.databinding.OrderItemBinding
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.OrdersRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderedItemsAdapter(val orderId: String) : ListAdapter<String, OrderedItemsViewHolder>(OrderedItemsAdapter) {

    private val ordersRepository = OrdersRepository.getRepository()
    private val productsRepository = ProductsRepository.getRepository()

  //  private var countValues: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = OrderItemBinding.inflate(view, parent, false)
        return OrderedItemsViewHolder(binding, productsRepository)
    }

    override fun onBindViewHolder(holder: OrderedItemsViewHolder, position: Int) {
        var productId = getItem(position)

        Log.d(TAG, "position $position product id: $productId")
 //       if(countValues.isNotEmpty())
        holder.bind(productId, orderId, ordersRepository)

    }

//    fun addCountValues(values: List<Int>?){
//        if(values != null)
//        countValues = values.toMutableList()
//    }

    companion object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}

class OrderedItemsViewHolder(val binding: OrderItemBinding,val productsRepository: ProductsRepository) : RecyclerView.ViewHolder(binding.root){

    fun bind(productId: String, orderId: String, ordersRepository: OrdersRepository) {
//            FirebaseService.testGetProductReference(productId).get().addOnSuccessListener {
//                val productModel = it.toObject<ProductModel>()
        CoroutineScope(Dispatchers.Main).launch {
            val productModel = withContext(Dispatchers.IO) {
                productsRepository.getProduct(productId)
            }
            binding.orderItemName.text = productModel?.name

            binding.orderItemPrice.text = binding.orderItemPrice.context.getString(R.string.currency) + String.format("%.2f", productModel.price )

            Glide.with(binding.orderItemImage.context)
                .load(productModel?.image)
                .placeholder(R.drawable.spinner1)
                .into(binding.orderItemImage)

            //Log.d(TAG, "$productQuantity ")
            val productQuantity = withContext(Dispatchers.IO){
                ordersRepository.getOrderItemQuantity(orderId, productId)

                // binding.orderItemTotalPrice.text = productQuantity * productM
            }
            binding.orderItemCount.text = productQuantity.toString()

            binding.orderItemTotalPrice.text = binding.orderItemTotalPrice.context.getString(R.string.currency) + String.format("%.2f", (productModel.price) *  productQuantity )
        }
    }

//    fun bind(productId: String, quantity: Int){
//        FirebaseService.testGetProductReference(productId).get().addOnSuccessListener {
//            val productModel = it.toObject<ProductModel>()
//
//            if(productModel != null){
//                binding.orderItemName.text = productModel.name
//
//                binding.orderItemPrice.text = "₹" + productModel.price
//
//                binding.orderItemCount.text = quantity.toString() + " x "
//
//                binding.orderItemTotalPrice.text = String.format("%.2f", (productModel.price) *  quantity )
//
//                Glide.with(binding.orderItemImage.context)
//                    .load(productModel.image)
//                    .into(binding.orderItemImage)
//            }
//
//        }
//
//    }

}