package com.mifos.mifosxdroid.offline.syncclientpayloads;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SyncPayloadsAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.ClientPayload;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This Class for Syncing the clients that is created in offline mode.
 * For syncing the clients user make sure that he/she is in the online mode.
 *
 * Created by Rajan Maurya on 08/07/16.
 */
public class SyncClientPayloadsFragment extends MifosBaseFragment
        implements SyncClientPayloadsMvpView {

    @BindView(R.id.rv_sync_payload)
    RecyclerView rv_payload_clients;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noPayloadText)
    TextView mNoPayloadText;

    @BindView(R.id.noPayloadIcon)
    ImageView mNoPayloadIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    SyncClientPayloadsPresenter mSyncPayloadsPresenter;

    View rootView;

    List<ClientPayload> clientPayloads;

    SyncPayloadsAdapter mSyncPayloadsAdapter;

    int mClientSyncIndex = 0;

    public static SyncClientPayloadsFragment newInstance() {
        Bundle arguments = new Bundle();
        SyncClientPayloadsFragment fragment = new SyncClientPayloadsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        clientPayloads = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_syncpayload, container, false);

        ButterKnife.bind(this, rootView);
        mSyncPayloadsPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_payload_clients.setLayoutManager(mLayoutManager);
        rv_payload_clients.setHasFixedSize(true);


        mSyncPayloadsPresenter.loadDatabaseClientPayload();

        /**
         * Loading All Client Payloads from Database
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R
                .color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSyncPayloadsPresenter.loadDatabaseClientPayload();

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    /**
     * Show when Database response is null or failed to fetch the client payload
     * Onclick Send Fresh Request for Client Payload.
     */
    @OnClick(R.id.noPayloadIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mSyncPayloadsPresenter.loadDatabaseClientPayload();
    }


    /**
     * This method is showing the client payload in the recyclerView.
     * If Database Table have no entry then showing make recyclerView visibility gone and
     * visible to the noPayloadIcon and noPayloadText to alert the user there is nothing
     * to show.
     *
     * @param clientPayload
     */
    @Override
    public void showPayloads(List<ClientPayload> clientPayload) {
        clientPayloads = clientPayload;
        if (clientPayload.size() == 0) {
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText("There is No Client Payload to Sync");
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            mSyncPayloadsAdapter = new SyncPayloadsAdapter(getActivity(), clientPayload);
            rv_payload_clients.setAdapter(mSyncPayloadsAdapter);
        }
    }

    /**
     * Showing Error when failed to fetch client payload from Database
     * @param s Error String
     */
    @Override
    public void showError(String s) {
        ll_error.setVisibility(View.VISIBLE);
        mNoPayloadText.setText(s + "\n Click to Refresh ");
        mSyncPayloadsPresenter.loadDatabaseClientPayload();
        Toaster.show(rootView, s);
    }


    /**
     * Called Whenever any client payload is synced to server.
     * then first delete that client from database and sync again from Database
     * and update the recyclerView
     */
    @Override
    public void showSyncResponse() {
        mSyncPayloadsPresenter.deleteAndUpdateClientPayload(clientPayloads
                .get(mClientSyncIndex).getId());
    }

    /**
     * Called when client payload synced is failed then there can be some problem with the
     * client payload data example externalId or phone number already exist.
     * If client synced failed the there is no need to delete from the Database and increase
     * the mClientSyncIndex by one and sync the next client payload
     */
    @Override
    public void showClientSyncFailed() {
        mClientSyncIndex = mClientSyncIndex + 1;
        mSyncPayloadsPresenter.syncClientPayload(clientPayloads.get(mClientSyncIndex));
    }

    /**
     * This Method will called whenever user trying to sync the client payload in
     * offline mode.
     */
    @Override
    public void showOfflineModeDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);
        builder.setTitle("Offline Mode");
        builder.setMessage("You are in offline mode, Please go to Navigation drawer " +
                "and switch to online mode. \n\n If you are trying to sync clients" +
                " Please make sure your internet connection is working well");
        builder.setPositiveButton("OK", null);
        builder.show();
    }



    /**
     * This is called whenever a client  payload is synced and synced client payload is
     * deleted from the Database and update UI
     * @param clients
     */
    @Override
    public void showPayloadDeletedAndUpdatePayloads(List<ClientPayload> clients) {
        mClientSyncIndex = 0;
        clientPayloads.clear();
        this.clientPayloads = clients;
        mSyncPayloadsAdapter.notifyDataSetChanged();
        if (clientPayloads.size() != 0) {
            mSyncPayloadsPresenter.syncClientPayload(clientPayloads.get(mClientSyncIndex));
        } else {
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText("All Clients have been Sync");
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        }
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressBar();
        } else {
            hideMifosProgressBar();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            switch (PrefManager.getUserStatus()) {
                case 0:
                    if (clientPayloads.size() != 0) {
                        mSyncPayloadsPresenter.syncClientPayload(clientPayloads
                                .get(mClientSyncIndex));
                    }
                    break;
                case 1:
                    showOfflineModeDialog();
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideMifosProgressBar();
        mSyncPayloadsPresenter.detachView();
    }
}
