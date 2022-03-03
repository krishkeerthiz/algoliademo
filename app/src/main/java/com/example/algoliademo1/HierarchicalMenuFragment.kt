package com.example.algoliademo1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.hierarchical.HierarchicalPresenterImpl
import com.algolia.instantsearch.helper.hierarchical.HierarchicalView


//class HierarchicalMenuFragment : Fragment() {
//
//    private val connection = ConnectionHandler()
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_hierarchical_menu, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val view : HierarchicalView = HierarchicalAdapter() // your HierarchicalView implementation
//        connection += hierarchical.connectView(view, HierarchicalPresenterImpl(separator))
//    }
//}