package com.mifos.mifosxdroid.offline.syncgrouppayloads;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadsFragment;
import com.mifos.objects.group.GroupPayload;

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

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSyncGroupPayloadsPresenter.loanDatabaseGroupPayload();
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
    public void showGroupSyncResponse(List<GroupPayload> groupPayloads) {
        mSyncGroupPayloadAdapter.setGroupPayload(groupPayloads);
    }

    @Override
    public void showGroupSyncFailed(String s) {
        Toaster.show(rootView, s);
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
    public void onDestroyView() {
        super.onDestroyView();
        mSyncGroupPayloadsPresenter.detachView();
    }
}
