/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.mifos.objects.client.Page;
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
 * <p/>
 * ClientListFragment Fetching Showing ClientList in RecyclerView from
 * </>demo.openmf.org/fineract-provider/api/v1/clients?paged=true&offset=offset_value&limit
 * =limit_value</>
 */
public class ClientListFragment extends MifosBaseFragment
        implements OnItemClickListener, ClientListMvpView {

    @BindView(R.id.rv_clients)
    RecyclerView rv_clients;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noClientText)
    TextView mNoClientText;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    ClientNameListAdapter clientNameListAdapter;

    @Inject
    ClientListPresenter mClientListPresenter;

    private View rootView;
    private List<Client> clientList;
    private List<Client> selectedClients;
    private int limit = 100;
    private int mApiRestCounter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

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

    public static ClientListFragment newInstance(List<Client> clientList) {
        ClientListFragment clientListFragment = new ClientListFragment();
        if (clientList != null) {
            clientListFragment.setClientList(clientList);
        }
        return clientListFragment;
    }

    public static ClientListFragment newInstance(List<Client> clientList, boolean
            isParentFragmentAGroupFragment) {
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setClientList(clientList);
        return clientListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        clientList = new ArrayList<>();
        selectedClients = new ArrayList<>();
        actionModeCallback = new ActionModeCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client, container, false);
        setHasOptionsMenu(true);
        setToolbarTitle(getResources().getString(R.string.clients));

        ButterKnife.bind(this, rootView);
        mClientListPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_clients.setLayoutManager(mLayoutManager);
        rv_clients.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_clients.setHasFixedSize(true);

        mApiRestCounter = 1;
        mClientListPresenter.loadClients(true, 0, limit);
        mClientListPresenter.loadDatabaseClients();

        /**
         * Setting mApiRestCounter to 1 and send Fresh Request to Server
         */
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mApiRestCounter = 1;

                mClientListPresenter.loadClients(true, 0, limit);
                mClientListPresenter.loadDatabaseClients();

                if (actionMode != null) actionMode.finish();

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mCenterList.size()) and limit(100).
         */
        rv_clients.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                mApiRestCounter = mApiRestCounter + 1;
                mClientListPresenter.loadClients(true, clientList.size(), limit);
            }
        });

        return rootView;
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Client list.
     */
    @OnClick(R.id.noClientIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mClientListPresenter.loadClients(true, 0, limit);
        mClientListPresenter.loadDatabaseClients();
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }


    /**
     * Setting Data in RecyclerView of the ClientListFragment if the mApiRestCounter value is 1,
     * otherwise adding value in ArrayList and updating the ClientListAdapter.
     * If the Response is have null then show Toast to User There is No Center Available.
     *
     * @param clientPage is the List<Client> and
     *                   TotalValue of center API Response by Server
     */
    @Override
    public void showClientList(Page<Client> clientPage) {
        /**
         * if mApiRestCounter is 1, So this is the first Api Request.
         * else if mApiRestCounter is greater than 1, SO this is for loadmore request.
         */
        if (mApiRestCounter == 1) {
            clientList = clientPage.getPageItems();
            clientNameListAdapter = new ClientNameListAdapter(getActivity(), clientList);
            rv_clients.setAdapter(clientNameListAdapter);

            ll_error.setVisibility(View.GONE);
        } else {

            clientList.addAll(clientPage.getPageItems());
            clientNameListAdapter.notifyDataSetChanged();

            //checking the response size if size is zero then show toast No More
            // Clients Available for fetch
            if (clientPage.getPageItems().size() == 0 &&
                    (clientPage.getTotalFilteredRecords() == clientList.size()))
                Toaster.show(rootView,
                        getResources().getString(R.string.no_more_clients_available));
        }
    }

    /**
     * Check the mApiRestCounter value is the value is 1,
     * So there no data to show and setVisibility VISIBLE
     * of Error ImageView and TextView layout and otherwise simple
     * show the Toast Message of Error Message.
     */
    @Override
    public void showErrorFetchingClients() {
        if (mApiRestCounter == 1) {
            ll_error.setVisibility(View.VISIBLE);
            String errorMessage = getResources().getString(R.string.failed_to_load_client)
                    + getResources().getString(R.string.new_line) +
                    getResources().getString(R.string.click_to_refresh);
            mNoClientText.setText(errorMessage);
        }

        Toaster.show(rootView, getResources().getString(R.string.failed_to_load_client) );
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
        mClientListPresenter.detachView();
        //As the Fragment Detach Finish the ActionMode
        if (actionMode != null) actionMode.finish();
    }

    /**
     * Toggle the selection state of an item.
     * <p/>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        clientNameListAdapter.toggleSelection(position);
        int count = clientNameListAdapter.getSelectedItemCount();

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
                    for (Integer position : clientNameListAdapter.getSelectedItems()) {
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
            clientNameListAdapter.clearSelection();
            actionMode = null;
        }
    }
}

