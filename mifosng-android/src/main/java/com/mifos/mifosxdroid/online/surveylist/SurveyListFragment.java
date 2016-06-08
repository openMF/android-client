/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.surveylist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SurveyListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.survey.Survey;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class SurveyListFragment extends ProgressableFragment implements SurveyListMvpView {

    private static final String CLIENTID = "ClientID";

    @InjectView(R.id.lv_surveys_list)
    ListView lv_surveys_list;

    @Inject
    SurveyListPresenter mSurveyListPresenter;

    private SurveyListAdapter surveyListAdapter;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private int clientId;

    public static SurveyListFragment newInstance(int clientid) {
        SurveyListFragment fragment = new SurveyListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CLIENTID, clientid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_survey_list, container, false);

        ButterKnife.inject(this, rootView);
        mSurveyListPresenter.attachView(this);

        clientId = getArguments().getInt(CLIENTID);

        mSurveyListPresenter.loadSurveyList();

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mItem_search)
            getActivity().finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    @Override
    public void showAllSurvey(final List<Survey> surveys) {
        surveyListAdapter = new SurveyListAdapter(getActivity(), surveys);
        lv_surveys_list.setAdapter(surveyListAdapter);
        lv_surveys_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.loadSurveyQuestion(surveys.get(i), clientId);
            }
        });
    }

    @Override
    public void showFetchingError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSurveyListPresenter.detachView();
    }

    public interface OnFragmentInteractionListener {

        void loadSurveyQuestion(Survey survey, int Clientid);
    }

}
