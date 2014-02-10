package com.mifos.mifosxdroid;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mifos.objects.Page;
import com.mifos.objects.PageItem;
import com.mifos.utils.ClientService;
import com.mifos.utils.MifosRestAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 10/02/14.
 */
public class ClientSearchFragment extends Fragment {

    public ClientSearchFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_client, container, false);
        ListView lv_clients = (ListView) rootView.findViewById(R.id.lv_clients);
        //TODO Update the Key
        MifosRestAdapter mifosRestAdapter = new MifosRestAdapter("KEY HERE");

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClientSearchFragment.this.getActivity(),android.R.layout.simple_list_item_1,
                names);

        lv_clients.setAdapter(adapter);

        return rootView;
    }
}
