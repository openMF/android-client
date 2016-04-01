/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.survey.Scorecard;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyLastFragment extends Fragment implements Communicator {

    public interface DisableSwipe {
        void disableSwipe();
    }

    @InjectView(R.id.btn_submit) Button btn_submit;
    @InjectView(R.id.survey_submit_textView) TextView tv_submit;
    private DisableSwipe mDetachFragment;
    public Context context;
    private Scorecard mScorecard;
    private int mSurveyId;


    public static SurveyLastFragment newInstance() {
        SurveyLastFragment fragment = new SurveyLastFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_last, container, false);
        ButterKnife.inject(this, view);
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
    public void submitScore(View v){
        if(mScorecard.getScorecardValues().size()>=1) {
            mDetachFragment.disableSwipe();
            btn_submit.setText("Submitting Survey");
            btn_submit.setEnabled(false);
            App.apiManager.submitScore(mSurveyId, mScorecard, new Callback<Scorecard>() {
                @Override
                public void success(Scorecard scorecard, Response response) {
                    Toast.makeText(context, "Scorecard created successfully", Toast.LENGTH_LONG).show();
                    tv_submit.setText("Survey Successfully Submitted ! \n Thanks for taking Survey ");
                    btn_submit.setVisibility(View.GONE);

                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show();
                    tv_submit.setText("Error While Submitting Survey. \n Please Try Again");
                    btn_submit.setVisibility(View.GONE);
                }
            });
        }else
            Toast.makeText(context, "Please Attempt AtLeast One Question ", Toast.LENGTH_SHORT).show();
    }


}

