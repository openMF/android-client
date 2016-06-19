/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListner;
import com.mifos.mifosxdroid.core.RecyclerItemClickListner.OnItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ishankhanna on 11/03/14.
 */

//TODO Replace ListView to RecyclerView 
public class CenterListFragment extends MifosBaseFragment
        implements CenterListMvpView, OnItemClickListener{


    @BindView(R.id.rv_center_list)
    RecyclerView rv_centers;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    CenterListPresenter mCenterListPresenter;

    private View rootView;
    private CentersListAdapter centersListAdapter;
    private OnFragmentInteractionListener mListener;
    private List<Center> mCentersList;
    private LinearLayoutManager layoutManager;

    @Override
    public void onItemClick(View childView, int position) {
        mListener.loadGroupsOfCenter(mCentersList.get(position).getId());
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        mCenterListPresenter.loadCentersGroupAndMeeting(
                mCentersList.get(position).getId());
    }

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

        ButterKnife.bind(this, rootView);
        mCenterListPresenter.attachView(this);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_centers.setLayoutManager(layoutManager);
        rv_centers.addOnItemTouchListener(new RecyclerItemClickListner(getActivity(), this));
        rv_centers.setHasFixedSize(true);

        mCenterListPresenter.loadCenters();

        return rootView;
    }

    @Override
    public void showCenters(List<Center> centers) {
        if (getActivity() == null) return;

        mCentersList = centers;
        centersListAdapter = new CentersListAdapter(getActivity(), mCentersList);
        rv_centers.setAdapter(centersListAdapter);
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
        if (b) {
            showMifosProgressBar();
        } else {
            hideMifosProgressBar();
        }
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