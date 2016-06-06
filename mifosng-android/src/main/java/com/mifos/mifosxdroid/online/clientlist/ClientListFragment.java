/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListner;
import com.mifos.mifosxdroid.core.RecyclerItemClickListner.OnItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.ClientActivity;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.utils.Constants;
import com.mifos.utils.EspressoIdlingResource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by ishankhanna on 09/02/14.
 */
public class ClientListFragment extends MifosBaseFragment
        implements OnItemClickListener, ClientListMvpView{

    @InjectView(R.id.rv_clients)
    RecyclerView rv_clients;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private View rootView;

    private List<Client> clientList = new ArrayList<>();

    private LinearLayoutManager layoutManager;

    private int totalFilteredRecords = 0;

    private int limit = 100;

    private boolean isInfiniteScrollEnabled = true;

    ClientNameListAdapter clientNameListAdapter;

    @Inject
    ClientListPresenter mClientListPresenter;

    public static ClientListFragment newInstance(List<Client> clientList) {
        ClientListFragment clientListFragment = new ClientListFragment();
        if (clientList != null)
            clientListFragment.setClientList(clientList);
        return clientListFragment;
    }

    public static ClientListFragment newInstance(List<Client> clientList, boolean
            isParentFragmentAGroupFragment) {
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setClientList(clientList);
        if (isParentFragmentAGroupFragment)
            clientListFragment.setInfiniteScrollEnabled(false);
        return clientListFragment;
    }

    @Override
    public void onItemClick(View childView, int position) {
        Intent clientActivityIntent = new Intent(getActivity(), ClientActivity.class);
        clientActivityIntent.putExtra(Constants.CLIENT_ID, clientList.get(position).getId());
        startActivity(clientActivityIntent);
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity)getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);
        setHasOptionsMenu(true);
        setToolbarTitle(getResources().getString(R.string.clients));
        ButterKnife.inject(this, rootView);

        mClientListPresenter.attachView(this);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_clients.setLayoutManager(layoutManager);
        rv_clients.addOnItemTouchListener(new RecyclerItemClickListner(getActivity(), this));
        rv_clients.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R
                .color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchClientList();
            }
        });

        fetchClientList();

        return rootView;
    }

    public void inflateClientList() {
        clientNameListAdapter = new ClientNameListAdapter(getContext(), clientList);
        rv_clients.setAdapter(clientNameListAdapter);

        // initialize OnScroll Listener
        if (isInfiniteScrollEnabled)
            setInfiniteScrollListener(clientNameListAdapter);
    }

    public void fetchClientList() {
        EspressoIdlingResource.increment(); // App is busy until further notice.
        totalFilteredRecords = 0;
        mClientListPresenter.loadClients();
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    @SuppressWarnings("deprecation")
    public void setInfiniteScrollListener(final ClientNameListAdapter clientNameListAdapter) {

        rv_clients.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Toaster.show(rootView, "Loading More Clients");
                mClientListPresenter.loadMoreClients(clientList.size(), limit);

            }
        });

    }

    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }

    @Override
    public void showClientList(Page<Client> clientPage) {
        totalFilteredRecords = clientPage.getTotalFilteredRecords();
        clientList = clientPage.getPageItems();
        inflateClientList();
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        EspressoIdlingResource.decrement(); // App is idle.
    }

    @Override
    public void showErrorFetchingClients(String s) {
        Toaster.show(rootView, s);
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        EspressoIdlingResource.decrement(); // App is idle.
    }

    @Override
    public void showMoreClientsList(Page<Client> clientPage) {
        clientList.addAll(clientPage.getPageItems());
        clientNameListAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

        //checking the response size if size is zero then show toast No More
        // Clients Available for fetch
        if (clientPage.getPageItems().size() == 0 && (totalFilteredRecords ==
                clientList.size()))
            Toaster.show(rootView, "No more clients Available");
    }

    @Override
    public void showProgressbar(boolean b) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientListPresenter.detachView();
    }
}
