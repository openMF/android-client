/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.activity;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

/**
 * @author fomenkoo
 */
public class MifosMapActivity extends MifosBaseActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    protected GoogleMap map;

    protected void loadMap(int container) {
        GoogleMapOptions options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        replaceFragment(mapFragment, false, container);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMyLocationChange(Location loc) {
        CameraUpdate position = CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 13);
        map.moveCamera(position);
        map.setOnMyLocationChangeListener(null);
    }
}
