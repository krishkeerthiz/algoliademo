package com.example.algoliademo1

import android.view.View
import android.widget.Filter
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.core.Callback
import com.algolia.instantsearch.core.selectable.list.SelectableItem
import com.algolia.instantsearch.helper.hierarchical.HierarchicalItem
import com.algolia.instantsearch.helper.hierarchical.HierarchicalView

//class HierarchicalAdapter: HierarchicalView,
//    ListAdapter<HierarchicalItem, HierarchicalViewHolder>(DiffUtilItem())  {
//    override var onSelectionChanged: Callback<String>? = null
//
//
//    override fun setTree(tree: List<HierarchicalItem>) {
//        submitList(tree)
//    }
//}
//
//class DiffUtilItem<T : Filter> : DiffUtil.ItemCallback<SelectableItem<T>>() {
//    override fun areItemsTheSame(oldItem: SelectableItem<T>, newItem: SelectableItem<T>): Boolean {
//        return oldItem.first == newItem.first
//    }
//
//    override fun areContentsTheSame(oldItem: SelectableItem<T>, newItem: SelectableItem<T>): Boolean {
//        return oldItem == newItem
//    }
//}
//
//class HierarchicalViewHolder(val view: View): RecyclerView.ViewHolder(view){
//    fun bind(){
//        v
//    }
//}