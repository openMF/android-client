/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.generatecollectionsheet;


import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.api.model.ClientsAttendance;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.collectionsheet.BulkSavingsDueTransaction;
import com.mifos.objects.collectionsheet.CenterDetail;
import com.mifos.objects.collectionsheet.ClientCollectionSheet;
import com.mifos.objects.collectionsheet.CollectionSheetPayload;
import com.mifos.objects.collectionsheet.CollectionSheetRequestPayload;
import com.mifos.objects.collectionsheet.CollectionSheetResponse;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;
import com.mifos.objects.collectionsheet.ProductiveCollectionSheetPayload;
import com.mifos.objects.collectionsheet.SavingsCollectionSheet;
import com.mifos.objects.collectionsheet.SavingsProduct;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenerateCollectionSheetFragment extends MifosBaseFragment
        implements GenerateCollectionSheetMvpView, Spinner.OnItemSelectedListener,
        View.OnClickListener, MFDatePicker.OnDatePickListener {

    public static final String LIMIT = "limit";
    public static final String ORDER_BY = "orderBy";
    public static final String SORT_ORDER = "sortOrder";
    public static final String ASCENDING = "ASC";
    public static final String ORDER_BY_FIELD_NAME = "name";
    public static final String STAFF_ID = "staffId";

    private final String TYPE_LOAN = "1";
    private final String TYPE_SAVING = "2";

    private final int TAG_TYPE_PRODUCTIVE = 111;
    private final int TAG_TYPE_COLLECTION = 222;

    @BindView(R.id.sp_branch_offices)
    Spinner spOffices;

    @BindView(R.id.sp_staff)
    Spinner spStaff;

    @BindView(R.id.sp_centers)
    Spinner spCenters;

    @BindView(R.id.sp_groups)
    Spinner spGroups;

    @BindView(R.id.tv_meeting_date)
    TextView tvMeetingDate;

    @BindView(R.id.btn_generate_collection_sheet)
    Button btnFetchCollectionSheet;

    @BindView(R.id.btn_generate_productive_collection_sheet)
    Button btnFetchProductiveCollectionSheet;

    @BindView(R.id.table_sheet)
    TableLayout tableProductive;

    @BindView(R.id.table_additional)
    TableLayout tableAdditional;

    @BindView(R.id.btn_submit_productive)
    Button btnSubmitProductive;

    @Inject
    GenerateCollectionSheetPresenter presenter;

    private View rootView;
    private DialogFragment datePicker;

    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> centerNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> groupNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> additionalPaymentTypeMap = new HashMap<>();

    private HashMap<String, Integer> attendanceTypeOptions = new HashMap<>();

    private List<String> centerNames = new ArrayList<String>();
    private List<String> staffNames = new ArrayList<String>();
    private List<String> officeNames = new ArrayList<String>();
    private List<String> groupNames = new ArrayList<>();
    private List<String> paymentTypes = new ArrayList<>();
    private int officeId = -1;
    private int centerId = -1;
    private int groupId = -1;
    private int staffId = -1;

    //id of the center whose Productive CollectionSheet has to be retrieved.
    private int productiveCenterId = -1;

    private int calendarId = -1;

    public static GenerateCollectionSheetFragment newInstance() {
        GenerateCollectionSheetFragment generateCollectionSheetFragment = new
                GenerateCollectionSheetFragment();
        return generateCollectionSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_generate_collection_sheet, container, false);

        ButterKnife.bind(this, rootView);
        presenter.attachView(this);
        setUpUi();
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mItem_search)
            getActivity().finish();
        return super.onOptionsItemSelected(item);
    }

    public void inflateOfficeSpinner() {
        presenter.loadOffices();
    }

    public void inflateStaffSpinner(final int officeId) {
        presenter.loadStaffInOffice(officeId);
    }

    public void inflateCenterSpinner(final int officeId, int staffId) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(LIMIT, -1);
        params.put(ORDER_BY, ORDER_BY_FIELD_NAME);
        params.put(SORT_ORDER, ASCENDING);
        if (staffId >= 0) {
            params.put(STAFF_ID, staffId);
        }
        presenter.loadCentersInOffice(officeId, params);
    }

    public void inflateGroupSpinner(final int officeId, int staffId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LIMIT, -1);
        params.put(ORDER_BY, ORDER_BY_FIELD_NAME);
        params.put(SORT_ORDER, ASCENDING);
        if (staffId >= 0)
            params.put(STAFF_ID, staffId);

        presenter.loadGroupsInOffice(officeId, params);
    }

    public void inflateGroupSpinner(final int centerId) {
        presenter.loadGroupByCenter(centerId);
    }

    @Override
    public void showOffices(List<Office> offices) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;
        officeNameIdHashMap = presenter.createOfficeNameIdMap(offices, officeNames);
        setSpinner(spOffices, officeNames);
        spOffices.setOnItemSelectedListener(this);
    }

    @Override
    public void showStaffInOffice(List<Staff> staffs, final int officeId) {
        this.officeId = officeId;
        staffNameIdHashMap = presenter.createStaffIdMap(staffs, staffNames);
        setSpinner(spStaff, staffNames);
        spStaff.setOnItemSelectedListener(this);
        staffId = -1; //Reset staff id
    }

    @Override
    public void showCentersInOffice(List<Center> centers) {
        centerNameIdHashMap = presenter.createCenterIdMap(centers, centerNames);
        setSpinner(spCenters, centerNames);
        spCenters.setOnItemSelectedListener(this);
        centerId = -1; //Reset Center id.
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.sp_centers:
                centerId = centerNameIdHashMap.get(centerNames.get(i));
                if (centerId != -1) {
                    inflateGroupSpinner(centerId);
                } else {
                    Toaster.show(rootView, getString(R.string.error_select_center));
                }
                break;

            case R.id.sp_staff:
                staffId = staffNameIdHashMap.get(staffNames.get(i));
                if (staffId != -1) {
                    inflateCenterSpinner(officeId, staffId);
                    inflateGroupSpinner(officeId, staffId);
                } else {
                    Toaster.show(rootView, getString(R.string.error_select_staff));
                }
                break;

            case R.id.sp_branch_offices:
                officeId = officeNameIdHashMap.get(officeNames.get(i));
                if (officeId != -1) {
                    inflateStaffSpinner(officeId);
                    inflateCenterSpinner(officeId, -1);
                    inflateGroupSpinner(officeId, -1);
                } else {
                    Toaster.show(rootView, getString(R.string.error_select_office));
                }
                break;

            case R.id.sp_groups:
                groupId = groupNameIdHashMap.get(groupNames.get(i));
                if (groupId == -1) {
                    Toaster.show(rootView, getString(R.string.error_select_group));
                }
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_meeting_date:
                setMeetingDate();
                break;

            case R.id.btn_generate_collection_sheet:
                fetchCollectionSheet();
                break;

            case R.id.btn_generate_productive_collection_sheet:
                fetchCenterDetails();
                break;

            case R.id.btn_submit_productive:
                switch ((int) view.getTag()) {
                    case TAG_TYPE_PRODUCTIVE:
                        submitProductiveSheet();
                        break;

                    case TAG_TYPE_COLLECTION:
                        submitCollectionSheet();
                        break;
                }
        }
    }

    private void setUpUi() {
        inflateOfficeSpinner();
        inflateMeetingDate();
        tvMeetingDate.setOnClickListener(this);
        btnFetchProductiveCollectionSheet.setOnClickListener(this);
        btnFetchCollectionSheet.setOnClickListener(this);
    }

    private void fetchCollectionSheet() {
        if (groupId == -1) {
            Toaster.show(rootView, getString(R.string.spinner_group));
            return;
        }
        CollectionSheetRequestPayload requestPayload = new CollectionSheetRequestPayload();
        requestPayload.setTransactionDate(tvMeetingDate.getText().toString());
        requestPayload.setCalendarId(calendarId);
        presenter.loadCollectionSheet(groupId, requestPayload);
    }

    private void fetchProductiveCollectionSheet() {
        //Make RequestPayload and retrieve Productive CollectionSheet.
        CollectionSheetRequestPayload requestPayload = new CollectionSheetRequestPayload();
        requestPayload.setTransactionDate(tvMeetingDate.getText().toString());
        requestPayload.setCalendarId(calendarId);
        presenter.loadProductiveCollectionSheet(productiveCenterId, requestPayload);

    }

    private void fetchCenterDetails() {
        presenter.loadCenterDetails(Constants.DATE_FORMAT_LONG, Constants.LOCALE_EN,
                tvMeetingDate.getText().toString(), officeId, staffId);
    }

    @Override
    public void onCenterLoadSuccess(List<CenterDetail> centerDetails) {
        if (centerDetails.size() == 0) {
            Toaster.show(rootView, getString(R.string.no_collectionsheet_found));
            return;
        }

        //Set CalendarId and fetch ProductiveCollectionSheet
        calendarId = centerDetails.get(0).getMeetingFallCenters().get(0).
                getCollectionMeetingCalendar().getId();
        productiveCenterId = centerDetails.get(0).getMeetingFallCenters().get(0).getId();
        fetchProductiveCollectionSheet();
    }

    @Override
    public void showProductive(CollectionSheetResponse sheet) {
        inflateProductiveCollectionTable(sheet);
    }

    @Override
    public void showCollection(CollectionSheetResponse sheet) {
        inflateCollectionTable(sheet);
    }

    private void inflateCollectionTable(CollectionSheetResponse collectionSheetResponse) {
        //Clear old views in case they are present.
        if (tableProductive.getChildCount() > 0) {
            tableProductive.removeAllViews();
        }

        //A List to be used to inflate Attendance Spinners
        ArrayList<String> attendanceTypes = new ArrayList<>();
        attendanceTypeOptions.clear();
        attendanceTypeOptions = presenter.filterAttendanceTypes(collectionSheetResponse
                .getAttendanceTypeOptions(), attendanceTypes);

        additionalPaymentTypeMap.clear();
        additionalPaymentTypeMap = presenter.filterPaymentTypes(collectionSheetResponse
                .getPaymentTypeOptions(), paymentTypes);

        //Add the heading Row
        TableRow headingRow = new TableRow(getContext());
        TableRow.LayoutParams headingRowParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headingRowParams.gravity = Gravity.CENTER;
        headingRowParams.setMargins(0, 0, 0, 10);
        headingRow.setLayoutParams(headingRowParams);

        TextView tvGroupName = new TextView(getContext());
        tvGroupName.setText(collectionSheetResponse.getGroups().get(0).getGroupName());
        tvGroupName.setTypeface(tvGroupName.getTypeface(), Typeface.BOLD);
        tvGroupName.setGravity(Gravity.CENTER);
        headingRow.addView(tvGroupName);

        for (LoanProducts loanProduct : collectionSheetResponse.getLoanProducts()) {
            TextView tvProduct = new TextView(getContext());
            tvProduct.setText(getString(R.string.collection_loan_product, loanProduct.getName()));
            tvProduct.setTypeface(tvProduct.getTypeface(), Typeface.BOLD);
            tvProduct.setGravity(Gravity.CENTER);
            headingRow.addView(tvProduct);
        }

        for (SavingsProduct savingsProduct : collectionSheetResponse.getSavingsProducts()) {
            TextView tvSavingProduct = new TextView(getContext());
            tvSavingProduct.setText(getString(R.string.collection_saving_product,
                    savingsProduct.getName()));
            tvSavingProduct.setTypeface(tvSavingProduct.getTypeface(), Typeface.BOLD);
            tvSavingProduct.setGravity(Gravity.CENTER);
            headingRow.addView(tvSavingProduct);
        }

        TextView tvAttendance = new TextView(getContext());
        tvAttendance.setText(getString(R.string.attendance));
        tvAttendance.setGravity(Gravity.CENTER);
        tvAttendance.setTypeface(tvAttendance.getTypeface(), Typeface.BOLD);
        headingRow.addView(tvAttendance);

        tableProductive.addView(headingRow);

        for (ClientCollectionSheet clientCollectionSheet : collectionSheetResponse
                .getGroups().get(0).getClients()) {
            //Insert rows
            TableRow row = new TableRow(getContext());
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.gravity = Gravity.CENTER;
            rowParams.setMargins(0, 0, 0, 10);
            row.setLayoutParams(rowParams);


            //Column 1: Client Name and Id
            TextView tvClientName = new TextView(getContext());
            tvClientName.setText(concatIdWithName(clientCollectionSheet.getClientName(),
                    clientCollectionSheet.getClientId()));
            row.addView(tvClientName);

            //Subsequent columns: The Loan products
            for (LoanProducts loanProduct : collectionSheetResponse.getLoanProducts()) {
                //Since there may be several items in this column, create a container.
                LinearLayout productContainer = new LinearLayout(getContext());
                productContainer.setOrientation(LinearLayout.HORIZONTAL);

                //Iterate through all the loans in of this type and add in the container
                for (LoanCollectionSheet loan : clientCollectionSheet.getLoans()) {
                    if (loanProduct.getName().equals(loan.getProductShortName())) {
                        //This loan should be shown in this column. So, add it in the container.
                        EditText editText = new EditText(getContext());
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0));
                        //Set the loan id as the Tag of the EditText
                        // in format 'TYPE:ID' which
                        //will later be used as the identifier for this.
                        editText.setTag(TYPE_LOAN + ":" + loan.getLoanId());
                        productContainer.addView(editText);
                    }
                }
                row.addView(productContainer);
            }

            //After Loans, show Savings columns
            for (SavingsProduct product : collectionSheetResponse.getSavingsProducts()) {
                //Since there may be several Savings items in this column, create a container.
                LinearLayout productContainer = new LinearLayout(getContext());
                productContainer.setOrientation(LinearLayout.HORIZONTAL);

                //Iterate through all the Savings in of this type and add in the container
                for (SavingsCollectionSheet saving : clientCollectionSheet.getSavings()) {
                    if (saving.getProductId() == product.getId()) {
                        //Add the saving in the container
                        EditText editText = new EditText(getContext());
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0));
                        //Set the Saving id as the Tag of the EditText
                        // in 'TYPE:ID' format which
                        //will later be used as the identifier for this.
                        editText.setTag(TYPE_SAVING + ":" + saving.getSavingsId());
                        productContainer.addView(editText);
                    }
                }
                row.addView(productContainer);
            }

            Spinner spAttendance = new Spinner(getContext());
            //Set the clientId as its tag which will be used as identifier later.
            spAttendance.setTag(clientCollectionSheet.getClientId());
            setSpinner(spAttendance, attendanceTypes);
            row.addView(spAttendance);

            tableProductive.addView(row);
        }

        if (btnSubmitProductive.getVisibility() != View.VISIBLE) {
            //Show the button the first time sheet is loaded.
            btnSubmitProductive.setVisibility(View.VISIBLE);
            btnSubmitProductive.setOnClickListener(this);
        }

        //If this block has been executed, that means the CollectionSheet
        //which is already shown is for groups.
        btnSubmitProductive.setTag(TAG_TYPE_COLLECTION);

        if (tableAdditional.getVisibility() != View.VISIBLE) {
            tableAdditional.setVisibility(View.VISIBLE);
        }
        //Show Additional Views
        TableRow rowPayment = new TableRow(getContext());
        TextView tvLabelPayment = new TextView(getContext());
        tvLabelPayment.setText(getString(R.string.payment_type));
        rowPayment.addView(tvLabelPayment);
        Spinner spPayment = new Spinner(getContext());
        setSpinner(spPayment, paymentTypes);
        rowPayment.addView(spPayment);
        tableAdditional.addView(rowPayment);

        TableRow rowAccount = new TableRow(getContext());
        TextView tvLabelAccount = new TextView(getContext());
        tvLabelAccount.setText(getString(R.string.account_number));
        rowAccount.addView(tvLabelAccount);
        EditText etPayment = new EditText(getContext());
        rowAccount.addView(etPayment);
        tableAdditional.addView(rowAccount);

        TableRow rowCheck = new TableRow(getContext());
        TextView tvLabelCheck = new TextView(getContext());
        tvLabelCheck.setText(getString(R.string.cheque_number));
        rowCheck.addView(tvLabelCheck);
        EditText etCheck = new EditText(getContext());
        rowCheck.addView(etCheck);
        tableAdditional.addView(rowCheck);

        TableRow rowRouting = new TableRow(getContext());
        TextView tvLabelRouting = new TextView(getContext());
        tvLabelRouting.setText(getString(R.string.routing_code));
        rowRouting.addView(tvLabelRouting);
        EditText etRouting = new EditText(getContext());
        rowRouting.addView(etRouting);
        tableAdditional.addView(rowRouting);

        TableRow rowReceipt = new TableRow(getContext());
        TextView tvLabelReceipt = new TextView(getContext());
        tvLabelReceipt.setText(getString(R.string.receipt_number));
        rowReceipt.addView(tvLabelReceipt);
        EditText etReceipt = new EditText(getContext());
        rowReceipt.addView(etReceipt);
        tableAdditional.addView(rowReceipt);

        TableRow rowBank = new TableRow(getContext());
        TextView tvLabelBank = new TextView(getContext());
        tvLabelBank.setText(getString(R.string.bank_number));
        rowBank.addView(tvLabelBank);
        EditText etBank = new EditText(getContext());
        rowBank.addView(etBank);
        tableAdditional.addView(rowBank);
    }

    private void inflateProductiveCollectionTable(CollectionSheetResponse collectionSheetResponse) {

        //Clear old views in case they are present.
        if (tableProductive.getChildCount() > 0) {
            tableProductive.removeAllViews();
        }

        if (tableAdditional.getVisibility() == View.VISIBLE) {
            tableAdditional.removeAllViews();
            tableAdditional.setVisibility(View.GONE);
        }

        //A List to be used to inflate Attendance Spinners
        ArrayList<String> attendanceTypes = new ArrayList<>();
        attendanceTypeOptions.clear();
        attendanceTypeOptions = presenter.filterAttendanceTypes(collectionSheetResponse
                .getAttendanceTypeOptions(), attendanceTypes);

        //Add the heading Row
        TableRow headingRow = new TableRow(getContext());
        TableRow.LayoutParams headingRowParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headingRowParams.gravity = Gravity.CENTER;
        headingRowParams.setMargins(0, 0, 0, 10);
        headingRow.setLayoutParams(headingRowParams);

        TextView tvGroupName = new TextView(getContext());
        tvGroupName.setText(collectionSheetResponse.getGroups().get(0).getGroupName());
        tvGroupName.setTypeface(tvGroupName.getTypeface(), Typeface.BOLD);
        tvGroupName.setGravity(Gravity.CENTER);
        headingRow.addView(tvGroupName);

        for (LoanProducts loanProduct : collectionSheetResponse.getLoanProducts()) {
            TextView tvProduct = new TextView(getContext());
            tvProduct.setText(getString(R.string.collection_heading_charges,
                    loanProduct.getName()));
            tvProduct.setTypeface(tvProduct.getTypeface(), Typeface.BOLD);
            tvProduct.setGravity(Gravity.CENTER);
            headingRow.addView(tvProduct);
        }

        TextView tvAttendance = new TextView(getContext());
        tvAttendance.setText(getString(R.string.attendance));
        tvAttendance.setGravity(Gravity.CENTER);
        tvAttendance.setTypeface(tvAttendance.getTypeface(), Typeface.BOLD);
        headingRow.addView(tvAttendance);

        tableProductive.addView(headingRow);

        for (ClientCollectionSheet clientCollectionSheet :
                collectionSheetResponse.getGroups().get(0).getClients()) {
            //Insert rows
            TableRow row = new TableRow(getContext());
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.gravity = Gravity.CENTER;
            rowParams.setMargins(0, 0, 0, 10);
            row.setLayoutParams(rowParams);


            //Column 1: Client Name and Id
            TextView tvClientName = new TextView(getContext());
            tvClientName.setText(concatIdWithName(clientCollectionSheet.getClientName(),
                    clientCollectionSheet.getClientId()));
            row.addView(tvClientName);

            //Subsequent columns: The Loan products
            for (LoanProducts loanProduct : collectionSheetResponse.getLoanProducts()) {
                //Since there may be several items in this column, create a container.
                LinearLayout productContainer = new LinearLayout(getContext());
                productContainer.setOrientation(LinearLayout.HORIZONTAL);

                //Iterate through all the loans in of this type and add in the container
                for (LoanCollectionSheet loanCollectionSheet : clientCollectionSheet.getLoans()) {
                    if (loanProduct.getName().equals(loanCollectionSheet.getProductShortName())) {
                        //This loan should be shown in this column. So, add it in the container.
                        EditText editText = new EditText(getContext());
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0));
                        //Set the loan id as the Tag of the EditText which
                        //will later be used as the identifier for this.
                        editText.setTag(TYPE_LOAN + ":" + loanCollectionSheet.getLoanId());
                        productContainer.addView(editText);
                    }
                }
                row.addView(productContainer);
            }

            Spinner spAttendance = new Spinner(getContext());
            setSpinner(spAttendance, attendanceTypes);
            row.addView(spAttendance);

            tableProductive.addView(row);
        }

        if (btnSubmitProductive.getVisibility() != View.VISIBLE) {
            //Show the button the first time sheet is loaded.
            btnSubmitProductive.setVisibility(View.VISIBLE);
            btnSubmitProductive.setOnClickListener(this);
        }

        //If this block has been executed, that the CollectionSheet
        //which is already shown on screen is for center - Productive.
        btnSubmitProductive.setTag(TAG_TYPE_PRODUCTIVE);
    }

    private void submitProductiveSheet() {
        ProductiveCollectionSheetPayload payload = new ProductiveCollectionSheetPayload();

        payload.setCalendarId(calendarId);
        payload.setTransactionDate(tvMeetingDate.getText().toString());

        for (int i = 0; i < tableProductive.getChildCount(); i++) {
            //In the tableRows which depicts the details of that client.
            //Loop through all the view of this TableRows.

            TableRow row = (TableRow) tableProductive.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                //In a particular TableRow
                //Loop through the views and check if it's LinearLayout
                //because the required views - EditTexts are there only.

                View v = row.getChildAt(j);
                if (v instanceof LinearLayout) {
                    //So, we got into the container containing the EditTexts
                    //Now, extract the values and the loanId associated to this
                    //particular TextView which was set as its Tag.

                    for (int k = 0; k < ((LinearLayout) v).getChildCount(); k++) {
                        EditText et = (EditText) ((LinearLayout) v).getChildAt(k);
                        String[] typeId = et.getTag().toString().split(":");
                        //Switch on basis if whether it's LOAN or SAVING
                        switch (typeId[0]) {
                            case TYPE_LOAN:
                                int loanId = Integer.parseInt(typeId[1]);
                                Double amount = Double.parseDouble(et.getText().toString());
                                payload.getBulkRepaymentTransactions().add(
                                        new BulkRepaymentTransactions(loanId, amount));
                                break;
                            //Saving products are NOT shown in ProductiveSheet.
                            //So, not processing them.
                        }
                    }
                }
            }
        }

        //Payload with all the items is ready. Now, hit the endpoint and submit it.
        presenter.submitProductiveSheet(productiveCenterId, payload);
    }

    private void submitCollectionSheet() {
        CollectionSheetPayload payload = new CollectionSheetPayload();

        payload.setCalendarId(calendarId);
        payload.setTransactionDate(tvMeetingDate.getText().toString());
        payload.setActualDisbursementDate(tvMeetingDate.getText().toString());

        for (int i = 0; i < tableProductive.getChildCount(); i++) {
            //In the tableRows which depicts the details of that client.
            //Loop through all the view of this TableRows.

            TableRow row = (TableRow) tableProductive.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                //In a particular TableRow
                //Loop through the views and check if it's LinearLayout
                //because the required views - EditTexts are there only.

                View v = row.getChildAt(j);
                if (v instanceof LinearLayout) {
                    //So, we got into the container containing the EditTexts
                    //Now, extract the values and the loanId associated to this
                    //particular TextView which was set as its Tag.

                    for (int k = 0; k < ((LinearLayout) v).getChildCount(); k++) {
                        EditText et = (EditText) ((LinearLayout) v).getChildAt(k);
                        String[] typeId = et.getTag().toString().split(":");
                        //Switch on basis if whether it's LOAN or SAVING
                        switch (typeId[0]) {
                            case TYPE_LOAN:
                                int loanId = Integer.parseInt(typeId[1]);
                                Double amount = Double.parseDouble(et.getText().toString());
                                payload.getBulkRepaymentTransactions().add(
                                        new BulkRepaymentTransactions(loanId, amount));
                                break;

                            case TYPE_SAVING:
                                //Add to Savings
                                int savingsId = Integer.parseInt(typeId[1]);
                                String amountSaving = et.getText().toString();
                                payload.getBulkSavingsDueTransactions().add(
                                        new BulkSavingsDueTransaction(savingsId, amountSaving));
                                break;
                        }

                    }
                } else if (v instanceof Spinner) {
                    //Attendance
                    Spinner spinner = (Spinner) v;
                    int clientId = (int) spinner.getTag();
                    int attendanceTypeId = attendanceTypeOptions
                            .get(spinner.getSelectedItem().toString());
                    payload.getClientsAttendance()
                            .add(new ClientsAttendance(clientId, attendanceTypeId));
                }
            }
        }

        //Check if Additional details are there
        if (tableAdditional != null && tableAdditional.getChildCount() > 0) {
            for (int i = 0; i < 6; i++) {
                TableRow row = (TableRow) tableAdditional.getChildAt(i);
                View v = row.getChildAt(1);

                if (v instanceof Spinner) {
                    int paymentId = additionalPaymentTypeMap
                            .get(((Spinner) v).getSelectedItem().toString());
                    if (paymentId != -1) {
                        payload.setPaymentTypeId(paymentId);
                    }
                } else if (v instanceof EditText) {
                    String value = ((EditText) v).getText().toString();
                    if (!value.equals("")) {
                        switch (i) {
                            case 1:
                                payload.setAccountNumber(value);
                                break;

                            case 2:
                                payload.setCheckNumber(value);
                                break;

                            case 3:
                                payload.setRoutingCode(value);
                                break;

                            case 4:
                                payload.setReceiptNumber(value);
                                break;

                            case 5:
                                payload.setBankNumber(value);
                                break;
                        }
                    }
                }
            }
        }

        //Payload with all the items is ready. Now, hit the endpoint and submit it.
        presenter.submitCollectionSheet(groupId, payload);
    }

    private String concatIdWithName(String name, int id) {
        return "(" + id + ")" + name;
    }

    private void inflateMeetingDate() {
        datePicker = MFDatePicker.newInsance(this);
        String date = DateHelper.
                getDateAsStringUsedForCollectionSheetPayload(MFDatePicker.getDatePickedAsString());
        tvMeetingDate.setText(date.replace('-', ' '));
    }

    private void setMeetingDate() {
        datePicker.show(getActivity().getSupportFragmentManager(),
                FragmentConstants.DFRAG_DATE_PICKER);
    }

    @Override
    public void onDatePicked(String date) {
        String newDate = DateHelper.getDateAsStringUsedForCollectionSheetPayload(date);
        tvMeetingDate.setText(newDate.replace('-', ' '));
    }

    @Override
    public void showGroupsInOffice(List<Group> groups) {
        groupNameIdHashMap = presenter.createGroupIdMap(groups, groupNames);
        setSpinner(spGroups, groupNames);
    }

    @Override
    public void showGroupByCenter(CenterWithAssociations centerWithAssociations) {
        groupNameIdHashMap = presenter.createGroupIdMap(
                centerWithAssociations.getGroupMembers(), groupNames);
        setSpinner(spGroups, groupNames);
        calendarId = centerWithAssociations.getCollectionMeetingCalendar().getId();
        groupId = -1; //Reset group Id
        spGroups.setOnItemSelectedListener(this);
    }

    private void setSpinner(Spinner spinner, List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, values);
        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void showError(String s) {
        Toaster.show(rootView, s);
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
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }
}
