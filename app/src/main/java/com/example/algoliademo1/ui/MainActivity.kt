package com.example.algoliademo1.ui

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.algoliademo1.R
import com.example.algoliademo1.data.source.local.entity.Order
import com.example.algoliademo1.databinding.ActivityMainBinding
import com.example.algoliademo1.ui.address.AddressFragment
import com.example.algoliademo1.ui.cart.CartFragment
import com.example.algoliademo1.ui.filters.FacetFragment
import com.example.algoliademo1.ui.orders.OrdersFragment
import com.example.algoliademo1.ui.orderdetail.OrderDetailFragment
import com.example.algoliademo1.ui.products.ProductFragment
import com.example.algoliademo1.ui.prductdetail.ProductDetailFragment
import com.example.algoliademo1.ui.wishlist.WishlistFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.DocumentReference

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.setGraph(R.navigation.navigation_graph)

        binding.bottomNavigation.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.productFragment,
                R.id.cartFragment,
                R.id.wishlistFragment,
                R.id.ordersFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.bottomNavigation.setOnItemReselectedListener { menuItem ->
//            Log.d(TAG, "onCreate: bottom nav reselected")
//            navController.popBackStack(menuItem.itemId, inclusive = false)
//
//        }

//        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
//            Log.d(TAG, "onCreate: bottom nav selected")
//            //navController.popBackStack(menuItem.itemId, inclusive = false)
//
//            true
//        }
        
        navController.addOnDestinationChangedListener(this)
        //  showProductFragment()

//        val navigationItemSelectedListener =
//            BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.home -> {
//                        Log.d(TAG, "onCreate: inside home")
//                        showProductFragment()
//                        true
//                    }
//                    R.id.cart -> {
//                        Log.d(TAG, "onCreate: inside cart")
//                        //Toast.makeText(this, "cart clicked", Toast.LENGTH_SHORT).show()
//                        showCartFragment()
//                        true
//                    }
//                    R.id.wishlist -> {
//                        showWishlistFragment()
//                        true
//                    }
//                    R.id.orders -> {
//                        showOrdersFragment()
//                        true
//
//                    }
//                    else -> super.onOptionsItemSelected(menuItem)
//                }
//
//            }
//        binding.bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)

    }

    fun showProductFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ProductFragment())
            //.addToBackStack("Products page")
            .commit()
    }

    fun showFacetFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, FacetFragment())
            //.addToBackStack("facet")
            .commit()
    }

//    fun showProductDetailFragment(id: String) {
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.container, ProductDetailFragment(id))
//            .addToBackStack("Product detail")
//            .commit()
//    }

    fun showCartFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, CartFragment())
            .addToBackStack("Cart items")
            .commit()
    }

    fun showWishlistFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, WishlistFragment())
            //.addToBackStack("Cart items")
            .commit()
    }

    fun showAddressFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, AddressFragment())
            //.addToBackStack("Address fragment")
            .commit()
    }

    fun showOrdersFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, OrdersFragment())
            //.addToBackStack("Orders fragment")
            .commit()
    }

//    fun showOrderDetailFragment(order: Order) {
//        supportFragmentManager
//            .beginTransaction()
//            .replace(
//                R.id.container, OrderDetailFragment(order)
//            )  // replace document reference with order
//            .addToBackStack("Order Detail fragment")
//            .commit()
//
//    }

    override fun onSupportNavigateUp(): Boolean {
        //supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(destination.id){
            R.id.productDetailFragment -> {
                binding.bottomNavigation.visibility = View.GONE
            }
            R.id.orderDetailFragment -> {
                binding.bottomNavigation.visibility = View.GONE
            }
            R.id.facetFragment -> binding.bottomNavigation.visibility = View.GONE
            R.id.addressFragment -> binding.bottomNavigation.visibility = View.GONE

            else -> binding.bottomNavigation.visibility = View.VISIBLE
        }
    }

}
