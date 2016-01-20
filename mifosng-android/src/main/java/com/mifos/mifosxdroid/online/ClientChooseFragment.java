package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientChooseAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.mifos.mifosxdroid.core.BaseFragment;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.utils.Constants;
import com.mifos.utils.MifosApplication;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import android.content.SharedPreferences;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.widget.Toast;


/**
 * Created by Nasim Banu on 19,January,2016.
 */
public class ClientChooseFragment extends BaseFragment implements AdapterView.OnItemClickListener{
    private static final String TAG = ClientSearchFragment.class.getSimpleName();
    public static final String PREFS_NAME = "MY_PREFS";

    @InjectView(R.id.lv_clients)
    ListView results;

    private List<Client> clients = new ArrayList<>();
    List<Integer> clientIds = new ArrayList<Integer>();
    private ClientChooseAdapter adapter;

    public static ClientChooseFragment newInstance() {
        ClientChooseFragment clientChooseFragment = new ClientChooseFragment();
        return clientChooseFragment;
    }

    public ClientChooseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_client_choose, null);
        ButterKnife.inject(this, rootView);
        adapter = new ClientChooseAdapter(getContext(), clients, R.layout.list_item_client);
        results.setAdapter(adapter);
        results.setOnItemClickListener(this);
        loadClients();
        return rootView;
    }

    public void loadClients() {
        ((MifosApplication) getActivity().getApplication()).api.clientService.listAllClients(new Callback<Page<Client>>() {
            @Override
            public void success(Page<Client> page, Response response) {

                clients = page.getPageItems();
                adapter.setList(clients);
                adapter.notifyDataSetChanged();
                if (!clients.isEmpty()) {
                    Iterator<Client> iterator = clients.iterator();
                    clientIds.clear();
                    while (iterator.hasNext()) {
                        Client client = iterator.next();
                        clientIds.add(client.getId());
                    }
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(getActivity(), "Cannot get clients, There might be some problem!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = preferences.edit();
        edt.putInt("CLIENT_ID", clientIds.get(i));
        edt.commit();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        Fragment surveyListFragment = new SurveyListFragment();
        fragmentTransaction
                .replace(R.id.container, surveyListFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
