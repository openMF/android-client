package com.mifos.mifosxdroid.offline.offlinedashbarod;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.OfflineDashboardAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListner;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.utils.ItemOffsetDecoration;

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

    View rootView;

    @Inject
    OfflineDashboardPresenter mOfflineDashboardPresenter;

    @Inject
    OfflineDashboardAdapter mOfflineDashboardAdapter;

    @Override
    public void onItemClick(View childView, int position) {
        Toaster.show(rootView, "Click");
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_offline_dashboard, container, false);

        ButterKnife.bind(this, rootView);
        mOfflineDashboardPresenter.attachView(this);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_offline_dashboard.setLayoutManager(mLayoutManager);
        rv_offline_dashboard.setHasFixedSize(true);
        rv_offline_dashboard.addOnItemTouchListener(new RecyclerItemClickListner(getActivity(),
                this));
        rv_offline_dashboard.setItemAnimator(new DefaultItemAnimator());
        rv_offline_dashboard.addItemDecoration(new ItemOffsetDecoration(getActivity(),
                R.dimen.item_offset));
        rv_offline_dashboard.setAdapter(mOfflineDashboardAdapter);
        pb_offline_dashboard.setVisibility(View.GONE);
        rv_offline_dashboard.setVisibility(View.VISIBLE);

        Log.d(LOG_TAG, mOfflineDashboardAdapter.getItemCount() + "");




        return rootView;
    }

    @Override
    public void showClients() {

    }

    @Override
    public void showGroups() {

    }

    @Override
    public void showError() {

    }

    @Override
    public void showProgressbar(boolean b) {

    }

}
