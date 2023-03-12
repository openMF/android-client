package com.mifos.mifosxdroid.online.runreports.reportdetail;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.databinding.FragmentClientReportDetailsBinding;
import com.mifos.mifosxdroid.online.runreports.report.ReportFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.runreports.DataRow;
import com.mifos.objects.runreports.FullParameterListResponse;
import com.mifos.objects.runreports.client.ClientReportTypeItem;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


/**
 * Created by Tarun on 04-08-17.
 */

public class ReportDetailFragment extends MifosBaseFragment
        implements ReportDetailMvpView, MFDatePicker.OnDatePickListener {

    private FragmentClientReportDetailsBinding binding;
    TextView tvReportName;

    TextView tvReportType;

    TextView tvReportCategory;


    @Inject
    ReportDetailPresenter presenter;

    private ClientReportTypeItem reportItem;

    private boolean fetchLoanOfficer = false;
    private boolean fetchLoanProduct = false;

    private HashMap<String, Integer> fundMap;
    private HashMap<String, Integer> loanOfficerMap;
    private HashMap<String, Integer> loanProductMap;
    private HashMap<String, Integer> loanPurposeMap;
    private HashMap<String, Integer> officeMap;
    private HashMap<String, Integer> parMap;
    private HashMap<String, Integer> subStatusMap;
    private HashMap<String, Integer> glAccountNoMap;
    private HashMap<String, Integer> obligDateTypeMap;
    private HashMap<String, String> currencyMap;

    private String dateField;
    public DialogFragment datePicker;

    private EditText tvField;

    public ReportDetailFragment() {
    }


    public static ReportDetailFragment newInstance(Bundle args) {
        ReportDetailFragment fragment = new ReportDetailFragment();
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
        binding = FragmentClientReportDetailsBinding
                .inflate(inflater, container, false);
        setHasOptionsMenu(true);
        presenter.attachView(this);

        reportItem = getArguments().getParcelable(Constants.CLIENT_REPORT_ITEM);

        tvReportName = binding.includeClientReportDetails.tvReportName;
        tvReportType = binding.includeClientReportDetails.tvReportType;
        tvReportCategory = binding.includeClientReportDetails.tvReportCategory;
        setUpUi();

        return binding.getRoot();
    }

    private void setUpUi() {
        tvReportName.setText(reportItem.getReportName());
        tvReportCategory.setText(reportItem.getReportCategory());
        tvReportType.setText(reportItem.getReportType());

        String reportName = "'" + reportItem.getReportName() + "'";
        presenter.fetchFullParameterList(reportName, true);
        datePicker = MFDatePicker.newInsance(this);
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

        final Spinner spinner = new Spinner(getContext());
        row.addView(spinner);

        ArrayList<String> spinnerValues = new ArrayList<>();

        // Add the parameter keys as the text so that we can identify the spinner
        // and can later add the corresponding values in the parameter-list while
        // requesting the report.
        switch (identifier) {
            case Constants.LOAN_OFFICER_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_OFFICER_ID);
                loanOfficerMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.loan_officer));
                break;
            case Constants.LOAN_PRODUCT_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_PRODUCT_ID);
                loanProductMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.loanproduct));
                break;
            case Constants.LOAN_PURPOSE_ID_SELECT:
                spinner.setTag(Constants.R_LOAN_PURPOSE_ID);
                loanPurposeMap = presenter.filterIntHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.report_loan_purpose));
                break;
            case Constants.FUND_ID_SELECT:
                spinner.setTag(Constants.R_FUND_ID);
                fundMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.loan_fund));
                break;
            case Constants.CURRENCY_ID_SELECT:
                spinner.setTag(Constants.R_CURRENCY_ID);
                currencyMap = presenter.filterStringHashMapForSpinner(data.getData(),
                        spinnerValues);
                tvLabel.setText(getString(R.string.currency));
                break;
            case Constants.OFFICE_ID_SELECT:
                spinner.setTag(Constants.R_OFFICE_ID);
                officeMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.office));
                break;
            case Constants.PAR_TYPE_SELECT:
                spinner.setTag(Constants.R_PAR_TYPE);
                parMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.par_calculation));
                break;
            case Constants.SAVINGS_ACCOUNT_SUB_STATUS:
                spinner.setTag(Constants.R_SUB_STATUS);
                subStatusMap = presenter.filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.savings_acc_deposit));
                break;
            case Constants.SELECT_GL_ACCOUNT_NO:
                spinner.setTag(Constants.R_ACCOUNT);
                glAccountNoMap = presenter.
                        filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.glaccount));
                break;
            case Constants.OBLIG_DATE_TYPE_SELECT:
                spinner.setTag(Constants.R_OBLIG_DATE_TYPE);
                obligDateTypeMap = presenter.
                        filterIntHashMapForSpinner(data.getData(), spinnerValues);
                tvLabel.setText(getString(R.string.obligation_date_type));

        }
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getTag().toString().equals(Constants.R_OFFICE_ID) && fetchLoanOfficer) {
                    int officeId = officeMap.get(spinner.getSelectedItem().toString());
                    presenter.fetchOffices(Constants.LOAN_OFFICER_ID_SELECT, officeId, true);
                } else if (spinner.getTag().toString().
                        equals(Constants.R_CURRENCY_ID) && fetchLoanProduct) {
                    String currencyId = currencyMap.get(spinner.getSelectedItem().toString());
                    presenter.fetchProduct(Constants.LOAN_PRODUCT_ID_SELECT, currencyId, true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.tableDetails.addView(row);
    }

    private void runReport() {
        if (binding.tableDetails.getChildCount() < 1) {
            Toaster.show(binding.getRoot(), getString(R.string.msg_report_empty));
        } else {
            Integer fundId;
            Integer loanOfficeId;
            Integer loanProductId;
            Integer loanPurposeId;
            Integer officeId;
            Integer parId;
            Integer subId;
            Integer obligId;
            Integer glAccountId;
            String currencyId;

            Map<String, String> map = new HashMap<>();

             /* There are variable number of parameters in the request query.
              Hence, create a Map instead of hardcoding the number of
              query parameters in the Retrofit Service.*/

            for (int i = 0; i < binding.tableDetails.getChildCount(); i++) {
                TableRow tableRow = (TableRow) binding.tableDetails.getChildAt(i);
                if (tableRow.getChildAt(1) instanceof Spinner) {
                    Spinner sp = (Spinner) tableRow.getChildAt(1);
                    switch (sp.getTag().toString()) {
                        case Constants.R_LOAN_OFFICER_ID:
                            loanOfficeId = loanOfficerMap.get(sp.getSelectedItem().toString());
                            if (loanOfficeId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(loanOfficeId));
                            }
                            break;
                        case Constants.R_LOAN_PRODUCT_ID:
                            loanProductId = loanProductMap.get(sp.getSelectedItem().toString());
                            if (loanProductId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(loanProductId));
                            }
                            break;
                        case Constants.R_LOAN_PURPOSE_ID:
                            loanPurposeId = loanPurposeMap.get(sp.getSelectedItem().toString());
                            if (loanPurposeId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(loanPurposeId));
                            }
                            break;
                        case Constants.R_FUND_ID:
                            fundId = fundMap.get(sp.getSelectedItem().toString());
                            if (fundId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(fundId));
                            }
                            break;
                        case Constants.R_CURRENCY_ID:
                            currencyId = currencyMap.get(sp.getSelectedItem().toString());
                            if (!currencyId.equals("")) {
                                map.put(sp.getTag().toString(), currencyId);
                            }
                            break;
                        case Constants.R_OFFICE_ID:
                            officeId = officeMap.get(sp.getSelectedItem().toString());
                            if (officeId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(officeId));
                            }
                            break;
                        case Constants.R_PAR_TYPE:
                            parId = parMap.get(sp.getSelectedItem().toString());
                            if (parId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(parId));
                            }
                            break;
                        case Constants.R_ACCOUNT:
                            glAccountId = glAccountNoMap.get(sp.getSelectedItem().toString());
                            if (glAccountId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(glAccountId));
                            }
                            break;
                        case Constants.R_SUB_STATUS:
                            subId = subStatusMap.get(sp.getSelectedItem().toString());
                            if (subId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(subId));
                            }
                            break;
                        case Constants.R_OBLIG_DATE_TYPE:
                            obligId = obligDateTypeMap.get(sp.getSelectedItem().toString());
                            if (obligId != -1) {
                                map.put(sp.getTag().toString(), String.valueOf(obligId));
                            }
                            break;
                    }
                } else if (tableRow.getChildAt(1) instanceof EditText) {
                    EditText et = (EditText) tableRow.getChildAt(1);
                    map.put(et.getTag().toString(), et.getText().toString());
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
        fragmentTransaction.replace(R.id.container, ReportFragment.newInstance(bundle))
                .commit();
    }

    @Override
    public void showOffices(FullParameterListResponse response, String identifier) {
        for (int i = 0; i < binding.tableDetails.getChildCount(); i++) {
            TableRow tableRow = (TableRow) binding.tableDetails.getChildAt(i);
            if (tableRow.getChildAt(1) instanceof EditText) {
                continue;
            }
            Spinner sp = (Spinner) tableRow.getChildAt(1);
            if (sp.getTag().toString().equals(Constants.R_LOAN_OFFICER_ID)) {
                ArrayList<String> spinnerValues = new ArrayList<>();
                loanOfficerMap = presenter.filterIntHashMapForSpinner(response.getData(),
                        spinnerValues);
                ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, spinnerValues);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adapter);
                return;
            }
        }
        addTableRow(response, identifier);

    }

    @Override
    public void showProduct(FullParameterListResponse response, String identifier) {
        for (int i = 0; i < binding.tableDetails.getChildCount(); i++) {
            TableRow tableRow = (TableRow) binding.tableDetails.getChildAt(i);
            if (tableRow.getChildAt(1) instanceof EditText) {
                continue;
            }
            Spinner sp = (Spinner) tableRow.getChildAt(1);
            if (sp.getTag().toString().equals(Constants.R_LOAN_PRODUCT_ID)) {
                ArrayList<String> spinnerValues = new ArrayList<>();
                loanProductMap = presenter.filterIntHashMapForSpinner(response.getData(),
                        spinnerValues);
                ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, spinnerValues);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adapter);
                return;
            }
        }
        addTableRow(response, identifier);
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
        Toaster.show(binding.getRoot(), error);
    }

    @Override
    public void showFullParameterResponse(FullParameterListResponse response) {
        for (DataRow row : response.getData()) {
            switch (row.getRow().get(0)) {
                case Constants.LOAN_OFFICER_ID_SELECT:
                    fetchLoanOfficer = true;
                    break;
                case Constants.LOAN_PRODUCT_ID_SELECT:
                    fetchLoanProduct = true;
                    break;
                case Constants.START_DATE_SELECT:
                    addTextView(Constants.START_DATE_SELECT);
                    break;
                case Constants.END_DATE_SELECT:
                    addTextView(Constants.END_DATE_SELECT);
                    break;
                case Constants.SELECT_ACCOUNT:
                    addTextView(Constants.SELECT_ACCOUNT);
                    break;
                case Constants.FROM_X_SELECT:
                    addTextView(Constants.FROM_X_SELECT);
                    break;
                case Constants.TO_Y_SELECT:
                    addTextView(Constants.TO_Y_SELECT);
                    break;
                case Constants.OVERDUE_X_SELECT:
                    addTextView(Constants.OVERDUE_X_SELECT);
                    break;
                case Constants.OVERDUE_Y_SELECT:
                    addTextView(Constants.OVERDUE_Y_SELECT);
                    break;
            }
            presenter.fetchParameterDetails(row.getRow().get(0), true);
        }
    }

    private void addTextView(String identifier) {

        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.gravity = Gravity.CENTER;
        rowParams.setMargins(0, 0, 0, 10);
        row.setLayoutParams(rowParams);
        final TextView tvLabel = new TextView(getContext());
        row.addView(tvLabel);
        tvField = new EditText(getContext());
        row.addView(tvField);
        switch (identifier) {
            case Constants.START_DATE_SELECT:
                tvField.setTag(Constants.R_START_DATE);
                tvLabel.setText(getString(R.string.start_date));
                break;
            case Constants.END_DATE_SELECT:
                tvField.setTag(Constants.R_END_DATE);
                tvLabel.setText(getString(R.string.end_date));
                break;
            case Constants.SELECT_ACCOUNT:
                tvField.setTag(Constants.R_ACCOUNT_NO);
                tvLabel.setText(getString(R.string.enter_account_no));
                break;
            case Constants.FROM_X_SELECT:
                tvField.setTag(Constants.R_FROM_X);
                tvLabel.setText(getString(R.string.from_x_number));
                break;
            case Constants.TO_Y_SELECT:
                tvField.setTag(Constants.R_TO_Y);
                tvLabel.setText(getString(R.string.to_y_number));
                break;
            case Constants.OVERDUE_X_SELECT:
                tvField.setTag(Constants.R_OVERDUE_X);
                tvLabel.setText(getString(R.string.overdue_x_number));
                break;
            case Constants.OVERDUE_Y_SELECT:
                tvField.setTag(Constants.R_OVERDUE_Y);
                tvLabel.setText(getString(R.string.overdue_y_number));
                break;
        }
        tvField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateField = v.getTag().toString();
                if (dateField.equals(Constants.R_START_DATE) ||
                        dateField.equals(Constants.R_END_DATE)) {
                    datePicker.show(getActivity().getSupportFragmentManager(),
                            FragmentConstants.DFRAG_DATE_PICKER);
                }
            }
        });
        binding.tableDetails.addView(row);
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

    @Override
    public void onDatePicked(String date) {
        for (int i = 0; i < binding.tableDetails.getChildCount(); i++) {
            TableRow tableRow = (TableRow) binding.tableDetails.getChildAt(i);
            if (tableRow.getChildAt(1) instanceof Spinner) {
                continue;
            }
            EditText et = (EditText) tableRow.getChildAt(1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date dateModified = null;
            try {
                dateModified = simpleDateFormat.parse(date);
            } catch (ParseException e) {

            }
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            if (et.getTag().toString().equals(dateField)) {
                et.setText(simpleDateFormat1.format(dateModified));
                break;
            }
        }
    }
}
