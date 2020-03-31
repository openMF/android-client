/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.objects.survey.QuestionDatas;
import com.mifos.objects.survey.ScorecardValues;
import com.mifos.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestionFragment extends MifosBaseFragment implements
        RadioGroup.OnCheckedChangeListener {

    private static final String LOG_TAG = SurveyQuestionFragment.class.getSimpleName();

    @BindView(R.id.survey_question_textView)
    TextView tv_question;

    @BindView(R.id.radio_btn_answer)
    RadioGroup radioGroupAnswer;

    RadioButton rb_add_answer;

    private OnAnswerSelectedListener mCallback;
    private QuestionDatas mQuestionDatas;
    private String answer;
    private ScorecardValues mScorecardValues;

    public static SurveyQuestionFragment newInstance(String QuestionDatas) {
        SurveyQuestionFragment fragment = new SurveyQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.QUESTION_DATA, QuestionDatas);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestionDatas = (new Gson()).fromJson(getArguments().getString(Constants.QUESTION_DATA),
                QuestionDatas.class);
        mScorecardValues = new ScorecardValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_survey_question, container, false);
        ButterKnife.bind(this, view);

        tv_question.setText(mQuestionDatas.getText());

        for (int i = 0; i < mQuestionDatas.getResponseDatas().size(); i++) {
            rb_add_answer = new RadioButton(getActivity());
            rb_add_answer.setId(i);
            rb_add_answer.setText(mQuestionDatas.getResponseDatas().get(i).getText());
            radioGroupAnswer.addView(rb_add_answer);
            radioGroupAnswer.setOnCheckedChangeListener(this);
        }

        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int j = 0; j < group.getChildCount(); j++) {
            RadioButton btn = (RadioButton) group.getChildAt(j);
            int t = group.getId();
            Log.d(LOG_TAG, "" + t);

            if (btn.getId() == checkedId) {
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

