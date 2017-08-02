package com.mifos.mifosxdroid.online.runreports.clientreportdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.runreports.clientreport.ClientReportFragment;
import com.mifos.objects.runreports.DataRow;
import com.mifos.objects.runreports.FullParameterListResponse;
import com.mifos.objects.runreports.client.ClientReportTypeItem;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 04-08-17.
 */

public class ClientReportDetailFragment extends MifosBaseFragment
        implements ClientReportDetailMvpView {

    @BindView(R.id.tv_report_name)
    TextView tvReportName;

    @BindView(R.id.tv_report_type)
    TextView tvReportType;

    @BindView(R.id.tv_report_category)
    TextView tvReportCategory;

    @BindView(R.id.table_details)
    TableLayout tableDetails;

    @Inject
    ClientReportDetailPresenter presenter;

    private View rootView;
    private ClientReportTypeItem reportItem;

    private HashMap<String, Integer> fundMap;
    private HashMap<String, Integer> loanOfficerMap;
    private HashMap<String, Integer> loanProductMap;
    private HashMap<String, Integer> loanPurposeMap;
    private HashMap<String, Integer> officeMap;
    private HashMap<String, String> currencyMap;

    public ClientReportDetailFragment() {}

    public static ClientReportDetailFragment newInstance(Bundle args) {
        ClientReportDetailFragment fragment = new ClientReportDetailFragment();
        fragment.setArguments(args);
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
        rootView = inflater.inflate(R.layout.fragment_client_report_details, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        presenter.attachView(this);

        reportItem = getArguments().getParcelable(Constants.CLIENT_REPORT_ITEM);
        setUpUi();

        return rootView;
    }

    private void setUpUi() {
        tvReportName.setText(reportItem.getReportName());
        tvReportCategory.setText(reportItem.getReportCategory());
        tvReportType.setText(reportItem.getReportType());

        String reportName = "'" + reportItem.getReportName() + "'";
        presenter.fetchFullParameterList(reportName, true);
    }

    private void addTableRow(FullParameterListResponse data, String identifier) {

        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.gravity = Gravity.CENTER;
        rowParams.setMargins(0, 0, 0, 10);
        row.setLayoutParams(rowParams);

        TextView tvLabel = new TextView(getContext());
        row.addView(tvLabel);

        Spinner spinner = new Spinner(getContext());
        row.addView(spinner);

        ArrayList<String> spinnerValues = new ArrayList<>();

        // Add the parameter keys as the text so that we can identify the spinner
        // and can later add the corresponding values in the parameter-list while
        // requesting the report.
        switch (identifier) {
            case "loanOfficerIdSelectAll":
                spinner.setTag("R_loanOfficerId");
                loanOfficerMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.loan_officer));
                break;
            case "loanProductIdSelectAll":
                spinner.setTag("R_loanProductId");
                loanProductMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText("Loan Product");
                break;
            case "loanPurposeIdSelectAll":
                spinner.setTag("R_loanPurposeId");
                loanPurposeMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText("Loan Purpose");
                break;
            case "fundIdSelectAll":
                spinner.setTag("R_fundId");
                fundMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText("Fund");
                break;
            case "currencyIdSelectAll":
                spinner.setTag("R_currencyId");
                currencyMap = presenter.filterStringHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText("Currency");
                break;
            case "OfficeIdSelectOne":
                spinner.setTag("R_officeId");
                officeMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText("Office");
                break;
        }

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        tableDetails.addView(row);
    }

    private void runReport() {
        if (tableDetails.getChildCount() < 1) {
            Toaster.show(rootView, getString(R.string.msg_report_empty));
        } else {
            Integer fundId;
            Integer loanOfficeId;
            Integer loanProductId;
            Integer loanPurposeId;
            Integer officeId;
            String currencyId;

            Map<String, String> map = new HashMap<>();

             /* There are variable number of parameters in the request query.
              Hence, create a Map instead of hardcoding the number of
              query parameters in the Retrofit Service.*/

            for (int i = 0; i < tableDetails.getChildCount(); i++) {
                TableRow tableRow = (TableRow) tableDetails.getChildAt(i);
                Spinner sp = (Spinner) tableRow.getChildAt(1);
                switch (sp.getTag().toString()) {
                    case "R_loanOfficerId":
                        loanOfficeId = loanOfficerMap.get(sp.getSelectedItem().toString());
                        if (loanOfficeId != -1) {
                            map.put(sp.getTag().toString(), String.valueOf(loanOfficeId));
                        }
                        break;
                    case "R_loanProductId":
                        loanProductId = loanProductMap.get(sp.getSelectedItem().toString());
                        if (loanProductId != -1) {
                            map.put(sp.getTag().toString(), String.valueOf(loanProductId));
                        }
                        break;
                    case "R_loanPurposeId":
                        loanPurposeId = loanPurposeMap.get(sp.getSelectedItem().toString());
                        if (loanPurposeId != -1) {
                            map.put(sp.getTag().toString(), String.valueOf(loanPurposeId));
                        }
                        break;
                    case "R_fundId":
                        fundId = fundMap.get(sp.getSelectedItem().toString());
                        if (fundId != -1) {
                            map.put(sp.getTag().toString(), String.valueOf(fundId));
                        }
                        break;
                    case "R_currencyId":
                        currencyId = currencyMap.get(sp.getSelectedItem().toString());
                        if (!currencyId.equals("")) {
                            map.put(sp.getTag().toString(), currencyId);
                        }
                        break;
                    case "R_officeId":
                        officeId = officeMap.get(sp.getSelectedItem().toString());
                        if (officeId != -1) {
                            map.put(sp.getTag().toString(), String.valueOf(officeId));
                        }
                        break;
                }
            }
            presenter.fetchRunReportWithQuery(reportItem.getReportName(), map);
        }
    }

    @Override
    public void showRunReport(FullParameterListResponse response) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.REPORT_NAME, response);
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack("ClientDetails");
        fragmentTransaction.replace(R.id.container, ClientReportFragment.newInstance(bundle))
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_runreport, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_run_report:
                runReport();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showError(String error) {
        Toaster.show(rootView, error);
    }

    @Override
    public void showFullParameterResponse(FullParameterListResponse response) {
        for (DataRow row : response.getData()) {
            presenter.fetchParameterDetails(row.getRow().get(0), true);
        }
    }

    @Override
    public void showParameterDetails(FullParameterListResponse response, String identifier) {
        addTableRow(response, identifier);
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
