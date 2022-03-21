package com.example.algoliademo1.ui.filters

import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.helper.android.filter.facet.FacetListViewHolder
import com.algolia.instantsearch.helper.android.inflate
import com.algolia.search.model.search.Facet
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FacetItemBinding

class MyFacetListViewHolder3(view: View) : FacetListViewHolder(view) {
    override fun bind(facet: Facet, selected: Boolean, onClickListener: View.OnClickListener) {
        val binding = FacetItemBinding.bind(view)
        view.setOnClickListener(onClickListener)
        binding.facetCount.text = facet.count.toString()
        binding.facetCount.visibility = View.VISIBLE
        binding.icon.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        binding.facetName.text = facet.value
    }

    object Factory : FacetListViewHolder.Factory {
        override fun createViewHolder(parent: ViewGroup): FacetListViewHolder {
//            val view = LayoutInflater.from(parent.context)
//            val binding = FacetItemBinding.inflate(view, parent, false)
            return MyFacetListViewHolder(parent.inflate(R.layout.facet_item))
        }
    }
}