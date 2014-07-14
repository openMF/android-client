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

import com.mifos.mifosxdroid.R;
import com.mifos.objects.organisation.Office;
import com.mifos.services.API;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GenerateCollectionSheetFragment extends Fragment {

    @InjectView(R.id.sp_branch_offices)
    Spinner sp_offices;


    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private List<String> officeNames;
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

                officeNames = new ArrayList<String>();
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

                        if (position > 0) {
                            //inflateStaff
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






}
