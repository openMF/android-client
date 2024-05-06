/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.surveyquestion

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.survey.Scorecard
import com.mifos.core.objects.survey.ScorecardValues
import com.mifos.core.objects.survey.Survey
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SurveyPagerAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivitySurveyQuestionBinding
import com.mifos.mifosxdroid.online.surveyquestion.SurveyQuestionFragment.OnAnswerSelectedListener
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment.Companion.newInstance
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment.DisableSwipe
import com.mifos.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.Vector

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@AndroidEntryPoint
class SurveyQuestionActivity : MifosBaseActivity(), OnAnswerSelectedListener, DisableSwipe,
    OnPageChangeListener {

    private lateinit var binding: ActivitySurveyQuestionBinding

    private val fragments: MutableList<Fragment> = Vector()
    private val listScorecardValues: MutableList<ScorecardValues> = ArrayList()
    private var survey: Survey? = null
    private val mScorecard = Scorecard()
    private var mScorecardValue: ScorecardValues? = null
    private var mMapScores = HashMap<Int, ScorecardValues>()
    private var clientId = 0
    private var mCurrentQuestionPosition = 1
    var fragmentCommunicator: Communicator? = null
    private var mPagerAdapter: PagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        //Getting Survey Gson Object
        val mIntent = intent
        survey = Gson().fromJson(mIntent.getStringExtra(Constants.SURVEYS), Survey::class.java)
        clientId = mIntent.getIntExtra(Constants.CLIENT_ID, 1)
        setSubtitleToolbar()
        mPagerAdapter = SurveyPagerAdapter(supportFragmentManager, fragments)
        binding.surveyPager.adapter = mPagerAdapter
        binding.surveyPager.addOnPageChangeListener(this)
        loadSurvey(survey)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnNext.setOnClickListener {
            onClickButtonNext()
        }

    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        updateAnswerList()
        mCurrentQuestionPosition = position + 1
        setSubtitleToolbar()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    private fun onClickButtonNext() {
        updateAnswerList()
        binding.surveyPager.setCurrentItem(binding.surveyPager.currentItem + 1, true)
        setSubtitleToolbar()
    }

    override fun answer(scorecardValues: ScorecardValues?) {
        mScorecardValue = scorecardValues
    }

    private fun loadSurvey(survey: Survey?) {
        if (survey != null) {
            if (survey.questionDatas.isNotEmpty()) {
                for (i in survey.questionDatas.indices) {
                    fragments.add(
                        SurveyQuestionFragment.newInstance(
                            Gson().toJson(
                                survey
                                    .questionDatas[i]
                            )
                        )
                    )
                }
                fragments.add(newInstance())
                mPagerAdapter!!.notifyDataSetChanged()
            } else {
                binding.surveyPager.visibility = View.GONE
                binding.btnNext.visibility = View.GONE
                binding.tvSurveyEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpScoreCard() {
        listScorecardValues.clear()
        for ((_, value) in mMapScores) {
            listScorecardValues.add(value)
        }
        mScorecard.clientId = clientId
        mScorecard.userId = PrefManager.getUserId()
        mScorecard.createdOn = Date()
        mScorecard.scorecardValues = listScorecardValues
    }

    private fun updateAnswerList() {
        if (mScorecardValue != null) {
            Log.d(
                LOG_TAG,
                "" + mScorecardValue?.questionId + mScorecardValue?.responseId + mScorecardValue?.value
            )
            mMapScores[mScorecardValue?.questionId!!] = mScorecardValue!!
            mScorecardValue = null
        }
        nextButtonState()
        if (fragmentCommunicator != null) {
            setUpScoreCard()
            fragmentCommunicator?.passScoreCardData(mScorecard, survey!!.id)
        }
    }

    private fun nextButtonState() {
        if (binding.surveyPager.currentItem == mPagerAdapter!!.count - 1) {
            binding.btnNext.visibility = View.GONE
        } else {
            binding.btnNext.visibility = View.VISIBLE
        }
    }

    private fun setSubtitleToolbar() {
        if (survey!!.questionDatas.size == 0) {
            toolbar?.subtitle = resources.getString(R.string.survey_subtitle)
        } else if (mCurrentQuestionPosition <= survey!!.questionDatas.size) {
            toolbar?.subtitle = mCurrentQuestionPosition.toString() +
                    resources.getString(R.string.slash) + survey!!.questionDatas.size
        } else {
            toolbar?.subtitle = resources.getString(R.string.submit_survey)
        }
    }

    override fun disableSwipe() {
        binding.surveyPager.beginFakeDrag()
        toolbar?.subtitle = null
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putSerializable(Constants.ANSWERS, mMapScores)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mMapScores =
            savedInstanceState.getSerializable(Constants.ANSWERS) as HashMap<Int, ScorecardValues>
    }

    companion object {
        val LOG_TAG = SurveyQuestionActivity::class.java.simpleName
    }
}