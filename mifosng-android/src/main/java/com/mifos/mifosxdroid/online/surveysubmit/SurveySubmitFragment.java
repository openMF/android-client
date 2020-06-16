/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online.surveysubmit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.online.Communicator;
import com.mifos.mifosxdroid.online.SurveyQuestionActivity;
import com.mifos.objects.survey.Scorecard;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveySubmitFragment extends MifosBaseFragment implements
        Communicator, SurveySubmitMvpView {

    public Context context;

    @BindView(R.id.btn_submit)
    Button btn_submit;

    @BindView(R.id.survey_submit_textView)
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_last, container, false);

        ButterKnife.bind(this, view);
        mSurveySubmitPresenter.attachView(this);

        return view;
    }

    @Override
    public void passScoreCardData(Scorecard scorecard, int surveyId) {
        mScorecard = scorecard;
        mSurveyId = surveyId;
        if ((isAdded())) {
            String submitText = getResources().getString(R.string.attempt_question) +
                    mScorecard.getScorecardValues().size();
            tv_submit.setText(submitText);
            btn_submit.setText(getResources().getString(R.string.submit_survey));
        }
    }

    @OnClick(R.id.btn_submit)
    void submitScore() {
        if (mScorecard.getScorecardValues().size() >= 1) {
            mDetachFragment.disableSwipe();
            btn_submit.setText(getResources().getString(R.string.submitting_surveys));
            btn_submit.setEnabled(false);
            btn_submit.setVisibility(View.GONE);
            mSurveySubmitPresenter.submitSurvey(mSurveyId, mScorecard);
        } else {
            Toast.makeText(context, getResources()
                    .getString(R.string.please_attempt_atleast_one_question), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void showSurveySubmittedSuccessfully(Scorecard scorecard) {
        Toast.makeText(context, getResources().getString(R.string.scorecard_created_successfully),
                Toast.LENGTH_LONG).show();
        tv_submit.setText(getResources().getString(R.string.survey_successfully_submitted));
        btn_submit.setVisibility(View.GONE);
    }

    @Override
    public void showError(int errorMessage) {
        Toast.makeText(context, getResources().getString(errorMessage), Toast.LENGTH_LONG).show();
        tv_submit.setText(getResources().getString(R.string.error_submitting_survey));
        btn_submit.setVisibility(View.GONE);
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
    public void onDestroyView() {
        super.onDestroyView();
        mSurveySubmitPresenter.detachView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        ((SurveyQuestionActivity) context).fragmentCommunicator = this;
        Activity activity = (Activity) context;
        try {
            mDetachFragment = (DisableSwipe) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAnswerSelectedListener");
        }
    }

    public interface DisableSwipe {
        void disableSwipe();
    }
}

