/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.mifos.mifosxdroid.LocationService;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author fomenkoo
 */
public class PathTrackingActivity extends MifosBaseActivity implements ServiceConnection {

    @InjectView(R.id.start)
    Button start;

    @InjectView(R.id.stop)
    Button stop;
    boolean bound = false;
    private LocationService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_tracker);
        ButterKnife.inject(this);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.startLogging();
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
}
