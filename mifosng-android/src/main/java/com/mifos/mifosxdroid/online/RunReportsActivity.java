package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.runreports.report.ReportFragment;
import com.mifos.mifosxdroid.online.runreports.reportcategory.ReportCategoryFragment;
import com.mifos.mifosxdroid.online.runreports.reportdetail.ReportDetailFragment;
import com.mifos.utils.Constants;

/**
 * Created by Tarun on 02-08-17.
 */

public class RunReportsActivity extends MifosBaseActivity
        implements Spinner.OnItemSelectedListener {

    private Intent intent;
    private Spinner spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = new Spinner(getSupportActionBar().getThemedContext());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_runreport, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        toolbar.addView(spinner);
        intent = new Intent(Constants.ACTION_REPORT);
        ReportCategoryFragment fragment = new ReportCategoryFragment();
        replaceFragment(fragment, false, R.id.container);
        addOnBackStackChangedListener();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0: //Clients
                intent.putExtra(Constants.REPORT_CATEGORY, Constants.CLIENT);
                sendBroadcast(intent);
                break;

            case 1:
                intent.putExtra(Constants.REPORT_CATEGORY, Constants.LOAN);
                sendBroadcast(intent);
                break;

            case 2:
                intent.putExtra(Constants.REPORT_CATEGORY, Constants.SAVINGS);
                sendBroadcast(intent);
                break;

            case 3:
                intent.putExtra(Constants.REPORT_CATEGORY, Constants.FUND);
                sendBroadcast(intent);
                break;

            case 4:
                intent.putExtra(Constants.REPORT_CATEGORY, Constants.ACCOUNTING);
                sendBroadcast(intent);
                break;

            case 5:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void addOnBackStackChangedListener() {
        if (getSupportFragmentManager() == null) {
            return;
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        final FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment fragment = fragmentManager.findFragmentById(R.id.container);

                        if (fragment instanceof ReportDetailFragment) {
                            spinner.setOnItemSelectedListener(
                                    new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(
                                                AdapterView<?> adapterView,
                                                View view, int i, long l) {
                                            switch (i) {
                                                case 0: //Clients
                                                    sendBroadcastFromReportDetailsFragment(
                                                            fragmentManager, Constants.CLIENT);
                                                    break;
                                                case 1: //Loan
                                                    sendBroadcastFromReportDetailsFragment(
                                                            fragmentManager, Constants.LOAN);
                                                    break;
                                                case 2:
                                                    sendBroadcastFromReportDetailsFragment(
                                                            fragmentManager, Constants.SAVINGS);
                                                    break;

                                                case 3:
                                                    sendBroadcastFromReportDetailsFragment(
                                                            fragmentManager, Constants.FUND);
                                                    break;

                                                case 4:
                                                    sendBroadcastFromReportDetailsFragment(
                                                            fragmentManager, Constants.ACCOUNTING);
                                                    break;

                                                case 5:
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    }
                            );
                        } else if (fragment instanceof ReportFragment) {
                            spinner.setOnItemSelectedListener(
                                    new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(
                                                AdapterView<?> adapterView,
                                                View view, int i, long l) {
                                            switch (i) {
                                                case 0: //Clients
                                                    sendBroadcastFromReportFragment(
                                                            fragmentManager, Constants.CLIENT);
                                                    break;

                                                case 1: //Loan
                                                    sendBroadcastFromReportFragment(
                                                            fragmentManager, Constants.LOAN);
                                                    break;

                                                case 2:
                                                    sendBroadcastFromReportFragment(
                                                            fragmentManager, Constants.SAVINGS);
                                                    break;

                                                case 3:
                                                    sendBroadcastFromReportFragment(
                                                            fragmentManager, Constants.FUND);
                                                    break;

                                                case 4:
                                                    sendBroadcastFromReportFragment(
                                                            fragmentManager, Constants.ACCOUNTING);
                                                    break;

                                                case 5:
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    }
                            );
                        }
                    }
                }
        );
    }

    private void sendBroadcastFromReportFragment(FragmentManager fragmentManager,
                                                 String reportCategory) {
        fragmentManager.popBackStack();
        fragmentManager.popBackStack();
        fragmentManager.executePendingTransactions();
        intent.putExtra(Constants.REPORT_CATEGORY, reportCategory);
        sendBroadcast(intent);
    }

    private void sendBroadcastFromReportDetailsFragment(FragmentManager fragmentManager,
                                                        String reportCategory) {
        fragmentManager.popBackStack();
        fragmentManager.executePendingTransactions();
        intent.putExtra(Constants.REPORT_CATEGORY, reportCategory);
        sendBroadcast(intent);
    }
}

