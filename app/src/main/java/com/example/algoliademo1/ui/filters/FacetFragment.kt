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
import com.algolia.instantsearch.helper.filter.clear.connectView
import com.algolia.instantsearch.helper.filter.facet.connectView
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentFacetBinding
import com.example.algoliademo1.ui.ProductsFiltersViewModel
import com.example.algoliademo1.ui.ProductsFiltersViewModelFactory

class FacetFragment : Fragment() {

    private val connection = ConnectionHandler()
    private lateinit var binding: FragmentFacetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_facet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFacetBinding.bind(view)

        //connection.clear()

        val viewModelFactory = ProductsFiltersViewModelFactory(requireContext())
        val viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ProductsFiltersViewModel::class.java]

        val adapterFacet1 = FacetListAdapter(MyFacetListViewHolder1.Factory)
        val adapterFacet2 = FacetListAdapter(MyFacetListViewHolder2.Factory)
        val adapterFacet3 = FacetListAdapter(MyFacetListViewHolder3.Factory)
        val adapterFacet4 = FacetListAdapter(MyFacetListViewHolder4.Factory)

        binding.applyFilter.setOnClickListener {
            val action = FacetFragmentDirections.actionFacetFragmentToProductFragment()
            view.findNavController().navigate(action)
        }
//
//        binding.clearFilter.setOnClickListener {
//            val action = FacetFragmentDirections.actionFacetFragmentToProductFragment()
//            view.findNavController().navigate(action)
//        }

        // Setting default selected chip
        binding.categoryChip.isChecked = true

        binding.facetList1.apply {
            adapter = adapterFacet1
            layoutManager = LinearLayoutManager(requireContext())
            //autoScrollToStart(adapterFacet1)
        }

        binding.facetList2.apply {
            adapter = adapterFacet2
            layoutManager = LinearLayoutManager(requireContext())
            //autoScrollToStart(adapterFacet1)
        }

        binding.facetList3.apply {
            adapter = adapterFacet3
            layoutManager = LinearLayoutManager(requireContext())
            //autoScrollToStart(adapterFacet1)
        }

        binding.facetList4.apply {
            adapter = adapterFacet4
            layoutManager = LinearLayoutManager(requireContext())
            //autoScrollToStart(adapterFacet1)
        }

        connection += viewModel.facetList1.connectView(adapterFacet1, viewModel.facetPresenter)
        connection += viewModel.facetList2.connectView(adapterFacet2, viewModel.facetPresenter)
        connection += viewModel.facetList3.connectView(adapterFacet3, viewModel.facetPresenter)
        connection += viewModel.facetList4.connectView(adapterFacet4, viewModel.facetPresenter)
        connection += viewModel.clearAll.connectView(FilterClearViewImpl(binding.clearFilter))

        binding.filterGroup.setOnCheckedChangeListener { _, chipId ->
            when (chipId) {
                R.id.categoryChip -> {
                    binding.apply {
                        facetList1.visibility = View.VISIBLE
                        facetList2.visibility = View.GONE
                        facetList3.visibility = View.GONE
                        facetList4.visibility = View.GONE
                    }
                }
                R.id.typeChip -> {
                    binding.apply {
                        facetList1.visibility = View.GONE
                        facetList2.visibility = View.VISIBLE
                        facetList3.visibility = View.GONE
                        facetList4.visibility = View.GONE
                    }
                }
                R.id.brandChip -> {
                    binding.apply {
                        facetList1.visibility = View.GONE
                        facetList2.visibility = View.GONE
                        facetList3.visibility = View.VISIBLE
                        facetList4.visibility = View.GONE
                    }
                }
                R.id.priceRangeChip -> {
                    binding.apply {
                        facetList1.visibility = View.GONE
                        facetList2.visibility = View.GONE
                        facetList3.visibility = View.GONE
                        facetList4.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
    }

}
