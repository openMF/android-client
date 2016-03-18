/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
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
public class CreateNewCenterFragment extends MifosBaseFragment implements MFDatePicker.OnDatePickListener {
    @InjectView(R.id.et_center_name)
    EditText et_centerName;
    @InjectView(R.id.et_center_external_id)
    EditText et_centerexternalId;
    @InjectView(R.id.cb_center_active_status)
    CheckBox cb_centerActiveStatus;
    @InjectView(R.id.tv_center_activation_date)
    TextView tv_activationDate;
    @InjectView(R.id.tv_center_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.tv_center_activationdate)
    TextView tv_actDate;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.sp_staff)
    Spinner sp_staff;
    @InjectView(R.id.bt_submit)
    Button bt_submit;

    int officeId;
    int staffId;
    Boolean result = true;
    Boolean onTick;
    Boolean onSubmit;
    Boolean onActivate;
    private View rootView;
    private String dateString;
    private String dateofsubmissionstring;
    private DialogFragment mfDatePicker;
    private DialogFragment newDatePicker;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();

    public static CreateNewCenterFragment newInstance() {
        CreateNewCenterFragment createNewCenterFragment = new CreateNewCenterFragment();
        return createNewCenterFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_center, null);
        ButterKnife.inject(this, rootView);
        inflateOfficeSpinner();
        inflateSubmissionDate();
        inflateDateofBirth();
        onSubmit = false;
        onTick = false;
        onActivate = false;
        tv_activationDate.setVisibility(View.GONE);
        tv_actDate.setVisibility(View.GONE);


        //client active checkbox onCheckedListener
        //client active checkbox onCheckedListener
        cb_centerActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                {
                    tv_actDate.setVisibility(View.VISIBLE);
                    tv_activationDate.setVisibility(View.VISIBLE);
                    onTick = true;
                    inflateActivationDate();
                }
                else
                {
                    tv_activationDate.setVisibility(View.GONE);
                    tv_actDate.setVisibility(View.GONE);
                    onTick = false;


                }
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
                hideProgress();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                hideProgress();
            }
        });
    }

    public void inflateStaffSpinner(final int officeId) {
        App.apiManager.getStaffInOffice(officeId, new Callback<List<Staff>>() {
            @Override
            public void success(List<Staff> staffs, Response response) {

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
                hideProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
            }
        });
    }

    private void initiateCenterCreation(CenterPayload centerPayload) {
        showProgress();
        App.apiManager.createCenter(centerPayload, new Callback<Center>() {
            @Override
            public void success(Center center, Response response) {
                Toast.makeText(getActivity(), "Group created successfully", Toast.LENGTH_LONG).show();
                hideProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void inflateSubmissionDate() {
        onSubmit=true;
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
        onActivate=true;
        mfDatePicker = MFDatePicker.newInsance(this);

        tv_activationDate.setText(MFDatePicker.getDatePickedAsString());
        class ActDate{}
        tv_activationDate.setOnClickListener(new View.OnClickListener() {
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

        if(onTick!=true) {

            tv_submissionDate.setText(date);

        }
        else if(onTick==true && onSubmit==true) {

            tv_submissionDate.setText(date);

        }
        else if(onTick==true && onActivate==true) {

            tv_activationDate.setText(date);

        }
        else tv_activationDate.setText(date);



    }
}