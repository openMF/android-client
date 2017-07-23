package com.mifos.mifosxdroid.online.collectionsheetindividual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.api.model.RequestCollectionSheetPayload;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.IndividualCollectionSheetAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;
import com.mifos.objects.collectionsheet.LoanAndClientName;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 05-07-2017.
 */

public class IndividualCollectionSheetFragment extends MifosBaseFragment implements
        IndividualCollectionSheetMvpView, MFDatePicker.OnDatePickListener,
        Spinner.OnItemSelectedListener, View.OnClickListener, OnRetrieveSheetItemData {

    @BindView(R.id.btn_fetch_collection_sheet)
    Button btnFetchSheet;

    @BindView(R.id.sp_office_list)
    Spinner spOffices;

    @BindView(R.id.sp_staff_list)
    Spinner spStaff;

    @BindView(R.id.tv_repayment_date)
    TextView tvRepaymentDate;

    @BindView(R.id.recycler_collections)
    RecyclerView recyclerSheets;

    @BindView(R.id.tv_no_sheet_found)
    TextView tvNoSheetFound;

    @Inject
    IndividualCollectionSheetPresenter presenter;

    @Inject
    IndividualCollectionSheetAdapter sheetsAdapter;

    private IndividualCollectionSheet sheet;
    private DialogFragment datePicker;
    private RequestCollectionSheetPayload requestPayload;
    private View rootView;
    private ArrayAdapter<String> officeAdapter;
    private ArrayList<String> officeNameList;
    private List<Office> officeList;
    private ArrayAdapter<String> staffAdapter;
    private ArrayList<String> staffNameList;
    private List<Staff> staffList;
    private List<String> paymentTypeList;
    private List<LoanAndClientName> loansAndClientNames;
    private int officeId;
    private int staffId;

    private IndividualCollectionSheetPayload payload;

    private String actualDisbursementDate;
    private String transactionDate;

    public IndividualCollectionSheetFragment() {
    }

    public static IndividualCollectionSheetFragment newInstance() {
        Bundle args = new Bundle();
        IndividualCollectionSheetFragment fragment = new IndividualCollectionSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (savedInstanceState != null) {
            sheet = (IndividualCollectionSheet) savedInstanceState.get(
                    Constants.EXTRA_COLLECTION_INDIVIDUAL);
            showCollectionSheetViews(sheet);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_individual_recycler, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet));
        presenter.attachView(this);

        setUpUi();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_individual_collectionsheet, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_submit_sheet:
                submitSheet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setRepaymentDate() {
        datePicker = MFDatePicker.newInsance(this);
        String date = DateHelper.
                getDateAsStringUsedForCollectionSheetPayload(MFDatePicker.getDatePickedAsString());
        tvRepaymentDate.setText(date.replace('-', ' '));
        transactionDate = date.replace('-', ' ');
        actualDisbursementDate = transactionDate;
    }

    private void prepareRequestPayload() {
        requestPayload = new RequestCollectionSheetPayload();
        requestPayload.setOfficeId(officeId);
        requestPayload.setStaffId(staffId);
        requestPayload.setTransactionDate(tvRepaymentDate.getText().toString());
    }

    private void setUpUi() {
        setRepaymentDate();
        officeNameList = new ArrayList<>();
        officeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, officeNameList);
        officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOffices.setAdapter(officeAdapter);
        spOffices.setOnItemSelectedListener(this);

        staffNameList = new ArrayList<>();
        staffAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, staffNameList);
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStaff.setAdapter(staffAdapter);

        if (sheet != null) {
            showCollectionSheetViews(sheet);
        }

        tvRepaymentDate.setOnClickListener(this);
        btnFetchSheet.setOnClickListener(this);
        presenter.fetchOffices();

    }

    @Override
    public void onShowSheetMandatoryItem(BulkRepaymentTransactions transaction, int position) {
        //Add the data as received
        payload.getBulkRepaymentTransactions().set(position, transaction);
    }

    @Override
    public void onSaveAdditionalItem(BulkRepaymentTransactions transaction, int position) {
        //Add or amend the data as changed by the user
        payload.getBulkRepaymentTransactions().set(position, transaction);
    }


    private void showCollectionSheetViews(IndividualCollectionSheet sheet) {

        if (tvNoSheetFound.getVisibility() == View.VISIBLE) {
            tvNoSheetFound.setVisibility(View.GONE);
        }

        paymentTypeList = presenter.filterPaymentTypeOptions(sheet.getPaymentTypeOptions());
        loansAndClientNames = presenter.filterLoanAndClientNames(sheet.getClients());

        //Initialize payload's BulkRepaymentTransactions array with default values.
        //The changes made (if any) will be updated by the interface 'OnRetrieveSheetItemData'
        //methods.

        payload = new IndividualCollectionSheetPayload();
        for (LoanAndClientName l : presenter.filterLoanAndClientNames(sheet.getClients())) {
            LoanCollectionSheet loan = l.getLoan();
            payload.getBulkRepaymentTransactions().add(new BulkRepaymentTransactions(
                    loan.getLoanId(),
                    loan.getChargesDue() == null ? loan.getTotalDue() :
                            loan.getTotalDue() + loan.getChargesDue()));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerSheets.getContext(), layoutManager.getOrientation());
        recyclerSheets.setLayoutManager(layoutManager);
        recyclerSheets.addItemDecoration(dividerItemDecoration);
        recyclerSheets.setAdapter(sheetsAdapter);

        sheetsAdapter.setSheetItemClickListener(this);
        sheetsAdapter.setLoans(loansAndClientNames);
        sheetsAdapter.setPaymentTypeList(paymentTypeList);
        sheetsAdapter.setPaymentTypeOptionsList(sheet.getPaymentTypeOptions());
        sheetsAdapter.notifyDataSetChanged();
    }

    private void submitSheet() {
        if (payload == null) {
            Toaster.show(rootView, getStringMessage(R.string.error_generate_sheet_first));
        } else {
            payload.setActualDisbursementDate(actualDisbursementDate);
            payload.setTransactionDate(transactionDate);
            presenter.submitIndividualCollectionSheet(payload);
        }
    }


    @Override
    public void setOfficeSpinner(List<Office> list) {
        officeList = list;
        officeNameList.clear();
        officeNameList.addAll(presenter.filterOffices(officeList));
        officeNameList.add(getString(R.string.spinner_office));
        officeAdapter.notifyDataSetChanged();
        spOffices.setSelection(officeList.size());
    }

    @Override
    public void onDatePicked(String date) {
        String d = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date);
        tvRepaymentDate.setText(d.replace('-', ' '));
    }

    public void retrieveCollectionSheet() {
        prepareRequestPayload();
        presenter.fetchIndividualCollectionSheet(requestPayload);
    }

    public void setTvRepaymentDate() {
        datePicker.show(getActivity().getSupportFragmentManager(),
                FragmentConstants.DFRAG_DATE_PICKER);
    }


    @Override
    public void setStaffSpinner(List<Staff> list) {

        spStaff.setOnItemSelectedListener(this);
        staffList = list;
        staffNameList.clear();
        staffNameList.addAll(presenter.filterStaff(staffList));
        staffNameList.add(getString(R.string.spinner_staff));
        staffAdapter.notifyDataSetChanged();
        spStaff.setSelection(staffList.size());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.sp_office_list:
                if (i == officeList.size()) {
                    Toaster.show(rootView, getStringMessage(R.string.error_select_office));
                } else {
                    Toaster.show(rootView, officeList.get(i).getName());
                    officeId = officeList.get(i).getId();
                    presenter.fetchStaff(officeId);
                }
                break;

            case R.id.sp_staff_list:
                if (i == staffList.size()) {
                    Toaster.show(rootView, getStringMessage(R.string.error_select_staff));
                } else {
                    staffId = staffList.get(i).getId();
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void showSheet(IndividualCollectionSheet collectionSheet) {
        sheet = collectionSheet;
        if (recyclerSheets.getVisibility() == View.GONE) {
            recyclerSheets.setVisibility(View.VISIBLE);
        }
        showCollectionSheetViews(sheet);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sheet != null) {
            showCollectionSheetViews(sheet);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.EXTRA_COLLECTION_INDIVIDUAL, sheet);
    }

    @Override
    public void showSuccess() {
        Toast.makeText(getContext(), getStringMessage(R.string.collectionsheet_submit_success),
                Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showError(String message) {
        Toaster.show(rootView, message);
    }

    @Override
    public void showNoSheetFound() {
        recyclerSheets.setVisibility(View.GONE);
        if (tvNoSheetFound.getVisibility() == View.GONE) {
            tvNoSheetFound.setVisibility(View.VISIBLE);
        }
        Toaster.show(rootView, getStringMessage(R.string.no_collectionsheet_found));
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_repayment_date:
                setTvRepaymentDate();
                break;

            case R.id.btn_fetch_collection_sheet:
                retrieveCollectionSheet();
                break;
        }

    }

}
