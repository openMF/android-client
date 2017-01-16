/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mifos.mifosxdroid.LocationService;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.utils.CheckSelfPermissionAndRequest;
import com.mifos.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author fomenkoo
 */
public class PathTrackingActivity extends MifosBaseActivity implements ServiceConnection {


    @BindView(R.id.start)
    Button start;

    @BindView(R.id.stop)
    Button stop;
    boolean bound = false;
    private LocationService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_tracker);
        ButterKnife.bind(this);
        start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkPermissionAndRequest()) {
                        service.startLogging();
                    }
                }
            });

        stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    service.stopLogging();
                }
            });


        showBackButton();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(this);
            bound = false;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        bound = true;
        LocationService.LocalBinder binder = (LocationService.LocalBinder) iBinder;
        service = binder.getService();
        start.setEnabled(true);
        stop.setEnabled(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        bound = false;
        service = null;
        start.setEnabled(false);
        stop.setEnabled(false);
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
                getResources().getString(R.string.dialog_message_permission_never_ask_location),
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
            case Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    service.startLogging();

                } else {

                    // permission denied, boo! Disable the
                    Toast.makeText(getApplicationContext(), getResources()
                            .getString(R.string.permission_denied_to_access_fine_location) ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
