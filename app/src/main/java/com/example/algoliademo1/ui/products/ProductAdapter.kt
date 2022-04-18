package com.example.algoliademo1.ui.products

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.databinding.ProductCardBinding
import com.example.algoliademo1.model.ProductInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductAdapter(
    val onClickListener: OnClickListener
) : ListAdapter<ProductInfo, ProductViewHolder>(ProductAdapter) {  // PagedListAdapter is converted to ListAdapter

    private val productsRepository = ProductsRepository.getRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ProductCardBinding.inflate(view, parent, false)
        return ProductViewHolder(binding, productsRepository)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = getItem(position)
        if (product != null) {
            holder.bind(product)
        }

        holder.itemView.setOnClickListener {
            onClickListener.onClick(product!!)
        }
    }

    companion object : DiffUtil.ItemCallback<ProductInfo>() {

        override fun areItemsTheSame(
            oldItem: ProductInfo,
            newItem: ProductInfo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: ProductInfo,
            newItem: ProductInfo
        ): Boolean {
            return oldItem == newItem
        }
    }

}

class ProductViewHolder(
    val binding: ProductCardBinding,
    val productsRepository: ProductsRepository
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(productInfo: ProductInfo) {

        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "bind: id ${productInfo.id}")

            var productModel = withContext(Dispatchers.IO) {
                productsRepository.getProduct(productInfo.id.removeSurrounding("\"", "\""))
            }

            binding.shimmerFrameLayout.visibility = View.VISIBLE
            binding.shimmerFrameLayout.startShimmer()
            binding.card.visibility = View.INVISIBLE
            this@ProductViewHolder.itemView.isClickable = false

            while (productModel == null) {
                productModel = withContext(Dispatchers.IO) {
                    productsRepository.getProduct(productInfo.id.removeSurrounding("\"", "\""))
                }

                Log.d(TAG, "Product adapter : inside loop")
            }

            Log.d(TAG, "Product adapter : outside loop")
            binding.shimmerFrameLayout.visibility = View.INVISIBLE
            binding.shimmerFrameLayout.stopShimmer()
            binding.card.visibility = View.VISIBLE
            this@ProductViewHolder.itemView.isClickable = true

            Log.d(TAG, "bind: productModel $productModel")

            binding.productName.text = productModel?.name
            binding.productBrand.text = productModel?.brand

            binding.productPrice.text =
                binding.productPrice.context.getString(R.string.currency) + productModel?.price

            binding.productRating.rating = productModel?.rating?.div(2)?.toFloat() ?: 2.5f

            Glide.with(binding.productImage.context)
                .load(productModel?.image)
                .placeholder(R.drawable.spinner1)
                .into(binding.productImage)
        }
        Log.d(TAG, "bind: db loaded")
    }
}

class OnClickListener(
    val clickListener: (productInfo: ProductInfo) -> Unit
) {
    fun onClick(productInfo: ProductInfo) = clickListener(productInfo)
}


