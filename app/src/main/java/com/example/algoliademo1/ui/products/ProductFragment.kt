package com.example.algoliademo1.ui.products

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.item.StatsTextView
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.FragmentProductBinding
import com.example.algoliademo1.ui.MyViewModel
import com.example.algoliademo1.ui.signIn.SignInActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private val connection = ConnectionHandler()

    private lateinit var viewModel: MyViewModel

    private var currentVisiblePosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        Log.d(TAG, "onCreateView: product")
        viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductBinding.bind(view)

        val auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }

        binding.apply {
            shimmerFrameLayout.visibility = View.VISIBLE
            shimmerFrameLayout.startShimmer()
            productList.visibility = View.INVISIBLE
        }


        val adapterProduct = ProductAdapter(
            OnClickListener { id -> onItemClicked(id.removeSurrounding("\"", "\"")) }
        )

        viewModel.products.observe(viewLifecycleOwner) { hits ->

            binding.apply {
                shimmerFrameLayout.visibility = View.INVISIBLE
                shimmerFrameLayout.stopShimmer()
                productList.visibility = View.VISIBLE
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val products = viewModel.getProducts(hits)
                withContext(Dispatchers.Main){
                    adapterProduct.submitList(products)
                    if (hits.isEmpty()) {
                        binding.noResultImage.visibility = View.VISIBLE
                    } else {
                        binding.noResultImage.visibility = View.INVISIBLE
                        //adapterProduct.submitList(hits)
                    }
                }

            }


        }

        binding.productList.apply {
            itemAnimator = null
            adapter = adapterProduct
            layoutManager = GridLayoutManager(requireContext(), 2)
            autoScrollToStart(adapterProduct)

        }

        val searchBoxView = SearchBoxViewAppCompat(binding.searchView)

        val statsView = StatsTextView(binding.stats)

        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

    }

    override fun onPause() {
        super.onPause()
        currentVisiblePosition =
            (binding.productList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()
        (binding.productList.layoutManager as LinearLayoutManager).scrollToPosition(
            currentVisiblePosition
        )
        currentVisiblePosition = 0
    }

    private fun onItemClicked(id: String) {
        val currentDestinationIsProductsPage =
            this.findNavController().currentDestination == this.findNavController()
                .findDestination(R.id.productFragment)
        val currentDestinationIsProductsDetails =
            this.findNavController().currentDestination == this.findNavController()
                .findDestination(R.id.productDetailFragment)

        if (currentDestinationIsProductsPage && !currentDestinationIsProductsDetails) {
            val action = ProductFragmentDirections.actionProductFragmentToProductDetailFragment(id)
            view?.findNavController()?.navigate(action)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.product_fragment_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                signOut()
            }
            R.id.filter -> {
                gotoFilterFragment()
            }
            R.id.search -> {
                if (binding.searchView.visibility == View.GONE) {
                    binding.searchView.visibility = View.VISIBLE
                    binding.searchView.requestFocus()
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    item.icon = AppCompatResources.getDrawable(requireContext(),R.drawable.ic_baseline_close_24)
                }
                else {
                    binding.searchView.visibility = View.GONE
                    item.icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_search_24)
                    binding.searchView.setQuery("", true)
                    binding.searchView.clearFocus()
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun signOut() {
        val fragmentActivity = requireActivity()
        fragmentActivity.finish()
        AuthUI.getInstance().signOut(fragmentActivity)
        startActivity(Intent(fragmentActivity, SignInActivity::class.java))

    }

    private fun gotoFilterFragment() {
        val action = ProductFragmentDirections.actionProductFragmentToFacetFragment()
        view?.findNavController()?.navigate(action)
    }

}
