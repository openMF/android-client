/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.SurveyPagerAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.SurveyQuestionFragment.OnAnswerSelectedListener
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment.Companion.newInstance
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment.DisableSwipe
import com.mifos.objects.survey.Scorecard
import com.mifos.objects.survey.ScorecardValues
import com.mifos.objects.survey.Survey
import com.mifos.utils.Constants
import com.mifos.utils.PrefManager
import java.util.*

/**
 * Created by Nasim Banu on 28,January,2016.
 */
class SurveyQuestionActivity : MifosBaseActivity(), OnAnswerSelectedListener, DisableSwipe, OnPageChangeListener {
    @JvmField
    @BindView(R.id.surveyPager)
    var mViewPager: ViewPager? = null

    @JvmField
    @BindView(R.id.btnNext)
    var btnNext: Button? = null

    @JvmField
    @BindView(R.id.tv_surveyEmpty)
    var tv_surveyEmpty: TextView? = null

    @JvmField
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null
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
        setContentView(R.layout.activity_survey_question)
        ButterKnife.bind(this)

        //Getting Survey Gson Object
        val mIntent = intent
        survey = Gson().fromJson(mIntent.getStringExtra(Constants.SURVEYS), Survey::class.java)
        clientId = mIntent.getIntExtra(Constants.CLIENT_ID, 1)
        setSubtitleToolbar()
        mPagerAdapter = SurveyPagerAdapter(supportFragmentManager, fragments)
        mViewPager!!.adapter = mPagerAdapter
        mViewPager!!.addOnPageChangeListener(this)
        loadSurvey(survey)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        updateAnswerList()
        mCurrentQuestionPosition = position + 1
        setSubtitleToolbar()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    @OnClick(R.id.btnNext)
    fun onClickButtonNext() {
        updateAnswerList()
        mViewPager!!.setCurrentItem(mViewPager!!.currentItem + 1, true)
        setSubtitleToolbar()
    }

    override fun answer(scorecardValues: ScorecardValues?) {
        mScorecardValue = scorecardValues
    }

    fun loadSurvey(survey: Survey?) {
        if (survey != null) {
            if (survey.questionDatas != null && survey.questionDatas.size > 0) {
                for (i in survey.questionDatas.indices) {
                    fragments.add(SurveyQuestionFragment.newInstance(Gson().toJson(survey
                            .questionDatas[i])))
                }
                fragments.add(newInstance())
                mPagerAdapter!!.notifyDataSetChanged()
            } else {
                mViewPager!!.visibility = View.GONE
                btnNext!!.visibility = View.GONE
                tv_surveyEmpty!!.visibility = View.VISIBLE
            }
        }
    }

    fun setUpScoreCard() {
        listScorecardValues.clear()
        for ((_, value) in mMapScores) {
            listScorecardValues.add(value)
        }
        mScorecard.clientId = clientId
        mScorecard.userId = PrefManager.getUserId()
        mScorecard.createdOn = Date()
        mScorecard.scorecardValues = listScorecardValues
    }

    fun updateAnswerList() {
        if (mScorecardValue != null) {
            Log.d(LOG_TAG, "" + mScorecardValue!!.questionId + mScorecardValue!!
                    .responseId + mScorecardValue!!.value)
            mMapScores[mScorecardValue!!.questionId] = mScorecardValue!!
            mScorecardValue = null
        }
        nextButtonState()
        if (fragmentCommunicator != null) {
            setUpScoreCard()
            fragmentCommunicator!!.passScoreCardData(mScorecard, survey!!.id)
        }
    }

    fun nextButtonState() {
        if (mViewPager!!.currentItem == mPagerAdapter!!.count - 1) {
            btnNext!!.visibility = View.GONE
        } else {
            btnNext!!.visibility = View.VISIBLE
        }
    }

    fun setSubtitleToolbar() {
        if (survey!!.questionDatas.size == 0) {
            mToolbar!!.subtitle = resources.getString(R.string.survey_subtitle)
        } else if (mCurrentQuestionPosition <= survey!!.questionDatas.size) {
            mToolbar!!.subtitle = mCurrentQuestionPosition.toString() +
                    resources.getString(R.string.slash) + survey!!.questionDatas.size
        } else {
            mToolbar!!.subtitle = resources.getString(R.string.submit_survey)
        }
    }

    override fun disableSwipe() {
        mViewPager!!.beginFakeDrag()
        mToolbar!!.subtitle = null
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putSerializable(Constants.ANSWERS, mMapScores)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mMapScores = savedInstanceState.getSerializable(Constants.ANSWERS) as HashMap<Int, ScorecardValues>
    }

    companion object {
        val LOG_TAG = SurveyQuestionActivity::class.java.simpleName
    }
}