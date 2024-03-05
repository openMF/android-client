package com.mifos.mifosxdroid.activity.pinpointclient

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.location.places.ui.PlacePicker
import com.mifos.core.objects.client.ClientAddressRequest
import com.mifos.core.objects.client.ClientAddressResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.PinpointClientAdapter
import com.mifos.mifosxdroid.core.MaterialDialog
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.ActivityPinpointLocationBinding
import com.mifos.utils.CheckSelfPermissionAndRequest
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @author fomenkoo
 */
@AndroidEntryPoint
class PinpointClientActivity : MifosBaseActivity(), OnRefreshListener,
    PinpointClientAdapter.OnItemClick {

    private lateinit var binding: ActivityPinpointLocationBinding
    private val arg: PinpointClientActivityArgs by navArgs()

    private lateinit var viewModel: PinPointClientViewModel

    @Inject
    lateinit var pinpointClientAdapter: PinpointClientAdapter

    private var clientId = 0
    private var apptableId: Int? = 0
    private var dataTableId: Int? = 0
    private var addresses: List<ClientAddressResponse> = ArrayList()
    override fun onItemLongClick(position: Int) {
        apptableId = addresses[position].clientId
        dataTableId = addresses[position].id
        MaterialDialog.Builder().init(this)
            .setItems(
                R.array.client_pinpoint_location_options
            ) { dialog, which ->
                when (which) {
                    0 -> if (CheckSelfPermissionAndRequest.checkSelfPermission(
                            this@PinpointClientActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        showPlacePiker(REQUEST_UPDATE_PLACE_PICKER)
                    } else {
                        requestPermission(REQUEST_UPDATE_PLACE_PICKER)
                    }

                    1 -> apptableId?.let {
                        dataTableId?.let { it1 ->
                            viewModel.deleteClientPinpointLocation(
                                it, it1
                            )
                        }
                    }

                    else -> {}
                }
            }
            .createMaterialDialog()
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinpointLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PinPointClientViewModel::class.java]
        showBackButton()
        clientId = arg.clientId
        showUserInterface()
        viewModel.getClientPinpointLocations(clientId)

        viewModel.pinPointClientUiState.observe(this) {
            when (it) {
                is PinPointClientUiState.ShowClientPinpointLocations -> {
                    showProgressbar(false)
                    showClientPinpointLocations(it.clientAddressResponses)
                }

                is PinPointClientUiState.ShowEmptyAddress -> {
                    showProgressbar(false)
                    showEmptyAddress()
                }

                is PinPointClientUiState.ShowFailedToFetchAddress -> {
                    showProgressbar(false)
                    showFailedToFetchAddress()
                }

                is PinPointClientUiState.ShowMessage -> {
                    showProgressDialog(false, null)
                    showMessage(it.message)
                }

                is PinPointClientUiState.ShowProgressDialog -> showProgressDialog(
                    it.show,
                    it.message
                )

                is PinPointClientUiState.ShowProgressbar -> showProgressbar(true)
                is PinPointClientUiState.UpdateClientAddress -> {
                    showProgressDialog(false, null)
                    updateClientAddress(it.genericResponse)
                }
            }
        }
    }

    private fun showUserInterface() {
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        pinpointClientAdapter.setContext(this)
        binding.rvPinpointLocation.layoutManager = mLayoutManager
        binding.rvPinpointLocation.setHasFixedSize(true)
        pinpointClientAdapter.setItemClick(this)
        binding.rvPinpointLocation.adapter = pinpointClientAdapter
        binding.swipeContainer.setColorSchemeColors(
            *this
                .resources.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener(this)
    }

    private fun showPlacePiker(requestCode: Int) {
        try {
            val intentBuilder = PlacePicker.IntentBuilder()
            val intent = intentBuilder.build(this)
            startActivityForResult(intent, requestCode)
        } catch (e: GooglePlayServicesRepairableException) {
            GooglePlayServicesUtil.getErrorDialog(e.connectionStatusCode, this, 0)
        } catch (e: GooglePlayServicesNotAvailableException) {
            Toast.makeText(
                this, getString(R.string.google_play_services_not_available),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /**
     * This Method is Requesting the Permission
     */
    private fun requestPermission(requestCode: Int) {
        CheckSelfPermissionAndRequest.requestPermission(
            this@PinpointClientActivity,
            Manifest.permission.ACCESS_FINE_LOCATION,
            requestCode,
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
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ADD_PLACE_PICKER -> {
                run {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    ) {

                        // permission was granted, yay! Do the
                        showPlacePiker(REQUEST_ADD_PLACE_PICKER)
                    } else {

                        // permission denied, boo! Disable the
                        show(
                            findViewById(android.R.id.content),
                            getString(R.string.permission_denied_to_access_fine_location)
                        )
                    }
                }
                run {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    ) {

                        // permission was granted, yay! Do the
                        showPlacePiker(REQUEST_UPDATE_PLACE_PICKER)
                    } else {

                        // permission denied, boo! Disable the
                        show(
                            findViewById(android.R.id.content),
                            getString(R.string.permission_denied_to_access_fine_location)
                        )
                    }
                }
            }

            REQUEST_UPDATE_PLACE_PICKER -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    showPlacePiker(REQUEST_UPDATE_PLACE_PICKER)
                } else {
                    show(
                        findViewById(android.R.id.content),
                        getString(R.string.permission_denied_to_access_fine_location)
                    )
                }
            }
        }
    }

    override fun onRefresh() {
        binding.llError.visibility = View.GONE
        viewModel.getClientPinpointLocations(clientId)
    }

    private fun showClientPinpointLocations(clientAddressResponses: List<ClientAddressResponse>) {
        binding.llError.visibility = View.GONE
        addresses = clientAddressResponses
        pinpointClientAdapter.setAddress(clientAddressResponses)
    }

    private fun showFailedToFetchAddress() {
        binding.llError.visibility = View.VISIBLE
        binding.tvNoLocation.text = getString(R.string.failed_to_fetch_pinpoint_location)
    }

    private fun showEmptyAddress() {
        binding.llError.visibility = View.VISIBLE
        binding.tvNoLocation.text = getString(R.string.empty_client_address)
    }

    private fun updateClientAddress(message: Int) {
        showMessage(message)
        viewModel.getClientPinpointLocations(clientId)
    }

    private fun showProgressbar(show: Boolean) {
        binding.swipeContainer.isRefreshing = show
    }

    private fun showProgressDialog(show: Boolean, message: Int?) {
        if (show) {
            showProgress(getString(message!!))
        } else {
            hideProgress()
        }
    }

    private fun showMessage(message: Int) {
        show(findViewById(android.R.id.content), getString(message))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_client_save_pin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_pin) {
            if (CheckSelfPermissionAndRequest.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                showPlacePiker(REQUEST_ADD_PLACE_PICKER)
            } else {
                requestPermission(REQUEST_ADD_PLACE_PICKER)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val place = PlacePicker.getPlace(this@PinpointClientActivity, data)
            val clientAddressRequest = ClientAddressRequest(
                place.id,
                place.address.toString(),
                place.latLng.latitude,
                place.latLng.longitude
            )
            if (requestCode == REQUEST_ADD_PLACE_PICKER) {
                viewModel.addClientPinpointLocation(clientId, clientAddressRequest)
            } else if (requestCode == REQUEST_UPDATE_PLACE_PICKER) {
                apptableId?.let {
                    dataTableId?.let { it1 ->
                        viewModel.updateClientPinpointLocation(
                            it, it1,
                            clientAddressRequest
                        )
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val REQUEST_ADD_PLACE_PICKER = 1
        private const val REQUEST_UPDATE_PLACE_PICKER = 2
    }
}