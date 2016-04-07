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
import android.widget.TextView;

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

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Tarun on 2,April,2016.
 */
public class MapAddressFragment extends MifosBaseFragment {

    private View rootView;

    @InjectView(R.id.tv_client_address_heading)
    TextView headingTextView;

    @InjectView(R.id.lv_client_address_list)
    ListView addressList;
    private double latitude;
    private double longitude;
    private int clientId;
    public static String EXTRA_LAT = "lat";
    public static String EXTRA_LONG = "long";
    public static String Extra_CLIENT_ID = "client_id";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.client_save_pin, menu);
        menu.findItem(R.id.save_pin).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_address_list, container, false);
        ButterKnife.inject(this, rootView);

        clientId = getArguments().getInt(PinpointClientActivity.EXTRA_CLIENT_ID);
        latitude = getArguments().getDouble(PinpointClientActivity.EXTRA_LATITUDE);
        longitude = getArguments().getDouble(PinpointClientActivity.EXTRA_LONGITUDE);
        setHasOptionsMenu(true);
        headingTextView.setVisibility(View.INVISIBLE);
        showProgress(getResources().getString(R.string.progress_fetching_addresses));
        showList();

        return rootView;
    }

    public void showList() {
        headingTextView.setVisibility(View.VISIBLE);
        App.apiManager.getClientAddress(clientId, new Callback<List<ClientAddress>>() {
            @Override
            public void success(final List<ClientAddress> clientAddressList, Response response) {
                hideProgress();
                final ClientAddressAdapter clientAddressAdapter = new ClientAddressAdapter(getActivity(), clientAddressList);
                if (clientAddressList.size() > 0) {
                    headingTextView.setText(R.string.tv_select_address);
                    addressList.setAdapter(clientAddressAdapter);

                    addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            int rowId = clientAddressList.get(i).getId();
                            String city = clientAddressList.get(i).getCity();
                            String state = clientAddressList.get(i).getState();

                            showProgress(getResources().getString(R.string.message_update_address));
                            updateAddress(clientId, rowId, city, state);
                        }
                    });
                } else {

                    headingTextView.setText(R.string.message_no_address_found);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(R.string.message_no_address_found_add_new)
                            .setTitle(R.string.message).setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Bundle bundle = new Bundle();
                                    bundle.putDouble(EXTRA_LAT, latitude);
                                    bundle.putDouble(EXTRA_LONG, longitude);
                                    bundle.putInt(Extra_CLIENT_ID, clientId);
                                    ClientAddAddressFragment fragment = new ClientAddAddressFragment();
                                    android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    fragment.setArguments(bundle);
                                    transaction.replace(R.id.container, fragment).commit();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getActivity().finish();
                                }
                            }).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toaster.show(getView(), R.string.error_unknown);
            }
        });
    }

    private void updateAddress(int cId, int rId, String city, String state) {
        App.apiManager.updateClientAddressRow(cId, rId,
                new ClientAddressUpdateRequest(city, state, latitude, longitude),
                new Callback<ClientAddressResponse>() {
                        @Override
                        public void success(ClientAddressResponse clientAddressResponse, Response response) {
                            hideProgress();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle(R.string.message);
                            dialog.setMessage(R.string.message_success).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().finish();
                                }
                            }).create().show();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            hideProgress();
                            Toaster.show(getView(), R.string.error_unknown);
                        }
                    });
    }

}
