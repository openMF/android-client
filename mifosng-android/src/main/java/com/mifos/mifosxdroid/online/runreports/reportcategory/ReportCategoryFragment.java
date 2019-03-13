package com.mifos.mifosxdroid.online.runreports.reportcategory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientReportAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailFragment;
import com.mifos.objects.runreports.client.ClientReportTypeItem;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 02-08-17.
 */

public class ReportCategoryFragment extends MifosBaseFragment
        implements ReportCategoryMvpView,
        RecyclerItemClickListener.OnItemClickListener {

    @BindView(R.id.recycler_report)
    RecyclerView rvReports;

    @Inject
    ReportCategoryPresenter presenter;

    @Inject
    ClientReportAdapter reportAdapter;

    private View rootView;
    private List<ClientReportTypeItem> reportTypeItems;
    private String reportCategory;
    BroadcastReceiver broadCastNewMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reportCategory = intent.getStringExtra(Constants.REPORT_CATEGORY);
            presenter.fetchCategories(reportCategory, false, true);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.broadCastNewMessage,
                new IntentFilter(Constants.ACTION_REPORT));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.broadCastNewMessage);
    }

    public ReportCategoryFragment() {
    }

    public static ReportCategoryFragment newInstance() {
        ReportCategoryFragment fragment = new ReportCategoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_runreport, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        presenter.attachView(this);
        presenter.fetchCategories(reportCategory, false, true);

        return rootView;
    }

    @Override
    public void showError(String error) {
        Toaster.show(rootView, error);
    }

    @Override
    public void showReportCategories(List<ClientReportTypeItem> reportTypes) {
        reportTypeItems = reportTypes;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                rvReports.getContext(), layoutManager.getOrientation());
        rvReports.setLayoutManager(layoutManager);
        rvReports.addItemDecoration(dividerItemDecoration);
        rvReports.setAdapter(reportAdapter);
        rvReports.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));

        reportAdapter.setReportItems(reportTypes);
        reportAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View childView, int position) {
        openDetailFragment(position);
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    private void openDetailFragment(int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CLIENT_REPORT_ITEM, reportTypeItems.get(pos));
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("ClientCategory");
        fragmentTransaction.replace(R.id.container,
                ReportDetailFragment.newInstance(bundle)).commit();
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }
}
