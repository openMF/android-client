package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.JsonArray;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.noncore.DataTable;
import com.mifos.services.API;
import com.mifos.utils.DataTableUIBuilder;
import com.mifos.utils.SafeUIBlockingUtility;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DataTableDataFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;

    private static DataTable dataTable;
    private static int entityId;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;




    public static DataTableDataFragment newInstance(DataTable dataTable, int entityId) {

        DataTableDataFragment fragment = new DataTableDataFragment();
        fragment.dataTable = dataTable;
        fragment.entityId = entityId;
        return fragment;
    }
    public DataTableDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_datatable, container, false);

        activity = (ActionBarActivity) getActivity();
        actionBar = activity.getSupportActionBar();
        actionBar.setTitle(dataTable.getRegisteredTableName());

        safeUIBlockingUtility = new SafeUIBlockingUtility(DataTableDataFragment.this.getActivity());

        API.dataTableService.getDataOfDataTable(dataTable.getRegisteredTableName(), entityId, new Callback<JsonArray>() {
            @Override
            public void success(JsonArray jsonElements, Response response) {

                if(jsonElements!=null)
                {
                    rootView = DataTableUIBuilder.getDataTableLayout(dataTable, jsonElements, (LinearLayout)rootView, getActivity());
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Log.i(getActivity().getLocalClassName(), retrofitError.getLocalizedMessage());

            }

        });

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//
//    }

}
