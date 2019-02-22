/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online.createnewclient;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class CreateNewClientFragment extends ProgressableFragment
        implements MFDatePicker.OnDatePickListener, CreateNewClientMvpView, AdapterView
        .OnItemSelectedListener {

    private final String LOG_TAG = getClass().getSimpleName();

    public DialogFragment datePickerSubmissionDate, datePickerDateOfBirth;

    @BindView(R.id.et_client_first_name)
    EditText etClientFirstName;

    @BindView(R.id.et_client_last_name)
    EditText etClientLastName;

    @BindView(R.id.et_client_middle_name)
    EditText etClientMiddleName;

    @BindView(R.id.et_client_mobile_no)
    EditText etClientMobileNo;

    @BindView(R.id.et_client_external_id)
    EditText etClientExternalId;

    @BindView(R.id.cb_client_active_status)
    CheckBox cbClientActiveStatus;

    @BindView(R.id.tv_submission_date)
    TextView tvSubmissionDate;

    @BindView(R.id.tv_dateofbirth)
    TextView tvDateOfBirth;

    @BindView(R.id.sp_offices)
    Spinner spOffices;

    @BindView(R.id.sp_gender)
    Spinner spGender;

    @BindView(R.id.sp_client_type)
    Spinner spClientType;

    @BindView(R.id.sp_staff)
    Spinner spStaff;

    @BindView(R.id.sp_client_classification)
    Spinner spClientClassification;

    @BindView(R.id.layout_submission)
    LinearLayout layout_submission;

    @Inject
    CreateNewClientPresenter createNewClientPresenter;

    View rootView;
    private boolean hasDataTables;
    private int officeId;
    private int clientTypeId;
    private int staffId;
    private int genderId;
    private int clientClassificationId;
    private Boolean result = true;
    private String submissionDateString;
    private String dateOfBirthString;
    private ClientsTemplate clientsTemplate;
    private List<Office> clientOffices;
    private List<Staff> clientStaff;
    private View mCurrentDateView;    // the view whose click opened the date picker

    private List<String> genderOptionsList;
    private List<String> clientClassificationList;
    private List<String> clientTypeList;
    private List<String> officeList;
    private List<String> staffList;

    private ArrayAdapter<String> genderOptionsAdapter;
    private ArrayAdapter<String> clientClassificationAdapter;
    private ArrayAdapter<String> clientTypeAdapter;
    private ArrayAdapter<String> officeAdapter;
    private ArrayAdapter<String> staffAdapter;

    public static CreateNewClientFragment newInstance() {
        CreateNewClientFragment createNewClientFragment = new CreateNewClientFragment();
        Bundle args = new Bundle();
        createNewClientFragment.setArguments(args);
        return createNewClientFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        genderOptionsList = new ArrayList<>();
        clientClassificationList = new ArrayList<>();
        clientTypeList = new ArrayList<>();
        officeList = new ArrayList<>();
        staffList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_client, null);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        ButterKnife.bind(this, rootView);
        createNewClientPresenter.attachView(this);

        showUserInterface();

        createNewClientPresenter.loadClientTemplate();

        return rootView;
    }

    @Override
    public void showUserInterface() {

        genderOptionsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, genderOptionsList);
        genderOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderOptionsAdapter);
        spGender.setOnItemSelectedListener(this);

        clientClassificationAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, clientClassificationList);
        clientClassificationAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClientClassification.setAdapter(clientClassificationAdapter);
        spClientClassification.setOnItemSelectedListener(this);

        clientTypeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, clientTypeList);
        clientTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClientType.setAdapter(clientTypeAdapter);
        spClientType.setOnItemSelectedListener(this);

        officeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, officeList);
        officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOffices.setAdapter(officeAdapter);
        spOffices.setOnItemSelectedListener(this);

        staffAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, staffList);
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStaff.setAdapter(staffAdapter);
        spStaff.setOnItemSelectedListener(this);

        datePickerSubmissionDate = MFDatePicker.newInsance(this);
        datePickerDateOfBirth = MFDatePicker.newInsance(this);
        tvSubmissionDate.setText(MFDatePicker.getDatePickedAsString());
        tvDateOfBirth.setText(MFDatePicker.getDatePickedAsString());
    }

    @OnClick(R.id.tv_submission_date)
    public void onClickTextViewSubmissionDate() {
        datePickerSubmissionDate.show(getActivity().getSupportFragmentManager(),
                FragmentConstants.DFRAG_DATE_PICKER);
        mCurrentDateView = tvSubmissionDate;
    }

    @OnClick(R.id.tv_dateofbirth)
    public void onClickTextViewDateOfBirth() {
        datePickerDateOfBirth.show(getActivity().getSupportFragmentManager(),
                FragmentConstants.DFRAG_DATE_PICKER);
        mCurrentDateView = tvDateOfBirth;
    }

    @OnClick(R.id.btn_submit)
    public void onClickSubmitButton() {

        submissionDateString = tvSubmissionDate.getText().toString();
        submissionDateString = DateHelper
                .getDateAsStringUsedForCollectionSheetPayload(submissionDateString)
                .replace("-", " ");
        dateOfBirthString = tvDateOfBirth.getText().toString();
        dateOfBirthString = DateHelper.getDateAsStringUsedForDateofBirth(dateOfBirthString)
                .replace("-", " ");

        ClientPayload clientPayload = new ClientPayload();

        //Mandatory Fields
        clientPayload.setFirstname(etClientFirstName.getEditableText().toString());
        clientPayload.setLastname(etClientLastName.getEditableText().toString());
        clientPayload.setOfficeId(officeId);

        //Optional Fields, we do not need to add any check because these fields carry some
        // default values
        clientPayload.setActive(cbClientActiveStatus.isChecked());
        clientPayload.setActivationDate(submissionDateString);
        clientPayload.setDateOfBirth(dateOfBirthString);

        //Optional Fields
        if (!TextUtils.isEmpty(etClientMiddleName.getEditableText().toString())) {
            clientPayload.setMiddlename(etClientMiddleName.getEditableText().toString());
        }

        if (PhoneNumberUtils.isGlobalPhoneNumber(etClientMobileNo.getEditableText().toString())) {
            clientPayload.setMobileNo(etClientMobileNo.getEditableText().toString());
        }

        if (!TextUtils.isEmpty(etClientExternalId.getEditableText().toString())) {
            clientPayload.setExternalId(etClientExternalId.getEditableText().toString());
        }

        if (!clientStaff.isEmpty()) {
            clientPayload.setStaffId(staffId);
        }

        if (!genderOptionsList.isEmpty()) {
            clientPayload.setGenderId(genderId);
        }

        if (!clientTypeList.isEmpty()) {
            clientPayload.setClientTypeId(clientTypeId);
        }

        if (!clientClassificationList.isEmpty()) {
            clientPayload.setClientClassificationId(clientClassificationId);
        }

        if (!isFirstNameValid()) {
            return;
        }
        if (!isMiddleNameValid()) {
            return;
        }
        if (isLastNameValid()) {
            if (hasDataTables) {
                DataTableListFragment fragment = DataTableListFragment.newInstance(
                        clientsTemplate.getDataTables(),
                        clientPayload, Constants.CREATE_CLIENT);

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST);
                fragmentTransaction.replace(R.id.container, fragment).commit();
            } else {
                clientPayload.setDatatables(null);
                createNewClientPresenter.createClient(clientPayload);
            }
        }
    }

    @OnCheckedChanged(R.id.cb_client_active_status)
    public void onClickActiveCheckBox() {
        layout_submission.setVisibility(cbClientActiveStatus.isChecked()
                ? View.VISIBLE : View.GONE);
    }

    public void onDatePicked(String date) {
        if (mCurrentDateView != null && mCurrentDateView == tvSubmissionDate) {
            tvSubmissionDate.setText(date);
        } else if (mCurrentDateView != null && mCurrentDateView == tvDateOfBirth) {
            tvDateOfBirth.setText(date);
        }
    }

    @Override
    public void showClientTemplate(ClientsTemplate clientsTemplate) {
        this.clientsTemplate = clientsTemplate;

        if (!clientsTemplate.getDataTables().isEmpty()) {
            hasDataTables = true;
        }

        genderOptionsList.addAll(
                createNewClientPresenter.filterOptions(clientsTemplate.getGenderOptions()));
        genderOptionsAdapter.notifyDataSetChanged();

        clientTypeList.addAll(
                createNewClientPresenter.filterOptions(clientsTemplate.getClientTypeOptions()));
        clientTypeAdapter.notifyDataSetChanged();

        clientClassificationList.addAll(createNewClientPresenter
                .filterOptions(clientsTemplate.getClientClassificationOptions()));
        clientClassificationAdapter.notifyDataSetChanged();
    }

    @Override
    public void showOffices(List<Office> offices) {
        clientOffices = offices;
        officeList.addAll(createNewClientPresenter.filterOffices(offices));
        Collections.sort(officeList);
        officeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showStaffInOffices(List<Staff> staffs) {
        if (staffs.isEmpty()) {
            showMessage(R.string.no_staff_associated_with_office);
        }
        clientStaff = staffs;
        staffList.clear();
        staffList.addAll(createNewClientPresenter.filterStaff(staffs));
        staffAdapter.notifyDataSetChanged();
    }

    @Override
    public void showClientCreatedSuccessfully(int message) {
        Toaster.show(rootView, message);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showMessage(int message) {
        Toaster.show(rootView, message);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean show) {
        showProgress(show);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        createNewClientPresenter.detachView();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_offices:
                officeId = clientOffices.get(position).getId();
                createNewClientPresenter.loadStaffInOffices(officeId);
                break;
            case R.id.sp_gender:
                genderId = clientsTemplate.getGenderOptions().get(position).getId();
                break;
            case R.id.sp_client_type:
                clientTypeId = clientsTemplate.getClientTypeOptions().get(position).getId();
                break;
            case R.id.sp_staff:
                staffId = clientStaff.get(position).getId();
                break;
            case R.id.sp_client_classification:
                clientClassificationId = clientsTemplate.getClientClassificationOptions()
                        .get(position).getId();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean isFirstNameValid() {
        result = true;
        try {
            if (TextUtils.isEmpty(etClientFirstName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.first_name),
                        getResources().getString(R.string.error_cannot_be_empty));
            }
            if (!ValidationUtil.isNameValid(etClientFirstName.getEditableText().toString())) {
                throw new InvalidTextInputException(getResources().getString(R.string.first_name),
                        getResources().getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS);
            }
        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }
        return result;
    }

    public boolean isMiddleNameValid() {
        result = true;
        try {
            if (!TextUtils.isEmpty(etClientMiddleName.getEditableText().toString())
                    && !ValidationUtil.isNameValid(etClientMiddleName.getEditableText()
                    .toString())) {
                throw new InvalidTextInputException(
                        getResources().getString(R.string.middle_name),
                        getResources().getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS);
            }
        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }
        return result;
    }

    public boolean isLastNameValid() {
        result = true;
        try {
            if (TextUtils.isEmpty(etClientLastName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.last_name),
                        getResources().getString(R.string.error_cannot_be_empty));
            }

            if (!ValidationUtil.isNameValid(etClientLastName.getEditableText().toString())) {
                throw new InvalidTextInputException(getResources().getString(R.string.last_name),
                        getResources().getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS);
            }
        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }
        return result;
    }
}
