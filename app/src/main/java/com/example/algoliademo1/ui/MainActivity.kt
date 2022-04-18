package com.example.algoliademo1.ui

import android.content.IntentFilter
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.algoliademo1.R
import com.example.algoliademo1.databinding.ActivityMainBinding
import com.example.algoliademo1.util.NetworkReceiver
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    val bottomNavPreference = "COMPLETED_ONBOARDING_BOTTOM_NAVIGATION"
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

        navController.addOnDestinationChangedListener(this)

        val sharedPreferences = getSharedPreferences("Shopizy", MODE_PRIVATE)
        val firstTime = sharedPreferences.getBoolean(bottomNavPreference, false) // put key in constant

        // Tap targets
        if(!firstTime){
            TapTargetSequence(this).targets(
                TapTarget.forView(view.findViewById(R.id.productFragment), "Home page", "You can find all products here")
                    .outerCircleColor(R.color.teal_200)
                    .outerCircleAlpha(0.96f)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .titleTextColor(R.color.white)
                    .descriptionTextSize(10)
                    .descriptionTextColor(R.color.black)
                    .textColor(R.color.black)
                    .textTypeface(Typeface.SANS_SERIF)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(true)
                    .transparentTarget(true)
                    .targetRadius(60),
                TapTarget.forView(view.findViewById(R.id.cartFragment), "Cart", "You can find all cart items here")
                    .outerCircleColor(R.color.teal_200)
                    .outerCircleAlpha(0.96f)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .titleTextColor(R.color.white)
                    .descriptionTextSize(10)
                    .descriptionTextColor(R.color.black)
                    .textColor(R.color.black)
                    .textTypeface(Typeface.SANS_SERIF)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(true)
                    .transparentTarget(true)
                    .targetRadius(60),
                TapTarget.forView(view.findViewById(R.id.wishlistFragment), "Wishlist", "You can find all wish listed items here")
                    .outerCircleColor(R.color.teal_200)
                    .outerCircleAlpha(0.96f)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .titleTextColor(R.color.white)
                    .descriptionTextSize(10)
                    .descriptionTextColor(R.color.black)
                    .textColor(R.color.black)
                    .textTypeface(Typeface.SANS_SERIF)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(true)
                    .transparentTarget(true)
                    .targetRadius(60),
                TapTarget.forView(view.findViewById(R.id.ordersFragment), "Orders", "You can find all orders here")
                    .outerCircleColor(R.color.teal_200)
                    .outerCircleAlpha(0.96f)
                    .targetCircleColor(R.color.white)
                    .titleTextSize(20)
                    .titleTextColor(R.color.white)
                    .descriptionTextSize(10)
                    .descriptionTextColor(R.color.black)
                    .textColor(R.color.black)
                    .textTypeface(Typeface.SANS_SERIF)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(true)
                    .transparentTarget(true)
                    .targetRadius(60),
            ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    val sharedPreferencesEdit = sharedPreferences.edit()
                    sharedPreferencesEdit.putBoolean(bottomNavPreference, true)
                    sharedPreferencesEdit.apply()
                }

                override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                    Toast.makeText(this@MainActivity, "GREAT!", Toast.LENGTH_SHORT).show()
                }

                override fun onSequenceCanceled(lastTarget: TapTarget) {}
            }).start()
        }


        checkInternet()

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

    private fun checkInternet(){
        registerReceiver(NetworkReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

}
