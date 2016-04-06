/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

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

import com.mifos.App;
import com.mifos.api.model.ClientPayload;
import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.objects.templates.clients.Options;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

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


public class CreateNewClientFragment extends MifosBaseFragment implements MFDatePicker.OnDatePickListener {

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
    int clientTypeId;
    int staffId;
    int genderId;
    int clientClassificationId;
    Boolean result = true;
    View rootView;
    String dateString;
    String dateofbirthstring;
    public DialogFragment mfDatePicker;
    public DialogFragment newDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> genderNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> clientTypeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> clientClassificationNameIdHashMap = new HashMap<String, Integer>();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_client, null);
        ButterKnife.inject(this, rootView);

        inflateOfficeSpinner();
        inflateSubmissionDate();
        inflateDateofBirth();
	    getClientTemplate();

        //client active checkbox onCheckedListener
        cb_clientActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                tv_submissionDate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
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
	    final ArrayList<String> genderNames = FilterListObject(clientstemplate.getGenderOptions());
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
                    Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void inflateClientClassificationOptions() {
	    final ArrayList<String> ClientClassificationNames = FilterListObject(clientstemplate
			    .getClientClassificationOptions());
	    ArrayAdapter<String> ClientClassificationAdapter = new ArrayAdapter<String>(getActivity(),
			    android.R.layout.simple_spinner_item, ClientClassificationNames);
	    ClientClassificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spClientClassification.setAdapter(ClientClassificationAdapter);
	    spClientClassification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

		    @Override
		    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			    clientClassificationId = clientstemplate.getClientClassificationOptions()
					    .get(i).getId();
			    Log.d("clientClassificationId" + ClientClassificationNames.get(i), String.valueOf(clientClassificationId));
			    if (clientClassificationId != -1) {

			    }
			    else {

				    Toast.makeText(getActivity(), getString(R.string.error_select_client_type), Toast.LENGTH_SHORT).show();

			    }

		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {

		    }
	    });
    }

    private void inflateClientTypeOptions() {
	    final ArrayList<String> ClientTypeNames = FilterListObject(clientstemplate
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
                    Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

	private void getClientTemplate(){
		showProgress();
		App.apiManager.getClientTemplate(new Callback<ClientsTemplate>() {
            @Override
            public void success(ClientsTemplate clientsTemplate, Response response) {

                if (response.getStatus() == 200) {
                    clientstemplate = clientsTemplate;
                    inflateGenderSpinner();
                    inflateClientTypeOptions();
                    inflateClientClassificationOptions();
                }
                hideProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
            }
        });
	}

    //inflating office list spinner
    private void inflateOfficeSpinner() {
        showProgress();
        App.apiManager.getOffices(new Callback<List<Office>>() {
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
                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> adapterView) {

                                              }
                                          });
                                          hideProgress();
                                      }

                                      @Override
                                      public void failure(RetrofitError error) {
                                          hideProgress();
                                      }
                                  }
        );
    }

    private void initiateClientCreation(ClientPayload clientPayload) {
        //Date validation : check for date less than or equal to current date
        if (!isValidDate()) {
            Toaster.show(rootView, "Date cannot be in future");
            return;
        }

        if (!isValidFirstName()) {
            return;
        }
        if (!isValidMiddleName()) {
            return;
        }
        if (!isValidLastName()) {
            return;
        }
        if (!isValidMobileNo()) {
            return;
        }
       // Date validation : check for date less than or equal to current date
        if (!isValidDate()) {
            Toast.makeText(getActivity(), "Date cannot be in future", Toast.LENGTH_LONG).show();
        }
        if (!isValidDateofBirth()) {
            Toast.makeText(getActivity(), "Date cannot be in future", Toast.LENGTH_LONG).show();
       }
         else {
            showProgress();
            App.apiManager.createClient(clientPayload, new Callback<Client>() {
                @Override
                public void success(Client client, Response response) {
                    hideProgress();
                    Toaster.show(rootView, "Client created successfully");
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgress();
                    Toaster.show(rootView, "Error creating client");
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
                newDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
                mCurrentDateView = tv_dateofbirth;
            }

        });


    }

    public void onDatePicked(String date) {
        if (mCurrentDateView != null && mCurrentDateView == tv_submissionDate)
            tv_submissionDate.setText(date);
        else if (mCurrentDateView != null && mCurrentDateView == tv_dateofbirth)
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
            if (!et_clientMiddleName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.middle_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
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

    public boolean isValidDate() {

        List<Integer> date1 = DateHelper.getCurrentDateAsListOfIntegers();
        List<Integer> date2 = DateHelper.getDateList(tv_submissionDate.getText().toString(), "-");

        Collections.reverse(date2);
        int i = DateHelper.dateComparator(date1, date2);
        if (i == -1) {
            result = false;
        }
        return result;
    }

    public boolean isValidDateofBirth() {

        List<Integer> date1 = new ArrayList<>();
        List<Integer> date2 = new ArrayList<>();
        date1 = DateHelper.getCurrentDateAsListOfIntegers();
        date2 = DateHelper.getDateList(tv_dateofbirth.getText().toString(), "-");

        Collections.reverse(date2);
        int i = DateHelper.dateComparator(date1, date2);
        if (i == -1) {
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

	public ArrayList<String> FilterListObject(List<Options> optionsList){

		ArrayList<String> optionsNameList = new ArrayList<>();
		for(Options options : optionsList){
			optionsNameList.add(options.getName());
		}
		return optionsNameList;
	}

}
