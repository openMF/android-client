package com.mifos.mifosxdroid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mifos.App;
import com.mifos.api.model.GpsLocationSurvey;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.MapAddressFragment;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author fomenkoo
 */
public class PinpointClientActivity extends MifosMapActivity {

    public static final String EXTRA_CLIENT_ID = "extra_client_id";
    public static final String EXTRA_SURVEY_ID = "extra_survey_id";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    private MarkerOptions client = new MarkerOptions();
    private int surveyId;
    private int clientId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        loadMap(R.id.container);
        clientId = getIntent().getIntExtra(EXTRA_CLIENT_ID, 0);
        surveyId = getIntent().getIntExtra(EXTRA_SURVEY_ID, 0);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_save_pin, menu);
        menu.findItem(R.id.save_pin).setIcon(new IconDrawable(this, MaterialIcons.md_pin_drop).colorRes(R.color.white).actionBarSize());
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
                Toaster.show(findViewById(android.R.id.content), "You should drop client pin first!");
                return false;
            }

            if (surveyId > 0) {        //To post Location during Survey
                App.apiManager.sendLocationSurvey(clientId, new GpsLocationSurvey(client.getPosition(), surveyId) , new Callback<GpsLocationSurvey>() {
                    public void success(GpsLocationSurvey gpsLocationSurvey, Response response) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(PinpointClientActivity.this);
                        dialog.setTitle("Message");
                        dialog.setMessage(R.string.message_success).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        }).create().show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("\nGPS Survey", "Failure\n");
                    }
                });
            } else {

                MapAddressFragment addressFragment = new MapAddressFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(EXTRA_CLIENT_ID, clientId);
                bundle.putDouble(EXTRA_LATITUDE, client.getPosition().latitude);
                bundle.putDouble(EXTRA_LONGITUDE, client.getPosition().longitude);
                addressFragment.setArguments(bundle);
                replaceFragment(addressFragment, false, R.id.container);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}