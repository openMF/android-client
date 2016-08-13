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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupNameListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.GroupsActivity;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nellyk on 2/27/2016.
 * GroupsListFragment Fetching Showing GroupsList in RecyclerView from
 * </>demo.openmf.org/fineract-provider/api/v1/groups?paged=true&offset=offset_value&limit
 * =limit_value</>
 */
public class GroupsListFragment extends MifosBaseFragment implements GroupsListMvpView,
        RecyclerItemClickListener.OnItemClickListener {


    @BindView(R.id.rv_groups)
    RecyclerView rv_groups;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noGroupsText)
    TextView mNoGroupsText;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    GroupsListPresenter mGroupsListPresenter;
    List<Group> mGroupList = new ArrayList<>();
    private GroupNameListAdapter mGroupListAdapter;
    private View rootView;
    private int limit = 100;
    private int mApiRestCounter;


    //TODO Remove this default constructor
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
        return groupListFragment;
    }

    @Override
    public void onItemClick(View childView, int position) {
        Intent groupActivityIntent = new Intent(getActivity(), GroupsActivity.class);
        groupActivityIntent.putExtra(Constants.GROUP_ID, mGroupList.get(position).getId());
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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_groups.setLayoutManager(mLayoutManager);
        rv_groups.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_groups.setHasFixedSize(true);

        mApiRestCounter = 1;
        mGroupsListPresenter.loadGroups(true, 0, limit);

        /**
         * Setting mApiRestCounter to 1 and send Fresh Request to Server
         */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mApiRestCounter = 1;

                mGroupsListPresenter.loadGroups(true, 0, limit);

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mGroupsList.size()) and limit(100).
         */
        rv_groups.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                mApiRestCounter = mApiRestCounter + 1;
                mGroupsListPresenter.loadGroups(true, mGroupList.size(), limit);
            }
        });

        return rootView;
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Client list.
     */
    @OnClick(R.id.noGroupsIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mGroupsListPresenter.loadGroups(true, 0, limit);
    }

    public void setGroupList(List<Group> groupList) {
        this.mGroupList = groupList;
    }

    /**
     * Setting Data in RecyclerView of the GroupsListFragment if the mApiRestCounter value is 1,
     * otherwise adding value in ArrayList and updating the GroupListAdapter.
     * If the Response is have null then show Toast to User There is No Center Available.
     *
     * @param groupPage is the List<Group> and
     *                  TotalValue of center API Response by Server
     */
    @Override
    public void showGroups(Page<Group> groupPage) {
        /**
         * if mApiRestCounter is 1, So this is the first Api Request.
         * else if mApiRestCounter is greater than 1, SO this is for loadmore request.
         */
        if (mApiRestCounter == 1) {
            mGroupList = groupPage.getPageItems();
            mGroupListAdapter = new GroupNameListAdapter(getActivity(), mGroupList);
            rv_groups.setAdapter(mGroupListAdapter);

            ll_error.setVisibility(View.GONE);
        } else {

            mGroupList.addAll(groupPage.getPageItems());
            mGroupListAdapter.notifyDataSetChanged();

            //checking the response size if size is zero then show toast No More
            // Clients Available for fetch
            if (groupPage.getPageItems().size() == 0 &&
                    (groupPage.getTotalFilteredRecords() == mGroupList.size()))
                Toaster.show(rootView, "No more Groups Available");
        }
    }

    /**
     * Check the mApiRestCounter value is the value is 1,
     * So there no data to show and setVisibility VISIBLE
     * of Error ImageView and TextView layout and otherwise simple
     * show the Toast Message of Error Message.
     *
     * @param s is the Error Message given by GroupsListPresenter
     */
    @Override
    public void showFetchingError(String s) {
        if (mApiRestCounter == 1) {
            ll_error.setVisibility(View.VISIBLE);
            mNoGroupsText.setText(s + "\n Click to Refresh ");
        }

        Toaster.show(rootView, s);
    }

    /**
     * Check mApiRestCounter value, if the value is 1 then
     * show MifosBaseActivity ProgressBar and if it is greater than 1,
     * It means this Request is the second and so on than show SwipeRefreshLayout
     * Check the the b is true or false
     *
     * @param b is the status of the progressbar
     */
    @Override
    public void showProgressbar(boolean b) {

        if (mApiRestCounter == 1) {
            if (b) {
                showMifosProgressBar();
            } else {
                hideMifosProgressBar();
            }
        } else {
            swipeRefreshLayout.setRefreshing(b);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideMifosProgressBar();
        mGroupsListPresenter.detachView();
    }
}

