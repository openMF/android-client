/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.survey.QuestionDatas;
import com.mifos.objects.survey.ScorecardValues;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestionFragment extends Fragment {


    private static final String QUESTION_DATA = "Question Data";
    private final String LOG_TAG = getClass().getSimpleName();
    @InjectView(R.id.survey_question_textView)
    TextView tv_question;
    @InjectView(R.id.radio1)
    RadioGroup radioGroup1;
    RadioButton button1;
    RadioButton btn;
    Context thiscontext;
    private OnAnswerSelectedListener mCallback;
    private QuestionDatas mQuestionDatas;
    private String answer;
    private ScorecardValues mScorecardValues;

    public static SurveyQuestionFragment newInstance(String QuestionDatas) {
        SurveyQuestionFragment fragment = new SurveyQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(QUESTION_DATA, QuestionDatas);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_question, container, false);
        ButterKnife.inject(this, view);
        thiscontext = container.getContext();
        mQuestionDatas = (new Gson()).fromJson(getArguments().getString(QUESTION_DATA),
                QuestionDatas.class);
        mScorecardValues = new ScorecardValues();

        tv_question.setText(mQuestionDatas.getText());

        ViewGroup hourButtonLayout = (ViewGroup) view.findViewById(R.id.radio1);
        for (int i = 0; i < mQuestionDatas.getResponseDatas().size(); i++) {
            button1 = new RadioButton(thiscontext);
            button1.setId(i);
            button1.setText(mQuestionDatas.getResponseDatas().get(i).getText());
            hourButtonLayout.addView(button1);
            radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup mRadioGroup2,
                                             int checkedId2) {
                    for (int j = 0; j < mRadioGroup2.getChildCount(); j++) {
                        btn = (RadioButton) mRadioGroup2.getChildAt(j);
                        int t = mRadioGroup2.getId();
                        Log.d(LOG_TAG, "" + t);

                        if (btn.getId() == checkedId2) {
                            answer = btn.getText().toString();
                            mScorecardValues.setQuestionId(mQuestionDatas.getQuestionId());
                            mScorecardValues.setResponseId(mQuestionDatas.getResponseDatas()
                                    .get(j).getResponseId());
                            mScorecardValues.setValue(mQuestionDatas.getResponseDatas()
                                    .get(j).getValue());
                            mCallback.answer(mScorecardValues);
                            Log.d(LOG_TAG, "Q R V" + mQuestionDatas.getQuestionId() + " " +
                                    mQuestionDatas.getResponseDatas().get(j).getResponseId()
                                    + " " + mQuestionDatas.getResponseDatas().get(j).getValue());
                            return;
                        }
                    }
                }
            });
        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            mCallback = (OnAnswerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAnswerSelectedListener");
        }
    }

    public interface OnAnswerSelectedListener {
        void answer(ScorecardValues scorecardValues);
    }

}

