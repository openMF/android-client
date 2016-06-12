/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.generatecollectionsheet;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GenerateCollectionSheetFragment extends ProgressableFragment
        implements GenerateCollectionSheetMvpView {

    public static final String LIMIT = "limit";
    public static final String ORDER_BY = "orderBy";
    public static final String SORT_ORDER = "sortOrder";
    public static final String ASCENDING = "ASC";
    public static final String ORDER_BY_FIELD_NAME = "name";
    public static final String STAFF_ID = "staffId";

    @BindView(R.id.sp_branch_offices)
    Spinner sp_offices;

    @BindView(R.id.sp_loan_officers)
    Spinner sp_loan_officers;

    @BindView(R.id.sp_centers)
    Spinner sp_centers;

    @BindView(R.id.sp_groups)
    Spinner sp_groups;

    @Inject
    GenerateCollectionSheetPresenter mGenerateCollectionSheetPresenter;

    private View rootView;

    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> staffNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> centerNameIdHashMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> groupNameIdHashMap = new HashMap<String, Integer>();

    public static GenerateCollectionSheetFragment newInstance() {
        GenerateCollectionSheetFragment generateCollectionSheetFragment = new
                GenerateCollectionSheetFragment();
        return generateCollectionSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_generate_collection_sheet, container, false);

        ButterKnife.bind(this, rootView);
        mGenerateCollectionSheetPresenter.attachView(this);

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
        mGenerateCollectionSheetPresenter.loadOffices();
    }

    public void inflateStaffSpinner(final int officeId) {
        mGenerateCollectionSheetPresenter.loadStaffInOffice(officeId);
    }

    public void inflateCenterSpinner(final int officeId, int staffId) {
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

        mGenerateCollectionSheetPresenter.loadGroupsInOffice(officeId, params);
    }

    public void inflateGroupSpinner(final int centerId) {
        mGenerateCollectionSheetPresenter.loadGroupByCenter(centerId);
    }

    @Override
    public void showOffices(List<Office> offices) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        final List<String> officeNames = new ArrayList<String>();
        officeNames.add(getString(R.string.spinner_office));
        officeNameIdHashMap.put(getString(R.string.spinner_office), -1);
        for (Office office : offices) {
            officeNames.add(office.getName());
            officeNameIdHashMap.put(office.getName(), office.getId());
        }
        ArrayAdapter<String> officeAdapter = new ArrayAdapter<>(getActivity(), android.R
                .layout.simple_spinner_item, officeNames);
        officeAdapter.notifyDataSetChanged();
        officeAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_offices.setAdapter(officeAdapter);
        sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                int officeId = officeNameIdHashMap.get(officeNames.get(position));
                if (officeId != -1) {
                    inflateStaffSpinner(officeId);
                    inflateCenterSpinner(officeId, -1);
                    inflateGroupSpinner(officeId, -1);
                } else {
                    Toaster.show(rootView, getString(R.string.error_select_office));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showStaffInOffice(List<Staff> staffs, final int officeId) {
        final List<String> staffNames = new ArrayList<String>();
        staffNames.add(getString(R.string.spinner_staff));
        staffNameIdHashMap.put(getString(R.string.spinner_staff), -1);

        for (Staff staff : staffs) {
            staffNames.add(staff.getDisplayName());
            staffNameIdHashMap.put(staff.getDisplayName(), staff.getId());
        }

        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, staffNames);

        staffAdapter.notifyDataSetChanged();
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_loan_officers.setAdapter(staffAdapter);
        sp_loan_officers.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int
                            position,
                                               long id) {
                        int staffId = staffNameIdHashMap.get(staffNames.get(position));
                        if (staffId != -1) {
                            inflateCenterSpinner(officeId, staffId);
                            inflateGroupSpinner(officeId, staffId);
                        } else {
                            Toaster.show(rootView, getString(R.string.error_select_staff));
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }

    @Override
    public void showCentersInOffice(List<Center> centers) {
        final List<String> centerNames = new ArrayList<String>();

        centerNames.add(getString(R.string.spinner_center));
        centerNameIdHashMap.put(getString(R.string.spinner_center), -1);

        for (Center center : centers) {
            centerNames.add(center.getName());
            centerNameIdHashMap.put(center.getName(), center.getId());
        }
        ArrayAdapter<String> centerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, centerNames);
        centerAdapter.notifyDataSetChanged();
        centerAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        sp_centers.setAdapter(centerAdapter);

        sp_centers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                int centerId = centerNameIdHashMap.get(centerNames.get(position));
                if (centerId != -1) {
                    inflateGroupSpinner(centerId);
                } else {
                    Toaster.show(rootView, getString(R.string.error_select_center));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void showGroupsInOffice(List<Group> groups) {
        List<String> groupNames = new ArrayList<String>();

        groupNames.add(getString(R.string.spinner_group));
        groupNameIdHashMap.put(getString(R.string.spinner_group), -1);

        for (Group group : groups) {
            groupNames.add(group.getName());
            groupNameIdHashMap.put(group.getName(), group.getId());
        }

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.notifyDataSetChanged();
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_groups.setAdapter(groupAdapter);
    }

    @Override
    public void showGroupByCenter(CenterWithAssociations centerWithAssociations) {
        List<Group> groups = centerWithAssociations.getGroupMembers();
        List<String> groupNames = new ArrayList<String>();
        groupNames.add(getString(R.string.spinner_group));
        groupNameIdHashMap.put(getString(R.string.spinner_group), -1);

        for (Group group : groups) {
            groupNames.add(group.getName());
            groupNameIdHashMap.put(group.getName(), group.getId());
        }
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, groupNames);
        groupAdapter.notifyDataSetChanged();
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_groups.setAdapter(groupAdapter);
    }

    @Override
    public void showFetchingError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGenerateCollectionSheetPresenter.detachView();
    }
}
