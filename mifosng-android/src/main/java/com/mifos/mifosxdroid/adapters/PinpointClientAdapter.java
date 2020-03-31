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
import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.ClientAddressResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 16/01/17.
 */
public class PinpointClientAdapter extends RecyclerView.Adapter<PinpointClientAdapter.ViewHolder> {

    private List<ClientAddressResponse> addressResponses;
    private Context context;
    private OnItemClick itemClick;

    @Inject
    public PinpointClientAdapter() {
        addressResponses = new ArrayList<>();
    }

    @Override
    public PinpointClientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pinpoint_location, parent, false);
        return new PinpointClientAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PinpointClientAdapter.ViewHolder holder, int position) {

        ClientAddressResponse addressResponse = addressResponses.get(position);

        holder.tvAddress.setText(addressResponse.getPlaceAddress());
        holder.mvClientLocation.setTag(addressResponse);

        // Ensure the map has been initialised by the on map ready callback in ViewHolder.
        // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
        // when the callback is received.
        if (holder.map != null) {
            // The map is already ready to be used
            setMapLocation(holder.map, addressResponse);
        }

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClick.onItemLongClick(holder.getAdapterPosition());
                return false;
            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAddress(List<ClientAddressResponse> addressResponses) {
        this.addressResponses = addressResponses;
        notifyDataSetChanged();
    }

    public ClientAddressResponse getItem(int position) {
        return this.addressResponses.get(position);
    }

    public void setItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return addressResponses.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        // Cleanup MapView here
        if (holder.map != null) {
            holder.map.clear();
            holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    private void setMapLocation(GoogleMap map, ClientAddressResponse location) {
        // Add a marker for this item and set the camera
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
        map.addMarker(new MarkerOptions().position(latLng));
        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        @BindView(R.id.mv_client_location)
        MapView mvClientLocation;

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
            ClientAddressResponse data = (ClientAddressResponse) mvClientLocation.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mvClientLocation != null) {
                // Initialise the MapView
                mvClientLocation.onCreate(null);
                mvClientLocation.onResume();
                // Set the map ready callback to receive the GoogleMap object
                mvClientLocation.getMapAsync(this);
            }
        }
    }

    public interface OnItemClick {
        void onItemLongClick(int position);
    }
}