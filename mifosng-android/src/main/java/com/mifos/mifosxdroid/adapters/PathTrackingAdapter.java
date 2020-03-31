package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.user.UserLatLng;
import com.mifos.objects.user.UserLocation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 24/01/17.
 */
public class PathTrackingAdapter extends RecyclerView.Adapter<PathTrackingAdapter.ViewHolder> {

    private List<UserLocation> userLocations;
    private List<UserLatLng> userLatLngs;
    private Context context;

    @Inject
    public PathTrackingAdapter() {
        userLocations = new ArrayList<>();
        userLatLngs = new ArrayList<>();
    }

    @Override
    public PathTrackingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pinpoint_location, parent, false);
        return new PathTrackingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PathTrackingAdapter.ViewHolder holder, int position) {

        UserLocation location = userLocations.get(position);
        userLatLngs = getLatLngList(location.getLatlng());

        holder.tvAddress.setText(location.getDate() + " from " + location.getStartTime() + " to " +
                location.getStopTime());
        try {
            holder.mvUserLocation.setTag(userLatLngs.get(0));
        } catch (IndexOutOfBoundsException e) {
            /* Prevents crashing upon calling an item not in the list */
        }

        // Ensure the map has been initialised by the on map ready callback in ViewHolder.
        // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
        // when the callback is received.
        if (holder.map != null) {
            // The map is already ready to be used
            try {
                setMapLocation(holder.map, userLatLngs.get(0));
            } catch (IndexOutOfBoundsException e) {
                /* Prevents crashing upon calling an item not in the list */
            }
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPathTracker(List<UserLocation> userLocations) {
        this.userLocations = userLocations;
        notifyDataSetChanged();
    }

    public UserLocation getItem(int position) {
        return this.userLocations.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return userLocations.size();
    }

    @Override
    public void onViewRecycled(PathTrackingAdapter.ViewHolder holder) {
        // Cleanup MapView here
        if (holder.map != null) {
            holder.map.clear();
            holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    private void setMapLocation(GoogleMap map, UserLatLng location) {
        // Add a marker for this item and set the camera
        PolylineOptions polylineOptions = new PolylineOptions();
        for (UserLatLng userLatLng : userLatLngs) {
            polylineOptions.add(new LatLng(userLatLng.getLat(), userLatLng.getLng()));
        }
        map.addPolyline(polylineOptions);
        LatLng startLatLng = new LatLng(location.getLat(), location.getLng());
        LatLng stopLatLng = new LatLng(userLatLngs.get(userLatLngs.size() - 1).getLat(),
                userLatLngs.get(userLatLngs.size() - 1).getLng());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 13f));
        map.addMarker(new MarkerOptions().position(startLatLng));
        map.addMarker(new MarkerOptions().position(stopLatLng));
        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        @BindView(R.id.mv_client_location)
        MapView mvUserLocation;

        @BindView(R.id.tv_address)
        TextView tvAddress;

        @BindView(R.id.card_view)
        CardView cardView;

        GoogleMap map;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            initializeMapView();
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            UserLatLng data = (UserLatLng) mvUserLocation.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mvUserLocation != null) {
                // Initialise the MapView
                mvUserLocation.onCreate(null);
                mvUserLocation.onResume();
                // Set the map ready callback to receive the GoogleMap object
                mvUserLocation.getMapAsync(this);
            }
        }
    }

    public List<UserLatLng> getLatLngList(String latLngString) {
        Gson gson = new Gson();
        List<UserLatLng> latLngs = gson.fromJson(latLngString,
                new TypeToken<List<UserLatLng>>() {
                }.getType());
        return latLngs;
    }
}