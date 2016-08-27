/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener.OnItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.syncclientsdialog.SyncClientsDialogFragment;
import com.mifos.mifosxdroid.online.ClientActivity;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by ishankhanna on 09/02/14.
 * <p>
 * ClientListFragment Fetching Showing ClientList in RecyclerView from
 * </>demo.openmf.org/fineract-provider/api/v1/clients?paged=true&offset=offset_value&limit
 * =limit_value</>
 */
public class ClientListFragment extends MifosBaseFragment
        implements OnItemClickListener, ClientListMvpView, SwipeRefreshLayout.OnRefreshListener {

    public static final String LOG_TAG = ClientListFragment.class.getSimpleName();

    @BindView(R.id.rv_clients)
    RecyclerView rv_clients;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noClientText)
    TextView mNoClientText;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @BindView(R.id.noClientIcon)
    ImageView mNoClientIcon;

    @Inject
    ClientNameListAdapter mClientNameListAdapter;

    @Inject
    ClientListPresenter mClientListPresenter;

    private View rootView;
    private List<Client> clientList;
    private List<Client> selectedClients;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private Boolean isParentFragmentAGroupFragment = false;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onItemClick(View childView, int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            Intent clientActivityIntent = new Intent(getActivity(), ClientActivity.class);
            clientActivityIntent.putExtra(Constants.CLIENT_ID, clientList.get(position).getId());
            startActivity(clientActivityIntent);
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

    public static ClientListFragment newInstance() {
        Bundle arguments = new Bundle();
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setArguments(arguments);
        return clientListFragment;
    }

    public static ClientListFragment newInstance(List<Client> clientList,
                                                 boolean isParentFragmentAGroupFragment) {
        ClientListFragment clientListFragment = new ClientListFragment();
        Bundle args = new Bundle();
        if (isParentFragmentAGroupFragment) {
            if (clientList != null) {
                args.putParcelableArrayList(Constants.CLIENTS,
                        (ArrayList<? extends Parcelable>) clientList);
                args.putBoolean(Constants.IS_PARENT_FRAGMENT_A_GROUP_FRAGMENT, true);
                clientListFragment.setArguments(args);
            }
        }
        return clientListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        clientList = new ArrayList<>();
        selectedClients = new ArrayList<>();
        actionModeCallback = new ActionModeCallback();
        if (getArguments() != null) {
            clientList = getArguments().getParcelableArrayList(Constants.CLIENTS);
            isParentFragmentAGroupFragment = getArguments()
                    .getBoolean(Constants.IS_PARENT_FRAGMENT_A_GROUP_FRAGMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);
        setHasOptionsMenu(true);
        setToolbarTitle(getResources().getString(R.string.clients));

        ButterKnife.bind(this, rootView);
        mClientListPresenter.attachView(this);

        showUserInterface();

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mCenterList.size()) and limit(100).
         */
        rv_clients.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                mClientListPresenter.loadClients(true, clientList.size());
            }
        });

        if (isParentFragmentAGroupFragment) {
            mClientListPresenter.showGroupClients(clientList);
        } else {
            mClientListPresenter.loadClients(false, 0);
        }
        mClientListPresenter.loadDatabaseClients();

        return rootView;
    }

    @Override
    public void showUserInterface() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mClientNameListAdapter.setContext(getActivity());
        rv_clients.setLayoutManager(mLayoutManager);
        rv_clients.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_clients.setHasFixedSize(true);
        rv_clients.setAdapter(mClientNameListAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mClientListPresenter.loadClients(false, 0);
        if (actionMode != null) actionMode.finish();
    }


    @Override
    public void unregisterSwipeAndScrollListener() {
        rv_clients.clearOnScrollListeners();
        swipeRefreshLayout.setEnabled(false);
        mNoClientIcon.setEnabled(false);
    }

    @Override
    public void showMessage(int message) {
        Toaster.show(rootView, getStringMessage(message));
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Client list.
     */
    @OnClick(R.id.noClientIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mClientListPresenter.loadClients(false, 0);
        mClientListPresenter.loadDatabaseClients();
    }

    /**
     * Setting Data in RecyclerView of the ClientListFragment if the mApiRestCounter value is 1,
     * otherwise adding value in ArrayList and updating the ClientListAdapter.
     * If the Response is have null then show Toast to User There is No Center Available.
     */
    @Override
    public void showClientList(List<Client> clients) {
        clientList = clients;
        mClientNameListAdapter.setClients(clients);
        mClientNameListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadMoreClients(List<Client> clients) {
        clientList.addAll(clients);
        mClientNameListAdapter.addClients(clients);
        mClientNameListAdapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "" + clientList.size());
    }

    @Override
    public void showGroupClients(List<Client> clients) {
        mClientNameListAdapter.setClients(clients);
        mClientNameListAdapter.notifyDataSetChanged();
    }

    @Override
    public String getStringMessage(int message) {
        return getResources().getString(message);
    }

    @Override
    public void showEmptyClientList(int message) {
        rv_clients.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        mNoClientText.setText(getStringMessage(message));
    }

    /**
     * Check the mApiRestCounter value is the value is 1,
     * So there no data to show and setVisibility VISIBLE
     * of Error ImageView and TextView layout and otherwise simple
     * show the Toast Message of Error Message.
     */
    @Override
    public void showError() {
        rv_clients.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        String errorMessage = getResources().getString(R.string.failed_to_load_client)
                + getResources().getString(R.string.new_line) +
                getResources().getString(R.string.click_to_refresh);
        mNoClientText.setText(errorMessage);
    }


    /**
     * Check mApiRestCounter value, if the value is 1 then
     * show MifosBaseActivity ProgressBar and if it is greater than 1,
     * It means this Request is the second and so on than show SwipeRefreshLayout
     * Check the the b is true or false
     */
    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && mClientNameListAdapter.getItemCount() == 0) {
            showMifosProgressBar();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            hideMifosProgressBar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideMifosProgressBar();
        mClientListPresenter.detachView();
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
        mClientNameListAdapter.toggleSelection(position);
        int count = mClientNameListAdapter.getSelectedItemCount();

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

                    selectedClients.clear();
                    for (Integer position : mClientNameListAdapter.getSelectedItems()) {
                        selectedClients.add(clientList.get(position));
                    }

                    SyncClientsDialogFragment syncClientsDialogFragment =
                            SyncClientsDialogFragment.newInstance(selectedClients);
                    FragmentTransaction fragmentTransaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_SYNC);
                    syncClientsDialogFragment.setCancelable(false);
                    syncClientsDialogFragment.show(fragmentTransaction,
                            getResources().getString(R.string.sync_clients));
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mClientNameListAdapter.clearSelection();
            actionMode = null;
        }
    }
}

