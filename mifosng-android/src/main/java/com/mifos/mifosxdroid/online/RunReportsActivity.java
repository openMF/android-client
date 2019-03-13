package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.runreports.reportcategory.ReportCategoryFragment;
import com.mifos.utils.Constants;

/**
 * Created by Tarun on 02-08-17.
 */

public class RunReportsActivity extends MifosBaseActivity
        implements Spinner.OnItemSelectedListener {

    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Spinner spinner = new Spinner(getSupportActionBar().getThemedContext());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_runreport, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        toolbar.addView(spinner);
        intent = new Intent(Constants.ACTION_REPORT);
        ReportCategoryFragment fragment = new ReportCategoryFragment();
        replaceFragment(fragment, false, R.id.container);
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
}
