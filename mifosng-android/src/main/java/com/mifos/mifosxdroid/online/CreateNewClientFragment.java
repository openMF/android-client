/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ClientNameListAdapter;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.ClientClassificationOptions;
import com.mifos.objects.organisation.ClientTypeOptions;
import com.mifos.objects.organisation.GenderOptions;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.data.ClientPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CreateNewClientFragment extends Fragment implements MFDatePicker.OnDatePickListener {


    private static final String TAG = "CreateNewClient";
    @InjectView(R.id.et_client_first_name)
    EditText et_clientFirstName;
    @InjectView(R.id.et_client_last_name)
    EditText et_clientLastName;
    @InjectView(R.id.et_client_middle_name)
    EditText et_clientMiddleName;
    @InjectView(R.id.et_client_mobile_no)
    EditText et_clientMobileNo;
    @InjectView(R.id.et_client_external_id)
    EditText et_clientexternalId;
    @InjectView(R.id.cb_client_active_status)
    CheckBox cb_clientActiveStatus;
    @InjectView(R.id.tv_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.tv_dateofbirth)
    TextView tv_dateofbirth;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.sp_gender)
    Spinner spGender;
    @InjectView(R.id.sp_client_type)
    Spinner spClientType;
    @InjectView(R.id.sp_staff)
    Spinner sp_staff;
    @InjectView(R.id.sp_client_classification)
    Spinner spClientClassification;
    @InjectView(R.id.bt_submit)
    Button bt_submit;

    int officeId;
    int genderId;
    int staffId;
    Boolean result = true;
    View rootView;
    String dateString;
    String dateofbirthstring;
    SafeUIBlockingUtility safeUIBlockingUtility;
    private DialogFragment mfDatePicker;
    private DialogFragment newDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> genderNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> clientTypeNameIdHashMap = new HashMap<String, Integer>();
    public CreateNewClientFragment() {
        // Required empty public constructor
    }

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity().getActionBar() != null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_create_new_client, null);
        ButterKnife.inject(this, rootView);
        inflateOfficeSpinner();
        inflateSubmissionDate();
        inflateDateofBirth();
        inflateGenderSpinner();
        inflateClientTypeOptions();
        inflateClientClassificationOptions();


        //client active checkbox onCheckedListener
        cb_clientActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    tv_submissionDate.setVisibility(View.VISIBLE);
                else
                    tv_submissionDate.setVisibility(View.GONE);
            }
        });

        dateString = tv_submissionDate.getText().toString();
        dateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(dateString).replace("-", " ");
        dateofbirthstring = tv_dateofbirth.getText().toString();
        dateofbirthstring = DateHelper.getDateAsStringUsedForDateofBirth(dateofbirthstring).replace("-", " ");
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
                clientPayload.setGenderId(genderId);


                initiateClientCreation(clientPayload);

            }
        });

        return rootView;
    }

    private void inflateGenderSpinner() {
        ((MifosApplication) getActivity().getApplicationContext()).api.clientService.getClientTemplate(new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                Log.d(TAG, "");
               // you can use this array to find the gender ID based on name
                final ArrayList<GenderOptions> genderOption = new ArrayList<GenderOptions>();
                // you can use this array to populate your spinner
                ArrayList<String> genderNames = new ArrayList<String>();
                //Try to get response body
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                try {
                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }


                    JSONObject obj = new JSONObject(sb.toString());
                    if(obj.has("genderOptions")){
                        JSONArray genderOptions=obj.getJSONArray("genderOptions");
                        for(int i=0;i<genderOptions.length();i++){
                            JSONObject genderObject = genderOptions.getJSONObject(i);

                            GenderOptions gender = new GenderOptions();
                           /* gender.get("id");
                            gender.get("name");*/
                            gender.setId(genderObject.optInt("id"));
                            gender.setName(genderObject.optString("name"));

                            genderOption.add(gender);
                            genderNames.add(genderObject.optString("name"));
                            genderNameIdHashMap.put(gender.getName(), gender.getId());
                        }


                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG,"",e);
                }
                ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, genderNames);
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spGender.setAdapter(genderAdapter);
               /* spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                         genderId = genderNameIdHashMap.get(genderOption.get(i));
                        Log.d("genderId " + genderOption.get(i), String.valueOf(genderId));
                        if (genderId != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_gender), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });*/

                safeUIBlockingUtility.safelyUnBlockUI();

            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
    private void inflateClientClassificationOptions() {
        ((MifosApplication) getActivity().getApplicationContext()).api.clientService.getClientTemplate(new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                Log.d(TAG, "");
                // you can use this array to find the gender ID based on name
                final List<ClientClassificationOptions> clientClassificationOptions = new ArrayList<ClientClassificationOptions>();
                // you can use this array to populate your spinner
                ArrayList<String> ClientClassificationNames = new ArrayList<String>();
                //Try to get response body
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                try {

                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }


                    JSONObject obj = new JSONObject(sb.toString());
                    if(obj.has("clientClassificationOptions")){
                        JSONArray clientClassification=obj.getJSONArray("clientClassificationOptions");
                        for(int i=0;i<clientClassification.length();i++){
                            JSONObject clientClassificationObject = clientClassification.getJSONObject(i);

                            ClientClassificationOptions clientClassifications = new ClientClassificationOptions();
                           /* gender.get("id");
                            gender.get("name");*/
                            clientClassifications.setId(clientClassificationObject.optInt("id"));
                            clientClassifications.setName(clientClassificationObject.optString("name"));

                            clientClassificationOptions.add(clientClassifications);
                            ClientClassificationNames.add(clientClassificationObject.optString("name"));
                            clientTypeNameIdHashMap.put(clientClassifications.getName(), clientClassifications.getId());
                        }


                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG,"",e);
                }
                ArrayAdapter<String> ClientClassificationAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, ClientClassificationNames);
                ClientClassificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spClientClassification.setAdapter(ClientClassificationAdapter);
               /* spClientType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        clientTypeId = clientTypeNameIdHashMap.get(clienttypeoptions.get(i));
                        Log.d("clientTypeId " + clienttypeoptions.get(i), String.valueOf(clientTypeId));
                        if (clientTypeId != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_client_type), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                safeUIBlockingUtility.safelyUnBlockUI();
*/
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
    private void inflateClientTypeOptions() {
        ((MifosApplication) getActivity().getApplicationContext()).api.clientService.getClientTemplate(new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                Log.d(TAG, "");
                // you can use this array to find the gender ID based on name
                final List<ClientTypeOptions> clienttypeoptions = new ArrayList<ClientTypeOptions>();
                // you can use this array to populate your spinner
                ArrayList<String> ClientTypeNames = new ArrayList<String>();
                //Try to get response body
                BufferedReader reader = null;
                StringBuilder sb = new StringBuilder();
                try {

                    reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }


                    JSONObject obj = new JSONObject(sb.toString());
                    if(obj.has("clientTypeOptions")){
                        JSONArray clientTypeOptions=obj.getJSONArray("clientTypeOptions");
                        for(int i=0;i<clientTypeOptions.length();i++){
                            JSONObject clientTypeObject = clientTypeOptions.getJSONObject(i);

                            ClientTypeOptions clienttype = new ClientTypeOptions();
                           /* gender.get("id");
                            gender.get("name");*/
                            clienttype.setId(clientTypeObject.optInt("id"));
                            clienttype.setName(clientTypeObject.optString("name"));

                            clienttypeoptions.add(clienttype);
                            ClientTypeNames.add(clientTypeObject.optString("name"));
                            clientTypeNameIdHashMap.put(clienttype.getName(), clienttype.getId());
                        }


                    }
                    String stringResult = sb.toString();
                } catch (Exception e) {
                    Log.e(TAG,"",e);
                }
                ArrayAdapter<String> clientTypeAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, ClientTypeNames);
                clientTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spClientType.setAdapter(clientTypeAdapter);
               /* spClientType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        clientTypeId = clientTypeNameIdHashMap.get(clienttypeoptions.get(i));
                        Log.d("clientTypeId " + clienttypeoptions.get(i), String.valueOf(clientTypeId));
                        if (clientTypeId != -1) {


                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_client_type), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                safeUIBlockingUtility.safelyUnBlockUI();
*/
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
    //inflating office list spinner
    private void inflateOfficeSpinner() {
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        MifosApplication.getApi().officeService.getAllOffices(new Callback<List<Office>>() {

            @Override
            public void success(List<Office> offices, Response response) {
                final List<String> officeList = new ArrayList<String>();

                for (Office office : offices) {
                    officeList.add(office.getName());
                    officeNameIdHashMap.put(office.getName(), office.getId());
                }
                ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, officeList);
                officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_offices.setAdapter(officeAdapter);
                sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        officeId = officeNameIdHashMap.get(officeList.get(i));
                        Log.d("officeId " + officeList.get(i), String.valueOf(officeId));
                        if (officeId != -1) {

                            inflateStaffSpinner(officeId);


                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });

    }
    public void inflateStaffSpinner(final int officeId) {


        ((MifosApplication) getActivity().getApplicationContext()).api.staffService.getStaffForOffice(officeId, new Callback<List<Staff>>() {
            @Override
            public void success(List<Staff> staffs, Response response) {

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
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        staffId = staffNameIdHashMap.get(staffNames.get(position));
                        Log.d("staffId " + staffNames.get(position), String.valueOf(staffId));

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }

                });
                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError error) {
                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });
    }
    private void initiateClientCreation(ClientPayload clientPayload) {

        //TextField validations

        if (!isValidFirstName()) {
            return;
        }
        if (!isValidMiddleName()) {
            return;
        }
        if (!isValidLastName()) {
            return;
        }

        else {

                safeUIBlockingUtility.safelyBlockUI();

            MifosApplication.getApi().clientService.createClient(clientPayload, new Callback<Client>() {
                @Override
                public void success(Client client, Response response) {
                    safeUIBlockingUtility.safelyUnBlockUI();
                    Toast.makeText(getActivity(), "Client created successfully", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        safeUIBlockingUtility.safelyUnBlockUI();
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }


    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tv_submissionDate.setText(MFDatePicker.getDatePickedAsString());

        tv_submissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }
        });

    }

    public void inflateDateofBirth() {
        newDatePicker = MFDatePicker.newInsance(this);

        tv_dateofbirth.setText(MFDatePicker.getDatePickedAsString());

        tv_dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }

        });


    }

    public void onDatePicked(String date) {
        tv_submissionDate.setText(date);
        tv_dateofbirth.setText(date);

    }

    public boolean isValidFirstName() {
        try {
            if (TextUtils.isEmpty(et_clientFirstName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.first_name), getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_clientFirstName.getEditableText().toString().trim().length() < 4 && et_clientFirstName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.first_name), 4);
            }
            if (!et_clientFirstName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.first_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
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
            if (TextUtils.isEmpty(et_clientMiddleName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.middle_name), getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_clientMiddleName.getEditableText().toString().trim().length() < 4 && et_clientMiddleName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.middle_name), 4);
            }
            if (!et_clientMiddleName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.middle_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
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

    public boolean isValidLastName() {
        result = true;
        try {
            if (TextUtils.isEmpty(et_clientLastName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.last_name), getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_clientLastName.getEditableText().toString().trim().length() < 4 && et_clientFirstName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.last_name), 4);
            }

            if (!et_clientLastName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.last_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
