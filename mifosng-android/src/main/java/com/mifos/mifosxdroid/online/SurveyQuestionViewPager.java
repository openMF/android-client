/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SurveyPagerAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.ScorecardValues;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestionViewPager extends MifosBaseActivity implements
        SurveyQuestionFragment.OnAnswerSelectedListener,SurveyLastFragment.DisableSwipe {


    public Communicator fragmentCommunicator;

    @InjectView(R.id.surveyPager) ViewPager mViewPager;
    @InjectView(R.id.btnNext) Button btnNext;
    @InjectView(R.id.tv_surveyEmpty) TextView tv_surveyEmpty;
    @InjectView(R.id.toolbar) Toolbar mToolbar;
    private PagerAdapter mPagerAdapter = null;
    private List<Fragment> fragments = null;
    private Survey survey;
    private Scorecard mScorecard;
    private List<ScorecardValues> listScorecardValues;
    private ScorecardValues mScorecardValue = null;
    private int clientId;
    private int mCurrentQuestionPosition = 1;
    private HashMap<Integer, ScorecardValues> mMapScores = new HashMap<>();
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_survey_question);
        ButterKnife.inject(this);
        context = SurveyQuestionViewPager.this;
        mScorecard = new Scorecard();
        listScorecardValues = new ArrayList<>();
        fragments = new Vector<Fragment>();

        //Getting Survey Gson Object
        Intent mIntent = getIntent();
        survey = (new Gson()).fromJson(mIntent.getStringExtra("Survey"), Survey.class);
        clientId = mIntent.getIntExtra("ClientId",1);
        setSubtitleToolbar();


        this.mPagerAdapter = new SurveyPagerAdapter(super.getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(this.mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateAnswerList();
                mCurrentQuestionPosition = position + 1;
                setSubtitleToolbar();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateAnswerList();
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                setSubtitleToolbar();
            }
        });

        loadSurvey(survey);

    }

    @Override
    public void answer(ScorecardValues scorecardValues) {
        this.mScorecardValue = scorecardValues;
    }

    public void loadSurvey(Survey survey){

        if (survey != null) {

            if (survey.getQuestionDatas() != null && survey.getQuestionDatas().size() > 0) {
                for (int i = 0; i < survey.getQuestionDatas().size(); i++) {
                    fragments.add(SurveyQuestionFragment.newInstance((new Gson()).toJson(survey.getQuestionDatas().get(i))));
                }
                fragments.add(SurveyLastFragment.newInstance());
                mPagerAdapter.notifyDataSetChanged();

            }else {
                mViewPager.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                tv_surveyEmpty.setVisibility(View.VISIBLE);
            }

        }
    }

    public void setUpScoreCard(){
        listScorecardValues.clear();
        for(Map.Entry<Integer,ScorecardValues> map : mMapScores.entrySet()){
            listScorecardValues.add(map.getValue());
        }
        mScorecard.setClientId(clientId);
        mScorecard.setUserId(PrefManager.getUserId());
        mScorecard.setCreatedOn(new Date());
        mScorecard.setScorecardValues(listScorecardValues);
    }

    public void updateAnswerList(){

        if(mScorecardValue != null) {
            Log.d("SurveyViewPager" ,"" + mScorecardValue.getQuestionId() + mScorecardValue.getResponseId()+mScorecardValue.getValue());
            mMapScores.put(mScorecardValue.getQuestionId(),mScorecardValue);
            mScorecardValue = null;
        }
        nextButtonState();

        if (fragmentCommunicator != null){
            setUpScoreCard();
            fragmentCommunicator.passScoreCardData(mScorecard, survey.getId());
        }
    }

    public void nextButtonState(){
        if (mViewPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
            btnNext.setVisibility(View.GONE);
        }else
            btnNext.setVisibility(View.VISIBLE);
    }

    public void setSubtitleToolbar(){
        if(survey.getQuestionDatas().size()==0){
            mToolbar.setSubtitle(("0/0"));
        }else if(mCurrentQuestionPosition <= survey.getQuestionDatas().size()){
            mToolbar.setSubtitle((mCurrentQuestionPosition) +"/" + survey.getQuestionDatas().size());
        }else
            mToolbar.setSubtitle("Submit Survey");

    }

    @Override
    public void disableSwipe() {
        mViewPager.beginFakeDrag();
        mToolbar.setSubtitle(null);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("answers", mMapScores);

    }

    @SuppressWarnings (value="unchecked")
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapScores = (HashMap<Integer, ScorecardValues>) savedInstanceState.getSerializable("answers");
    }
}

