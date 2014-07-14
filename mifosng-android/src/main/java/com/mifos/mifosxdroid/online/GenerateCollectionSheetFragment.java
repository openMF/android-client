package com.mifos.mifosxdroid.online;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.group.Center;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.API;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GenerateCollectionSheetFragment extends Fragment {

    @InjectView(R.id.sp_branch_offices) Spinner sp_offices;
    @InjectView(R.id.sp_loan_officers) Spinner sp_loan_officers;
    @InjectView(R.id.sp_centers) Spinner sp_centers;

    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> centerNameIdHashMap = new HashMap<String, Integer>();

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    public GenerateCollectionSheetFragment() {
        // Required empty public constructor
    }

    public static GenerateCollectionSheetFragment newInstance() {

        GenerateCollectionSheetFragment generateCollectionSheetFragment = new GenerateCollectionSheetFragment();

        return generateCollectionSheetFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_generate_collection_sheet, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(GenerateCollectionSheetFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();

        ButterKnife.inject(this, rootView);

        inflateOfficeSpinner();


        return rootView;
    }

    public void inflateOfficeSpinner() {

        safeUIBlockingUtility.safelyBlockUI();

        API.officeService.getAllOffices(new Callback<List<Office>>() {
            @Override
            public void success(List<Office> offices, Response response) {

                final List<String> officeNames = new ArrayList<String>();
                officeNames.add(getString(R.string.spinner_office));
                officeNameIdHashMap.put(getString(R.string.spinner_office), -1);
                for (Office office : offices) {
                    officeNames.add(office.getName());
                    officeNameIdHashMap.put(office.getName(),office.getId());
                    Log.i("Office", office.getName());
                }

                ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, officeNames);

                officeAdapter.notifyDataSetChanged();

                officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_offices.setAdapter(officeAdapter);

                sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        int officeId = officeNameIdHashMap.get(officeNames.get(position));

                        if ( officeId != -1) {

                            inflateStaffSpinner(officeId);
                            inflateCenterSpinner(officeId, -1);

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


        API.staffService.getStaffForOffice(officeId, new Callback<List<Staff>>() {
            @Override
            public void success(List<Staff> staffs, Response response) {

                final List<String> staffNames = new ArrayList<String>();

                staffNames.add(getString(R.string.spinner_staff));
                staffNameIdHashMap.put(getString(R.string.spinner_staff),-1);

                for (Staff staff : staffs) {
                    staffNames.add(staff.getDisplayName());
                    staffNameIdHashMap.put(staff.getDisplayName(),staff.getId());
                }


                ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, staffNames);

                staffAdapter.notifyDataSetChanged();

                staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_loan_officers.setAdapter(staffAdapter);

                sp_loan_officers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        int staffId = staffNameIdHashMap.get(staffNames.get(position));

                        if (staffId != -1) {

                            inflateCenterSpinner(officeId, staffId);

                        } else {

                            Toast.makeText(getActivity(), getString(R.string.error_select_staff), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());


            }
        });


    }

    public void inflateCenterSpinner(int officeId, int staffId) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("limit",-1);
        params.put("orderBy","name");
        params.put("sortOrder", "ASC");
        if (staffId >= 0) {
            params.put("staffId", staffId);
        }

        API.centerService.getAllCentersInOffice(officeId, params, new Callback<List<Center>>() {
            @Override
            public void success(List<Center> centers, Response response) {

                List<String> centerNames = new ArrayList<String>();

                centerNames.add(getString(R.string.spinner_center));
                centerNameIdHashMap.put(getString(R.string.spinner_center),-1);

                for (Center center : centers) {
                    centerNames.add(center.getName());
                    staffNameIdHashMap.put(center.getName(),center.getId());
                }


                ArrayAdapter<String> centerAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, centerNames);

                centerAdapter.notifyDataSetChanged();

                centerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_centers.setAdapter(centerAdapter);

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                System.out.println(retrofitError.getLocalizedMessage());

            }
        });



    }






}
