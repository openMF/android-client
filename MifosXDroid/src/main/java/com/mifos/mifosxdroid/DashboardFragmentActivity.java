package com.mifos.mifosxdroid;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mifos.objects.Client;
import com.mifos.objects.Page;
import com.mifos.objects.PageItem;
import com.mifos.utils.ClientService;
import com.mifos.utils.MifosRestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class DashboardFragmentActivity extends ActionBarActivity{

    //TODO Remove this Sample Key
    public static String key;
    public static Context context;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        key = getIntent().getStringExtra("Key");
        context = DashboardFragmentActivity.this.getBaseContext();
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_dashboard, new PlaceholderFragment())
                    .commit();
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_client, container, false);
            ListView lv_clients = (ListView) rootView.findViewById(R.id.lv_clients);

            MifosRestAdapter mifosRestAdapter = new MifosRestAdapter(key);

            ClientService clientService = mifosRestAdapter.getRestAdapter().create(ClientService.class);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Page clientPage = clientService.listAllClients();
            List<PageItem> pageItems = clientPage.getPageItems();

            List<String> clientNames = new ArrayList<String>();
            for(int i=0;i<pageItems.size();i++)
            {
                clientNames.add(pageItems.get(i).getDisplayName());
            }

            String[] names = clientNames.toArray(new String[clientNames.size()]);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,
                    names);

            lv_clients.setAdapter(adapter);

            return rootView;
        }
    }

}
