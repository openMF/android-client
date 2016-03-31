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
import java.util.List;
import java.util.Vector;
import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestionViewPager extends MifosBaseActivity implements SurveyQuestionFragment.OnAnswerSelectedListener {


    public Communicator fragmentCommunicator;

    @InjectView(R.id.surveyPager) ViewPager pager;
    @InjectView(R.id.btnNext) Button btnNext;
    @InjectView(R.id.tv_surveyEmpty) TextView tv_surveyEmpty;
    private PagerAdapter mPagerAdapter = null;
    private List<Fragment> fragments = null;;
    private Survey survey;
    private Scorecard mScorecard;
    private List<ScorecardValues> listScorecardValues;
    private ScorecardValues mScorecardValue;
    private int clientId;
    ViewPager.OnPageChangeListener mPageChangeListener;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_survey_question);
        ButterKnife.inject(this);
        context = SurveyQuestionViewPager.this;
        mScorecard = new Scorecard();
        listScorecardValues = new ArrayList<>();
        this.mScorecardValue = new ScorecardValues();
        fragments = new Vector<Fragment>();

        //Getting Survey Gson Object
        Intent mIntent = getIntent();
        survey = (new Gson()).fromJson(mIntent.getStringExtra("Survey"), Survey.class);
        clientId = mIntent.getIntExtra("ClientId",1);


        this.mPagerAdapter = new SurveyPagerAdapter(super.getSupportFragmentManager(), fragments);
        pager.setAdapter(this.mPagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (pager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
                    btnNext.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (pager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
                    btnNext.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Called when the scroll state changes:
                // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            }

        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fragmentCommunicator != null){
                    setUpScoreCard();
                    fragmentCommunicator.createScoreCard(mScorecard, survey.getId());
                }

                int current = pager.getCurrentItem();

                if (current < fragments.size()){
                    pager.setCurrentItem(current + 1, true);
                    if(mScorecardValue != null){
                        listScorecardValues.add(mScorecardValue);
                        mScorecardValue = null;
                    }
                }


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
                pager.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                tv_surveyEmpty.setVisibility(View.VISIBLE);
            }

        }
    }

    public void setUpScoreCard(){
        mScorecard.setClientId(clientId);
        mScorecard.setUserId(PrefManager.getUserId());
        mScorecard.setCreatedOn(new Date());
        mScorecard.setScorecardValues(listScorecardValues);
    }

}

