/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.groupslist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.mifosxdroid.online.GroupsActivity;
import com.mifos.mifosxdroid.online.grouplist.GroupListFragment;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit.client.Response;

/**
 * Created by nellyk on 2/27/2016.
 */
public class GroupsListFragment extends MifosBaseFragment implements GroupsListMvpView {


    @BindView(R.id.lv_groups)
    ListView lv_groups;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    GroupsListPresenter mGroupsListPresenter;
    List<Group> groupList = new ArrayList<Group>();
    private GroupNameListAdapter mGroupListAdapter;
    private GroupListFragment.OnFragmentInteractionListener mListener;
    private View rootView;
    private Context context;
    private int offset = 0;
    private int limit = 200;
    private int index = 0;
    private int top = 0;

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
        context = getActivity().getApplicationContext();

        ButterKnife.bind(this, rootView);
        mGroupsListPresenter.attachView(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Do Nothing For Now
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        fetchGroupList();

        return rootView;
    }

    public void inflateGroupList() {

        mGroupListAdapter = new GroupNameListAdapter(context, groupList);
        lv_groups.setAdapter(mGroupListAdapter);
        lv_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent groupActivityIntent = new Intent(getActivity(), GroupsActivity.class);
                groupActivityIntent.putExtra(Constants.GROUP_ID, groupList.get(i).getId());
                startActivity(groupActivityIntent);

            }
        });


        if (isInfiniteScrollEnabled) {
            setInfiniteScrollListener(mGroupListAdapter);
        }


    }

    public void fetchGroupList() {
        if (groupList.size() > 0) {
            inflateGroupList();
        } else {
            mGroupsListPresenter.loadAllGroup();
        }
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public void setInfiniteScrollListener(final GroupNameListAdapter groupListAdapter) {

        lv_groups.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int
                    visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {

                    offset += limit + 1;
                    //Load More Groups
                    mGroupsListPresenter.loadMoreGroups(offset, limit);
                }
            }
        });

    }

    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }

    @Override
    public void showGroups(Page<Group> groupPage) {
        groupList = groupPage.getPageItems();
        inflateGroupList();
    }

    @Override
    public void showMoreGroups(Page<Group> groupPage) {
        groupList.addAll(groupPage.getPageItems());
        mGroupListAdapter.notifyDataSetChanged();
        index = lv_groups.getFirstVisiblePosition();
        View v = lv_groups.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        lv_groups.setSelectionFromTop(index, top);
    }

    @Override
    public void showFetchingError(String s, Response response) {
        if (getActivity() != null) {
            try {
                Log.i("Error", "" + response.getStatus());
                if (response.getStatus() == 401) {
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

