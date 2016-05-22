package com.mifos.mifosxdroid.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mifos.App;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.util.Toaster;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author fomenkoo
 */
public class PinpointClientActivity extends MifosMapActivity {

    public static final String EXTRA_CLIENT_ID = "extra_client_id";
    private MarkerOptions client = new MarkerOptions();
    private int clientId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        loadMap(R.id.container);
        clientId = getIntent().getIntExtra(EXTRA_CLIENT_ID, 0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_save_pin, menu);
        menu.findItem(R.id.save_pin).setIcon(new IconDrawable(this, MaterialIcons.md_pin_drop)
                .colorRes(R.color.white).actionBarSize());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                client.position(latLng);
                client.title(String.valueOf(clientId));
                map.clear();
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                map.addMarker(client);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_pin) {
            if (client.getPosition() == null) {
                Toaster.show(findViewById(android.R.id.content), "You should drop client pin " +
                        "first!");
                return false;
            }

            App.apiManager.updateGpsData(clientId, new GpsCoordinatesRequest(client.getPosition()
            ), new Callback<GpsCoordinatesResponse>() {
                @Override
                public void success(GpsCoordinatesResponse gpsCoordinatesResponse, Response
                        response) {
                    hideProgress();
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toaster.show(findViewById(android.R.id.content), "Error saving client " +
                            "location!");
                    hideProgress();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
