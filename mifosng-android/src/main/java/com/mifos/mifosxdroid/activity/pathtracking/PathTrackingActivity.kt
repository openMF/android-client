/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.activity.pathtracking

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.therajanmaurya.sweeterror.SweetUIErrorHandler
import com.mifos.core.objects.user.UserLocation
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.PathTrackingAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivityPathTrackerBinding
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.Constants
import com.mifos.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author fomenkoo
 */
@AndroidEntryPoint
class PathTrackingActivity : MifosBaseActivity(), OnRefreshListener {

    private lateinit var binding: ActivityPathTrackerBinding

    private lateinit var viewModel: PathTrackingViewModel

    private var pathTrackingAdapter: PathTrackingAdapter? = null
    private var intentLocationService: Intent? = null
    private var notificationReceiver: BroadcastReceiver? = null
    private var userLocations: List<UserLocation>? = null
    private var sweetUIErrorHandler: SweetUIErrorHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPathTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PathTrackingViewModel::class.java]
        showBackButton()
        intentLocationService = Intent(this, PathTrackingService::class.java)
        createNotificationReceiver()
        showUserInterface()
        viewModel.loadPathTracking(PrefManager.getUserId())
        binding.layoutError.findViewById<Button>(com.github.therajanmaurya.sweeterror.R.id.btnTryAgain)
            .setOnClickListener {
                reloadOnError()
            }
        viewModel.pathTrackingUiState.observe(this) {
            when (it) {
                is PathTrackingUiState.ShowProgress -> showProgressbar(it.state)
                is PathTrackingUiState.ShowEmptyPathTracking -> {
                    hideProgress()
                    showEmptyPathTracking()
                }

                is PathTrackingUiState.ShowError -> {
                    hideProgress()
                    showError()
                }

                is PathTrackingUiState.ShowPathTracking -> {
                    hideProgress()
                    showPathTracking(it.userLocations)
                }
            }
        }
    }

    private fun showUserInterface() {
        userLocations = ArrayList()
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        binding.rvPathTracker.layoutManager = mLayoutManager
        binding.rvPathTracker.setHasFixedSize(false)
        binding.rvPathTracker.scrollToPosition(0)
        pathTrackingAdapter = PathTrackingAdapter { userLocation: UserLocation ->
            val userLatLngs = pathTrackingAdapter?.getLatLngList(userLocation.latlng)
            val uri = userLatLngs?.let { userLatLngs ->
                if (userLatLngs.isNotEmpty()) {
                    val originLatLng = userLatLngs[0]
                    val destinationLatLng = userLatLngs[userLatLngs.size - 1]
                    "http://maps.google.com/maps?f=d&hl=en&saddr=${originLatLng.lat},${originLatLng.lng}&daddr=${destinationLatLng.lat},${destinationLatLng.lng}"
                } else {
                    // Handle the case when userLatLngs is empty
                    ""
                }
            } ?: ""

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setClassName(
                "com.google.android.apps.maps", "com.google.android.maps.MapsActivity"
            )
            startActivity(Intent.createChooser(intent, getString(R.string.start_tracking)))
        }
        binding.rvPathTracker.adapter = pathTrackingAdapter
        binding.swipeContainer.setColorSchemeColors(
            *this.resources.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener(this)
        sweetUIErrorHandler = SweetUIErrorHandler(this, findViewById(android.R.id.content))
    }

    override fun onRefresh() {
        viewModel.loadPathTracking(PrefManager.getUserId())
    }

    private fun showPathTracking(userLocations: List<UserLocation>) {
        this.userLocations = userLocations
        pathTrackingAdapter?.setPathTracker(userLocations)
    }


    private fun reloadOnError() {
        sweetUIErrorHandler?.hideSweetErrorLayoutUI(binding.rvPathTracker, binding.layoutError)
        viewModel.loadPathTracking(PrefManager.getUserId())
    }

    private fun showEmptyPathTracking() {
        sweetUIErrorHandler?.showSweetEmptyUI(
            getString(R.string.path_tracker),
            R.drawable.ic_error_black_24dp,
            binding.rvPathTracker,
            binding.layoutError
        )
    }

    private fun showError() {
        sweetUIErrorHandler?.showSweetErrorUI(
            getString(R.string.failed_to_fetch_path_tracking_details),
            R.drawable.ic_error_black_24dp,
            binding.rvPathTracker,
            binding.layoutError
        )
    }

    /**
     * This Method Checking the Permission ACCESS_FINE_LOCATION is granted or not.
     * If not then prompt user a dialog to grant the ACCESS_FINE_LOCATION permission.
     * and If Permission is granted already then start Intent to get the
     */
    private fun checkPermissionAndRequest(): Boolean {
        return if (CheckSelfPermissionAndRequest.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            true
        } else {
            requestPermission()
            false
        }
    }

    /**
     * This Method is Requesting the Permission
     */
    fun requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
            resources.getString(
                R.string.dialog_message_access_fine_location_permission_denied
            ),
            resources.getString(
                R.string.dialog_message_permission_never_ask_again_fine_location
            ),
            Constants.ACCESS_FINE_LOCATION_STATUS
        )
    }

    /**
     * This Method getting the Response after User Grant or denied the Permission
     *
     * @param requestCode  Request Code
     * @param permissions  Permission
     * @param grantResults GrantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    startService(intentLocationService)
                } else {
                    // permission denied
                    Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.permission_denied_to_access_fine_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_path_track, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_start_path_track -> {
                if (checkPermissionAndRequest()) {
                    startService(intentLocationService)
                    PrefManager.userStatus = true
                    invalidateOptionsMenu()
                }
                true
            }

            R.id.menu_stop_path_track -> {
                stopService(intentLocationService)
                PrefManager.userStatus = false
                invalidateOptionsMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.menu_start_path_track).isVisible =
            !PrefManager.userStatus
        menu.findItem(R.id.menu_stop_path_track).isVisible =
            PrefManager.userStatus
        return true
    }

    private fun createNotificationReceiver() {
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (Constants.STOP_TRACKING == action) {
                    invalidateOptionsMenu()
                    viewModel.loadPathTracking(PrefManager.getUserId())
                }
            }
        }
        registerReceiver(notificationReceiver, IntentFilter(Constants.STOP_TRACKING))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }

    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
        if (show && userLocations?.isEmpty() == true) {
            binding.pbPathTracking.visibility = View.VISIBLE
            binding.swipeContainer.isRefreshing = false
        } else {
            binding.pbPathTracking.visibility = View.GONE
        }
    }
}