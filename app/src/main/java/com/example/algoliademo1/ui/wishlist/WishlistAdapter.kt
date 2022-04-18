package com.example.algoliademo1

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.databinding.WishlistItemBinding
import com.example.algoliademo1.model.ProductModel
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WishlistAdapter(val onClickListener: WishlistClickListener) : ListAdapter<String, WishlistViewHolder>(WishlistAdapter) {

    private val productsRepository = ProductsRepository.getRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = WishlistItemBinding.inflate(view, parent, false)
        return WishlistViewHolder(binding, productsRepository)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val productId = getItem(position)
        Log.d("bind viewholder pid", productId)
        holder.bind(productId)

        holder.binding.addToCartButton.setOnClickListener {
            val price = holder.binding.wishlistItemPrice.text.trimStart('$').toString().toFloat()
            onClickListener.onAddItemClick(productId, price)
            Toast.makeText(it.context, "Added to cart", Toast.LENGTH_SHORT).show()
        }

        holder.binding.removeButton.setOnClickListener {
            onClickListener.onRemoveItemClick(productId)

        }

        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(productId)
            //Toast.makeText(it.context, "Item clicked", Toast.LENGTH_SHORT).show()
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

class WishlistViewHolder(val binding: WishlistItemBinding, val productsRepository: ProductsRepository) : RecyclerView.ViewHolder(binding.root){

    fun bind(productId: String){
        Log.d("wishlist adapter", productId)
        if(productId != "")
//        FirebaseService.testGetProductReference(productId).get().addOnSuccessListener {
//            val productModel = it.toObject<ProductModel>()

    CoroutineScope(Dispatchers.Main).launch {

        val productModel = withContext(Dispatchers.IO){
            productsRepository.getProduct(productId)
        }
        binding.wishlistItemName.text = productModel?.name

        binding.wishlistItemPrice.text = binding.wishlistItemPrice.context.getString(R.string.currency) + productModel?.price

        Glide.with(binding.cartItemImage.context)
            .load(productModel?.image)
            .placeholder(R.drawable.spinner1)
            .into(binding.cartItemImage)
    }

      //  }
    }
}

class WishlistClickListener(val itemClickListener: (productId: String) -> Unit,
    val addItemClickListener: (productId: String, price: Float) -> Unit,
    val removeItemClickListener: (productId: String) ->Unit){

    fun onItemClick(productId: String) = itemClickListener(productId)

    fun onAddItemClick(productId: String, price: Float) = addItemClickListener(productId, price)

    fun onRemoveItemClick(productId: String) = removeItemClickListener(productId)
}