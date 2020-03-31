package com.mifos.mifosxdroid.activity.pinpointclient;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.PinpointClientAdapter;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.ClientAddressRequest;
import com.mifos.objects.client.ClientAddressResponse;
import com.mifos.utils.CheckSelfPermissionAndRequest;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author fomenkoo
 */
public class PinpointClientActivity extends MifosBaseActivity implements PinPointClientMvpView,
        SwipeRefreshLayout.OnRefreshListener, PinpointClientAdapter.OnItemClick {

    private static final int REQUEST_ADD_PLACE_PICKER = 1;
    private static final int REQUEST_UPDATE_PLACE_PICKER = 2;

    @BindView(R.id.rv_pinpoint_location)
    RecyclerView rvPinPointLocation;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.tv_no_location)
    TextView tvNoLocation;

    @BindView(R.id.ll_error)
    LinearLayout llError;

    @BindView(R.id.iv_no_location)
    ImageView ivNoLocation;

    @Inject
    PinpointClientAdapter pinpointClientAdapter;

    @Inject
    PinPointClientPresenter pinPointClientPresenter;

    private int clientId;
    private int apptableId;
    private int dataTableId;
    private List<ClientAddressResponse> addresses = new ArrayList<>();

    @Override
    public void onItemLongClick(int position) {
        apptableId = addresses.get(position).getClientId();
        dataTableId = addresses.get(position).getId();
        new MaterialDialog.Builder().init(this)
                .setItems(R.array.client_pinpoint_location_options,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (CheckSelfPermissionAndRequest.checkSelfPermission
                                                (PinpointClientActivity.this,
                                                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                                            showPlacePiker(REQUEST_UPDATE_PLACE_PICKER);
                                        } else {
                                            requestPermission(REQUEST_UPDATE_PLACE_PICKER);
                                        }

                                        break;
                                    case 1:
                                        pinPointClientPresenter.deleteClientPinpointLocation(
                                                apptableId, dataTableId);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                .createMaterialDialog()
                .show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.activity_pinpoint_location);

        ButterKnife.bind(this);
        pinPointClientPresenter.attachView(this);

        showBackButton();
        clientId = getIntent().getIntExtra(Constants.CLIENT_ID, 1);

        showUserInterface();
        pinPointClientPresenter.getClientPinpointLocations(clientId);
    }

    @Override
    public void showUserInterface() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pinpointClientAdapter.setContext(this);
        rvPinPointLocation.setLayoutManager(mLayoutManager);
        rvPinPointLocation.setHasFixedSize(true);
        pinpointClientAdapter.setItemClick(this);
        rvPinPointLocation.setAdapter(pinpointClientAdapter);
        swipeRefreshLayout.setColorSchemeColors(this
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void showPlacePiker(int requestCode) {
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, getString(R.string.google_play_services_not_available),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * This Method is Requesting the Permission
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void requestPermission(int requestCode) {
        CheckSelfPermissionAndRequest.requestPermission(
                PinpointClientActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                requestCode,
                getResources().getString(
                        R.string.dialog_message_access_fine_location_permission_denied),
                getResources().getString(
                        R.string.dialog_message_permission_never_ask_again_fine_location),
                Constants.ACCESS_FINE_LOCATION_STATUS);
    }

    /**
     * This Method getting the Response after User Grant or denied the Permission
     *
     * @param requestCode  Request Code
     * @param permissions  Permission
     * @param grantResults GrantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ADD_PLACE_PICKER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    showPlacePiker(REQUEST_ADD_PLACE_PICKER);
                } else {

                    // permission denied, boo! Disable the
                    Toaster.show(findViewById(android.R.id.content),
                            getString(R.string.permission_denied_to_access_fine_location));
                }
            }
            case REQUEST_UPDATE_PLACE_PICKER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    showPlacePiker(REQUEST_UPDATE_PLACE_PICKER);
                } else {

                    // permission denied, boo! Disable the
                    Toaster.show(findViewById(android.R.id.content),
                            getString(R.string.permission_denied_to_access_fine_location));
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        llError.setVisibility(View.GONE);
        pinPointClientPresenter.getClientPinpointLocations(clientId);
    }

    @Override
    public void showClientPinpointLocations(List<ClientAddressResponse> clientAddressResponses) {
        llError.setVisibility(View.GONE);
        addresses = clientAddressResponses;
        pinpointClientAdapter.setAddress(clientAddressResponses);
    }

    @Override
    public void showFailedToFetchAddress() {
        llError.setVisibility(View.VISIBLE);
        tvNoLocation.setText(getString(R.string.failed_to_fetch_pinpoint_location));
    }

    @Override
    public void showEmptyAddress() {
        llError.setVisibility(View.VISIBLE);
        tvNoLocation.setText(getString(R.string.empty_client_address));
    }

    @Override
    public void updateClientAddress(int message) {
        showMessage(message);
        pinPointClientPresenter.getClientPinpointLocations(clientId);
    }

    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void showProgressDialog(boolean show, Integer message) {
        if (show) {
            showProgress(getString(message));
        } else {
            hideProgress();
        }
    }

    @Override
    public void showMessage(int message) {
        Toaster.show(findViewById(android.R.id.content), getString(message));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client_save_pin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_pin) {
            if (CheckSelfPermissionAndRequest.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPlacePiker(REQUEST_ADD_PLACE_PICKER);
            } else {
                requestPermission(REQUEST_ADD_PLACE_PICKER);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(PinpointClientActivity.this, data);
            ClientAddressRequest clientAddressRequest = new ClientAddressRequest(
                    place.getId(),
                    place.getAddress().toString(),
                    place.getLatLng().latitude,
                    place.getLatLng().longitude);

            if (requestCode == REQUEST_ADD_PLACE_PICKER) {
                pinPointClientPresenter.addClientPinpointLocation(clientId, clientAddressRequest);
            } else if (requestCode == REQUEST_UPDATE_PLACE_PICKER) {
                pinPointClientPresenter.updateClientPinpointLocation(apptableId, dataTableId,
                        clientAddressRequest);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pinPointClientPresenter.detachView();
    }
}
