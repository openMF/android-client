package com.mifos.mifosxdroid.online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.App;
import com.mifos.api.model.ClientAddressAddRequest;
import com.mifos.api.model.ClientAddressResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tarun on 14/04/2016.
 */
public class ClientAddAddressFragment extends MifosBaseFragment {

    @InjectView(R.id.btn_submit_address)
    Button submitButton;

    @InjectView(R.id.et_city)
    EditText editCity;

    @InjectView(R.id.et_state)
    EditText editState;

    @InjectView(R.id.tv_state_label)
    TextView stateLabel;

    @InjectView(R.id.tv_city_label)
    TextView cityLabel;

    private double latitude;
    private double longitude;
    private String city;
    private String state;
    private int clientId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_client_address, container, false);
        setHasOptionsMenu(true);
        ButterKnife.inject(this, rootView);

        latitude = getArguments().getDouble(MapAddressFragment.EXTRA_LAT);
        longitude = getArguments().getDouble(MapAddressFragment.EXTRA_LONG);
        clientId = getArguments().getInt(MapAddressFragment.Extra_CLIENT_ID);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = editCity.getEditableText().toString();
                state = editState.getEditableText().toString();
                if (validateAddress(city, state)) {
                    showProgress(getResources().getString(R.string.progress_adding_address));
                    submitAddress(clientId, latitude, longitude, city, state);
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(R.string.message_title_input_validation).setMessage(R.string.message_input_validation)
                            .show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.client_save_pin, menu);
        menu.findItem(R.id.save_pin).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void submitAddress(int cId, double latitude, double longitude, String city, String state) {
        App.apiManager.addClientAddress(cId, new ClientAddressAddRequest(city, state, latitude, longitude),
                new Callback<ClientAddressResponse>() {
                    @Override
                    public void success(ClientAddressResponse clientAddressResponse, Response response) {
                        hideProgress();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.message);
                        dialog.setMessage(R.string.message_success).setCancelable(false).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                            }
                        }).create().show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgress();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.message);
                        dialog.setMessage(R.string.error_unknown)
                                .create().show();
                    }
                });
    }

    private boolean validateAddress(String city, String state) {
        Pattern pattern = Pattern.compile("^[a-zA-z0-9 ]+$");
        Matcher c = pattern.matcher(city);
        Matcher s = pattern.matcher(state);
        return c.find() && s.find();
    }
}
