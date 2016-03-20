/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientlistfragment;

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
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.ClientActivity;
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
public class ClientListFragment extends MifosBaseFragment implements ClientListMvpView{

    @InjectView(R.id.lv_clients) ListView lv_clients;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;

    private DataManager mDatamanager;
    private ClientListPresenter mClientListPresenter;
    ClientNameListAdapter clientNameListAdapter;

    private View rootView;
    private List<Client> clientList = new ArrayList<>();
    private boolean loadmore = false;
    private boolean ClientAvailable = true;
    private int totalFilteredRecords = 0;
    private int CHECK_MORE_CLIENT = 0;
    private int limit = 200;
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
        mDatamanager = new DataManager();
        mClientListPresenter = new ClientListPresenter(mDatamanager);
        mClientListPresenter.attachView(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mClientListPresenter.loadclientlist();
                swipeRefreshLayout.setRefreshing(true);
                ClientAvailable = true;
            }
        });
        fetchClientList();
        return rootView;
        
    }

    public void inflateClientList() {
        clientNameListAdapter = new ClientNameListAdapter(getContext(), clientList);
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
        if (isInfiniteScrollEnabled)
            setInfiniteScrollListener();
    }

    public void fetchClientList() {
        if (clientList.size() > 0) {
            inflateClientList();
        } else {
            totalFilteredRecords = 0;
            mClientListPresenter.loadclientlist();
        }
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }


    /**
     * Method setup the OnScrollListener that get the values on every scroll
     * and when the user reached to last item it call the loadmore item request to API
     * and update the clientList.
     */
    public void setInfiniteScrollListener() {
        lv_clients.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount!=0) {


                    if(!loadmore && ClientAvailable){
                        loadmore = true;
                        swipeRefreshLayout.setRefreshing(true);
                        mClientListPresenter.loadmoreclientlist(clientList.size(),limit);
                    }else if(CHECK_MORE_CLIENT == 1)
                        Toaster.show(rootView,"No more clients Available");


                }
            }
        });
    }

    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientListPresenter.detachView();
    }

    @Override
    public void showClientList(Page<Client> page) {
        totalFilteredRecords = page.getTotalFilteredRecords();
        clientList = page.getPageItems();
        inflateClientList();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorFetchingList() {
        Toaster.show(rootView, "There was some error fetching list.");
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMoreClientList(Page<Client> clientPage) {
        clientList.addAll(clientPage.getPageItems());
        clientNameListAdapter.notifyDataSetChanged();
        loadmore = false;
        swipeRefreshLayout.setRefreshing(false);

        //checking the response size if size is zero then set the ClientAvailable = false
        //this will reflect into scroll method and it will show
        if(clientPage.getPageItems().size() == 0 && (totalFilteredRecords== clientList.size())){
            ClientAvailable = false;
            CHECK_MORE_CLIENT = 1;
        }


    }

    @Override
    public void showprogressbar(boolean status) {
        if(status){
            showProgress();
        }else {
            hideProgress();
        }
    }
}
