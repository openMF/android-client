/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mifos.mifosxdroid.LocationService;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.utils.CheckSelfPermissionAndRequest;

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

        if (!CheckSelfPermissionAndRequest
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || !CheckSelfPermissionAndRequest
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            CheckSelfPermissionAndRequest
                    .requestPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            100,
                            "Location Permission is necessary for this feature",
                            "Location Permission is necessary for this feature",
                            "Permission Denied");
        } else {

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (buildLocationEnableDialog()) {
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

        }

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

    private boolean buildLocationEnableDialog() {

        LocationManager locationManager =
                (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gpsEnabled && !networkEnabled) {
            // notify user
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Location seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes" , new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            String mLocationSettings = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                            startActivity(
                                    new Intent(mLocationSettings));
                        }
                    })
                    .setNegativeButton("No" , new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext() ,
                                    "This feature requires Location to be enabled" ,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return false;
        } else {
            return true;
        }
    }

}
