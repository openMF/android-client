/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.mifos.mifosxdroid.activity.PathTrackingActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fomenkoo
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = LocationService.class.getSimpleName();

    private final int NOTIFICATION = 0;

    private State state;
    private GoogleApiClient apiClient;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notification;
    private IBinder binder = new LocalBinder();
    private List<LatLng> path;
    private LocationRequest request = LocationRequest.create();
    private AtomicBoolean locationAvailable = new AtomicBoolean(false);
    private PendingIntent controlIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        request.setInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notification = new NotificationCompat.Builder(this)
                .setContentTitle("Mifos path tracker")
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentText("Preparing tracker")
                .setSmallIcon(R.drawable.ic_launcher);

        Intent resultIntent = new Intent(this, PathTrackingActivity.class);
        controlIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(TAG, "service created");
        apiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void startLogging() {
        if (state != State.FAILURE && state != State.LOGGING || state == State.READY) {
            path = new ArrayList<>();
            LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, request, this);
            state = State.LOGGING;
            startNotification("Tracking");
        }
    }

    public void stopLogging() {
        if (state == State.LOGGING || state == State.PAUSED) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
            state = State.STOPPED;
            notificationManager.cancel(NOTIFICATION);
        }
    }

    public String getPolyData() {
        return PolyUtil.encode(path);
    }

    public void saveData() {
        stopLogging();
        state = State.STOPPED;
    }

    public void startNotification(String state) {
        notification.setContentText(state);
        notification.setContentIntent(controlIntent);
        notificationManager.notify(NOTIFICATION, notification.build());
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationAvailable.set(true);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        Log.i(TAG, "Connected to location services");
        if (mLastLocation != null)
            Log.i(TAG, "Current location: " + mLastLocation.toString());

        // Ready to receive data
        state = State.READY;
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationAvailable.set(false);
        Log.i(TAG, "Disconnected from location services");
    }

    @Override
    public void onLocationChanged(Location location) {
        path.add(new LatLng(location.getLatitude(), location.getLongitude()));
        Log.i(TAG, "Data update received ~ " + location.toString());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationAvailable.set(false);
        state = State.FAILURE;
        Log.i(TAG, "Connection to location services failed" + connectionResult.getErrorCode());
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onDestroy() {
        if (apiClient != null)
            apiClient.disconnect();
        notificationManager.cancel(NOTIFICATION);
        Log.i(TAG, "Service destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private enum State {
        READY,
        LOGGING,
        PAUSED,
        STOPPED,
        FAILURE
    }
}
