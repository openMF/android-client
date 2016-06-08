/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online.surveysubmit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.online.Communicator;
import com.mifos.mifosxdroid.online.SurveyQuestionViewPager;
import com.mifos.objects.survey.Scorecard;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveySubmitFragment extends ProgressableFragment implements Communicator, SurveySubmitMvpView {

    public Context context;

    @InjectView(R.id.btn_submit)
    Button btn_submit;

    @InjectView(R.id.survey_submit_textView)
    TextView tv_submit;

    @Inject
    SurveySubmitPresenter mSurveySubmitPresenter;

    private DisableSwipe mDetachFragment;
    private Scorecard mScorecard;
    private int mSurveyId;

    public static SurveySubmitFragment newInstance() {
        SurveySubmitFragment fragment = new SurveySubmitFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        ((SurveyQuestionViewPager) context).fragmentCommunicator = this;
        Activity activity = (Activity) context;
        try {
            mDetachFragment = (DisableSwipe) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAnswerSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity)getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_last, container, false);

        ButterKnife.inject(this, view);
        mSurveySubmitPresenter.attachView(this);

        return view;
    }

    @Override
    public void passScoreCardData(Scorecard scorecard, int surveyId) {
        mScorecard = scorecard;
        mSurveyId = surveyId;
        tv_submit.setText("Attempt Questions : " + mScorecard.getScorecardValues().size());
        btn_submit.setText("Submit Survey");
    }

    @OnClick(R.id.btn_submit)
    public void submitScore(View v) {
        if (mScorecard.getScorecardValues().size() >= 1) {
            mDetachFragment.disableSwipe();
            btn_submit.setText("Submitting Survey");
            btn_submit.setEnabled(false);
            mSurveySubmitPresenter.submitSurvey(mSurveyId, mScorecard);
        } else {
            Toast.makeText(context, "Please Attempt AtLeast One Question ", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void showSurveySubmittedSuccessfully(Scorecard scorecard) {
        Toast.makeText(context, "Scorecard created successfully", Toast.LENGTH_LONG)
                .show();
        tv_submit.setText("Survey Successfully Submitted ! \n Thanks for taking " +
                "Survey ");
        btn_submit.setVisibility(View.GONE);
    }

    @Override
    public void showError(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        tv_submit.setText("Error While Submitting Survey. \n Please Try Again");
        btn_submit.setVisibility(View.GONE);

    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSurveySubmitPresenter.detachView();
    }

    public interface DisableSwipe {
        void disableSwipe();
    }


}

