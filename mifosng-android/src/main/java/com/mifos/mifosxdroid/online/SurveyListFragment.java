package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.client.Client;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.List;
import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import com.mifos.objects.survey.Survey;
import com.mifos.mifosxdroid.adapters.SurveyListAdapter;


/**
 * Created by Nasim Banu on 19,January,2016.
 */
public class SurveyListFragment extends Fragment {
    private static final String TAG = "SurveyListFragment";
    private View rootView;
    private ListView lv_surveys_list;
    private List<Survey> surveys;
    private SafeUIBlockingUtility safeUIBlockingUtility;
    private SurveyListAdapter surveyListAdapter;
    private Context context;
    private OnFragmentInteractionListener mListener;

    public SurveyListFragment(){

    }

    public static SurveyListFragment newInstance(List<Survey> surveyList) {
        SurveyListFragment surveyListFragment = new SurveyListFragment();
        return surveyListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_surveys_list,container,false);
        context = getActivity().getApplicationContext();
        setupUI();
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());

        safeUIBlockingUtility.safelyBlockUI();
        ((MifosApplication)getActivity().getApplication()).api.surveyService.getAllSurveys(new Callback<List<Survey>>() {
            @Override
            public void success(final List<Survey> surveys, Response response) {

                surveyListAdapter = new SurveyListAdapter(getActivity(), surveys);
                lv_surveys_list.setAdapter(surveyListAdapter);
                lv_surveys_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        mListener.loadSurveyQuestion(surveys.get(i).getId());
                    }
                });
                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                safeUIBlockingUtility.safelyUnblockUIForFailure(TAG, "Couldn't Fetch List of Surveys");
            }
        });
        return rootView;
    }

    public void setupUI(){

        lv_surveys_list = (ListView) rootView.findViewById(R.id.lv_surveys_list);
    }
    public interface OnFragmentInteractionListener {

        public void loadSurveyQuestion(int surveyId);
    }


}
