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
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.objects.survey.Scorecard
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.FragmentSurveyLastBinding
import com.mifos.mifosxdroid.online.surveyquestion.Communicator
import com.mifos.mifosxdroid.online.surveyquestion.SurveyQuestionActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@AndroidEntryPoint
class SurveySubmitFragment : MifosBaseFragment(), Communicator {

    private lateinit var binding: FragmentSurveyLastBinding

    private lateinit var viewModel: SurveySubmitViewModel

    private var mDetachFragment: DisableSwipe? = null
    private var mScorecard: Scorecard? = null
    private var mSurveyId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSurveyLastBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SurveySubmitViewModel::class.java]

        viewModel.surveySubmitUiState.observe(viewLifecycleOwner) {
            when (it) {
                is SurveySubmitUiState.ShowError -> {
                    showProgressbar(false)
                    showError(it.message)
                }

                is SurveySubmitUiState.ShowProgressbar -> showProgressbar(true)

                is SurveySubmitUiState.ShowSurveySubmittedSuccessfully -> {
                    showProgressbar(false)
                    showSurveySubmittedSuccessfully(it.scorecard)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmit.setOnClickListener {
            submitScore()
        }
    }

    override fun passScoreCardData(scorecard: Scorecard, surveyId: Int) {
        mScorecard = scorecard
        mSurveyId = surveyId
        if (isAdded) {
            val submitText = resources.getString(R.string.attempt_question) +
                    mScorecard?.scorecardValues?.size
            binding.surveySubmitTextView.text = submitText
            binding.btnSubmit.text = resources.getString(R.string.submit_survey)
        }
    }

    private fun submitScore() {
        if ((mScorecard?.scorecardValues?.size ?: 0) >= 1) {
            mDetachFragment?.disableSwipe()
            binding.btnSubmit.text = resources.getString(R.string.submitting_surveys)
            binding.btnSubmit.isEnabled = false
            binding.btnSubmit.visibility = View.GONE
            viewModel.submitSurvey(mSurveyId, mScorecard)
        } else {
            Toast.makeText(
                context, resources
                    .getString(R.string.please_attempt_atleast_one_question), Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun showSurveySubmittedSuccessfully(scorecard: Scorecard?) {
        Toast.makeText(
            context, resources.getString(R.string.scorecard_created_successfully),
            Toast.LENGTH_LONG
        ).show()
        binding.surveySubmitTextView.text =
            resources.getString(R.string.survey_successfully_submitted)
        binding.btnSubmit.visibility = View.GONE
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        binding.surveySubmitTextView.text = resources.getString(R.string.error_submitting_survey)
        binding.btnSubmit.visibility = View.GONE
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressBar()
        } else {
            hideMifosProgressBar()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as SurveyQuestionActivity).fragmentCommunicator = this
        val activity = context as Activity
        mDetachFragment = try {
            activity as DisableSwipe
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement OnAnswerSelectedListener"
            )
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