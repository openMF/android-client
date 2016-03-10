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
import android.widget.TextView;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.survey.ScorecardValues;
import com.mifos.utils.MyPreference;
import java.util.List;


/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyLastFragment extends Fragment {
    public static final String QUESTION = "question";
    public static final String ID = "id";
    public Context context;
    List<ScorecardValues> scorecardValues;
    MyPreference myPreference;
    Activity activity;

    private TextView tvQuestion;



    public static final SurveyLastFragment newInstance(int id, String question) {
        SurveyLastFragment fragment = new SurveyLastFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID, id);
        bundle.putString(QUESTION, question);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_last, container, false);
        tvQuestion = (TextView) view.findViewById(R.id.survey_question_textView);
        setQuestion(getArguments().getString(QUESTION));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myPreference = new MyPreference();
        scorecardValues = myPreference.getScorecards(activity);

    }

    public void setQuestion(String question){
        tvQuestion.setText(question);
    }

    @Override
    public void onStop() {
        scorecardValues.clear();
        myPreference = new MyPreference();
        myPreference.resetScorecard(activity);
        super.onStop();

    }

}


