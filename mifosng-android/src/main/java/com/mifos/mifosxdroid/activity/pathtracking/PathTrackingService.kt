/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.activity.pathtracking

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerDataTable
import com.mifos.core.objects.user.UserLatLng
import com.mifos.core.objects.user.UserLocation
import com.mifos.mifosxdroid.R
import com.mifos.utils.Constants
import com.mifos.utils.DateHelper
import com.mifos.utils.DateHelper.getCurrentDateTime
import com.mifos.utils.PrefManager
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author fomenkoo
 */
class PathTrackingService : Service(), ConnectionCallbacks, OnConnectionFailedListener,
    LocationListener {
    private val TAG = PathTrackingService::class.java.simpleName
    private val NOTIFICATION = 0
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var currentLocation: Location
    private var notificationManager: NotificationManager? = null
    private var notification: NotificationCompat.Builder? = null
    private var notificationReceiver: BroadcastReceiver? = null
    private var latLngs: MutableList<UserLatLng>? = null
    private var startTime: String? = null
    private var stopTime: String? = null
    private var date: String? = null

    @Inject
    lateinit var dataManagerDataTable: DataManagerDataTable
    private var subscription: Subscription? = null
    override fun onCreate() {
        super.onCreate()
        buildGoogleApiClient()
    }

    /**
     * Builds a GoogleApiClient. Uses the `#addApi` method to request the
     * LocationServices API.
     */
    @Synchronized
    protected fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        createLocationRequest()
    }

    protected fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest!!.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    protected fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleApiClient?.let {
            locationRequest?.let { it1 ->
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    it, it1, this
                )
            }
        }
    }

    private fun stopLocationUpdates() {
        googleApiClient?.let { LocationServices.FusedLocationApi.removeLocationUpdates(it, this) }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        latLngs = ArrayList()
        startTime = getCurrentDateTime(DateHelper.TIME_FORMAT_VALUE)
        date = getCurrentDateTime(DateHelper.DATE_FORMAT_VALUE)
        googleApiClient!!.connect()
        startNotification()
        createNotificationReceiver()
        return START_STICKY
    }

    override fun onConnected(bundle: Bundle?) {
        if (currentLocation == null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            currentLocation = googleApiClient?.let {
                LocationServices.FusedLocationApi.getLastLocation(
                    it
                )
            }!!
            latLngs?.add(
                UserLatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                )
            )
        }
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {
        googleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
        latLngs!!.add(UserLatLng(currentLocation.latitude, currentLocation.longitude))
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG, "Connection to location services failed" + connectionResult.errorCode)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    fun startNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notification = NotificationCompat.Builder(this)
            .setContentTitle(getString(R.string.mifos_path_tracker))
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentText(getString(R.string.description_location_tracking))
            .setSmallIcon(R.drawable.ic_launcher)
        val resultIntent = Intent()
        resultIntent.action = Constants.STOP_TRACKING
        val intentBroadCast = PendingIntent.getBroadcast(
            this, 0, resultIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        notification?.addAction(
            R.drawable.ic_assignment_turned_in_black_24dp,
            getString(R.string.stop_tracking), intentBroadCast
        )
        notification?.setContentIntent(intentBroadCast)
        notificationManager!!.notify(NOTIFICATION, notification?.build())
    }

    private fun stopNotification() {
        notificationManager!!.cancel(NOTIFICATION)
    }

    override fun onDestroy() {
        if (subscription != null) subscription!!.unsubscribe()
        stopLocationUpdates()
        googleApiClient!!.disconnect()
        stopNotification()
        unregisterReceiver(notificationReceiver)
        PrefManager.userStatus = false
        stopTime = getCurrentDateTime(DateHelper.TIME_FORMAT_VALUE)
        addPathTracking(PrefManager.getUserId(), buildUserLocation())
        super.onDestroy()
    }

    private fun createNotificationReceiver() {
        notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (Constants.STOP_TRACKING == action) {
                    onDestroy()
                }
            }
        }
        registerReceiver(notificationReceiver, IntentFilter(Constants.STOP_TRACKING))
    }

    private fun buildUserLocation(): UserLocation {
        val userLocation = UserLocation()
        userLocation.latlng = latLngs.toString()
        userLocation.startTime = startTime
        userLocation.stopTime = stopTime
        userLocation.date = date
        userLocation.userId = PrefManager.getUserId()
        return userLocation
    }

    private fun addPathTracking(userId: Int, userLocation: UserLocation?) {
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
        subscription = dataManagerDataTable
            .addUserPathTracking(userId, userLocation)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe(
                object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {}
                    override fun onNext(genericResponse: GenericResponse) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.tracks_submitted),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    companion object {
        const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }
}