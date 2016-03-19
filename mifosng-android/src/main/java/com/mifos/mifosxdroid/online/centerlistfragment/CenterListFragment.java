/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlistfragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

/**
 * Created by ishankhanna on 11/03/14.
 */
public class CenterListFragment extends MifosBaseFragment implements CenterListMvpView{

    private View rootView;
    private ListView lv_centers_list;
    private CentersListAdapter centersListAdapter;
    private OnFragmentInteractionListener mListener;
    private CenterListPresenter mCenterListPresenter;
    private List<Center> mCenterList;
    private int mListPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_centers_list, container, false);
        lv_centers_list = (ListView) rootView.findViewById(R.id.lv_center_list);
        mCenterListPresenter = new CenterListPresenter();
        mCenterListPresenter.attachView(this);

        mCenterListPresenter.loadCenters();

        lv_centers_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                mListPosition = position;
                mCenterListPresenter.loadCentersGroupAndMeeting(mCenterList.get(position).getId());
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void showCenterProgress(boolean progress) {
        if(progress){
            showProgress();
        }else {
            hideProgress();
        }
    }

    @Override
    public void showCenters(final List<Center> centers) {
        mCenterList = centers;
        centersListAdapter = new CentersListAdapter(getActivity(), centers);
        lv_centers_list.setAdapter(centersListAdapter);
        lv_centers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.loadGroupsOfCenter(centers.get(i).getId());
            }
        });
    }

    @Override
    public void showCentersGroupAndMeeting(final CenterWithAssociations centerWithAssociations) {
        MFDatePicker mfDatePicker = new MFDatePicker();
        mfDatePicker.setOnDatePickListener(new MFDatePicker.OnDatePickListener() {
            @Override
            public void onDatePicked(String date) {
                mListener.loadCollectionSheetForCenter(mCenterList.get(mListPosition).getId(), date, centerWithAssociations.getCollectionMeetingCalendar().getId());
            }
        });
        mfDatePicker.show(getActivity().getSupportFragmentManager(), MFDatePicker.TAG);
    }

    @Override
    public void showResponseError() {
        Toaster.show(rootView, "Cannot Generate Collection Sheet, There was some problem!");
    }

    public interface OnFragmentInteractionListener {
        void loadGroupsOfCenter(int centerId);

        void loadCollectionSheetForCenter(int centerId, String collectionDate, int calenderInstanceId);
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
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCenterListPresenter.detachView();
    }
}