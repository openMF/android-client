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
 * This class loading client, Here is two way to load the clients. First one to load clients
 * from Rest API
 * <p>
 * </>demo.openmf.org/fineract-provider/api/v1/clients?paged=true&offset=offset_value&limit
 * =limit_value</>
 * <p>
 * Offset : From Where index, client will be fetch.
 * limit : Total number of client, need to fetch
 * <p>
 * and showing in the ClientList.
 * <p>
 * and Second one is showing Group Clients. Here Group load the ClientList and send the
 * Client to ClientListFragment newInstance(List<Client> clientList,
 * boolean isParentFragment) {...}
 * and unregister the ScrollListener and SwipeLayout.
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
    private Boolean isParentFragment = false;
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

    /**
     * This method will be called, whenever ClientListFragment will not have Parent Fragment.
     * So, Presenter make the call to Rest API and fetch the Client List and show in UI
     *
     * @return ClientListFragment
     */
    public static ClientListFragment newInstance() {
        Bundle arguments = new Bundle();
        ClientListFragment clientListFragment = new ClientListFragment();
        clientListFragment.setArguments(arguments);
        return clientListFragment;
    }

    /**
     * This Method will be called, whenever Parent (Fragment or Activity) will be true and Presenter
     * do not need to make Rest API call to server. Parent (Fragment or Activity) already fetched
     * the clients and for showing, they call ClientListFragment.
     * <p>
     * Example : Showing Group Clients.
     *
     * @param clientList       List<Client>
     * @param isParentFragment true
     * @return ClientListFragment
     */
    public static ClientListFragment newInstance(List<Client> clientList,
                                                 boolean isParentFragment) {
        ClientListFragment clientListFragment = new ClientListFragment();
        Bundle args = new Bundle();
        if (isParentFragment && clientList != null) {
            args.putParcelableArrayList(Constants.CLIENTS,
                    (ArrayList<? extends Parcelable>) clientList);
            args.putBoolean(Constants.IS_A_PARENT_FRAGMENT, true);
            clientListFragment.setArguments(args);
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
            isParentFragment = getArguments()
                    .getBoolean(Constants.IS_A_PARENT_FRAGMENT);
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

        //setting all the UI content to the view
        showUserInterface();

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         */
        rv_clients.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                mClientListPresenter.loadClients(true, clientList.size());
            }
        });

        /**
         * First Check the Parent Fragment is true or false. If parent fragment is true then no
         * need to fetch clientList from Rest API, just need to showing parent fragment ClientList
         * and is Parent Fragment is false then Presenter make the call to Rest API and fetch the
         * Client Lis to show. and Presenter make transaction to Database to load saved clients.
         */
        if (isParentFragment) {
            mClientListPresenter.showParentClients(clientList);
        } else {
            mClientListPresenter.loadClients(false, 0);
        }
        mClientListPresenter.loadDatabaseClients();

        return rootView;
    }


    /**
     * This method initializes the all Views.
     */
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

    /**
     * This method will be called when user will swipe down to Refresh the ClientList then
     * Presenter make the Fresh call to Rest API to load ClientList from offset = 0 and fetch the
     * first 100 clients and update the client list.
     */
    @Override
    public void onRefresh() {
        mClientListPresenter.loadClients(false, 0);
        mClientListPresenter.loadDatabaseClients();
        if (actionMode != null) actionMode.finish();
    }

    /**
     * This Method unregister the RecyclerView OnScrollListener and SwipeRefreshLayout
     * and NoClientIcon click event.
     */
    @Override
    public void unregisterSwipeAndScrollListener() {
        rv_clients.clearOnScrollListeners();
        swipeRefreshLayout.setEnabled(false);
        mNoClientIcon.setEnabled(false);
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

    /**
     * Onclick Send Fresh Request for Client list.
     */
    @OnClick(R.id.noClientIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        rv_clients.setVisibility(View.VISIBLE);
        mClientListPresenter.loadClients(false, 0);
        mClientListPresenter.loadDatabaseClients();
    }

    /**
     * Setting ClientList to the Adapter and updating the Adapter.
     */
    @Override
    public void showClientList(List<Client> clients) {
        clientList = clients;
        mClientNameListAdapter.setClients(clients);
        mClientNameListAdapter.notifyDataSetChanged();
    }

    /**
     * Updating Adapter Attached ClientList
     *
     * @param clients List<Client></>
     */
    @Override
    public void showLoadMoreClients(List<Client> clients) {
        clientList.addAll(clients);
        mClientNameListAdapter.notifyDataSetChanged();
    }

    /**
     * Showing Fetched ClientList size is 0 and show there is no client to show.
     *
     * @param message String Message to show user.
     */
    @Override
    public void showEmptyClientList(int message) {
        rv_clients.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        mNoClientText.setText(getStringMessage(message));
    }

    /**
     * This Method Will be called. When Presenter failed to First page of ClientList from Rest API.
     * Then user look the Message that failed to fetch clientList.
     */
    @Override
    public void showError() {
        rv_clients.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        String errorMessage = getStringMessage(R.string.failed_to_load_client)
                + getStringMessage(R.string.new_line) + getStringMessage(R.string.click_to_refresh);
        mNoClientText.setText(errorMessage);
    }


    /**
     * show MifosBaseActivity ProgressBar, if mClientNameListAdapter.getItemCount() == 0
     * otherwise show SwipeRefreshLayout.
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

