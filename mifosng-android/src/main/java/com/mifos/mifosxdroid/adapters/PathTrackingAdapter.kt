package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mifos.mifosxdroid.R
import com.mifos.objects.user.UserLatLng
import com.mifos.objects.user.UserLocation


class PathTrackingAdapter(
    val onUserLocationClick: (UserLocation) -> Unit
) :RecyclerView.Adapter<PathTrackingAdapter.ViewHolder>() {

    private var userLocations: List<UserLocation> = ArrayList()
    private var userLatLngs: List<UserLatLng> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pinpoint_location, parent, false)
        )
        viewHolder.itemView.setOnClickListener {
            onUserLocationClick(userLocations[viewHolder.bindingAdapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = userLocations[position]
        userLatLngs = getLatLngList(location.latlng)
        holder.tvAddress.text = location.date + " from " + location.startTime + " to " + location.stopTime
        try {
            holder.mvUserLocation.tag = userLatLngs[0]
        } catch (e: IndexOutOfBoundsException) {
            /* Prevents crashing upon calling an item not in the list */
        }

        // Ensure the map has been initialised by the on map ready callback in ViewHolder.
        // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
        // when the callback is received.
        if (holder.map != null) {
            // The map is already ready to be used
            try {
                setMapLocation(holder.map, userLatLngs[0])
            } catch (e: IndexOutOfBoundsException) {
                /* Prevents crashing upon calling an item not in the list */
            }
        }
    }

    fun setPathTracker(userLocations: List<UserLocation>) {
        this.userLocations = userLocations
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = userLocations[position]


    override fun getItemId(i: Int) = 0L

    override fun getItemCount() = userLocations.size

    override fun onViewRecycled(holder: ViewHolder) {
        // Cleanup MapView here
        if (holder.map != null) {
            holder.map!!.clear()
            holder.map!!.mapType = GoogleMap.MAP_TYPE_NONE
        }
    }

    private fun setMapLocation(map: GoogleMap?, location: UserLatLng) {
        // Add a marker for this item and set the camera
        val polylineOptions = PolylineOptions()
        for (userLatLng in userLatLngs) {
            polylineOptions.add(LatLng(userLatLng.lat, userLatLng.lng))
        }
        map!!.addPolyline(polylineOptions)
        val startLatLng = LatLng(location.lat, location.lng)
        val stopLatLng = LatLng(
            userLatLngs[userLatLngs.size - 1].lat,
            userLatLngs[userLatLngs.size - 1].lng
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 13f))
        map.addMarker(MarkerOptions().position(startLatLng))
        map.addMarker(MarkerOptions().position(stopLatLng))
        // Set the map type back to normal.
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    inner class ViewHolder(val v: View) : RecyclerView.ViewHolder(v), OnMapReadyCallback {
        val mvUserLocation: MapView = v.findViewById(R.id.mv_client_location)
        val tvAddress: TextView = v.findViewById(R.id.tv_address)
        val cardView: CardView = v.findViewById(R.id.card_view)
        var map: GoogleMap? = null

        init {
            initializeMapView()
        }

        override fun onMapReady(googleMap: GoogleMap) {
            MapsInitializer.initialize(v.context)
            map = googleMap
            val data = mvUserLocation.tag as UserLatLng
            setMapLocation(map, data)
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        fun initializeMapView() {
            // Initialise the MapView
            mvUserLocation.onCreate(null)
            mvUserLocation.onResume()
            // Set the map ready callback to receive the GoogleMap object
            mvUserLocation.getMapAsync(this)
        }
    }

    fun getLatLngList(latLngString: String?): List<UserLatLng> {
        val gson = Gson()
        return gson.fromJson(
            latLngString,
            object : TypeToken<List<UserLatLng?>?>() {}.type
        )
    }
}