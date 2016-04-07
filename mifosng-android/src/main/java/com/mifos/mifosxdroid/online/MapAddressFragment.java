/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.App;
import com.mifos.api.model.ClientAddress;
import com.mifos.api.model.ClientAddressResponse;
import com.mifos.api.model.ClientAddressUpdateRequest;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.activity.PinpointClientActivity;
import com.mifos.mifosxdroid.adapters.ClientAddressAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tarun on 2,April,2016.
 */
public class MapAddressFragment extends MifosBaseFragment {

    private View rootView;
    private ListView addressList;
    private double latitude;
    private double longitude;
    private int clientId;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.client_save_pin, menu);
        menu.findItem(R.id.save_pin).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_address_list, container, false);
        addressList = (ListView) rootView.findViewById(R.id.lv_client_address_list);
        clientId = getArguments().getInt(PinpointClientActivity.EXTRA_CLIENT_ID);
        latitude = getArguments().getDouble(PinpointClientActivity.EXTRA_LATITUDE);
        longitude = getArguments().getDouble(PinpointClientActivity.EXTRA_LONGITUDE);
        setHasOptionsMenu(true);
        showList();

        return rootView;
    }

    public void showList() {
        App.apiManager.getClientAddress(clientId, new Callback<List<ClientAddress>>() {
            @Override
            public void success(final List<ClientAddress> clientAddressList, Response response) {
                ClientAddressAdapter clientAddressAdapter = new ClientAddressAdapter(getActivity(), clientAddressList);
                if (clientAddressList.size() > 0) {

                    addressList.setAdapter(clientAddressAdapter);

                    addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            int rowId = clientAddressList.get(i).getId();
                            App.apiManager.updateClientAddressRow(clientId, rowId,
                                    new ClientAddressUpdateRequest(clientAddressList.get(i).getCity(),
                                            clientAddressList.get(i).getState(), latitude, longitude),
                                    new Callback<ClientAddressResponse>() {
                                        @Override
                                        public void success(ClientAddressResponse clientAddressResponse, Response response) {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                            dialog.setTitle("Message");
                                            dialog.setMessage("Successfully Updated").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    getActivity().finish();
                                                }
                                            }).create().show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Toaster.show(getView(), "Update failed!");
                                        }
                                    });
                        }
                    });
                }

                else {
                    //TODO Implement Add Address API
                    Toaster.show(getView(),"No address");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toaster.show(getView(), "RETROFIT ERROR");
            }
        });
    }

}
