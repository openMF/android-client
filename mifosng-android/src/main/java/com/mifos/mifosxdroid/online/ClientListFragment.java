/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.utils.Constants;

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
public class ClientListFragment extends MifosBaseFragment {

    @InjectView(R.id.lv_clients)
    ListView lv_clients;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private View rootView;
    private List<Client> clientList = new ArrayList<>();
    private int offset = 0;
    private int limit = 200;
    private int index = 0;
    private int top = 0;

    private boolean isInfiniteScrollEnabled = true;

    public static ClientListFragment newInstance(List<Client> clientList) {
        ClientListFragment clientListFragment = new ClientListFragment();
        if (clientList != null)
            clientListFragment.setClientList(clientList);
        return clientListFragment;
    }

    public static ClientListFragment newInstance(List<Client> clientList, boolean isParentFragmentAGroupFragment) {
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setClientList(clientList);
        if (isParentFragmentAGroupFragment)
            clientListFragment.setInfiniteScrollEnabled(false);
        return clientListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);
        setHasOptionsMenu(true);
        ButterKnife.inject(this, rootView);

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        fetchClientList();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        lv_clients.setVisibility(View.GONE);
        super.onDestroyView();
    }

    public void inflateClientList() {
        ClientNameListAdapter clientNameListAdapter = new ClientNameListAdapter(getContext(), clientList);
        lv_clients.setAdapter(clientNameListAdapter);
        lv_clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent clientActivityIntent = new Intent(getActivity(), ClientActivity.class);
                clientActivityIntent.putExtra(Constants.CLIENT_ID, clientList.get(i).getId());
                startActivity(clientActivityIntent);
            }
        });
        // If the parent fragment is Group Fragment then the list of clients does not
        // require an infinite scroll as all the clients will be loaded at once
        hideProgress();
        if (isInfiniteScrollEnabled)
            setInfiniteScrollListener(clientNameListAdapter);
    }

    public void fetchClientList() {
        showProgress();
        if (clientList.size() > 0) {
            inflateClientList();
        } else {
            swipeRefreshLayout.setRefreshing(true);

            App.apiManager.listClients(new Callback<Page<Client>>() {
                @Override
                public void success(Page<Client> page, Response response) {
                    clientList = page.getPageItems();
                    inflateClientList();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toaster.show(rootView, "There was some error fetching list.");
                    hideProgress();
                }
            });
        }
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public void setInfiniteScrollListener(final ClientNameListAdapter clientNameListAdapter) {
        lv_clients.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    offset += limit + 1;
                    swipeRefreshLayout.setRefreshing(true);
                    App.apiManager.listClients(offset, limit, new Callback<Page<Client>>() {
                        @Override
                        public void success(Page<Client> clientPage, Response response) {
                            clientList.addAll(clientPage.getPageItems());
                            clientNameListAdapter.notifyDataSetChanged();
                            index = lv_clients.getFirstVisiblePosition();
                            View v = lv_clients.getChildAt(0);
                            top = (v == null) ? 0 : v.getTop();
                            lv_clients.setSelectionFromTop(index, top);
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            swipeRefreshLayout.setRefreshing(false);
                            Toaster.show(rootView, "There was some error fetching list.");
                        }
                    });
                }
            }
        });
    }

    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }
}
