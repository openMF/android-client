package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.GroupClient;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class GroupClientListFragment extends MifosBaseFragment {
    @InjectView(R.id.lv_grp_clients)
    ListView lv_grp_clients;
    @InjectView(R.id.swipe_container_grp_client)
    SwipeRefreshLayout swipeRefreshLayout;

    private View rootView;
    private List<Client> clientList = new ArrayList<>();
    private boolean areMoreClientsAvailable = true;
    private int totalFilteredRecords = 0;
    private boolean shouldCheckForMoreClients = false;
    private boolean loadmore = false;
    private int limit = 200;

    private boolean isInfiniteScrollEnabled = true;
    private int groupId ;

    public static GroupClientListFragment newInstance(int groupId) {
        GroupClientListFragment groupClientListFragment = new GroupClientListFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.GROUP_ID, groupId);
        groupClientListFragment.setArguments(args);
        return groupClientListFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getInt(Constants.GROUP_ID);
            Log.d("mayankqwer", " " + groupId);
        }
        Log.d("mayankqwer123", " " + groupId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_group_client_list, container, false);
        setHasOptionsMenu(true);
        setToolbarTitle(getResources().getString(R.string.clients));
        ButterKnife.inject(this, rootView);

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                fetchClientList();
            }
        });
        fetchClientList();
        return rootView;
    }

    public void inflateClientList() {
        ClientNameListAdapter clientNameListAdapter = new ClientNameListAdapter(getContext(), clientList);
        lv_grp_clients.setAdapter(clientNameListAdapter);
    }

    public void fetchClientList() {
        showProgress();
        shouldCheckForMoreClients = false;
        areMoreClientsAvailable = true;
        totalFilteredRecords = 0;
        App.apiManager.listClientsGroup(groupId ,new Callback<GroupClient<Client>>() {
            @Override
            public void success(GroupClient<Client> page, Response response) {
                clientList = page.getClientMembers();
                inflateClientList();
                if(clientList.size()==0){
                    Toast.makeText(getActivity(),R.string.empty_client, Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                hideProgress();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                Toaster.show(rootView, "There was some error fetching list.");
                hideProgress();
            }
        });
    }

}
