package com.mifos.mifosxdroid.offline.syncgrouppayloads;

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
import com.mifos.mifosxdroid.adapters.SyncGroupPayloadAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.group.GroupPayload;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 19/07/16.
 */
public class SyncGroupPayloadsFragment extends MifosBaseFragment implements
        SyncGroupPayloadsMvpView {

    @BindView(R.id.rv_sync_payload)
    RecyclerView rv_payload_group;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noPayloadText)
    TextView mNoPayloadText;

    @BindView(R.id.noPayloadIcon)
    ImageView mNoPayloadIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    SyncGroupPayloadsPresenter mSyncGroupPayloadsPresenter;

    @Inject
    SyncGroupPayloadAdapter mSyncGroupPayloadAdapter;

    View rootView;

    List<GroupPayload> groupPayloads;

    int mClientSyncIndex = 0;

    public static SyncGroupPayloadsFragment newInstance() {
        Bundle arguments = new Bundle();
        SyncGroupPayloadsFragment fragment = new SyncGroupPayloadsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        groupPayloads = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_syncpayload, container, false);

        ButterKnife.bind(this, rootView);
        mSyncGroupPayloadsPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_payload_group.setLayoutManager(mLayoutManager);
        rv_payload_group.setHasFixedSize(true);
        rv_payload_group.setAdapter(mSyncGroupPayloadAdapter);


        /**
         * Loading All Client Payloads from Database
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R
                .color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSyncGroupPayloadsPresenter.loanDatabaseGroupPayload();

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        mSyncGroupPayloadsPresenter.loanDatabaseGroupPayload();

        return rootView;
    }

    /**
     * Show when Database response is null or failed to fetch the client payload
     * Onclick Send Fresh Request for Client Payload.
     */
    @OnClick(R.id.noPayloadIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mSyncGroupPayloadsPresenter.loanDatabaseGroupPayload();
    }


    @Override
    public void showGroupSyncResponse() {
        mSyncGroupPayloadsPresenter.deleteAndUpdateGroupPayload(groupPayloads
                .get(mClientSyncIndex).getId());
    }

    @Override
    public void showOfflineModeDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.MaterialAlertDialogStyle);
        builder.setTitle("Offline Mode");
        builder.setMessage("You are in offline mode, Please go to Navigation drawer " +
                "and switch to online mode. \n\n If you are trying to sync Groups" +
                " Please make sure your internet connection is working well");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @Override
    public void showError(String s) {
        ll_error.setVisibility(View.VISIBLE);
        mNoPayloadText.setText(s + "\n Click to Refresh ");
        Toaster.show(rootView, s);
    }

    /**
     * This method is showing the group payload in the recyclerView.
     * If Database Table have no entry then showing make recyclerView visibility gone and
     * visible to the noPayloadIcon and noPayloadText to alert the user there is nothing
     * to show.
     *
     * @param groupPayload
     */
    @Override
    public void showGroups(List<GroupPayload> groupPayload) {
        groupPayloads = groupPayload;
        if (groupPayload.size() == 0) {
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText("There is No Group Payload to Sync");
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            mSyncGroupPayloadAdapter.setGroupPayload(groupPayloads);
        }

    }

    @Override
    public void showGroupSyncFailed(String error) {
        GroupPayload groupPayload = groupPayloads.get(mClientSyncIndex);
        groupPayload.setErrorMessage(error);
        mSyncGroupPayloadsPresenter.updateGroupPayload(groupPayload);

    }

    @Override
    public void showPayloadDeletedAndUpdatePayloads(List<GroupPayload>  groups) {
        mClientSyncIndex = 0;
        groupPayloads.clear();
        this.groupPayloads = groups;
        mSyncGroupPayloadAdapter.setGroupPayload(groupPayloads);
        if (groupPayloads.size() != 0) {
            mSyncGroupPayloadsPresenter.syncGroupPayload(groupPayloads.get(mClientSyncIndex));
        } else {
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText("All Groups have been Sync");
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        }
    }

    @Override
    public void showGroupPayloadUpdated(GroupPayload groupPayload) {
        groupPayloads.set(mClientSyncIndex,groupPayload);
        mSyncGroupPayloadAdapter.notifyDataSetChanged();

        mClientSyncIndex = mClientSyncIndex + 1;
        if (groupPayloads.size() != mClientSyncIndex) {
            mSyncGroupPayloadsPresenter.syncGroupPayload(groupPayloads.get(mClientSyncIndex));
        }
    }

    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && mSyncGroupPayloadAdapter.getItemCount() == 0) {
            showMifosProgressBar();
            swipeRefreshLayout.setRefreshing(false);
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
                    if (groupPayloads.size() != 0) {
                        mClientSyncIndex = 0;
                        mSyncGroupPayloadsPresenter.syncGroupPayload(groupPayloads
                                .get(mClientSyncIndex));
                    } else {
                        Toaster.show(rootView, "Nothing to Sync");
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
        mSyncGroupPayloadsPresenter.detachView();
    }
}
