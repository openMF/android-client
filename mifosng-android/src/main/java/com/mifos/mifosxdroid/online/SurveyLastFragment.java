/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.survey.Scorecard;
import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyLastFragment extends Fragment implements Communicator {


    @InjectView(R.id.survey_question_textView) TextView tv_question;
    public Context context;


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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_last, container, false);
        ButterKnife.inject(this, view);
        tv_question.setText("You have reached the end of Survey");
        return view;
    }


    @Override
    public void createScoreCard(Scorecard scorecard, int surveyId) {

        App.apiManager.submitScore(surveyId, scorecard, new Callback<Scorecard>() {
            @Override
            public void success(Scorecard scorecard, Response response) {
                Toast.makeText(context, "Scorecard created successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show();
            }
        });
    }

}

