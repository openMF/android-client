package com.mifos.mifosxdroid.offline.synccenterpayloads;

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
import com.mifos.mifosxdroid.adapters.SyncCenterPayloadAdapter;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.services.data.CenterPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncCenterPayloadsFragment extends MifosBaseFragment implements
        SyncCenterPayloadsMvpView, DialogInterface.OnClickListener {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.rv_sync_payload)
    RecyclerView rvPayloadCenter;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noPayloadText)
    TextView mNoPayloadText;

    @BindView(R.id.noPayloadIcon)
    ImageView mNoPayloadIcon;

    @BindView(R.id.ll_error)
    LinearLayout llError;

    @Inject
    SyncCenterPayloadsPresenter mSyncCenterPayloadsPresenter;

    @Inject
    SyncCenterPayloadAdapter mSyncCenterPayloadAdapter;

    View rootView;

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
        rootView = inflater.inflate(R.layout.fragment_syncpayload, container, false);

        ButterKnife.bind(this, rootView);
        mSyncCenterPayloadsPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPayloadCenter.setLayoutManager(mLayoutManager);
        rvPayloadCenter.setHasFixedSize(true);
        rvPayloadCenter.setAdapter(mSyncCenterPayloadAdapter);


        /**
         * Loading All Center Payloads from Database
         */
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mSyncCenterPayloadsPresenter.loadDatabaseCenterPayload();

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

        mSyncCenterPayloadsPresenter.loadDatabaseCenterPayload();

        return rootView;
    }

    /**
     * Show when Database response is null or failed to fetch the center payload
     * Onclick Send Fresh Request for Center Payload.
     */
    @OnClick(R.id.noPayloadIcon)
    public void reloadOnError() {
        llError.setVisibility(View.GONE);
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
                    Toaster.show(rootView,
                            getActivity().getResources().getString(R.string.nothing_to_sync));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showError(int stringId) {
        llError.setVisibility(View.VISIBLE);
        String message = stringId + getResources().getString(R.string.click_to_refresh);
        mNoPayloadText.setText(message);
        Toaster.show(rootView, stringId);
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
            llError.setVisibility(View.VISIBLE);
            mNoPayloadText.setText(getActivity()
                    .getResources().getString(R.string.no_center_payload_to_sync));
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
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
            llError.setVisibility(View.VISIBLE);
            mNoPayloadText.setText(getActivity()
                    .getResources().getString(R.string.all_centers_synced));
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
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
        swipeRefreshLayout.setRefreshing(show);
        if (show && mSyncCenterPayloadAdapter.getItemCount() == 0) {
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
                    if (centerPayloads.size() != 0) {
                        mCenterSyncIndex = 0;
                        syncCenterPayload();
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
