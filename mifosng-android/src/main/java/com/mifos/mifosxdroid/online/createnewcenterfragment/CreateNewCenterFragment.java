/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.createnewcenterfragment;

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
import com.mifos.objects.group.Center;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.data.CenterPayload;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by nellyk on 1/22/2016.
 */
public class CreateNewCenterFragment extends MifosBaseFragment implements MFDatePicker.OnDatePickListener,CreateNewCenterMvpView {

    @InjectView(R.id.et_center_name)
    EditText et_centerName;
    @InjectView(R.id.et_center_external_id)
    EditText et_centerexternalId;
    @InjectView(R.id.cb_center_active_status)
    CheckBox cb_centerActiveStatus;
    @InjectView(R.id.tv_submissiondate)
    TextView tv_submissionDate;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.sp_staff)
    Spinner sp_staff;
    @InjectView(R.id.sp_add_group)
    Spinner sp_add_group;
    @InjectView(R.id.bt_submit)
    Button bt_submit;

    int officeId;
    int staffId;
    private View rootView;
    Boolean result = true;
    private String dateString;
    private String dateofsubmissionstring;
    private DialogFragment mfDatePicker;
    private DialogFragment newDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private DataManager dataManager;
    private CreateNewCenterPresenter mCreateNewCenterPresenter;
    private int mCounterProgressBarCount = 0;

    public static CreateNewCenterFragment newInstance() {
        CreateNewCenterFragment createNewCenterFragment = new CreateNewCenterFragment();
        return createNewCenterFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_center, null);
        ButterKnife.inject(this, rootView);
        dataManager = new DataManager();
        mCreateNewCenterPresenter = new CreateNewCenterPresenter(dataManager);
        mCreateNewCenterPresenter.attachView(this);
        showProgress();
        inflateOfficeSpinner();
        inflateSubmissionDate();
        inflateDateofBirth();
        //client active checkbox onCheckedListener
        cb_centerActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                tv_submissionDate.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        dateString = tv_submissionDate.getText().toString();
        dateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload(dateString).replace("-", " ");
        dateofsubmissionstring = tv_submissionDate.getText().toString();
        dateofsubmissionstring = DateHelper.getDateAsStringUsedForDateofBirth(dateofsubmissionstring).replace("-", " ");

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CenterPayload centerPayload = new CenterPayload();

                centerPayload.setCenterName(et_centerName.getEditableText().toString());
                centerPayload.setExternalId(et_centerexternalId.getEditableText().toString());
                centerPayload.setActive(cb_centerActiveStatus.isChecked());
                centerPayload.setActivationDate(dateString);
                centerPayload.setSubmissionDate(dateofsubmissionstring);
                centerPayload.setOfficeId(officeId);
                centerPayload.setStaffId(staffId);
                initiateCenterCreation(centerPayload);
            }
        });
        return rootView;
    }

    //inflating office list spinner
    private void inflateOfficeSpinner() {
        mCreateNewCenterPresenter.loadofficelist();
    }

    public void inflateStaffSpinner(int officeId) {
       mCreateNewCenterPresenter.loadStafflist(officeId);
    }

    private void initiateCenterCreation(CenterPayload centerPayload) {

        if (!isValidCenterName()) {
            return;
        }
        else {
            mCreateNewCenterPresenter.createcenter(centerPayload);
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

    public boolean isValidCenterName() {
        try {
            if (TextUtils.isEmpty(et_centerName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.center_name), getResources().getString(R.string.error_cannot_be_empty));
            }
            if (et_centerName.getEditableText().toString().trim().length() < 4 && et_centerName.getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.center_name), 4);
            }
            if (!et_centerName.getEditableText().toString().matches("[a-zA-Z]+")) {
                throw new InvalidTextInputException(getResources().getString(R.string.center_name), getResources().getString(R.string.error_should_contain_only), InvalidTextInputException.TYPE_ALPHABETS);
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
    public void onDestroy() {
        super.onDestroy();
        mCreateNewCenterPresenter.detachView();
    }

    @Override
    public void showofficeList(List<Office> offices) {
        getProgressBarCount();
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
    }

    @Override
    public void showfailedtofetch(String s) {
        getProgressBarCount();
        Toaster.show(rootView, s);
    }

    @Override
    public void showStaffList(List<Staff> staffs) {
        getProgressBarCount();
        final List<String> staffNames = new ArrayList<String>();
        for (Staff staff : staffs) {
            staffNames.add(staff.getDisplayName());
            staffNameIdHashMap.put(staff.getDisplayName(), staff.getId());
        }
        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, staffNames);
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_staff.setAdapter(staffAdapter);
        sp_staff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                staffId = staffNameIdHashMap.get(staffNames.get(position));
                Log.d("staffId " + staffNames.get(position), String.valueOf(staffId));
                if (staffId != -1) {

                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_select_staff), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    @Override
    public void CreateCenter(Center center) {
        Toast.makeText(getActivity(), "Group created successfully", Toast.LENGTH_LONG).show();
    }

    public void getProgressBarCount(){
        ++mCounterProgressBarCount;
        if(mCounterProgressBarCount == 2 ){
            mCounterProgressBarCount = 0;
            hideProgress();
        }
    }
}