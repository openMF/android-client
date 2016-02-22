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
import com.mifos.mifosxdroid.adapters.SurveyListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.survey.Survey;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class SurveyListFragment extends MifosBaseFragment {

    private View rootView;
    private ListView lv_surveys_list;
    private SurveyListAdapter surveyListAdapter;
    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_survey_list, container, false);
        lv_surveys_list = (ListView) rootView.findViewById(R.id.lv_surveys_list);

        showProgress();
        App.apiManager.getAllSurveys(new Callback<List<Survey>>() {
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
                hideProgress();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toaster.show(rootView, "Couldn't Fetch List of Surveys");
                hideProgress();
            }
        });
        return rootView;
    }

    public interface OnFragmentInteractionListener {

        void loadSurveyQuestion(int surveyId);
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
}
