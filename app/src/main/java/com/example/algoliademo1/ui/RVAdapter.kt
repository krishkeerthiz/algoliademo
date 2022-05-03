package com.example.algoliademo1.ui

//import android.view.LayoutInflater
//import android.view.View.inflate
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewbinding.ViewBinding
//import androidx.viewbinding.ViewBindings
//import com.example.algoliademo1.databinding.ActivityMainBinding.inflate
//import com.example.algoliademo1.databinding.OrderCardBinding.inflate
//import com.example.algoliademo1.databinding.OrderItemBinding
//import com.example.algoliademo1.databinding.RatingDialogBinding.inflate
//import com.example.algoliademo1.model.ProductQuantityModel
//import com.example.algoliademo1.ui.orderdetail.OrderedItemsViewHolder

//open class RVAdapter<S : RecyclerView.ViewHolder, T, U: ViewBinding>(val fragmentBinding: U)  :
//    RecyclerView.Adapter<S>(){
//
//    protected var models: List<T> = listOf()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  S{
//    //    val view = LayoutInflater.from(parent.context)
////        val binding = fragmentBinding.infla
////        return S(binding)
//
//        return S()
//    }
//
//    override fun onBindViewHolder(holder: S, position: Int) {
//    }
//
//    override fun getItemCount() = models.size
//
//    fun addModels(modelList: List<T>){
//        models = modelList
//        notifyDataSetChanged()
//    }
//}