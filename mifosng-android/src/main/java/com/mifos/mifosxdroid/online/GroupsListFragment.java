/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online;

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

import com.mifos.App;
import com.mifos.mifosxdroid.LoginActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nellyk on 2/27/2016.
 */
public class GroupsListFragment extends MifosBaseFragment {


    @InjectView(R.id.lv_groups)
    ListView lv_groups;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    List<Group> groupList = new ArrayList<Group>();
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

    public static GroupsListFragment newInstance(List<Group> groupList, boolean isParentFragmentAGroupFragment) {
        GroupsListFragment groupListFragment = new GroupsListFragment();
        groupListFragment.setGroupList(groupList);
        if (isParentFragmentAGroupFragment) {
            groupListFragment.setInfiniteScrollEnabled(false);
        }
        return groupListFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        setToolbarTitle(getResources().getString(R.string.groups));
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        ButterKnife.inject(this, rootView);
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

        final GroupNameListAdapter groupListAdapter = new GroupNameListAdapter(context, groupList);
        lv_groups.setAdapter(groupListAdapter);
        lv_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent groupActivityIntent = new Intent(getActivity(), GroupsActivity.class);
                groupActivityIntent.putExtra(Constants.GROUP_ID, groupList.get(i).getId());
                startActivity(groupActivityIntent);

            }
        });


        if (isInfiniteScrollEnabled) {
            setInfiniteScrollListener(groupListAdapter);
        }


    }

    public void fetchGroupList() {
        if (groupList.size() > 0) {
            inflateGroupList();
        } else {

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            App.apiManager.listAllGroup(new Callback<Page<Group>>() {
                @Override
                public void success(Page<Group> page, Response response) {
                    groupList = page.getPageItems();
                    inflateGroupList();
                    swipeRefreshLayout.setRefreshing(false);

                }

                @Override
                public void failure(RetrofitError retrofitError) {

                    swipeRefreshLayout.setRefreshing(false);

                    if (getActivity() != null) {
                        try {
                            Log.i("Error", "" + retrofitError.getResponse().getStatus());
                            if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                                Toast.makeText(getActivity(), "Authorization Expired - Please Login Again", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();

                            } else {
                                Toast.makeText(getActivity(), "There was some error fetching list.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException npe) {
                            Toast.makeText(getActivity(), "There is some problem with your internet connection.", Toast.LENGTH_SHORT).show();

                        }


                    }
                }
            });

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
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {

                    offset += limit + 1;
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                        }
                    });

                    App.apiManager.listAllGroups(offset, limit, new Callback<Page<Group>>() {
                        @Override
                        public void success(Page<Group> groupPage, Response response) {

                            groupList.addAll(groupPage.getPageItems());
                            groupListAdapter.notifyDataSetChanged();
                            index = lv_groups.getFirstVisiblePosition();
                            View v = lv_groups.getChildAt(0);
                            top = (v == null) ? 0 : v.getTop();
                            lv_groups.setSelectionFromTop(index, top);
                            swipeRefreshLayout.setRefreshing(false);

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {

                            swipeRefreshLayout.setRefreshing(false);

                            if (getActivity() != null) {
                                try {
                                    Log.i("Error", "" + retrofitError.getResponse().getStatus());
                                    if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                                        Toast.makeText(getActivity(), "Authorization Expired - Please Login Again", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        getActivity().finish();

                                    } else {
                                        Toast.makeText(getActivity(), "There was some error fetching list.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NullPointerException npe) {
                                    Toast.makeText(getActivity(), "There is some problem with your internet connection.", Toast.LENGTH_SHORT).show();

                                }


                            }

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

