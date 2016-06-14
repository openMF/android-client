/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.groupslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupNameListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListner;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.online.GroupsActivity;
import com.mifos.mifosxdroid.online.grouplist.GroupListFragment;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nellyk on 2/27/2016.
 */
public class GroupsListFragment extends MifosBaseFragment implements GroupsListMvpView,
        RecyclerItemClickListner.OnItemClickListener {


    @BindView(R.id.rv_groups)
    RecyclerView rv_groups;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    GroupsListPresenter mGroupsListPresenter;
    List<Group> groupList = new ArrayList<>();
    private GroupNameListAdapter mGroupListAdapter;
    private GroupListFragment.OnFragmentInteractionListener mListener;
    private View rootView;
    private LinearLayoutManager layoutManager;
    private int totalFilteredRecords = 0;
    private int limit = 100;
    private boolean isInfiniteScrollEnabled = true;


    public GroupsListFragment() {

    }

    public static GroupsListFragment newInstance(List<Group> groupList) {
        GroupsListFragment groupListFragment = new GroupsListFragment();
        if (groupList != null)
            groupListFragment.setGroupList(groupList);
        return groupListFragment;
    }

    public static GroupsListFragment newInstance(List<Group> groupList, boolean
            isParentFragmentAGroupFragment) {
        GroupsListFragment groupListFragment = new GroupsListFragment();
        groupListFragment.setGroupList(groupList);
        if (isParentFragmentAGroupFragment) {
            groupListFragment.setInfiniteScrollEnabled(false);
        }
        return groupListFragment;
    }

    @Override
    public void onItemClick(View childView, int position) {
        Intent groupActivityIntent = new Intent(getActivity(), GroupsActivity.class);
        groupActivityIntent.putExtra(Constants.GROUP_ID, groupList.get(position).getId());
        startActivity(groupActivityIntent);
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        setToolbarTitle(getResources().getString(R.string.groups));
        setHasOptionsMenu(true);


        ButterKnife.bind(this, rootView);
        mGroupsListPresenter.attachView(this);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_groups.setLayoutManager(layoutManager);
        rv_groups.addOnItemTouchListener(new RecyclerItemClickListner(getActivity(), this));
        rv_groups.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchGroupList();
            }
        });

        fetchGroupList();

        return rootView;
    }

    public void inflateGroupList() {

        mGroupListAdapter = new GroupNameListAdapter(getActivity(), groupList);
        rv_groups.setAdapter(mGroupListAdapter);

        if (isInfiniteScrollEnabled) {
            setInfiniteScrollListener();
        }
    }

    public void fetchGroupList() {
        totalFilteredRecords = 0;
        mGroupsListPresenter.loadAllGroup();
    }


    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public void setInfiniteScrollListener() {

        rv_groups.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Toaster.show(rootView, "Loading More Clients");
                mGroupsListPresenter.loadMoreGroups(groupList.size(), limit);

            }
        });
    }

    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }

    @Override
    public void showGroups(Page<Group> groupPage) {
        totalFilteredRecords = groupPage.getTotalFilteredRecords();
        groupList = groupPage.getPageItems();
        inflateGroupList();
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMoreGroups(Page<Group> groupPage) {
        groupList.addAll(groupPage.getPageItems());
        mGroupListAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

        //checking the response size if size is zero then show toast No More
        // Clients Available for fetch
        if (groupPage.getPageItems().size() == 0 && (totalFilteredRecords == groupList.size()))
            Toaster.show(rootView, "No more clients Available");
    }

    @Override
    public void showFetchingError(String s, int response) {
        if (getActivity() != null) {
            try {
                Log.i("Error", "" + response);
                if (response == 401) {
                    Toast.makeText(getActivity(), "Authorization Expired - Please " +
                            "Login Again", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

                } else {
                    Toast.makeText(getActivity(), "There was some error fetching list" +
                            ".", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException npe) {
                Toast.makeText(getActivity(), "There is some problem with your " +
                        "internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showProgressbar(boolean b) {
        swipeRefreshLayout.setRefreshing(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGroupsListPresenter.detachView();
    }
}

