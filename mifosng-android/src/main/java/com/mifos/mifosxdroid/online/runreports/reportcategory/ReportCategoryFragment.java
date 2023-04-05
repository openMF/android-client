package com.mifos.mifosxdroid.online.runreports.reportcategory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientReportAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.FragmentRunreportBinding;
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailFragment;
import com.mifos.objects.runreports.client.ClientReportTypeItem;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;


/**
 * Created by Tarun on 02-08-17.
 */

public class ReportCategoryFragment extends MifosBaseFragment implements ReportCategoryMvpView {

    private FragmentRunreportBinding binding;

    @Inject
    ReportCategoryPresenter presenter;

    ClientReportAdapter reportAdapter;

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
        binding = FragmentRunreportBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        presenter.attachView(this);
        presenter.fetchCategories(reportCategory, false, true);

        return binding.getRoot();
    }

    @Override
    public void showError(String error) {
        Toaster.show(binding.getRoot(), error);
    }

    @Override
    public void showReportCategories(List<ClientReportTypeItem> reportTypes) {
        reportTypeItems = reportTypes;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerReport.getContext(), layoutManager.getOrientation());
        binding.recyclerReport.setLayoutManager(layoutManager);
        binding.recyclerReport.addItemDecoration(dividerItemDecoration);
        reportAdapter = new ClientReportAdapter(position -> {
                openDetailFragment(position);
                return null;
            }
        );
        binding.recyclerReport.setAdapter(reportAdapter);

        reportAdapter.setReportItems(reportTypes);
        reportAdapter.notifyDataSetChanged();
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
