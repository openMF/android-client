/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientChooseAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class ClientChooseFragment extends MifosBaseFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.lv_clients)
    ListView results;
    private View root;
    private List<Client> clients = new ArrayList<>();
    private ClientChooseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_client_choose, null);
        ButterKnife.inject(this, root);
        adapter = new ClientChooseAdapter(getContext(), clients, R.layout.list_item_client);
        results.setAdapter(adapter);
        results.setOnItemClickListener(this);
        loadClients();
        return root;
    }

    public void loadClients() {
        App.apiManager.listClients(new Callback<Page<Client>>() {
            @Override
            public void success(Page<Client> page, Response response) {
                clients = page.getPageItems();
                adapter.setList(clients);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toaster.show(root, "Cannot get clients, There might be some problem!");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
       // ((MifosBaseActivity) getActivity()).replaceFragment(new SurveyListFragment(), false, R.id.container);
        FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new SurveyListFragment(), "SurveyListFragment");
               fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
    }
}
