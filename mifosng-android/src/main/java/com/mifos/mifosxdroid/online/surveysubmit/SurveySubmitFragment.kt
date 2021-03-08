/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.surveysubmit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.online.Communicator
import com.mifos.mifosxdroid.online.SurveyQuestionActivity
import com.mifos.objects.survey.Scorecard
import javax.inject.Inject

/**
 * Created by Nasim Banu on 28,January,2016.
 */
class SurveySubmitFragment : MifosBaseFragment(), Communicator, SurveySubmitMvpView {

    @JvmField
    @BindView(R.id.btn_submit)
    var btn_submit: Button? = null

    @JvmField
    @BindView(R.id.survey_submit_textView)
    var tv_submit: TextView? = null

    @JvmField
    @Inject
    var mSurveySubmitPresenter: SurveySubmitPresenter? = null
    private var mDetachFragment: DisableSwipe? = null
    private var mScorecard: Scorecard? = null
    private var mSurveyId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_survey_last, container, false)
        ButterKnife.bind(this, view)
        mSurveySubmitPresenter!!.attachView(this)
        return view
    }

    override fun passScoreCardData(scorecard: Scorecard, surveyId: Int) {
        mScorecard = scorecard
        mSurveyId = surveyId
        if (isAdded) {
            val submitText = resources.getString(R.string.attempt_question) +
                    mScorecard!!.scorecardValues.size
            tv_submit!!.text = submitText
            btn_submit!!.text = resources.getString(R.string.submit_survey)
        }
    }

    @OnClick(R.id.btn_submit)
    fun submitScore() {
        if (mScorecard!!.scorecardValues.size >= 1) {
            mDetachFragment!!.disableSwipe()
            btn_submit!!.text = resources.getString(R.string.submitting_surveys)
            btn_submit!!.isEnabled = false
            btn_submit!!.visibility = View.GONE
            mSurveySubmitPresenter!!.submitSurvey(mSurveyId, mScorecard)
        } else {
            Toast.makeText(getContext(), resources
                    .getString(R.string.please_attempt_atleast_one_question), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun showSurveySubmittedSuccessfully(scorecard: Scorecard?) {
        Toast.makeText(getContext(), resources.getString(R.string.scorecard_created_successfully),
                Toast.LENGTH_LONG).show()
        tv_submit!!.text = resources.getString(R.string.survey_successfully_submitted)
        btn_submit!!.visibility = View.GONE
    }

    override fun showError(errorMessage: Int) {
        Toast.makeText(getContext(), resources.getString(errorMessage), Toast.LENGTH_LONG).show()
        tv_submit!!.text = resources.getString(R.string.error_submitting_survey)
        btn_submit!!.visibility = View.GONE
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressBar()
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSurveySubmitPresenter!!.detachView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as SurveyQuestionActivity).fragmentCommunicator = this
        val activity = context as Activity
        mDetachFragment = try {
            activity as DisableSwipe
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString()
                    + " must implement OnAnswerSelectedListener")
        }
    }

    interface DisableSwipe {
        fun disableSwipe()
    }

    companion object {
        @JvmStatic
        fun newInstance(): SurveySubmitFragment {
            val fragment = SurveySubmitFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }
}