/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 11/03/14.
 */
public class CenterListFragment extends ProgressableFragment {

    private View rootView;
    private ListView lv_centers_list;
    private CentersListAdapter centersListAdapter;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_centers_list, container, false);
        lv_centers_list = (ListView) rootView.findViewById(R.id.lv_center_list);
        setToolbarTitle(getResources().getString(R.string.title_activity_centers));

        showProgress(true);
        App.apiManager.getCenters(new Callback<List<Center>>() {
            @Override
            public void success(final List<Center> centers, Response response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                if (getActivity() == null) return;

                centersListAdapter = new CentersListAdapter(getActivity(), centers);
                lv_centers_list.setAdapter(centersListAdapter);
                lv_centers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mListener.loadGroupsOfCenter(centers.get(i).getId());
                    }
                });

                lv_centers_list.setOnItemLongClickListener(new AdapterView
                        .OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int
                            position, long id) {
                        showProgress(true);
                        App.apiManager.getCentersGroupAndMeeting(centers.get(position).getId(),
                                new Callback<CenterWithAssociations>() {
                                    @Override
                                    public void success(final CenterWithAssociations
                                                                centerWithAssociations, Response
                                                                response) {
                                        showProgress(false);
                                        MFDatePicker mfDatePicker = new MFDatePicker();
                                        mfDatePicker.setOnDatePickListener(new MFDatePicker
                                                .OnDatePickListener() {
                                            @Override
                                            public void onDatePicked(String date) {
                                                mListener.loadCollectionSheetForCenter(centers.get
                                                        (position).getId(), date,
                                                        centerWithAssociations
                                                        .getCollectionMeetingCalendar().getId());
                                            }
                                        });
                                        mfDatePicker.show(getActivity().getSupportFragmentManager(),
                                                MFDatePicker.TAG);
                                    }

                                    @Override
                                    public void failure(RetrofitError retrofitError) {
                                        showProgress(false);
                                        Toaster.show(rootView, "Cannot Generate Collection Sheet," +
                                                " There " +
                                                "was some problem!");
                                    }
                                });
                        return true;
                    }
                });
                showProgress(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
            }
        });
        return rootView;
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