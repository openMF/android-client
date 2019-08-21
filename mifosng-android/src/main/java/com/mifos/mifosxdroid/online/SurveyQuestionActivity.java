/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SurveyPagerAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.SurveyQuestionFragment.OnAnswerSelectedListener;
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment;
import com.mifos.mifosxdroid.online.surveysubmit.SurveySubmitFragment.DisableSwipe;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.ScorecardValues;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestionActivity extends MifosBaseActivity implements
        OnAnswerSelectedListener, DisableSwipe, OnPageChangeListener {

    public static final String LOG_TAG = SurveyQuestionActivity.class.getSimpleName();

    @BindView(R.id.surveyPager)
    ViewPager mViewPager;

    @BindView(R.id.btnNext)
    Button btnNext;

    @BindView(R.id.tv_surveyEmpty)
    TextView tv_surveyEmpty;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private List<Fragment> fragments = new Vector<>();
    private List<ScorecardValues> listScorecardValues = new ArrayList<>();

    private Survey survey;
    private Scorecard mScorecard = new Scorecard();
    private ScorecardValues mScorecardValue = null;
    private HashMap<Integer, ScorecardValues> mMapScores = new HashMap<>();

    private int clientId;
    private int mCurrentQuestionPosition = 1;

    public Communicator fragmentCommunicator;
    private PagerAdapter mPagerAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_question);
        ButterKnife.bind(this);

        //Getting Survey Gson Object
        Intent mIntent = getIntent();
        survey = (new Gson()).fromJson(mIntent.getStringExtra(Constants.SURVEYS), Survey.class);
        clientId = mIntent.getIntExtra(Constants.CLIENT_ID, 1);
        setSubtitleToolbar();

        mPagerAdapter = new SurveyPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        loadSurvey(survey);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateAnswerList();
        mCurrentQuestionPosition = position + 1;
        setSubtitleToolbar();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @OnClick(R.id.btnNext)
    void onClickButtonNext() {
        updateAnswerList();
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        setSubtitleToolbar();
    }

    @Override
    public void answer(ScorecardValues scorecardValues) {
        this.mScorecardValue = scorecardValues;
    }

    public void loadSurvey(Survey survey) {
        if (survey != null) {
            if (survey.getQuestionDatas() != null && survey.getQuestionDatas().size() > 0) {
                for (int i = 0; i < survey.getQuestionDatas().size(); i++) {
                    fragments.add(SurveyQuestionFragment.newInstance((new Gson()).toJson(survey
                            .getQuestionDatas().get(i))));
                }
                fragments.add(SurveySubmitFragment.newInstance());
                mPagerAdapter.notifyDataSetChanged();

            } else {
                mViewPager.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                tv_surveyEmpty.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setUpScoreCard() {
        listScorecardValues.clear();
        for (Entry<Integer, ScorecardValues> map : mMapScores.entrySet()) {
            listScorecardValues.add(map.getValue());
        }
        mScorecard.setClientId(clientId);
        mScorecard.setUserId(PrefManager.getUserId());
        mScorecard.setCreatedOn(new Date());
        mScorecard.setScorecardValues(listScorecardValues);
    }

    public void updateAnswerList() {
        if (mScorecardValue != null) {
            Log.d(LOG_TAG, "" + mScorecardValue.getQuestionId() + mScorecardValue
                    .getResponseId() + mScorecardValue.getValue());
            mMapScores.put(mScorecardValue.getQuestionId(), mScorecardValue);
            mScorecardValue = null;
        }
        nextButtonState();

        if (fragmentCommunicator != null) {
            setUpScoreCard();
            fragmentCommunicator.passScoreCardData(mScorecard, survey.getId());
        }
    }

    public void nextButtonState() {
        if (mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
            btnNext.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    public void setSubtitleToolbar() {
        if (survey.getQuestionDatas().size() == 0) {
            mToolbar.setSubtitle((getResources().getString(R.string.survey_subtitle)));
        } else if (mCurrentQuestionPosition <= survey.getQuestionDatas().size()) {
            mToolbar.setSubtitle((mCurrentQuestionPosition) +
                    getResources().getString(R.string.slash) + survey.getQuestionDatas().size());
        } else {
            mToolbar.setSubtitle(getResources().getString(R.string.submit_survey));
        }
    }

    @Override
    public void disableSwipe() {
        mViewPager.beginFakeDrag();
        mToolbar.setSubtitle(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(Constants.ANSWERS, mMapScores);

    }

    @SuppressWarnings(value = "unchecked")
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapScores = (HashMap<Integer, ScorecardValues>)
                savedInstanceState.getSerializable(Constants.ANSWERS);
    }
}

