package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.CenterDetailsActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.services.API;
import com.mifos.utils.Constants;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by ishankhanna on 09/02/14.
 */
public class ClientListFragment extends Fragment {


    @InjectView(R.id.lv_clients) ListView lv_clients;

    View rootView;

    List<Client> clientList = new ArrayList<Client>();
    private Context context;

    public ClientListFragment() {

    }

    public static ClientListFragment newInstance(List<Client> clientList) {
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setClientList(clientList);
        return clientListFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);
        ButterKnife.inject(this, rootView);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();

        fetchClientList();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dashbord_client, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mItem_search:
                startActivity(new Intent(getActivity(), ClientSearchActivity.class));
                break;

            case R.id.offline_menu:
                startActivity(new Intent(getActivity(), CenterDetailsActivity.class));
                break;

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void inflateClientList() {

        ClientNameListAdapter clientNameListAdapter = new ClientNameListAdapter(context, clientList);
        lv_clients.setAdapter(clientNameListAdapter);
        lv_clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent clientActivityIntent = new Intent(getActivity(),ClientActivity.class);
                clientActivityIntent.putExtra(Constants.CLIENT_ID, clientList.get(i).getId());
                startActivity(clientActivityIntent);

            }
        });

    }

    public void fetchClientList() {

        //Check if ClientListFragment has a clientList
        if(clientList.size() > 0) {
            inflateClientList();
        } else {

            //Get a Client List
            API.clientService.listAllClients(new Callback<Page<Client>>() {

                @Override
                public void success(Page<Client> page, Response response) {
                    clientList = page.getPageItems();
                    inflateClientList();
                }

                @Override
                public void failure(RetrofitError retrofitError) {

                    if(getActivity() != null) {
                        try {
                            Log.i("Error", "" + retrofitError.getResponse().getStatus());
                            if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                                Toast.makeText(getActivity(), "Authorization Expired - Please Login Again", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LogoutActivity.class));
                                getActivity().finish();

                            } else {
                                Toast.makeText(getActivity(), "There was some error fetching list.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException npe) {
                            Toast.makeText(getActivity(), "There is some problem with your internet connection.", Toast.LENGTH_SHORT).show();

                        }


                    }
                }
            });

        }





    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }
}
