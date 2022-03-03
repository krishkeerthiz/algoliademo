package com.example.algoliademo1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.item.StatsTextView
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.example.algoliademo1.databinding.FragmentProductBinding

class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private val connection = ConnectionHandler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductBinding.bind(view)

        val viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val adapterProduct = ProductAdapter()
        viewModel.products.observe(viewLifecycleOwner, Observer { hits -> adapterProduct.submitList(hits) })
        binding.productList.let {
            it.itemAnimator = null
            it.adapter = adapterProduct
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapterProduct)
        }

        val searchBoxView = SearchBoxViewAppCompat(binding.searchView)

        val statsView = StatsTextView(binding.stats)

        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        binding.filters.setOnClickListener {
            Toast.makeText(requireContext(), "Test", Toast.LENGTH_SHORT).show()
            (requireActivity() as MainActivity).showFacetFragment()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
    }
}
