package com.example.algoliademo1.ui.products

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.item.StatsTextView
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.remote.FirebaseService
import com.example.algoliademo1.databinding.FragmentProductBinding
import com.example.algoliademo1.model.ProductInfo
import com.example.algoliademo1.ui.ProductsFiltersViewModel
import com.example.algoliademo1.ui.signIn.SignInActivity
import com.example.algoliademo1.util.NetworkUtil
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding

    private val connection = ConnectionHandler()

    private lateinit var viewModel: ProductsFiltersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        Log.d(TAG, "onCreateView: product fragment")
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter
        val productAdapter = ProductAdapter(
            OnClickListener { id -> onItemClicked(id.removeSurrounding("\"", "\"")) },
        ).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        viewModel = ViewModelProvider(requireActivity())[ProductsFiltersViewModel::class.java]

        binding = FragmentProductBinding.bind(view)

        handleSignIn()

        // Shimmer effect start
        binding.apply {
            shimmerFrameLayout.visibility = View.VISIBLE
            shimmerFrameLayout.startShimmer()
            productList.visibility = View.INVISIBLE
        }

        // Recyclerview
        binding.productList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
            autoScrollToStart(productAdapter)  // Goes to start, can see while searching
        }

        // Live data
        viewModel.products.observe(viewLifecycleOwner) { hits ->  // Observe causes delay, so incorrect data has loaded

            if (hits.isEmpty()) {
                binding.emptyLayout.visibility = View.VISIBLE
                binding.productList.visibility = View.INVISIBLE

                if(NetworkUtil.isNetworkAvailable(requireContext()))
                    binding.retryButton.visibility = View.GONE
                else
                    binding.retryButton.visibility = View.VISIBLE

            } else {
                binding.emptyLayout.visibility = View.INVISIBLE
                binding.productList.visibility = View.VISIBLE

                lifecycleScope.launch(Dispatchers.IO) {
                    val productInfos = hits as List<ProductInfo>

                    val products = viewModel.getProducts(productInfos)

                    withContext(Dispatchers.Main) {
                        // Shimmer off
                        binding.apply {
                            shimmerFrameLayout.visibility = View.INVISIBLE
                            shimmerFrameLayout.stopShimmer()
                            productList.visibility = View.VISIBLE
                        }

                        productAdapter.addProducts(products)
                    }
                }
            }

        }

        binding.retryButton.setOnClickListener {

            if (NetworkUtil.isNetworkAvailable(requireContext()))
                restart()
            else
                Toast.makeText(
                    requireContext(),
                    "Please check internet connection",
                    Toast.LENGTH_SHORT
                ).show()

        }

        val searchBoxView = SearchBoxViewAppCompat(binding.searchView)

        val statsView = StatsTextView(binding.stats)

        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        Log.d(TAG, "onViewCreated: product fragment")
    }

    private fun restart() {
        requireActivity().finish()
        //requireActivity().overridePendingTransition(0, 0)
        startActivity(requireActivity().intent)
        //requireActivity().overridePendingTransition(0, 0)
    }

    private fun handleSignIn() {
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }
    }


    // handles error(View cannot call navigate to product detail fragment)
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

        val addProductsMenuItem = menu.findItem(R.id.add_product)

        addProductsMenuItem.isVisible = FirebaseService.userId == "b30r97HxJCgED0Y6JYd8L1j9yEj1"
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
                search(item)
            }
            R.id.add_product -> {
                gotoAddProductDetailFragment()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun gotoAddProductDetailFragment() {
        val action = ProductFragmentDirections.actionProductFragmentToAddProductDetailFragment()
        view?.findNavController()?.navigate(action)
    }

    private fun search(searchItem: MenuItem) {
        if (binding.searchView.visibility == View.GONE) {
            binding.searchView.visibility = View.VISIBLE
            binding.searchView.requestFocus()
            val imm =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            searchItem.icon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_close_24)
        } else {
            binding.searchView.visibility = View.GONE
            searchItem.icon =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_search_24)

            //binding.searchView.setQuery("", true)
            binding.searchView.clearFocus()
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

}
