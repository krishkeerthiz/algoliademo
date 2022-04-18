package com.example.algoliademo1.ui.products

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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

class ProductFragment : Fragment() {
    val menuPreference = "COMPLETED_ONBOARDING_MENU"
    private lateinit var binding: FragmentProductBinding
    private val connection = ConnectionHandler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onCreateView: product fragment")
        binding = FragmentProductBinding.bind(view)
        Log.d(TAG, "onCreateView: product fragment1")
        val viewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        Log.d(TAG, "onCreateView: product fragment2")
        val auth = Firebase.auth
        Log.d(TAG, "onCreateView: product fragment3")
        if (auth.currentUser == null) {
            Log.d(TAG, "onCreateView: product fragment4")
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            Log.d(TAG, "onCreateView: product fragment5")
            requireActivity().finish()
            Log.d(TAG, "onCreateView: product fragment6")
        }

        Log.d(TAG, "onCreateView: product fragment7")
        binding.shimmerFrameLayout.visibility = View.VISIBLE
        binding.shimmerFrameLayout.startShimmer()
        binding.productList.visibility = View.INVISIBLE

        val adapterProduct = ProductAdapter(
            OnClickListener { product -> onItemClicked(product.id.removeSurrounding("\"", "\"")) }
        )

        Log.d(TAG, "onCreateView: product fragment3")
        viewModel.products.observe(viewLifecycleOwner) { hits ->

            binding.shimmerFrameLayout.visibility = View.INVISIBLE
            binding.shimmerFrameLayout.stopShimmer()
            binding.productList.visibility = View.VISIBLE
            adapterProduct.submitList(hits)
            if (hits.isEmpty()) {
                binding.noResultImage.visibility = View.VISIBLE
            } else {
                binding.noResultImage.visibility = View.INVISIBLE
                adapterProduct.submitList(hits)
            }
        }

        binding.productList.let {
            it.itemAnimator = null
            it.adapter = adapterProduct
            it.layoutManager = GridLayoutManager(requireContext(), 2)
            it.autoScrollToStart(adapterProduct)

        }

        Log.d(TAG, "onCreateView: product fragment4")

        val searchBoxView = SearchBoxViewAppCompat(binding.searchView)

        val statsView = StatsTextView(binding.stats)

        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        Log.d(TAG, "onCreateView: product fragment5")
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
        return when (item.itemId) {
            R.id.sign_out -> {
                signOut()
                true
            }
            R.id.filter -> {
                gotoFilterFragment()
                true
            }
            R.id.search -> {
                //showSearchView()
                //item.isVisible = false
                if (binding.searchView.visibility == View.GONE) {
                    binding.searchView.visibility = View.VISIBLE
                    binding.searchView.requestFocus()
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    item.icon = context?.getDrawable(R.drawable.ic_baseline_close_24)
                } else {
                    binding.searchView.visibility = View.GONE
                    item.icon = context?.getDrawable(R.drawable.ic_baseline_search_24)
                    binding.searchView.setQuery("", true)
                    binding.searchView.clearFocus()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun shimmerOff() {
        binding.shimmerFrameLayout.visibility = View.INVISIBLE
        binding.shimmerFrameLayout.stopShimmer()
        binding.productList.visibility = View.VISIBLE
    }

}
