package com.mifos.mifosxdroid.offline.syncgrouppayloads;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SyncGroupPayloadAdapter;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.FragmentSyncpayloadBinding;
import com.mifos.objects.group.GroupPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Rajan Maurya on 19/07/16.
 */
public class SyncGroupPayloadsFragment extends MifosBaseFragment implements
        SyncGroupPayloadsMvpView, DialogInterface.OnClickListener {

    public final String LOG_TAG = getClass().getSimpleName();
    private FragmentSyncpayloadBinding binding;

    @Inject
    SyncGroupPayloadsPresenter mSyncGroupPayloadsPresenter;

    @Inject
    SyncGroupPayloadAdapter mSyncGroupPayloadAdapter;

    List<GroupPayload> groupPayloads;

    DialogInterface.OnCancelListener onCancelListener;

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

        binding = FragmentSyncpayloadBinding.inflate(inflater, container, false);
        mSyncGroupPayloadsPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvSyncPayload.setLayoutManager(mLayoutManager);
        binding.rvSyncPayload.setHasFixedSize(true);
        binding.rvSyncPayload.setAdapter(mSyncGroupPayloadAdapter);


        /**
         * Loading All Client Payloads from Database
         */
        binding.swipeContainer.setColorSchemeColors(getActivity()
                        .getResources().getIntArray(R.array.swipeRefreshColors));
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSyncGroupPayloadsPresenter.loanDatabaseGroupPayload();

                if (binding.swipeContainer.isRefreshing())
                    binding.swipeContainer.setRefreshing(false);
            }
        });

        mSyncGroupPayloadsPresenter.loanDatabaseGroupPayload();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.noPayloadIcon.setOnClickListener(view1 -> reloadOnError());
    }

    /**
     * Show when Database response is null or failed to fetch the client payload
     * Onclick Send Fresh Request for Client Payload.
     */
    public void reloadOnError() {
        binding.llError.setVisibility(View.GONE);
        mSyncGroupPayloadsPresenter.loanDatabaseGroupPayload();
    }


    @Override
    public void showGroupSyncResponse() {
        mSyncGroupPayloadsPresenter.deleteAndUpdateGroupPayload(groupPayloads
                .get(mClientSyncIndex).getId());
    }

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
                if (groupPayloads.size() != 0) {
                    mClientSyncIndex = 0;
                    syncGroupPayload();
                } else {
                    Toaster.show(binding.getRoot(),
                            getActivity().getResources().getString(R.string.nothing_to_sync));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showError(int stringId) {
        binding.llError.setVisibility(View.VISIBLE);
        String message = stringId + getResources().getString(R.string.click_to_refresh);
        binding.noPayloadText.setText(message);
        Toaster.show(binding.getRoot(), stringId);
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
            binding.llError.setVisibility(View.VISIBLE);
            binding.noPayloadText.setText(getActivity()
                    .getResources().getString(R.string.no_group_payload_to_sync));
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            mSyncGroupPayloadAdapter.setGroupPayload(groupPayloads);
        }

    }

    @Override
    public void showGroupSyncFailed(String errorMessage) {
        GroupPayload groupPayload = groupPayloads.get(mClientSyncIndex);
        groupPayload.setErrorMessage(errorMessage);
        mSyncGroupPayloadsPresenter.updateGroupPayload(groupPayload);

    }

    @Override
    public void showPayloadDeletedAndUpdatePayloads(List<GroupPayload> groups) {
        mClientSyncIndex = 0;
        this.groupPayloads = groups;
        mSyncGroupPayloadAdapter.setGroupPayload(groupPayloads);
        if (groupPayloads.size() != 0) {
            syncGroupPayload();
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.noPayloadText.setText(getActivity()
                    .getResources().getString(R.string.all_groups_synced));
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        }
    }

    @Override
    public void showGroupPayloadUpdated(GroupPayload groupPayload) {
        groupPayloads.set(mClientSyncIndex, groupPayload);
        mSyncGroupPayloadAdapter.notifyDataSetChanged();

        mClientSyncIndex = mClientSyncIndex + 1;
        if (groupPayloads.size() != mClientSyncIndex) {
            syncGroupPayload();
        }
    }

    @Override
    public void showProgressbar(boolean show) {
        binding.swipeContainer.setRefreshing(show);
        if (show && mSyncGroupPayloadAdapter.getItemCount() == 0) {
            showMifosProgressBar();
            binding.swipeContainer.setRefreshing(false);
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
                        syncGroupPayload();
                    } else {
                        Toaster.show(binding.getRoot(),
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

    public void syncGroupPayload() {
        for (int i = 0; i < groupPayloads.size(); ++i) {
            if (groupPayloads.get(i).getErrorMessage() == null) {
                mSyncGroupPayloadsPresenter.syncGroupPayload(groupPayloads.get(i));
                mClientSyncIndex = i;
                break;
            } else {
                Log.d(LOG_TAG,
                        getActivity().getResources().getString(R.string.error_fix_before_sync) +
                        groupPayloads.get(i).getErrorMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSyncGroupPayloadsPresenter.detachView();
    }

}
