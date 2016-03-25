/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

/**
 * Created by nellyk on 1/22/2016.
 */

import android.app.Activity;
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
import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.services.data.GroupPayload;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CreateNewGroupFragment extends MifosBaseFragment implements MFDatePicker.OnDatePickListener {


    private static final String TAG = "CreateNewGroup";
    @InjectView(R.id.et_group_name)
    EditText et_groupName;
    @InjectView(R.id.et_group_external_id)
    EditText et_groupexternalId;
    @InjectView(R.id.cb_group_active_status)
    CheckBox cb_groupActiveStatus;
    @InjectView(R.id.tv_group_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.tv_group_activationDate)
    TextView tv_activationDate;
    @InjectView(R.id.sp_group_offices)
    Spinner sp_offices;
    @InjectView(R.id.bt_submit)
    Button bt_submit;
    String activationdateString;
    int officeId;
    Boolean result = true;
    View rootView;
    String dateofsubmissionstring;
    SafeUIBlockingUtility safeUIBlockingUtility;
    private DialogFragment mfDatePicker;
    private DialogFragment newDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();


    public CreateNewGroupFragment() {
        // Required empty public constructor
    }

    public static CreateNewGroupFragment newInstance() {
        CreateNewGroupFragment createNewGroupFragment = new CreateNewGroupFragment();
        return createNewGroupFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_group, null);
        ButterKnife.inject(this, rootView);
        inflateOfficeSpinner();
        inflateSubmissionDate();
        inflateActivationDate();


        //client active checkbox onCheckedListener
        cb_groupActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    tv_activationDate.setVisibility(View.VISIBLE);
                else
                    tv_activationDate.setVisibility(View.GONE);
            }
        });

        activationdateString = tv_activationDate.getText().toString();
        activationdateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(activationdateString).replace("-", " ");
        dateofsubmissionstring = tv_submissionDate.getText().toString();
        dateofsubmissionstring = DateHelper.getDateAsStringUsedForDateofBirth(dateofsubmissionstring).replace("-", " ");

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GroupPayload groupPayload = new GroupPayload();

                groupPayload.setName(et_groupName.getEditableText().toString());
                groupPayload.setExternalId(et_groupexternalId.getEditableText().toString());
                groupPayload.setActive(cb_groupActiveStatus.isChecked());
                groupPayload.setActivationDate(activationdateString);
                groupPayload.setSubmissionDate(dateofsubmissionstring);
                groupPayload.setOfficeId(officeId);
                groupPayload.setDateFormat("dd MMMM yyyy");
                groupPayload.setLocale("en");

                initiateGroupCreation(groupPayload);

            }
        });

        return rootView;
    }

    //inflating office list spinner
    private void inflateOfficeSpinner() {
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        safeUIBlockingUtility.safelyBlockUI();
        App.apiManager.getOffices(new Callback<List<Office>>() {

            @Override
            public void success(List<Office> offices, Response response) {
                final ArrayList<String> officeList = new ArrayList<String>();

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

            }
        });

    }

    private void initiateGroupCreation(GroupPayload groupPayload) {
        //TextField validations

        if (!isValidGroupName()) {
            return;
        }
        App.apiManager.createGroup(groupPayload, new Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                safeUIBlockingUtility.safelyUnBlockUI();
                Toast.makeText(getActivity(), "Group created successfully", Toast.LENGTH_LONG).show();

            }

            @Override
            public void failure(RetrofitError error) {
                safeUIBlockingUtility.safelyUnBlockUI();
                Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
            }
        });
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

    public void inflateActivationDate() {
        newDatePicker = MFDatePicker.newInsance(this);

        tv_activationDate.setText(MFDatePicker.getDatePickedAsString());

        tv_activationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }

        });


    }

    public void onDatePicked(String date) {
        tv_submissionDate.setText(date);
        tv_activationDate.setText(date);

    }

    public boolean isValidGroupName() {
        try {
            if (TextUtils.isEmpty(et_groupName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.group_name), getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_groupName.getEditableText().toString().trim().length() < 4 && et_groupName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.group_name), 4);
            }
            if (!et_groupName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.group_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}