package com.mifos.mifosxdroid.online.collectionsheetindividual;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mifos.api.model.RequestCollectionSheetPayload;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog.CollectionSheetDialogFragment;
import com.mifos.mifosxdroid.online.collectionsheetindividualdetails.IndividualCollectionSheetDetailsFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;
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
 * Created by aksh on 18/6/18.
 */

public class NewIndividualCollectionSheetFragment extends MifosBaseFragment implements
        IndividualCollectionSheetMvpView, MFDatePicker.OnDatePickListener,
        Spinner.OnItemSelectedListener, View.OnClickListener {

    @BindView(R.id.btn_fetch_collection_sheet)
    Button btnFetchSheet;

    @BindView(R.id.sp_office_list)
    Spinner spOffices;

    @BindView(R.id.sp_staff_list)
    Spinner spStaff;

    @BindView(R.id.tv_repayment_date)
    TextView tvRepaymentDate;

    @Inject
    NewIndividualCollectionSheetPresenter presenter;

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
    private int officeId;
    private int staffId;
    private int requestCode = 1;
    private boolean success = true;

    private String actualDisbursementDate;
    private String transactionDate;


    public NewIndividualCollectionSheetFragment() {

    }

    public static NewIndividualCollectionSheetFragment newInstance() {
        Bundle args = new Bundle();
        NewIndividualCollectionSheetFragment fragment = new NewIndividualCollectionSheetFragment();
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
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_collection_sheet, container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet));
        presenter.attachView(this);

        setUpUi();
        return rootView;
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
        tvRepaymentDate.setOnClickListener(this);
        btnFetchSheet.setOnClickListener(this);
        presenter.fetchOffices();

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

    @Override
    public void setOfficeSpinner(List<Office> offices) {
        officeList = offices;
        officeNameList.clear();
        officeNameList.add(getString(R.string.spinner_office));
        officeNameList.addAll(presenter.filterOffices(officeList));
        officeAdapter.notifyDataSetChanged();
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
    public void setStaffSpinner(List<Staff> staffs) {

        spStaff.setOnItemSelectedListener(this);
        staffList = staffs;
        staffNameList.clear();
        staffNameList.add(getString(R.string.spinner_staff));
        staffNameList.addAll(presenter.filterStaff(staffList));
        staffAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.sp_office_list:
                if (i == officeList.size() || i == 0) {
                    Toaster.show(rootView, getStringMessage(R.string.error_select_office));
                } else {
                    Toaster.show(rootView, officeNameList.get(i));
                    officeId = officeList.get(i - 1).getId();
                    presenter.fetchStaff(officeId);
                }
                break;

            case R.id.sp_staff_list:
                if (i == staffList.size() || i == 0) {
                    Toaster.show(rootView, getStringMessage(R.string.error_select_staff));
                } else {
                    staffId = staffList.get(i - 1).getId();
                }
                break;
        }

    }

    public void popupDialog() {
        CollectionSheetDialogFragment collectionSheetDialogFragment =
                CollectionSheetDialogFragment.newInstance(tvRepaymentDate.getText().toString(),
                sheet.getClients().size());
        collectionSheetDialogFragment.setTargetFragment(this, requestCode);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_DOCUMENT_LIST);
        collectionSheetDialogFragment.show(fragmentTransaction, "Identifier Dialog Fragment");
    }

    public void getResponse(String response) {
        switch (response) {
            case Constants.FILLNOW:
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack();
                IndividualCollectionSheetDetailsFragment fragment = new
                        IndividualCollectionSheetDetailsFragment().newInstance(sheet,
                        actualDisbursementDate, transactionDate);
                ((MifosBaseActivity) getActivity()).replaceFragment(fragment,
                        true, R.id.container);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void showSheet(IndividualCollectionSheet individualCollectionSheet) {
        sheet = individualCollectionSheet;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.EXTRA_COLLECTION_INDIVIDUAL, sheet);
    }

    @Override
    public void showSuccess() {
        if (success) {
            popupDialog();
        }
    }

    @Override
    public void showError(String message) {
        Toaster.show(rootView, message);
    }

    @Override
    public void showNoSheetFound() {
        success = false;
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
