/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 11/03/14.
 */

//TODO Replace ListView to RecyclerView 
public class CenterListFragment extends ProgressableFragment implements CenterListMvpView {

    @InjectView(R.id.lv_center_list)
    ListView lv_centers_list;
    @Inject
    CenterListPresenter mCenterListPresenter;
    private View rootView;
    private CentersListAdapter centersListAdapter;
    private OnFragmentInteractionListener mListener;
    private List<Center> mCentersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCentersList = new ArrayList<>();
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_centers_list, container, false);

        setToolbarTitle(getResources().getString(R.string.title_activity_centers));

        ButterKnife.inject(this, rootView);
        mCenterListPresenter.attachView(this);

        mCenterListPresenter.loadCenters();

        lv_centers_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.loadGroupsOfCenter(mCentersList.get(position).getId());
            }
        });

        lv_centers_list.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                mCenterListPresenter.loadCentersGroupAndMeeting(
                        mCentersList.get(position).getId());

                return true;
            }
        });
        return rootView;
    }

    @Override
    public void showCenters(List<Center> centers) {
        if (getActivity() == null) return;

        mCentersList = centers;
        centersListAdapter = new CentersListAdapter(getActivity(), mCentersList);
        lv_centers_list.setAdapter(centersListAdapter);
    }

    @Override
    public void showCentersGroupAndMeeting(
            final CenterWithAssociations centerWithAssociations, final int id) {

        MFDatePicker mfDatePicker = new MFDatePicker();
        mfDatePicker.setOnDatePickListener(new MFDatePicker
                .OnDatePickListener() {
            @Override
            public void onDatePicked(String date) {
                mListener.loadCollectionSheetForCenter(id, date,
                        centerWithAssociations
                                .getCollectionMeetingCalendar()
                                .getId());
            }
        });
        mfDatePicker.show(getActivity().getSupportFragmentManager(),
                MFDatePicker.TAG);

    }

    @Override
    public void showCenterGroupFetchinError() {
        Toaster.show(rootView, "Cannot Generate Collection Sheet," +
                " There " +
                "was some problem!");
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mItem_search)
            getActivity().finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void loadGroupsOfCenter(int centerId);

        void loadCollectionSheetForCenter(int centerId, String collectionDate, int
                calenderInstanceId);
    }
}