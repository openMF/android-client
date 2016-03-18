/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.createnewgroupfragment;

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
import com.mifos.api.DataManager;
import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupCreationResponse;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.data.GroupPayload;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CreateNewGroupFragment extends MifosBaseFragment implements MFDatePicker.OnDatePickListener,CreateNewGroupMvpView{


    private static final String TAG = "CreateNewGroup";
    @InjectView(R.id.et_group_name)
    EditText et_groupName;
    @InjectView(R.id.et_group_external_id)
    EditText et_groupexternalId;
    @InjectView(R.id.cb_group_active_status)
    CheckBox cb_groupActiveStatus;
    @InjectView(R.id.tv_group_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.sp_staff)
    Spinner sp_staff;
    @InjectView(R.id.bt_submit)
    Button bt_submit;

    int officeId;
    int staffId;
    Boolean result = true;
    View rootView;
    String dateString;
    String dateofsubmissionstring;
    private DialogFragment mfDatePicker;
    private DialogFragment newDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private DataManager dataManager;
    private CreateNewGroupPresenter mCreateNewGroupPresenter;
    private int mCounterProgressBarCount = 0;


    public static CreateNewGroupFragment newInstance() {
        CreateNewGroupFragment createNewGroupFragment = new CreateNewGroupFragment();
        return createNewGroupFragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_group, null);
        ButterKnife.inject(this, rootView);
        dataManager = new DataManager();
        mCreateNewGroupPresenter = new CreateNewGroupPresenter(dataManager);
        mCreateNewGroupPresenter.attachView(this);
        showProgress();
        inflateOfficeSpinner();
        inflateSubmissionDate();
        inflateDateofBirth();


        //client active checkbox onCheckedListener
        cb_groupActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        dateofsubmissionstring = tv_submissionDate.getText().toString();
        dateofsubmissionstring = DateHelper.getDateAsStringUsedForDateofBirth(dateofsubmissionstring).replace("-", " ");

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GroupPayload groupPayload = new GroupPayload();

                groupPayload.setGroupName(et_groupName.getEditableText().toString());
                groupPayload.setExternalId(et_groupexternalId.getEditableText().toString());
                groupPayload.setActive(cb_groupActiveStatus.isChecked());
                groupPayload.setActivationDate(dateString);
                groupPayload.setSubmissionDate(dateofsubmissionstring);
                groupPayload.setOfficeId(officeId);
                groupPayload.setStaffId(staffId);

                showProgress();
                initiateGroupCreation(groupPayload);

            }
        });

        return rootView;
    }
    //inflating office list spinner
    private void inflateOfficeSpinner() {
        mCreateNewGroupPresenter.loadOfficeList();

    }

    public void inflateStaffSpinner(int officeId) {
       mCreateNewGroupPresenter.loadStaffInOfficeList(officeId);
    }


    private void initiateGroupCreation(GroupPayload groupPayload) {
        //TextField validations

        if (!isValidGroupName()) {
            return;
        }
        else {
            mCreateNewGroupPresenter.creategroup(groupPayload);
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

        tv_submissionDate.setText(MFDatePicker.getDatePickedAsString());

        tv_submissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }

        });


    }

    public void onDatePicked(String date) {
        tv_submissionDate.setText(date);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCreateNewGroupPresenter.detachView();
    }

    @Override
    public void showOfficeList(List<Office> offices) {
        getProgressBarCount();
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

                    inflateStaffSpinner(officeId);


                } else {

                    Toast.makeText(getActivity(), getString(R.string.error_select_office), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void ResponseError(String s) {
        getProgressBarCount();
        Toaster.show(rootView, s);
    }

    @Override
    public void showStaffOfficeList(List<Staff> staffs) {
        getProgressBarCount();
        final List<String> staffNames = new ArrayList<>();
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
                if (staffId != -1) {

                } else {
                    Toaster.show(rootView, getString(R.string.error_select_staff));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    @Override
    public void showGroupCreationResponse(GroupCreationResponse groupCreationResponse) {
        hideProgress();
        Toast.makeText(getActivity(), "Group created successfully", Toast.LENGTH_LONG).show();
    }

    @Override
    public void GroupCreationError(String error) {
        Toaster.show(rootView,error);
        hideProgress();
    }

    public void getProgressBarCount(){
        ++mCounterProgressBarCount;
        if(mCounterProgressBarCount == 2 ){
            mCounterProgressBarCount = 0;
            hideProgress();
        }
    }
}