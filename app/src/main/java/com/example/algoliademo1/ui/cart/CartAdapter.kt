package com.example.algoliademo1

import com.example.algoliademo1.R
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.databinding.CartItemBinding
import com.example.algoliademo1.model.CartModel
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.CartRepository
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.google.firebase.firestore.ktx.toObject
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartAdapter(val onClickListener: CartOnClickListener) :
    ListAdapter<String, CartViewHolder>(CartAdapter) {
    val cartRepository = CartRepository.getRepository()
    val productRepository = ProductsRepository.getRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = CartItemBinding.inflate(view, parent, false)
        return CartViewHolder(binding, productRepository, cartRepository)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: called")
        val productId = getItem(position)
        holder.bind(productId)

        holder.binding.deleteImage.setOnClickListener {
            val priceText = holder.binding.cartItemPrice.text.toString()
            var price = 0.0f
            if(priceText != "")
                price = priceText.trimStart('$').toFloat()
            //bug occurs here sometimes
            onClickListener.onDeleteClick(productId, price)
        }

//        holder.itemView.setOnClickListener {
//            onClickListener.onItemClick(productId)
//        }

        holder.binding.cartItemName.setOnClickListener {
            onClickListener.onItemClick(productId)
        }

        holder.binding.cartItemImage.setOnClickListener{
            onClickListener.onItemClick(productId)
        }
        holder.binding.cartItemPrice.setOnClickListener {
            onClickListener.onItemClick(productId)
        }

        holder.binding.addButton.setOnClickListener {
            onClickListener.onIncrementClick(productId)
        }

        holder.binding.removeButton.setOnClickListener {
            onClickListener.onDecrementClick(productId)
        }

    }

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

class CartViewHolder(val binding: CartItemBinding, val productsRepository: ProductsRepository, val cartRepository: CartRepository) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(productId: String) {
        Log.d(TAG, "bind: method called")

        CoroutineScope(Dispatchers.Main).launch {
            // Need to update, convert firebase to repository
//            FirebaseService.testGetProductReference(productId).get().addOnSuccessListener {
//                val productModel = it.toObject<ProductModel>()

            val productModel = withContext(Dispatchers.IO) {
                productsRepository.getProduct(productId)
            }
                binding.cartItemName.text = productModel?.name

                binding.cartItemPrice.text = binding.cartItemPrice.context.getString(R.string.currency) + String.format("%.2f", productModel.price )

                Glide.with(binding.cartItemImage.context)
                    .load(productModel?.image)
                    .placeholder(R.drawable.spinner1)
                    .into(binding.cartItemImage)

            //}

            val productQuantity = withContext(Dispatchers.IO) {
                cartRepository.getProductQuantity(FirebaseService.userId, productId)
            }

            if(productQuantity == 1)
                binding.removeButton.visibility = View.INVISIBLE
            else
                binding.removeButton.visibility = View.VISIBLE

            if(productQuantity == 5)
                binding.addButton.visibility = View.INVISIBLE
            else
                binding.addButton.visibility = View.VISIBLE

            Log.d(TAG, "bind: before binding count")
            binding.count.text = productQuantity.toString()

            Log.d(TAG, "bind: after binding $productQuantity")

        }

    }
}


//        FirebaseService.getCartReference().get().addOnSuccessListener {
//            val cartModel = it.toObject<CartModel>()
//
//            binding.cartItemCount.text = cartModel?.products?.get(productId).toString() + " n."
//        }

class CartOnClickListener(
    val itemClickListener: (productId: String) -> Unit,
    val deleteClickListener: (productId: String, price: Float) -> Unit,
    val incrementClickListener: (productId: String) -> Unit,
    val decrementClickListener: (productId: String) -> Unit
) {

    fun onItemClick(productId: String) = itemClickListener(productId)

    fun onDeleteClick(productId: String, price: Float) = deleteClickListener(productId, price)

    fun onIncrementClick(productId: String) = incrementClickListener(productId)

    fun onDecrementClick(productId: String) = decrementClickListener(productId)
}