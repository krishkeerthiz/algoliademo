package com.example.algoliademo1.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.item.StatsTextView
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.example.algoliademo1.*
import com.example.algoliademo1.databinding.FragmentProductBinding
import com.example.algoliademo1.ui.MainActivity
import com.example.algoliademo1.ui.MyViewModel
import com.example.algoliademo1.ui.signIn.SignInActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private val connection = ConnectionHandler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductBinding.bind(view)

        val viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val auth = Firebase.auth
        if(auth.currentUser == null){
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
            requireActivity().finish()
            return
        }

        val adapterProduct = ProductAdapter(
            OnClickListener{
                    product -> onItemClicked(product.id.removeSurrounding("\"", "\""))
            }
        )

        viewModel.products.observe(viewLifecycleOwner, Observer { hits -> adapterProduct.submitList(hits) })

        binding.productList.let {
            it.itemAnimator = null
            it.adapter = adapterProduct
            it.layoutManager = GridLayoutManager(requireContext(), 2)
            it.autoScrollToStart(adapterProduct)
        }

        val searchBoxView = SearchBoxViewAppCompat(binding.searchView)

        val statsView = StatsTextView(binding.stats)

        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        binding.filters.setOnClickListener {
            //Toast.makeText(requireContext(), "Test", Toast.LENGTH_SHORT).show()
            (requireActivity() as MainActivity).showFacetFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
    }

    private fun onItemClicked(id: String){
        (requireActivity() as MainActivity).showProductDetailFragment(id)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.product_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sign_out ->{
                signOut()
                true
            }
            R.id.cart -> {
                (requireActivity() as MainActivity).showCartFragment()
                true
            }
            R.id.wishlist -> {
                (requireActivity() as MainActivity).showWishlistFragment()
                true
            }
            R.id.orders ->{
                (requireActivity() as MainActivity).showOrdersFragment()
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    private fun signOut(){
        AuthUI.getInstance().signOut(requireActivity())
        startActivity(Intent(requireActivity(), SignInActivity::class.java))
        requireActivity().finish()
    }
}
