package com.mifos.mifosxdroid.offline.synccenterpayloads;

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
import com.mifos.mifosxdroid.adapters.SyncCenterPayloadAdapter;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.FragmentSyncpayloadBinding;
import com.mifos.services.data.CenterPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class SyncCenterPayloadsFragment extends MifosBaseFragment implements
        SyncCenterPayloadsMvpView, DialogInterface.OnClickListener {

    public final String LOG_TAG = getClass().getSimpleName();
    private FragmentSyncpayloadBinding binding;

    @Inject
    SyncCenterPayloadsPresenter mSyncCenterPayloadsPresenter;

    @Inject
    SyncCenterPayloadAdapter mSyncCenterPayloadAdapter;

    List<CenterPayload> centerPayloads;

    DialogInterface.OnCancelListener onCancelListener;

    int mCenterSyncIndex = 0;

    public static SyncCenterPayloadsFragment newInstance() {
        Bundle arguments = new Bundle();
        SyncCenterPayloadsFragment fragment = new SyncCenterPayloadsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        centerPayloads = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSyncpayloadBinding.inflate(inflater, container, false);
        mSyncCenterPayloadsPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvSyncPayload.setLayoutManager(mLayoutManager);
        binding.rvSyncPayload.setHasFixedSize(true);
        binding.rvSyncPayload.setAdapter(mSyncCenterPayloadAdapter);


        /**
         * Loading All Center Payloads from Database
         */
        binding.swipeContainer.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSyncCenterPayloadsPresenter.loadDatabaseCenterPayload();

                if (binding.swipeContainer.isRefreshing())
                    binding.swipeContainer.setRefreshing(false);
            }
        });

        mSyncCenterPayloadsPresenter.loadDatabaseCenterPayload();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.noPayloadIcon.setOnClickListener(view1 -> reloadOnError());
    }

    /**
     * Show when Database response is null or failed to fetch the center payload
     * Onclick Send Fresh Request for Center Payload.
     */
    public void reloadOnError() {
        binding.llError.setVisibility(View.GONE);
        mSyncCenterPayloadsPresenter.loadDatabaseCenterPayload();
    }

    @Override
    public void showCenterSyncResponse() {
        mSyncCenterPayloadsPresenter.deleteAndUpdateCenterPayload(centerPayloads
                .get(mCenterSyncIndex).getId());
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
                break;
            case DialogInterface.BUTTON_POSITIVE:
                PrefManager.setUserStatus(Constants.USER_ONLINE);
                if (centerPayloads.size() != 0) {
                    mCenterSyncIndex = 0;
                    syncCenterPayload();
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
     * This method is showing the center payload in the recyclerView.
     * If Database Table have no entry then showing make recyclerView visibility gone and
     * visible to the noPayloadIcon and noPayloadText to alert the user there is nothing
     * to show.
     *
     * @param centerPayload
     */
    @Override
    public void showCenters(List<CenterPayload> centerPayload) {
        centerPayloads = centerPayload;
        if (centerPayload.size() == 0) {
            binding.llError.setVisibility(View.VISIBLE);
            binding.noPayloadText.setText(getActivity()
                    .getResources().getString(R.string.no_center_payload_to_sync));
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            mSyncCenterPayloadAdapter.setCenterPayload(centerPayloads);
        }

    }

    @Override
    public void showCenterSyncFailed(String errorMessage) {
        CenterPayload centerPayload = centerPayloads.get(mCenterSyncIndex);
        centerPayload.setErrorMessage(errorMessage);
        mSyncCenterPayloadsPresenter.updateCenterPayload(centerPayload);

    }

    @Override
    public void showPayloadDeletedAndUpdatePayloads(List<CenterPayload> centers) {
        mCenterSyncIndex = 0;
        this.centerPayloads = centers;
        mSyncCenterPayloadAdapter.setCenterPayload(centerPayloads);
        if (centerPayloads.size() != 0) {
            syncCenterPayload();
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.noPayloadText.setText(getActivity()
                    .getResources().getString(R.string.all_centers_synced));
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        }
    }

    @Override
    public void showCenterPayloadUpdated(CenterPayload centerPayload) {
        centerPayloads.set(mCenterSyncIndex, centerPayload);
        mSyncCenterPayloadAdapter.notifyDataSetChanged();

        mCenterSyncIndex = mCenterSyncIndex + 1;
        if (centerPayloads.size() != mCenterSyncIndex) {
            syncCenterPayload();
        }
    }

    @Override
    public void showProgressbar(boolean show) {
        binding.swipeContainer.setRefreshing(show);
        if (show && mSyncCenterPayloadAdapter.getItemCount() == 0) {
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
                    if (centerPayloads.size() != 0) {
                        mCenterSyncIndex = 0;
                        syncCenterPayload();
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


    public void syncCenterPayload() {
        for (int i = 0; i < centerPayloads.size(); ++i) {
            if (centerPayloads.get(i).getErrorMessage() == null) {
                mSyncCenterPayloadsPresenter.syncCenterPayload(centerPayloads.get(i));
                mCenterSyncIndex = i;
                break;
            } else {
                Log.d(LOG_TAG,
                        getActivity().getResources().getString(R.string.error_fix_before_sync) +
                                centerPayloads.get(i).getErrorMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSyncCenterPayloadsPresenter.detachView();
    }
}
