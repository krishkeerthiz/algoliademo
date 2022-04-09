package com.example.algoliademo1

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.algoliademo1.databinding.ProductCardBinding
import com.example.algoliademo1.model.ProductInfo
import com.example.algoliademo1.data.source.repository.ProductsRepository
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
        val binding = ProductCardBinding.inflate(view, parent, false)
        return ProductViewHolder(binding, productsRepository)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        //holder.binding.productImage.setImageDrawable(h)
        val product = getItem(position)
        if(product != null){
            holder.bind(product)
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

class ProductViewHolder(val binding: ProductCardBinding, val productsRepository: ProductsRepository) : RecyclerView.ViewHolder(binding.root){

    fun bind(productInfo: ProductInfo) {
        val dummyImage = binding.productImage.context.getDrawable(R.drawable.plainbackground)
        binding.productImage.setImageDrawable(dummyImage)
//        binding.productName.text = productModel.name
//        binding.productBrand.text = productModel.brand
//        FirebaseService.testGetProductReference(productInfo.id).get().addOnSuccessListener {
//            val productModel = it.toObject<ProductModel>()

        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "bind: ${productInfo.id}")

            val productModel = withContext(Dispatchers.IO){
                productsRepository.getProduct(productInfo.id.removeSurrounding("\"", "\""))
            }

            if(productModel == null)
                bind(productInfo)

            Log.d(TAG, "bind: ${productModel.toString()}")

            binding.productName.text = productModel?.name
            binding.productBrand.text = productModel?.brand

            binding.productPrice.text = binding.productPrice.context.getString(R.string.currency) + productModel?.price

            binding.productRating.rating = productModel?.rating?.div(2)?.toFloat() ?: 2.5f

            Glide.with(binding.productImage.context)
                .load(productModel?.image)
                .into(binding.productImage)
//            val builder = Picasso.Builder(binding.productImage.context)
//
//            //with(binding.productImage.context).cancelRequest(binding.productImage)
////             builder.build().cancelRequest(binding.productImage)
//            builder.build()
//                .load(productModel?.image)
//                .into(binding.productImage)
        }
    }
}

class OnClickListener(val clickListener: (productInfo: ProductInfo) -> Unit){
    fun onClick(productInfo: ProductInfo) = clickListener(productInfo)
}


