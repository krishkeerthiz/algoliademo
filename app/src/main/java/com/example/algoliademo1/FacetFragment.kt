package com.example.algoliademo1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.filter.facet.FacetListAdapter
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.filter.facet.connectView
import com.example.algoliademo1.databinding.FragmentFacetBinding

class FacetFragment: Fragment(){

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

        Toast.makeText(requireContext(), "navigated to facet fragment", Toast.LENGTH_SHORT).show()

        binding.facetList.let {
            it.adapter = adapterFacet
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapterFacet)
        }

        binding.facetList2.let{
            it.adapter = adapterFacet2
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapterFacet)
        }

        connection += viewModel.facetList.connectView(adapterFacet, viewModel.facetPresenter)
        connection += viewModel.facetList2.connectView(adapterFacet2, viewModel.facetPresenter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
    }
}
