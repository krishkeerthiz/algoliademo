package com.example.algoliademo1

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.algoliademo1.data.source.local.ShoppingRoomDatabase
import com.example.algoliademo1.databinding.ProductCardBinding
import com.example.algoliademo1.model.ProductInfo
import com.example.algoliademo1.data.source.repository.ProductsRepository
import com.example.algoliademo1.databinding.FragmentProductBinding
import com.example.algoliademo1.ui.products.ProductFragment
import com.squareup.picasso.Picasso
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
        val productBinding = FragmentProductBinding.inflate(view)
        val binding = ProductCardBinding.inflate(view, parent, false)
        return ProductViewHolder(binding, productBinding, productsRepository)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        //holder.binding.productImage.setImageDrawable(h)
        val product = getItem(position)
        if (product != null) {
            holder.bind(product, onClickListener)
//            val productModel = FirebaseService.getProduct(product.id)
//            if (productModel != null) holder.bind(productModel)
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
    val productBinding: FragmentProductBinding,
    val productsRepository: ProductsRepository
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(productInfo: ProductInfo, onClickListener: OnClickListener) {
//        val dummyImage = binding.productImage.context.getDrawable(R.drawable.plainbackground)
//        binding.productImage.setImageDrawable(dummyImage)

//        Glide.with(binding.productImage.context)
//            .asGif()
//            .load(R.drawable.spinner)
//            .into(binding.productImage)
//        binding.productName.text = productModel.name
//        binding.productBrand.text = productModel.brand
//        FirebaseService.testGetProductReference(productInfo.id).get().addOnSuccessListener {
//            val productModel = it.toObject<ProductModel>()

        //val database = ShoppingApplication.instance?.database

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
//                binding.card.isClickable = false
//                binding.shimmerFrameLayout.isClickable = false
//                bind(productInfo, onClickListener)
            }

            Log.d(TAG, "Product adapter : outside loop")
            binding.shimmerFrameLayout.visibility = View.INVISIBLE
            binding.shimmerFrameLayout.stopShimmer()
            binding.card.visibility = View.VISIBLE
            this@ProductViewHolder.itemView.isClickable = true
//                binding.card.isClickable = true
//                binding.shimmerFrameLayout.isClickable = true

            //onClickListener.onShimmerOff()
            Log.d(TAG, "bind: productModel ${productModel.toString()}")

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
    val clickListener: (productInfo: ProductInfo) -> Unit,
    val shimmerOff: () -> Unit
) {
    fun onClick(productInfo: ProductInfo) = clickListener(productInfo)
    fun onShimmerOff() = shimmerOff
}


