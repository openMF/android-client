/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online.createnewclient;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.api.model.ClientPayload;
import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.objects.templates.clients.Options;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreateNewClientFragment extends ProgressableFragment
        implements MFDatePicker.OnDatePickListener, CreateNewClientMvpView {

    private final String LOG_TAG = getClass().getSimpleName();

    public DialogFragment mfDatePicker, newDatePicker;

    @BindView(R.id.et_client_first_name)
    EditText et_clientFirstName;

    @BindView(R.id.et_client_last_name)
    EditText et_clientLastName;

    @BindView(R.id.et_client_middle_name)
    EditText et_clientMiddleName;

    @BindView(R.id.et_client_mobile_no)
    EditText et_clientMobileNo;

    @BindView(R.id.et_client_external_id)
    EditText et_clientexternalId;

    @BindView(R.id.cb_client_active_status)
    CheckBox cb_clientActiveStatus;

    @BindView(R.id.tv_submission_date)
    TextView tv_submissionDate;

    @BindView(R.id.tv_dateofbirth)
    TextView tv_dateofbirth;

    @BindView(R.id.sp_offices)
    Spinner sp_offices;

    @BindView(R.id.sp_gender)
    Spinner spGender;

    @BindView(R.id.sp_client_type)
    Spinner spClientType;

    @BindView(R.id.sp_staff)
    Spinner sp_staff;

    @BindView(R.id.sp_client_classification)
    Spinner spClientClassification;

    @BindView(R.id.bt_submit)
    Button bt_submit;

    @Inject
    CreateNewClientPresenter mCreateNewClientPresenter;

    int officeId;
    int clientTypeId;
    int staffId;
    int genderId;
    int clientClassificationId;
    Boolean result = true;
    View rootView;
    String dateString;
    String dateofbirthstring;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> genderNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> clientTypeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> clientClassificationNameIdHashMap = new HashMap<String,
            Integer>();
    private ClientsTemplate clientstemplate = new ClientsTemplate();
    private View mCurrentDateView;    // the view whose click opened the date picker

    public static CreateNewClientFragment newInstance() {
        CreateNewClientFragment createNewClientFragment = new CreateNewClientFragment();
        return createNewClientFragment;
    }

    public static boolean isValidMsisdn(String msisdn) {
        if (msisdn == null || msisdn.trim().isEmpty()) {
            return false;
        }
        String expression = "^[+]?\\d{10,13}$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(expression);
        matcher = pattern.matcher(msisdn);
        return matcher.matches();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_client, null);

        ButterKnife.bind(this, rootView);
        mCreateNewClientPresenter.attachView(this);

        inflateOfficeSpinner();
        inflateSubmissionDate();
        inflateDateofBirth();
        getClientTemplate();

        //client active checkbox onCheckedListener
        cb_clientActiveStatus.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                tv_submissionDate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        dateString = tv_submissionDate.getText().toString();
        dateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(dateString).replace
                ("-", " ");
        dateofbirthstring = tv_dateofbirth.getText().toString();
        dateofbirthstring = DateHelper.getDateAsStringUsedForDateofBirth(dateofbirthstring)
                .replace("-", " ");
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientPayload clientPayload = new ClientPayload();
                clientPayload.setFirstname(et_clientFirstName.getEditableText().toString());
                clientPayload.setMiddlename(et_clientMiddleName.getEditableText().toString());
                clientPayload.setMobileNo(et_clientMobileNo.getEditableText().toString());
                clientPayload.setExternalId(et_clientexternalId.getEditableText().toString());
                clientPayload.setLastname(et_clientLastName.getEditableText().toString());
                clientPayload.setActive(cb_clientActiveStatus.isChecked());
                clientPayload.setActivationDate(dateString);
                clientPayload.setDateOfBirth(dateofbirthstring);
                clientPayload.setOfficeId(officeId);
                clientPayload.setStaffId(staffId);
                clientPayload.setGenderId(genderId);
                clientPayload.setClientTypeId(clientTypeId);
                clientPayload.setClientClassificationId(clientClassificationId);

                initiateClientCreation(clientPayload);
            }
        });
        return rootView;
    }

    private void inflateGenderSpinner() {
        final ArrayList<String> genderNames = filterListObject(clientstemplate.getGenderOptions());
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, genderNames);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderId = clientstemplate.getGenderOptions().get(i).getId();
                Log.d("genderId " + genderNames.get(i), String.valueOf(genderId));
                if (genderId != -1) {


                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast
                            .LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void inflateClientClassificationOptions() {
        final ArrayList<String> ClientClassificationNames = filterListObject(clientstemplate
                .getClientClassificationOptions());
        ArrayAdapter<String> ClientClassificationAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, ClientClassificationNames);
        ClientClassificationAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spClientClassification.setAdapter(ClientClassificationAdapter);
        spClientClassification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clientClassificationId = clientstemplate.getClientClassificationOptions()
                        .get(i).getId();
                Log.d("clientClassificationId" + ClientClassificationNames.get(i), String.valueOf
                        (clientClassificationId));
                if (clientClassificationId != -1) {

                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_client_type),
                            Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void inflateClientTypeOptions() {
        final ArrayList<String> ClientTypeNames = filterListObject(clientstemplate
                .getClientTypeOptions());
        final ArrayAdapter<String> clientTypeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, ClientTypeNames);
        clientTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClientType.setAdapter(clientTypeAdapter);
        spClientType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clientTypeId = clientstemplate.getClientTypeOptions().get(i).getId();
                Log.d("clientTypeId " + ClientTypeNames.get(i), String.valueOf(clientTypeId));
                if (clientTypeId != -1) {
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast
                            .LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getClientTemplate() {
        mCreateNewClientPresenter.loadClientTemplate();
    }

    //inflating office list spinner
    private void inflateOfficeSpinner() {
        mCreateNewClientPresenter.loadOffices();
    }

    public void inflateStaffSpinner(final int officeId) {
        mCreateNewClientPresenter.loadStaffInOffices(officeId);
    }

    private void initiateClientCreation(ClientPayload clientPayload) {

        if (!isValidFirstName()) {
            return;
        }
        if (!isValidMiddleName()) {
            return;
        }
        if (isValidLastName()) {

            mCreateNewClientPresenter.createClient(clientPayload);
        }
    }


    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_submissionDate.setText(MFDatePicker.getDatePickedAsString());
        tv_submissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
                mCurrentDateView = tv_submissionDate;
            }
        });

    }

    public void inflateDateofBirth() {
        newDatePicker = MFDatePicker.newInsance(this);

        tv_dateofbirth.setText(MFDatePicker.getDatePickedAsString());

        tv_dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
                mCurrentDateView = tv_dateofbirth;
            }

        });


    }

    public void onDatePicked(String date) {
        if (mCurrentDateView != null && mCurrentDateView == tv_submissionDate) {
            tv_submissionDate.setText(date);
        } else if (mCurrentDateView != null && mCurrentDateView == tv_dateofbirth) {
            tv_dateofbirth.setText(date);
        }

    }

    public boolean isValidFirstName() {
        try {
            if (TextUtils.isEmpty(et_clientFirstName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.first_name),
                        getResources().getString(R.string.error_cannot_be_empty));
            }
            if (et_clientFirstName.getEditableText().toString().trim().length() < 4 &&
                    et_clientFirstName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.first_name), 4);
            }
            if (!et_clientFirstName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.first_name)
                        , getResources().getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS);
            }
        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (ShortOfLengthException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }
        return result;
    }

    public boolean isValidMiddleName() {
        try {
            if (!et_clientMiddleName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string
                        .middle_name), getResources().getString(R.string
                        .error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
            }
        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }

        return result;
    }

    public boolean isValidLastName() {
        result = true;
        try {
            if (TextUtils.isEmpty(et_clientLastName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.last_name),
                        getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_clientLastName.getEditableText().toString().trim().length() < 4 &&
                    et_clientFirstName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.last_name), 4);
            }

            if (!et_clientLastName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.last_name),
                        getResources().getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS);
            }

        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (ShortOfLengthException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }

        return result;
    }


    public boolean isValidMobileNo() {
        if (!isValidMsisdn(et_clientMobileNo.getEditableText().toString())) {
            et_clientMobileNo.setError(getString(R.string.phone_number_not_valid));

        }
        return result;
    }

    public ArrayList<String> filterListObject(List<Options> optionsList) {

        ArrayList<String> optionsNameList = new ArrayList<>();
        for (Options options : optionsList) {
            optionsNameList.add(options.getName());
        }
        return optionsNameList;
    }

    @Override
    public void showClientTemplate(ClientsTemplate clientsTemplate) {

        clientstemplate = clientsTemplate;
        inflateGenderSpinner();
        inflateClientTypeOptions();
        inflateClientClassificationOptions();
    }

    @Override
    public void showOffices(List<Office> offices) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        final List<String> officeList = new ArrayList<String>();

        for (Office office : offices) {
            officeList.add(office.getName());
            officeNameIdHashMap.put(office.getName(), office.getId());
        }
        ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, officeList);
        officeAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_offices.setAdapter(officeAdapter);
        sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long
                    l) {
                officeId = officeNameIdHashMap.get(officeList.get(i));
                Log.d("officeId " + officeList.get(i), String.valueOf(officeId));
                if (officeId != -1) {

                    inflateStaffSpinner(officeId);


                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_office)
                            , Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showStaffInOffices(List<Staff> staffs) {
        final List<String> staffNames = new ArrayList<String>();
        for (Staff staff : staffs) {
            staffNames.add(staff.getDisplayName());
            staffNameIdHashMap.put(staff.getDisplayName(), staff.getId());
        }
        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, staffNames);
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_staff.setAdapter(staffAdapter);
        sp_staff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {

                staffId = staffNameIdHashMap.get(staffNames.get(position));
                Log.d("staffId " + staffNames.get(position), String.valueOf(staffId));
                if (staffId != -1) {

                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_select_staff),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    @Override
    public void showClientCreatedSuccessfully(Client client) {
        Toaster.show(rootView, "Client created successfully");
    }

    @Override
    public void showFetchingError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCreateNewClientPresenter.detachView();
    }
}
