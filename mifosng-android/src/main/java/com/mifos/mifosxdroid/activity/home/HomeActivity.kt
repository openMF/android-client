package com.mifos.mifosxdroid.activity.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.test.espresso.IdlingResource
import com.google.android.material.navigation.NavigationView
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivityHomeBinding
import com.mifos.mifosxdroid.databinding.ViewNavDrawerHeaderBinding
import com.mifos.utils.Constants
import com.mifos.utils.EspressoIdlingResource
import com.mifos.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by shashankpriyadarshi on 19/06/20.
 */
@AndroidEntryPoint
open class HomeActivity : MifosBaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navHeaderBinding: ViewNavDrawerHeaderBinding

    private lateinit var menu: Menu
    private lateinit var userStatusToggle: SwitchCompat
    private var doubleBackToExitPressedOnce = false
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration.Builder()
            .setDrawerLayout(binding.drawer)
            .build()

        setupNavigationBar()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    binding.drawer.closeDrawer(GravityCompat.START)
                } else if (binding.navView.selectedItemId == R.id.navigation_dashboard) {
                    doubleBackToExit()
                }
                supportFragmentManager.popBackStackImmediate()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        binding.navView.setupWithNavController(binding.navHostFragment.findNavController())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // ignore the current selected item
        /*if (item.isChecked()) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return false;
        }*/
        clearFragmentBackStack()
        when (item.itemId) {
            R.id.item_checker_inbox -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.checkerInboxPendingTasksActivity)
            }

            R.id.item_path_tracker -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.pathTrackingActivity)
            }

            R.id.item_offline -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.offlineDashboardFragment)
            }

            R.id.individual_collection_sheet -> {
                val bundle = Bundle()
                bundle.putString(Constants.COLLECTION_TYPE, Constants.EXTRA_COLLECTION_INDIVIDUAL)
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.generateCollectionSheetActivity,
                    bundle
                )
            }

            R.id.collection_sheet -> {
                val bundle = Bundle()
                bundle.putString(
                    Constants.COLLECTION_TYPE,
                    Constants.EXTRA_COLLECTION_COLLECTION
                )
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.generateCollectionSheetActivity,
                    bundle
                )
            }

            R.id.item_settings -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.settingsActivity)
            }

            R.id.runreport -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.runReportsActivity)
            }

            R.id.about -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.aboutActivity)
            }
        }
        binding.drawer.closeDrawer(GravityCompat.START)
        return false
    }

    /**
     * This SwitchCompat Toggle Handling the User Status.
     * Setting the User Status to Offline or Online
     */
    private fun setupUserStatusToggle(headerView: View) {
        navHeaderBinding = ViewNavDrawerHeaderBinding.bind(headerView)
        userStatusToggle = navHeaderBinding.userStatusToggle

        if (PrefManager.userStatus == Constants.USER_OFFLINE) {
            userStatusToggle.isChecked = true
        }
        userStatusToggle.setOnClickListener {
            if (PrefManager.userStatus == Constants.USER_OFFLINE) {
                PrefManager.userStatus = Constants.USER_ONLINE
                userStatusToggle.isChecked = false
            } else {
                PrefManager.userStatus = Constants.USER_OFFLINE
                userStatusToggle.isChecked = true
            }
        }
    }

    /**
     * downloads the logged in user's username
     * sets dummy profile picture as no profile picture attribute available
     */
    private fun loadClientDetails() {
        // download logged in user
        val loggedInUser = PrefManager.getUser()
        navHeaderBinding.tvUserName.text = loggedInUser.username
        navHeaderBinding.ivUserPicture.setImageResource(R.drawable.ic_dp_placeholder)
    }

    @get:VisibleForTesting
    val countingIdlingResource: IdlingResource
        get() = EspressoIdlingResource.idlingResource

    private fun setupNavigationBar() {
//        mNavigationHeader = binding.navigationView.getHeaderView(0)
        binding.navigationView.getHeaderView(0)?.let { setupUserStatusToggle(it) }


        binding.navigationView.setNavigationItemSelectedListener(this as NavigationView.OnNavigationItemSelectedListener)

        // setup drawer layout and sync to toolbar
        val actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawer, toolbar, R.string.open_drawer, R.string.close_drawer
        ) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                setUserStatus(userStatusToggle)
                hideKeyboard(binding.drawer)
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                if (slideOffset != 0f) super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        binding.drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // make an API call to fetch logged in client's details
        loadClientDetails()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            finish()
        } else {
            doubleBackToExitPressedOnce = true
            Toast.makeText(
                this@HomeActivity,
                R.string.back_again,
                Toast.LENGTH_SHORT
            ).show()
            // Reset the flag after a short delay
            window.decorView.postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }
}
