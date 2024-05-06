package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mifos.core.objects.client.ClientAddressResponse
import com.mifos.mifosxdroid.databinding.ItemPinpointLocationBinding
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 16/01/17.
 */
class PinpointClientAdapter @Inject constructor() :
    RecyclerView.Adapter<PinpointClientAdapter.ViewHolder>() {
    private var addressResponses: List<ClientAddressResponse>
    private var context: Context? = null
    private var itemClick: OnItemClick? = null

    init {
        addressResponses = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPinpointLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressResponse = addressResponses[position]
        holder.binding.tvAddress.text = addressResponse.placeAddress
        holder.binding.mvClientLocation.tag = addressResponse

        // Ensure the map has been initialised by the on map ready callback in ViewHolder.
        // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
        // when the callback is received.
        setMapLocation(holder.map, addressResponse)
        holder.binding.cardView.setOnLongClickListener {
            itemClick!!.onItemLongClick(holder.adapterPosition)
            false
        }
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    fun setAddress(addressResponses: List<ClientAddressResponse>) {
        this.addressResponses = addressResponses
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ClientAddressResponse {
        return addressResponses[position]
    }

    fun setItemClick(itemClick: OnItemClick?) {
        this.itemClick = itemClick
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return addressResponses.size
    }

    override fun onViewRecycled(holder: ViewHolder) {
        // Cleanup MapView here
        holder.map.clear()
        holder.map.mapType = GoogleMap.MAP_TYPE_NONE
    }

    private fun setMapLocation(map: GoogleMap?, location: ClientAddressResponse) {
        // Add a marker for this item and set the camera
        val latLng = location.latitude?.let { location.longitude?.let { it1 -> LatLng(it, it1) } }
        latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 13f) }?.let { map!!.moveCamera(it) }
        latLng?.let { MarkerOptions().position(it) }?.let { map?.addMarker(it) }
        // Set the map type back to normal.
        map?.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    inner class ViewHolder(val binding: ItemPinpointLocationBinding) :
        RecyclerView.ViewHolder(binding.root), OnMapReadyCallback {
        lateinit var map: GoogleMap

        init {
            initializeMapView()
        }

        override fun onMapReady(googleMap: GoogleMap) {
            context?.let { MapsInitializer.initialize(it) }
            map = googleMap
            val data = binding.mvClientLocation.tag as ClientAddressResponse
            setMapLocation(map, data)
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        private fun initializeMapView() {
            // Initialise the MapView
            binding.mvClientLocation.onCreate(null)
            binding.mvClientLocation.onResume()
            // Set the map ready callback to receive the GoogleMap object
            binding.mvClientLocation.getMapAsync(this)
        }
    }

    interface OnItemClick {
        fun onItemLongClick(position: Int)
    }
}