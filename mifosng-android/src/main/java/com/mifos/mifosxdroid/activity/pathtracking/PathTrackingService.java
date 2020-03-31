/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.activity.pathtracking;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mifos.App;
import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDataTable;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.user.UserLatLng;
import com.mifos.objects.user.UserLocation;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author fomenkoo
 */
public class PathTrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = PathTrackingService.class.getSimpleName();

    private final int NOTIFICATION = 0;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notification;
    private BroadcastReceiver notificationReceiver;

    private List<UserLatLng> latLngs;
    private String startTime, stopTime, date;

    @Inject
    DataManagerDataTable dataManagerDataTable;
    private Subscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).getComponent().inject(this);
        buildGoogleApiClient();
    }


    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        latLngs = new ArrayList<>();
        startTime = DateHelper.getCurrentDateTime(DateHelper.TIME_FORMAT_VALUE);
        date = DateHelper.getCurrentDateTime(DateHelper.DATE_FORMAT_VALUE);
        googleApiClient.connect();
        startNotification();
        createNotificationReceiver();
        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            latLngs.add(new UserLatLng(currentLocation.getLatitude(),
                    currentLocation.getLongitude()));
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        latLngs.add(new UserLatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection to location services failed" + connectionResult.getErrorCode());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.mifos_path_tracker))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentText(getString(R.string.description_location_tracking))
                .setSmallIcon(R.drawable.ic_launcher);

        Intent resultIntent = new Intent();
        resultIntent.setAction(Constants.STOP_TRACKING);
        PendingIntent intentBroadCast = PendingIntent.getBroadcast(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.addAction(R.drawable.ic_assignment_turned_in_black_24dp,
                getString(R.string.stop_tracking), intentBroadCast);

        notification.setContentIntent(intentBroadCast);
        notificationManager.notify(NOTIFICATION, notification.build());
    }

    public void stopNotification() {
        notificationManager.cancel(NOTIFICATION);
    }

    @Override
    public void onDestroy() {
        if (subscription != null) subscription.unsubscribe();
        stopLocationUpdates();
        googleApiClient.disconnect();
        stopNotification();
        unregisterReceiver(notificationReceiver);
        PrefManager.putBoolean(Constants.SERVICE_STATUS, false);
        stopTime = DateHelper.getCurrentDateTime(DateHelper.TIME_FORMAT_VALUE);
        addPathTracking(PrefManager.getUserId(), buildUserLocation());
        super.onDestroy();
    }

    public void createNotificationReceiver() {
        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (Constants.STOP_TRACKING.equals(action)) {
                    onDestroy();
                }
            }
        };
        registerReceiver(notificationReceiver, new IntentFilter(Constants.STOP_TRACKING));
    }

    public UserLocation buildUserLocation() {
        UserLocation userLocation = new UserLocation();
        userLocation.setLatlng(latLngs.toString());
        userLocation.setStartTime(startTime);
        userLocation.setStopTime(stopTime);
        userLocation.setDate(date);
        userLocation.setUserId(PrefManager.getUserId());
        return userLocation;
    }

    public void addPathTracking(int userId, UserLocation userLocation) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = dataManagerDataTable
                .addUserPathTracking(userId, userLocation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(
                        new Subscriber<GenericResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(GenericResponse
                                                       genericResponse) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.tracks_submitted),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }
}
