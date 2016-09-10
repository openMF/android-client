/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.groupslist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.GroupNameListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerViewScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.GroupsActivity;
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
        RecyclerItemClickListener.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.rv_groups)
    RecyclerView rv_groups;

    @BindView(R.id.progressbar_group)
    ProgressBar pb_groups;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noGroupsText)
    TextView mNoGroupsText;

    @BindView(R.id.noGroupsIcon)
    ImageView mNoGroupIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    GroupsListPresenter mGroupsListPresenter;

    @Inject
    GroupNameListAdapter mGroupListAdapter;

    LinearLayoutManager mLayoutManager;
    private List<Group> mGroupList;
    private List<Group> selectedGroups;
    private Boolean isParentFragment = false;
    private View rootView;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;


    //TODO Remove this default constructor
    public GroupsListFragment() {

    }

    /**
     * This method will be called, whenever GroupsListFragment will not have Parent Fragment.
     * So, Presenter make the call to Rest API and fetch the Client List and show in UI
     *
     * @return GroupsListFragment
     */
    public static GroupsListFragment newInstance() {
        Bundle arguments = new Bundle();
        GroupsListFragment groupListFragment = new GroupsListFragment();
        groupListFragment.setArguments(arguments);
        return groupListFragment;
    }

    /**
     * This Method will be called, whenever isParentFragment will be true
     * and Presenter do not need to make Rest API call to server. Parent (Fragment or Activity)
     * already fetched the groups and for showing, they call GroupsListFragment.
     * <p/>
     * Example : Showing Parent Groups.
     *
     * @param groupList        List<Group>
     * @param isParentFragment true
     * @return GroupsListFragment
     */
    public static GroupsListFragment newInstance(List<Group> groupList,
                                                 boolean isParentFragment) {
        GroupsListFragment groupListFragment = new GroupsListFragment();
        Bundle args = new Bundle();
        if (isParentFragment && groupList != null) {
            args.putParcelableArrayList(Constants.GROUPS,
                    (ArrayList<? extends Parcelable>) groupList);
            args.putBoolean(Constants.IS_A_PARENT_FRAGMENT, true);
            groupListFragment.setArguments(args);
        }
        return groupListFragment;
    }

    @Override
    public void onItemClick(View childView, int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            Intent groupActivityIntent = new Intent(getActivity(), GroupsActivity.class);
            groupActivityIntent.putExtra(Constants.GROUP_ID, mGroupList.get(position).getId());
            startActivity(groupActivityIntent);
        }
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        if (actionMode == null) {
            actionMode = ((MifosBaseActivity) getActivity()).startSupportActionMode
                    (actionModeCallback);
        }
        toggleSelection(position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        mGroupList = new ArrayList<>();
        selectedGroups = new ArrayList<>();
        actionModeCallback = new ActionModeCallback();
        if (getArguments() != null) {
            mGroupList = getArguments().getParcelableArrayList(Constants.GROUPS);
            isParentFragment = getArguments()
                    .getBoolean(Constants.IS_A_PARENT_FRAGMENT);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        ButterKnife.bind(this, rootView);
        mGroupsListPresenter.attachView(this);

        //setting all the UI content to the view
        showUserInterface();

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         */
        rv_groups.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mGroupsListPresenter.loadGroups(true, totalItemsCount);
            }
        });

        /**
         * First Check the Parent Fragment is true or false. If parent fragment is true then no
         * need to fetch clientList from Rest API, just need to showing parent fragment ClientList
         * and is Parent Fragment is false then Presenter make the call to Rest API and fetch the
         * Client Lis to show. and Presenter make transaction to Database to load saved clients.
         */
        if (isParentFragment) {
            mGroupsListPresenter.showParentClients(mGroupList);
        } else {
            mGroupsListPresenter.loadGroups(false, 0);
        }
        mGroupsListPresenter.loadDatabaseGroups();

        return rootView;
    }

    @Override
    public void showUserInterface() {
        setToolbarTitle(getResources().getString(R.string.groups));
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_groups.setLayoutManager(mLayoutManager);
        rv_groups.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_groups.setHasFixedSize(true);
        rv_groups.setAdapter(mGroupListAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        mGroupsListPresenter.loadGroups(false, 0);
        mGroupsListPresenter.loadDatabaseGroups();
        if (actionMode != null) actionMode.finish();
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Client list.
     */
    @OnClick(R.id.noGroupsIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        rv_groups.setVisibility(View.VISIBLE);
        mGroupsListPresenter.loadGroups(false, 0);
        mGroupsListPresenter.loadDatabaseGroups();
    }

    /**
     * Setting GroupList to the Adapter and updating the Adapter.
     */
    @Override
    public void showGroups(List<Group> groups) {
        mGroupList = groups;
        mGroupListAdapter.setGroups(groups);
    }


    /**
     * Updating Adapter
     *
     * @param groups
     */
    @Override
    public void showLoadMoreGroups(List<Group> groups) {
        mGroupList.addAll(groups);
        mGroupListAdapter.notifyDataSetChanged();
    }

    /**
     * Showing Fetched GroupList size is 0 and show there is no Group to show.
     *
     * @param message String Message.
     */
    @Override
    public void showEmptyGroups(int message) {
        rv_groups.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        mNoGroupsText.setText(getStringMessage(message));
    }

    @Override
    public void unregisterSwipeAndScrollListener() {
        rv_groups.clearOnScrollListeners();
        swipeRefreshLayout.setEnabled(false);
        mNoGroupIcon.setEnabled(false);
    }

    /**
     * This Method showing the Simple Taster Message to user.
     *
     * @param message String Message to show.
     */
    @Override
    public void showMessage(int message) {
        Toaster.show(rootView, getStringMessage(message));
    }


    @Override
    public void showFetchingError() {
        rv_groups.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        String errorMessage = getStringMessage(R.string.failed_to_fetch_groups)
                + getStringMessage(R.string.new_line) + getStringMessage(R.string.click_to_refresh);
        mNoGroupsText.setText(errorMessage);
    }


    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && mGroupListAdapter.getItemCount() == 0) {
            pb_groups.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            pb_groups.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGroupsListPresenter.detachView();
        //As the Fragment Detach Finish the ActionMode
        if (actionMode != null) actionMode.finish();
    }

    /**
     * Toggle the selection state of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        mGroupListAdapter.toggleSelection(position);
        int count = mGroupListAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    /**
     * This ActionModeCallBack Class handling the User Event after the Selection of Clients. Like
     * Click of Menu Sync Button and finish the ActionMode
     */
    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String LOG_TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_sync, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_sync:

                    selectedGroups.clear();
                    for (Integer position : mGroupListAdapter.getSelectedItems()) {
                        selectedGroups.add(mGroupList.get(position));
                    }

                    Toaster.show(rootView, "Selected Groups : " + selectedGroups.size());
                    //TODO added SyncDialogFragment to syn Groups

                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mGroupListAdapter.clearSelection();
            actionMode = null;
        }
    }
}

