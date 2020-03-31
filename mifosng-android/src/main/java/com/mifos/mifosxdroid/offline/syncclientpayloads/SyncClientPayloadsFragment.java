package com.mifos.mifosxdroid.offline.syncclientpayloads;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
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
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.client.ClientPayload;
import com.mifos.utils.Constants;
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
 * <p/>
 * Created by Rajan Maurya on 08/07/16.
 */
public class SyncClientPayloadsFragment extends MifosBaseFragment
        implements SyncClientPayloadsMvpView, DialogInterface.OnClickListener {

    public final String LOG_TAG = getClass().getSimpleName();

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
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
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
            mNoPayloadText.setText(getActivity()
                    .getResources().getString(R.string.no_client_payload_to_sync));
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            mSyncPayloadsAdapter = new SyncPayloadsAdapter(getActivity(), clientPayload);
            rv_payload_clients.setAdapter(mSyncPayloadsAdapter);
        }
    }

    /**
     * Showing Error when failed to fetch client payload from Database
     *
     * @param s Error String
     */
    @Override
    public void showError(int stringId) {
        ll_error.setVisibility(View.VISIBLE);
        String message =
                stringId + getActivity().getResources().getString(R.string.click_to_refresh);
        mNoPayloadText.setText(message);
        Toaster.show(rootView, getResources().getString(stringId));
    }


    /**
     * Called Whenever any client payload is synced to server.
     * then first delete that client from database and sync again from Database
     * and update the recyclerView
     */
    @Override
    public void showSyncResponse() {
        mSyncPayloadsPresenter.deleteAndUpdateClientPayload(clientPayloads
                .get(mClientSyncIndex).getId(),
                clientPayloads.get(mClientSyncIndex).getClientCreationTime());
    }

    /**
     * Called when client payload synced is failed then there can be some problem with the
     * client payload data example externalId or phone number already exist.
     * If client synced failed the there is no need to delete from the Database and increase
     * the mClientSyncIndex by one and sync the next client payload
     */
    @Override
    public void showClientSyncFailed(String errorMessage) {
        ClientPayload clientPayload = clientPayloads.get(mClientSyncIndex);
        clientPayload.setErrorMessage(errorMessage);
        mSyncPayloadsPresenter.updateClientPayload(clientPayload);

    }

    /**
     * This Method will called whenever user trying to sync the client payload in
     * offline mode.
     */
    @Override
    public void showOfflineModeDialog() {
        new MaterialDialog.Builder().init(getActivity())
                .setTitle(R.string.offline_mode)
                .setMessage(R.string.dialog_message_offline_sync_alert)
                .setPositiveButton(R.string.dialog_action_go_online, this)
                .setNegativeButton(R.string.dialog_action_cancel, this)
                .createMaterialDialog()
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                //TODO Write Negative Button Click Event Logic
                break;
            case DialogInterface.BUTTON_POSITIVE:
                PrefManager.setUserStatus(Constants.USER_ONLINE);
                if (clientPayloads.size() != 0) {
                    mClientSyncIndex = 0;
                    syncClientPayload();
                } else {
                    Toaster.show(rootView,
                            getActivity().getResources().getString(R.string.nothing_to_sync));
                }
                break;
            default:
                break;
        }
    }

    /**
     * This method will update client Payload in List<ClientPayload> after adding Error message in
     * database
     *
     * @param clientPayload
     */
    @Override
    public void showClientPayloadUpdated(ClientPayload clientPayload) {
        clientPayloads.set(mClientSyncIndex, clientPayload);
        mSyncPayloadsAdapter.notifyDataSetChanged();

        mClientSyncIndex = mClientSyncIndex + 1;
        if (clientPayloads.size() != mClientSyncIndex) {
            syncClientPayload();
        }
    }


    /**
     * This is called whenever a client  payload is synced and synced client payload is
     * deleted from the Database and update UI
     *
     * @param clients
     */
    @Override
    public void showPayloadDeletedAndUpdatePayloads(List<ClientPayload> clients) {
        mClientSyncIndex = 0;
        clientPayloads.clear();
        this.clientPayloads = clients;
        mSyncPayloadsAdapter.setClientPayload(clientPayloads);
        if (clientPayloads.size() != 0) {
            syncClientPayload();
        } else {
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText(getActivity()
                    .getResources().getString(R.string.all_clients_synced));
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
                        mClientSyncIndex = 0;
                        syncClientPayload();
                    } else {
                        Toaster.show(rootView,
                                getActivity().getResources().getString(R.string.nothing_to_sync));
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

    public void syncClientPayload() {
        for (int i = 0; i < clientPayloads.size(); ++i) {
            if (clientPayloads.get(i).getErrorMessage() == null) {
                mSyncPayloadsPresenter.syncClientPayload(clientPayloads.get(i));
                mClientSyncIndex = i;
                break;
            } else {
                Log.d(LOG_TAG,
                        getActivity().getResources().getString(R.string.error_fix_before_sync) +
                        clientPayloads.get(i).getErrorMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideMifosProgressBar();
        mSyncPayloadsPresenter.detachView();
    }

}
