/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.mifos.objects.survey.Survey;
import com.mifos.objects.survey.ScorecardValues;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.List;

import com.mifos.mifosxdroid.R;
import com.mifos.App;
import com.mifos.utils.MyPreference;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestionFragment extends Fragment {
    public interface OnAnswerSelectedListener {
        public void answer(int id, int qid,int rid,int rvalue,int chk);
    }

    // static Strings to retrieve the  question and its answers and show them.
    public static final String QUESTION = "question";
    public static final String PREFS_NAME = "MY_PREFS";
    public static final String SCOREVALUES = "Score_Values";
    public static final String ANSWERS = "answers";
    public static final String ID = "id";
    public static final String SID = "sid";
    public static final String QSIZE = "qsize";
    //public static final int chk = 1;
    SharedPreferences sharedPreferences;

    private OnAnswerSelectedListener mCallback;
    List<ScorecardValues> scorecardValues ;
    ScorecardValues scorevalue;

    private TextView tvQuestion;
    private TextView tvQuestionNo;
    private RadioGroup radioGroup1;
    RadioButton button1;
    RadioButton btn;
    private Button btnNext;
    Context thiscontext;
    private Activity activity;
    private String text1;
    private boolean selected;

    private int Id;
    private int surveyId;
    private String[] answerarray;
    private int qId;
    private int qSize;
    private int qNo;
    private int rId;

    private int rValue;
    MyPreference myPreference;

    public static final SurveyQuestionFragment newInstance(int id, String question,int sid, String[] answers,int qsize) {
        SurveyQuestionFragment fragment = new SurveyQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID, id);
        bundle.putInt(SID, sid);
        bundle.putInt(QSIZE, qsize);
        bundle.putString(QUESTION, question);
        bundle.putStringArray(ANSWERS, answers);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        myPreference = new MyPreference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_question, container, false);
        tvQuestion = (TextView) view.findViewById(R.id.survey_question_textView);
        tvQuestionNo = (TextView) view.findViewById(R.id.survey_question_no);
        thiscontext = container.getContext();
        radioGroup1 = (RadioGroup) view.findViewById(R.id.radio1);
        Id = getArguments().getInt(ID);
        qNo = Id+1;
        surveyId = getArguments().getInt(SID);
        qSize = getArguments().getInt(QSIZE);
        answerarray =getArguments().getStringArray(ANSWERS);
        setQuestion(getArguments().getString(QUESTION));


        // setAnswers(getArguments().getStringArray(ANSWERS));
        ViewGroup hourButtonLayout = (ViewGroup) view.findViewById(R.id.radio1);
        for (int i = 0; i < answerarray.length; i++) {
            button1 = new RadioButton(thiscontext);
            button1.setId(i);
            button1.setText(answerarray[i]);
            hourButtonLayout.addView(button1);
            radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup mRadioGroup2,
                                             int checkedId2) {
                    for (int i = 0; i < mRadioGroup2.getChildCount(); i++) {
                        btn = (RadioButton) mRadioGroup2.getChildAt(i);
                        int t = mRadioGroup2.getId();

                        System.out.println(t);

                        if (btn.getId() == checkedId2) {

                            text1 = btn.getText().toString();
                            App.apiManager.getSurvey(surveyId, new Callback<Survey>() {
                                @Override
                                public void success(final Survey survey, Response response) {

                                    if (survey != null) {

                                        String qa = tvQuestion.getText().toString();
                                        int ival = 0;
                                        int k = 0;


                                        if (survey.getQuestionDatas() != null && survey.getQuestionDatas().size() > 0) {
                                            for (int i = 0; i < survey.getQuestionDatas().size(); i++) {
                                                if (survey.getQuestionDatas().get(i).getText().equals(qa)) {
                                                    qId = survey.getQuestionDatas().get(i).getQuestionId();
                                                    ival = i;
                                                    qSize = survey.getQuestionDatas().size();
                                                }
                                            }

                                            for (int j = 0; j < survey.getQuestionDatas().get(ival).getResponseDatas().size(); j++) {
                                                if (survey.getQuestionDatas().get(ival).getResponseDatas().get(j).getText().equals(text1)) {
                                                    rId = survey.getQuestionDatas().get(ival).getResponseDatas().get(j).getResponseId();
                                                    rValue = survey.getQuestionDatas().get(ival).getResponseDatas().get(j).getValue();

                                                }


                                            }
                                            int chk = 1;
                                            mCallback.answer(Id, qId, rId, rValue, chk);

                                        }
                                    }
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                    Toast.makeText(getActivity(), "survey not found", Toast.LENGTH_SHORT).show();

                                }
                            });

                            return;
                        }
                    }
                }
            });
        }

        tvQuestionNo.setText("question " + qNo + " of " +qSize);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activity = activity;
        try {
            mCallback = (OnAnswerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAnswerSelectedListener");
        }
    }

    public void setQuestion(String question){
        tvQuestion.setText(question);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onPause() {

        super.onPause();

    }
}