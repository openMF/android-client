package com.mifos.mifosxdroid.offline.offlinedashbarod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.OfflineDashboardAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListner;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadActivity;
import com.mifos.mifosxdroid.offline.syncgrouppayloads.SyncGroupPayloadsActivity;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.group.GroupPayload;
import com.mifos.utils.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rajan Maurya on 20/07/16.
 */
public class OfflineDashboardFragment extends MifosBaseFragment implements
        OfflineDashboardMvpView, RecyclerItemClickListner.OnItemClickListener {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.rv_offline_dashboard)
    RecyclerView rv_offline_dashboard;

    @BindView(R.id.pb_offline_dashboard)
    ProgressBar pb_offline_dashboard;

    @BindView(R.id.noPayloadText)
    TextView mNoPayloadText;

    @BindView(R.id.noPayloadIcon)
    ImageView mNoPayloadIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    View rootView;

    @Inject
    OfflineDashboardPresenter mOfflineDashboardPresenter;

    @Inject
    OfflineDashboardAdapter mOfflineDashboardAdapter;

    // update mPayloadIndex to number of request is going to fetch data in Presenter;
    private int mPayloadIndex = 2;

    private static final int GRID_COUNT = 2;

    private List<Class> mPayloadClasses;


    @Override
    public void onItemClick(View childView, int position) {
        showPayloadActivity(position);
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    public static OfflineDashboardFragment newInstance() {
        OfflineDashboardFragment fragment = new OfflineDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        mPayloadClasses = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_offline_dashboard, container, false);

        setToolbarTitle("Offline Sync");

        ButterKnife.bind(this, rootView);
        mOfflineDashboardPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),GRID_COUNT);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_offline_dashboard.setLayoutManager(mLayoutManager);
        rv_offline_dashboard.setHasFixedSize(true);
        rv_offline_dashboard.addOnItemTouchListener(new RecyclerItemClickListner(getActivity(),
                this));
        rv_offline_dashboard.setItemAnimator(new DefaultItemAnimator());
        rv_offline_dashboard.addItemDecoration(new ItemOffsetDecoration(getActivity(),
                R.dimen.item_offset));
        rv_offline_dashboard.setAdapter(mOfflineDashboardAdapter);

        mOfflineDashboardPresenter.loadDatabaseClientPayload();
        mOfflineDashboardPresenter.loanDatabaseGroupPayload();

        return rootView;
    }

    @Override
    public void showClients(List<ClientPayload> clientPayloads) {
        if (clientPayloads.size() != 0) {
            mOfflineDashboardAdapter.showClientCard("Payload : " +
                    String.valueOf(clientPayloads.size()));
            mPayloadClasses.add(SyncClientPayloadActivity.class);
        } else {
            mPayloadIndex = mPayloadIndex -1;
            showNoPayloadToShow();
        }
    }

    @Override
    public void showGroups(List<GroupPayload> groupPayloads) {
        if (groupPayloads.size() != 0) {
            mOfflineDashboardAdapter.showGroupCard("Payload : " +
                    String.valueOf(groupPayloads.size()));
            mPayloadClasses.add(SyncGroupPayloadsActivity.class);
        } else {
            mPayloadIndex = mPayloadIndex -1;
            showNoPayloadToShow();
        }
    }

    @Override
    public void showNoPayloadToShow() {
        if (mPayloadIndex == 0) {
            rv_offline_dashboard.setVisibility(View.GONE);
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText("Nothing To Sync :)");
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        }
    }

    @Override
    public void showError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            pb_offline_dashboard.setVisibility(View.VISIBLE);
        } else {
            pb_offline_dashboard.setVisibility(View.GONE);
        }
    }

    public void showPayloadActivity(int position) {
        switch (position) {
            case 0 : startPayloadActivity(mPayloadClasses.get(position));
                break;
            case 1 : startPayloadActivity(mPayloadClasses.get(position));
                break;
            default:
                break;
        }
    }

    public <T> void startPayloadActivity(Class<T> t) {
        Intent intent = new Intent(getActivity(), t);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOfflineDashboardPresenter.detachView();
    }

}
