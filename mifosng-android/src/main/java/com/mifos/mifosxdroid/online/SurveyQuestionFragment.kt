/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.objects.survey.QuestionDatas
import com.mifos.objects.survey.ScorecardValues
import com.mifos.utils.Constants

/**
 * Created by Nasim Banu on 28,January,2016.
 */
class SurveyQuestionFragment : MifosBaseFragment(), RadioGroup.OnCheckedChangeListener {
    @JvmField
    @BindView(R.id.survey_question_textView)
    var tv_question: TextView? = null

    @JvmField
    @BindView(R.id.radio_btn_answer)
    var radioGroupAnswer: RadioGroup? = null
    var rb_add_answer: RadioButton? = null
    private var mCallback: OnAnswerSelectedListener? = null
    private var mQuestionDatas: QuestionDatas? = null
    private var answer: String? = null
    private var mScorecardValues: ScorecardValues? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mQuestionDatas = Gson().fromJson(requireArguments().getString(Constants.QUESTION_DATA),
                QuestionDatas::class.java)
        mScorecardValues = ScorecardValues()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_survey_question, container, false)
        ButterKnife.bind(this, view)
        tv_question!!.text = mQuestionDatas!!.text
        for (i in mQuestionDatas!!.responseDatas.indices) {
            rb_add_answer = RadioButton(activity)
            rb_add_answer!!.id = i
            rb_add_answer!!.text = mQuestionDatas!!.responseDatas[i].text
            radioGroupAnswer!!.addView(rb_add_answer)
            radioGroupAnswer!!.setOnCheckedChangeListener(this)
        }
        return view
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        for (j in 0 until group.childCount) {
            val btn = group.getChildAt(j) as RadioButton
            val t = group.id
            Log.d(LOG_TAG, "" + t)
            if (btn.id == checkedId) {
                answer = btn.text.toString()
                mScorecardValues!!.questionId = mQuestionDatas!!.questionId
                mScorecardValues!!.responseId = mQuestionDatas!!.responseDatas[j].responseId
                mScorecardValues!!.value = mQuestionDatas!!.responseDatas[j].value
                mCallback!!.answer(mScorecardValues)
                Log.d(LOG_TAG, "Q R V" + mQuestionDatas!!.questionId + " " +
                        mQuestionDatas!!.responseDatas[j].responseId
                        + " " + mQuestionDatas!!.responseDatas[j].value)
                return
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = if (context is Activity) context else null
        mCallback = try {
            activity as OnAnswerSelectedListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                    + " must implement OnAnswerSelectedListener")
        }
    }

    interface OnAnswerSelectedListener {
        fun answer(scorecardValues: ScorecardValues?)
    }

    companion object {
        private val LOG_TAG = SurveyQuestionFragment::class.java.simpleName
        fun newInstance(QuestionDatas: String?): SurveyQuestionFragment {
            val fragment = SurveyQuestionFragment()
            val bundle = Bundle()
            bundle.putString(Constants.QUESTION_DATA, QuestionDatas)
            fragment.arguments = bundle
            return fragment
        }
    }
}