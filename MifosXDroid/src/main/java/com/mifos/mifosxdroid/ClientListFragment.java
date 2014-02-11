package com.mifos.mifosxdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mifos.objects.Page;
import com.mifos.objects.PageItem;
import com.mifos.objects.User;
import com.mifos.utils.ClientService;
import com.mifos.utils.MifosRestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class ClientListFragment extends Fragment {

    SharedPreferences sharedPreferences;

    ActionBarActivity activity;

    ListView lv_clients;

    View rootView;

    public ClientListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);


        activity = (ActionBarActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        setupUI();
        //TODO remove syso call
        System.out.println("Auth Key = " + sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA"));

        MifosRestAdapter mifosRestAdapter = new MifosRestAdapter(sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA"));

        ClientService clientService = mifosRestAdapter.getRestAdapter().create(ClientService.class);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Page clientPage = clientService.listAllClients();
        List<PageItem> pageItems = clientPage.getPageItems();

        List<String> clientNames = new ArrayList<String>();
        for (int i = 0; i < pageItems.size(); i++) {
            clientNames.add(pageItems.get(i).getDisplayName());
        }

        String[] names = clientNames.toArray(new String[clientNames.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClientListFragment.this.getActivity(), android.R.layout.simple_list_item_1,
                names);

        lv_clients.setAdapter(adapter);

        return rootView;
    }

    public void setupUI() {

        setHasOptionsMenu(true);

        lv_clients = (ListView) rootView.findViewById(R.id.lv_clients);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dashbord_client, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mItem_search:
                ((DashboardFragmentActivity) getActivity()).replaceFragments(new ClientSearchFragment());
                break;

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
