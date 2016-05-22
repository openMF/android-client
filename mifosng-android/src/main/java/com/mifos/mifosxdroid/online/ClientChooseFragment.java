/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientChooseAdapter;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class ClientChooseFragment extends ProgressableFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.lv_clients)
    ListView results;
    private View root;
    private List<Client> clients = new ArrayList<>();
    private ClientChooseAdapter adapter;
    private boolean areMoreClientsAvailable = true;
    private int totalFilteredRecords = 0;
    private boolean shouldCheckForMoreClients = false;
    private boolean loadmore = false;
    private int limit = 200;
    private boolean isInfiniteScrollEnabled = true;

    public static ClientChooseFragment newInstance() {
        ClientChooseFragment fragment = new ClientChooseFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

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
        showProgress(true);
        shouldCheckForMoreClients = false;
        areMoreClientsAvailable = true;
        totalFilteredRecords = 0;
        App.apiManager.listClients(new Callback<Page<Client>>() {
            @Override
            public void success(Page<Client> page, Response response) {
                clients = page.getPageItems();
                adapter.setList(clients);
                adapter.notifyDataSetChanged();
                showProgress(false);
                totalFilteredRecords = page.getTotalFilteredRecords();
                if (isInfiniteScrollEnabled)
                    setInfiniteScrollListener(adapter);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toaster.show(root, "Cannot get clients, There might be some problem!");
                showProgress(false);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, SurveyListFragment.newInstance(clients.get(i).getId()), "SurveyListFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setInfiniteScrollListener(final ClientChooseAdapter clientChooseAdapter) {
        results.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {

                    if (!loadmore && areMoreClientsAvailable) {
                        loadmore = true;
                        Toaster.show(root, "Loading More Clients");
                        App.apiManager.listClients(clients.size(), limit, new Callback<Page<Client>>() {
                            @Override
                            public void success(Page<Client> clientPage, Response response) {
                                clients.addAll(clientPage.getPageItems());
                                clientChooseAdapter.notifyDataSetChanged();
                                loadmore = false;

                                //checking the response size if size is zero then set the areMoreClientsAvailable = false
                                //this will reflect into scroll method and it will show
                                if (clientPage.getPageItems().size() == 0 && (totalFilteredRecords == clients.size())) {
                                    areMoreClientsAvailable = false;
                                    shouldCheckForMoreClients = true;
                                }
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                Toaster.show(root, "There was some error fetching list.");
                            }
                        });
                    } else if (shouldCheckForMoreClients)
                        Toaster.show(root, "No more clients Available");
                }
            }
        });
    }
}
