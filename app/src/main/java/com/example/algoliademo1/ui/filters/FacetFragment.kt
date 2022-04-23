package com.example.algoliademo1.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.filter.clear.FilterClearViewImpl
import com.algolia.instantsearch.helper.android.filter.facet.FacetListAdapter
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.filter.clear.connectView
import com.algolia.instantsearch.helper.filter.facet.connectView
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentFacetBinding
import com.example.algoliademo1.ui.MyViewModel

class FacetFragment : Fragment() {

    private val connection = ConnectionHandler()
    private lateinit var binding: FragmentFacetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_facet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFacetBinding.bind(view)

        val viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val adapterFacet = FacetListAdapter(MyFacetListViewHolder.Factory)

        val adapterFacet2 = FacetListAdapter(MyFacetListViewHolder2.Factory)
        val adapterFacet3 = FacetListAdapter(MyFacetListViewHolder3.Factory)

        val adapterFacet4 = FacetListAdapter(MyFacetListViewHolder4.Factory)

        binding.applyFilter.setOnClickListener {
            val action = FacetFragmentDirections.actionFacetFragmentToProductFragment()
            view.findNavController().navigate(action)
        }

        binding.clearFilter.setOnClickListener {
            val action = FacetFragmentDirections.actionFacetFragmentToProductFragment()
            view.findNavController().navigate(action)
        }

        binding.facetList.let {
            it.adapter = adapterFacet
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapterFacet)
        }

        binding.facetList2.let {
            it.adapter = adapterFacet2
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapterFacet)
        }

        binding.facetList3.let {
            it.adapter = adapterFacet3
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapterFacet)
        }

        binding.facetList4.let {
            it.adapter = adapterFacet4
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapterFacet)
        }

        connection += viewModel.facetList.connectView(adapterFacet, viewModel.facetPresenter)
        connection += viewModel.facetList2.connectView(adapterFacet2, viewModel.facetPresenter)
        connection += viewModel.facetList3.connectView(adapterFacet3, viewModel.facetPresenter)
        connection += viewModel.facetList4.connectView(adapterFacet4, viewModel.facetPresenter)
        connection += viewModel.clearAll.connectView(FilterClearViewImpl(binding.clearFilter))

        binding.filterGroup.setOnCheckedChangeListener { _, chipId ->
            when (chipId) {
                R.id.categoryChip -> {
                    binding.facetList.visibility = View.VISIBLE
                    binding.facetList2.visibility = View.GONE
                    binding.facetList3.visibility = View.GONE
                    binding.facetList4.visibility = View.GONE
                }
                R.id.typeChip -> {
                    binding.facetList.visibility = View.GONE
                    binding.facetList2.visibility = View.VISIBLE
                    binding.facetList3.visibility = View.GONE
                    binding.facetList4.visibility = View.GONE
                }
                R.id.brandChip -> {
                    binding.facetList.visibility = View.GONE
                    binding.facetList2.visibility = View.GONE
                    binding.facetList3.visibility = View.VISIBLE
                    binding.facetList4.visibility = View.GONE
                }
                R.id.priceRangeChip -> {
                    binding.facetList.visibility = View.GONE
                    binding.facetList2.visibility = View.GONE
                    binding.facetList3.visibility = View.GONE
                    binding.facetList4.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
    }
}
