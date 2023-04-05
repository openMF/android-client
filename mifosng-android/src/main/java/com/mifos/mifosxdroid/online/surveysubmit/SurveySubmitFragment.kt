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
import android.widget.Toast
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.FragmentSurveyLastBinding
import com.mifos.mifosxdroid.online.Communicator
import com.mifos.mifosxdroid.online.SurveyQuestionActivity
import com.mifos.objects.survey.Scorecard
import javax.inject.Inject

/**
 * Created by Nasim Banu on 28,January,2016.
 */
class SurveySubmitFragment : MifosBaseFragment(), Communicator, SurveySubmitMvpView {

    private lateinit var binding: FragmentSurveyLastBinding

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
        binding = FragmentSurveyLastBinding.inflate(inflater,container,false)
        mSurveySubmitPresenter!!.attachView(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener { submitScore() }
    }

    override fun passScoreCardData(scorecard: Scorecard, surveyId: Int) {
        mScorecard = scorecard
        mSurveyId = surveyId
        if (isAdded) {
            val submitText = resources.getString(R.string.attempt_question) +
                    mScorecard!!.scorecardValues.size
            binding.surveySubmitTextView.text = submitText
            binding.btnSubmit.text = resources.getString(R.string.submit_survey)
        }
    }

    fun submitScore() {
        if (mScorecard!!.scorecardValues.size >= 1) {
            mDetachFragment!!.disableSwipe()
            binding.btnSubmit.text = resources.getString(R.string.submitting_surveys)
            binding.btnSubmit.isEnabled = false
            binding.btnSubmit.visibility = View.GONE
            mSurveySubmitPresenter!!.submitSurvey(mSurveyId, mScorecard)
        } else {
            Toast.makeText(
                context, resources
                    .getString(R.string.please_attempt_atleast_one_question), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun showSurveySubmittedSuccessfully(scorecard: Scorecard?) {
        Toast.makeText(
            context, resources.getString(R.string.scorecard_created_successfully),
                Toast.LENGTH_LONG).show()
        binding.surveySubmitTextView.text = resources.getString(R.string.survey_successfully_submitted)
        binding.btnSubmit.visibility = View.GONE
    }

    override fun showError(errorMessage: Int) {
        Toast.makeText(context, resources.getString(errorMessage), Toast.LENGTH_LONG).show()
        binding.surveySubmitTextView.text = resources.getString(R.string.error_submitting_survey)
        binding.btnSubmit.visibility = View.GONE
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