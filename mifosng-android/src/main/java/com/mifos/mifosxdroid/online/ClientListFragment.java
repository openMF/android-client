package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.GroupActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.services.API;
import com.mifos.utils.Constants;

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

    List<Client> pageItems;
    FragmentChangeListener activityListener;
    private Context context;

    public ClientListFragment() {

    }


    public static interface FragmentChangeListener {
        void replaceFragments(Fragment fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);
        ButterKnife.inject(this, rootView);

        context = getActivity().getApplicationContext();
        activityListener = (FragmentChangeListener) getActivity();

        setupUI();

        API.clientService.listAllClients(new Callback<Page<Client>>() {
            @Override
            public void success(Page<Client> page, Response response) {
                pageItems = page.getPageItems();

                ClientNameListAdapter clientNameListAdapter = new ClientNameListAdapter(context, pageItems);
                lv_clients.setAdapter(clientNameListAdapter);
                lv_clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent clientActivityIntent = new Intent(getActivity(),ClientActivity.class);
                        clientActivityIntent.putExtra(Constants.CLIENT_ID, pageItems.get(i).getId());
                        startActivity(clientActivityIntent);

                    }
                });
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                if(getActivity() != null)
                    Toast.makeText(getActivity(), "There was some error fetching list.", Toast.LENGTH_SHORT).show();

            }
        });


        return rootView;
    }

    public void setupUI() {

        setHasOptionsMenu(true);

        lv_clients.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(getActivity() != null)
                Toast.makeText(getActivity(), "Client ID = " + pageItems.get(i).getId(), Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        lv_clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


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
                startActivity(new Intent(getActivity(), GroupActivity.class));
                break;

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
