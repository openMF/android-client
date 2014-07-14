package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.objects.group.Center;
import com.mifos.services.API;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 11/03/14.
 */
public class CenterListFragment extends Fragment {

    private static final String TAG = "CenterListFragment";

    private View rootView;
    private ListView lv_centers_list;
    private SharedPreferences sharedPreferences;
    private ActionBarActivity actionBarActivity;
    private List<Center> centers;
    private SafeUIBlockingUtility safeUIBlockingUtility;
    private CentersListAdapter centersListAdapter;

    private OnFragmentInteractionListener mListener;

    public CenterListFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_centers_list,container,false);

        actionBarActivity = (ActionBarActivity) getActivity();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(actionBarActivity);

        setupUI();

        safeUIBlockingUtility = new SafeUIBlockingUtility(actionBarActivity);

        safeUIBlockingUtility.safelyBlockUI();
        API.centerService.getAllCenters(new Callback<List<Center>>() {
            @Override
            public void success(final List<Center> centers, Response response) {

                centersListAdapter = new CentersListAdapter(actionBarActivity, centers);

                lv_centers_list.setAdapter(centersListAdapter);

                lv_centers_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        mListener.loadGroupsOfCenter(centers.get(i).getId());

                    }
                });

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                safeUIBlockingUtility.safelyUnblockUIForFailure(TAG, "Couldn't Fetch List of Centers");

            }
        });


        return rootView;
    }

    public void setupUI(){

        lv_centers_list = (ListView) rootView.findViewById(R.id.lv_center_list);

    }

    public interface OnFragmentInteractionListener {

        public void loadGroupsOfCenter(int centerId);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}