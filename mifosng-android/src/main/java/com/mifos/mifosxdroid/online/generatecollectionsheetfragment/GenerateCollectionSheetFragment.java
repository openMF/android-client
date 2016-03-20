/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.generatecollectionsheetfragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GenerateCollectionSheetFragment extends MifosBaseFragment implements GenerateCollectionSheetMvpView{

    public static final String LIMIT = "limit";
    public static final String ORDER_BY = "orderBy";
    public static final String SORT_ORDER = "sortOrder";
    public static final String ASCENDING = "ASC";
    public static final String ORDER_BY_FIELD_NAME = "name";
    public static final String STAFF_ID = "staffId";
    @InjectView(R.id.sp_branch_offices)
    Spinner sp_offices;
    @InjectView(R.id.sp_loan_officers)
    Spinner sp_loan_officers;
    @InjectView(R.id.sp_centers)
    Spinner sp_centers;
    @InjectView(R.id.sp_groups)
    Spinner sp_groups;

    private View rootView;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> centerNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> groupNameIdHashMap = new HashMap<String, Integer>();
    private DataManager dataManager;
    private GenerateCollectionSheetPresenter mGenerateCollectionSheetPresenter;
    private int officeid;
    private int mCounterProgressBarCount = 0;

    public static GenerateCollectionSheetFragment newInstance() {
        GenerateCollectionSheetFragment generateCollectionSheetFragment = new GenerateCollectionSheetFragment();
        return generateCollectionSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_generate_collection_sheet, container, false);
        ButterKnife.inject(this, rootView);
        dataManager = new DataManager();
        mGenerateCollectionSheetPresenter = new GenerateCollectionSheetPresenter(dataManager);
        mGenerateCollectionSheetPresenter.attachView(this);
        showProgress();
        inflateOfficeSpinner();
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mItem_search)
            getActivity().finish();
        return super.onOptionsItemSelected(item);
    }

    public void inflateOfficeSpinner() {
        mGenerateCollectionSheetPresenter.loadofficelist();
    }

    public void inflateStaffSpinner(int officeId) {
        mGenerateCollectionSheetPresenter.loadStafflist(officeId);
    }

    public void inflateCenterSpinner(int officeId, int staffId) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put(LIMIT, -1);
        params.put(ORDER_BY, ORDER_BY_FIELD_NAME);
        params.put(SORT_ORDER, ASCENDING);
        if (staffId >= 0) {
            params.put(STAFF_ID, staffId);
        }
        mGenerateCollectionSheetPresenter.loadCentersInOffice(officeId, params);
    }

    public void inflateGroupSpinner(final int officeId, int staffId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(LIMIT, -1);
        params.put(ORDER_BY, ORDER_BY_FIELD_NAME);
        params.put(SORT_ORDER, ASCENDING);
        if (staffId >= 0)
            params.put(STAFF_ID, staffId);

        mGenerateCollectionSheetPresenter.loadGroupByoffice(officeId, params);
    }

    public void inflateGroupSpinner(int centerId) {
        mGenerateCollectionSheetPresenter.loadGroupByCenter(centerId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGenerateCollectionSheetPresenter.detachView();
    }

    @Override
    public void showofficeList(List<Office> officeList) {
        getProgressBarCount();
        final List<String> officeNames = new ArrayList<String>();
        officeNames.add(getString(R.string.spinner_office));
        officeNameIdHashMap.put(getString(R.string.spinner_office), -1);
        for (Office office : officeList) {
            officeNames.add(office.getName());
            officeNameIdHashMap.put(office.getName(), office.getId());
        }
        ArrayAdapter<String> officeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, officeNames);
        officeAdapter.notifyDataSetChanged();
        officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_offices.setAdapter(officeAdapter);
        sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int officeId = officeNameIdHashMap.get(officeNames.get(position));
                if (officeId != -1) {
                    officeid = officeId;
                    inflateStaffSpinner(officeId);
                    inflateCenterSpinner(officeId, -1);
                    inflateGroupSpinner(officeId, -1);
                } else
                    Toaster.show(rootView, getString(R.string.error_select_office));
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
    public void showStaffList(List<Staff> staffs) {
        getProgressBarCount();
        final List<String> staffNames = new ArrayList<String>();
        staffNames.add(getString(R.string.spinner_staff));
        staffNameIdHashMap.put(getString(R.string.spinner_staff), -1);

        for (Staff staff : staffs) {
            staffNames.add(staff.getDisplayName());
            staffNameIdHashMap.put(staff.getDisplayName(), staff.getId());
        }

        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, staffNames);

        staffAdapter.notifyDataSetChanged();
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_loan_officers.setAdapter(staffAdapter);
        sp_loan_officers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int staffId = staffNameIdHashMap.get(staffNames.get(position));
                if (staffId != -1) {
                    inflateCenterSpinner(officeid, staffId);
                    inflateGroupSpinner(officeid, staffId);
                } else
                    Toaster.show(rootView, getString(R.string.error_select_staff));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showCenterOffice(List<Center> centers) {
        getProgressBarCount();
        final List<String> centerNames = new ArrayList<String>();

        centerNames.add(getString(R.string.spinner_center));
        centerNameIdHashMap.put(getString(R.string.spinner_center), -1);

        for (Center center : centers) {
            centerNames.add(center.getName());
            centerNameIdHashMap.put(center.getName(), center.getId());
        }
        ArrayAdapter<String> centerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, centerNames);
        centerAdapter.notifyDataSetChanged();
        centerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_centers.setAdapter(centerAdapter);

        sp_centers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int centerId = centerNameIdHashMap.get(centerNames.get(position));
                if (centerId != -1)
                    inflateGroupSpinner(centerId);
                else
                    Toaster.show(rootView, getString(R.string.error_select_center));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showGroupByOffice(List<Group> groups) {
        getProgressBarCount();
        List<String> groupNames = new ArrayList<String>();

        groupNames.add(getString(R.string.spinner_group));
        groupNameIdHashMap.put(getString(R.string.spinner_group), -1);

        for (Group group : groups) {
            groupNames.add(group.getName());
            groupNameIdHashMap.put(group.getName(), group.getId());
        }

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.notifyDataSetChanged();
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_groups.setAdapter(groupAdapter);
    }

    @Override
    public void showGroupsByCenter(CenterWithAssociations centerWithAssociations) {
        getProgressBarCount();
        List<Group> groups = centerWithAssociations.getGroupMembers();
        List<String> groupNames = new ArrayList<String>();
        groupNames.add(getString(R.string.spinner_group));
        groupNameIdHashMap.put(getString(R.string.spinner_group), -1);

        for (Group group : groups) {
            groupNames.add(group.getName());
            groupNameIdHashMap.put(group.getName(), group.getId());
        }
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.notifyDataSetChanged();
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_groups.setAdapter(groupAdapter);
    }

    public void getProgressBarCount(){
        ++mCounterProgressBarCount;
        if(mCounterProgressBarCount == 5 ){
            mCounterProgressBarCount = 0;
            hideProgress();
        }
    }
}
