/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.activity.pathtracking;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.therajanmaurya.sweeterror.SweetUIErrorHandler;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.PathTrackingAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.databinding.ActivityPathTrackerBinding;
import com.mifos.objects.user.UserLatLng;
import com.mifos.objects.user.UserLocation;
import com.mifos.utils.CheckSelfPermissionAndRequest;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author fomenkoo
 */
public class PathTrackingActivity extends MifosBaseActivity
        implements PathTrackingMvpView, SwipeRefreshLayout.OnRefreshListener {

    private ActivityPathTrackerBinding binding;

    @Inject
    PathTrackingPresenter pathTrackingPresenter;

    PathTrackingAdapter pathTrackingAdapter;

    private Intent intentLocationService;
    private BroadcastReceiver notificationReceiver;
    private List<UserLocation> userLocations;
    private SweetUIErrorHandler sweetUIErrorHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPathTrackerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getActivityComponent().inject(this);
        pathTrackingPresenter.attachView(this);

        showBackButton();
        intentLocationService = new Intent(this, PathTrackingService.class);
        createNotificationReceiver();

        showUserInterface();
        pathTrackingPresenter.loadPathTracking(PrefManager.getUserId());

        binding.layoutError.findViewById(R.id.btn_try_again)
                .setOnClickListener(view -> reloadOnError());

    }

    @Override
    public void showUserInterface() {
        userLocations = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        binding.rvPathTacker.setLayoutManager(mLayoutManager);
        binding.rvPathTacker.setHasFixedSize(false);
        binding.rvPathTacker.scrollToPosition(0);
        pathTrackingAdapter = new PathTrackingAdapter(userLocation -> {
                List<UserLatLng> userLatLngs =
                        pathTrackingAdapter.getLatLngList(userLocation.getLatlng());
                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="
                        + userLatLngs.get(0).getLat() + ","
                        + userLatLngs.get(0).getLng() + "&daddr="
                        + userLatLngs.get(userLatLngs.size() - 1).getLat() + "," + ""
                        + userLatLngs.get(userLatLngs.size() - 1).getLng();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(Intent.createChooser(intent, getString(R.string.start_tracking)));
                return null;
            }
        );
        binding.rvPathTacker.setAdapter(pathTrackingAdapter);
        binding.swipeContainer.setColorSchemeColors(this
                .getResources().getIntArray(R.array.swipeRefreshColors));
        binding.swipeContainer.setOnRefreshListener(this);
        sweetUIErrorHandler = new SweetUIErrorHandler(this, findViewById(android.R.id.content));
    }

    @Override
    public void onRefresh() {
        pathTrackingPresenter.loadPathTracking(PrefManager.getUserId());
    }

    @Override       
    public void showPathTracking(List<UserLocation> userLocations) {
        this.userLocations = userLocations;
        pathTrackingAdapter.setPathTracker(userLocations);
    }

    public void reloadOnError() {
        sweetUIErrorHandler.hideSweetErrorLayoutUI(binding.rvPathTacker, binding.layoutError);
        pathTrackingPresenter.loadPathTracking(PrefManager.getUserId());
    }


    @Override
    public void showEmptyPathTracking() {
        sweetUIErrorHandler.showSweetEmptyUI(getString(R.string.path_tracker),
                R.drawable.ic_error_black_24dp, binding.rvPathTacker, binding.layoutError);
    }

    @Override
    public void showError() {
        sweetUIErrorHandler.showSweetErrorUI(
                getString(R.string.failed_to_fetch_path_tracking_details),
                R.drawable.ic_error_black_24dp, binding.rvPathTacker, binding.layoutError);
    }

    /**
     * This Method Checking the Permission ACCESS_FINE_LOCATION is granted or not.
     * If not then prompt user a dialog to grant the ACCESS_FINE_LOCATION permission.
     * and If Permission is granted already then start Intent to get the
     */
    public boolean checkPermissionAndRequest() {
        if (CheckSelfPermissionAndRequest.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            return true;
        } else {
            requestPermission();
            return false;
        }
    }

    /**
     * This Method is Requesting the Permission
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void requestPermission() {
        CheckSelfPermissionAndRequest.requestPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    startService(intentLocationService);
                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(), getResources()
                                    .getString(R.string.permission_denied_to_access_fine_location),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_path_track, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_start_path_track:
                if (checkPermissionAndRequest()) {
                    startService(intentLocationService);
                    PrefManager.putBoolean(Constants.SERVICE_STATUS, true);
                    invalidateOptionsMenu();
                }
                return true;
            case R.id.menu_stop_path_track:
                stopService(intentLocationService);
                PrefManager.putBoolean(Constants.SERVICE_STATUS, false);
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_start_path_track)
                .setVisible(!PrefManager.getBoolean(Constants.SERVICE_STATUS, false));
        menu.findItem(R.id.menu_stop_path_track)
                .setVisible(PrefManager.getBoolean(Constants.SERVICE_STATUS, false));
        return true;
    }

    public void createNotificationReceiver() {
        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Constants.STOP_TRACKING.equals(action)) {
                    invalidateOptionsMenu();
                    pathTrackingPresenter.loadPathTracking(PrefManager.getUserId());
                }
            }
        };
        registerReceiver(notificationReceiver, new IntentFilter(Constants.STOP_TRACKING));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pathTrackingPresenter.detachView();
        unregisterReceiver(notificationReceiver);
    }


    @Override
    public void showProgressbar(boolean show) {
        binding.swipeContainer.setRefreshing(show);
        if (show && userLocations.size() == 0) {
            binding.pbPathTracking.setVisibility(View.VISIBLE);
            binding.swipeContainer.setRefreshing(false);
        } else {
            binding.pbPathTracking.setVisibility(View.GONE);
        }
    }
}
