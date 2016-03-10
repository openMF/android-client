/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import com.mifos.mifosxdroid.adapters.SurveyPagerAdapter;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.ScorecardValues;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.mifos.App;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.survey.Survey;
import com.mifos.api.model.ScorecardPayload;

import com.mifos.utils.MyPreference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestion extends MifosBaseActivity implements SurveyQuestionFragment.OnAnswerSelectedListener {
    private static final int CONTENT_VIEW_ID = 10101010;
    public Communicator fragmentCommunicator;
    private ViewPager pager = null;
    public static final String PREFS_NAME = "MY_PREFS";
    private Button btnNext;
    private Button btnSubmit;
    private Button btnPrevious;
    AppCompatActivity activity;
    private PagerAdapter mPagerAdapter = null;
    public static final String ID = "id";
    public static int surveyId;
    public static int qid;
    public static int pchk;
    public static int pfqid;
    public static int pfrid;
    public static int pfrvalue;
    public static boolean pfselect;
    private List<Fragment> fragments = null;
    SharedPreferences sharedPreferences;
    private String qs;
    ViewPager.OnPageChangeListener mPageChangeListener;
    MyPreference myPreference;
    Context context;
    List<ScorecardValues> scorecardValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_survey_question);
        context = SurveyQuestion.this;
        myPreference = new MyPreference();
        myPreference.resetScorecard(this);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        fragments = new Vector<Fragment>();
        showBackButton();
        Intent mIntent = getIntent();
        surveyId = mIntent.getIntExtra("SurveyId", 0);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putInt("SURVEY_ID", surveyId);
        edt.commit();


        App.apiManager.getSurvey(surveyId, new Callback<Survey>() {
            @Override
            public void success(final Survey survey, Response response) {

                if (survey != null) {

                    String answer[] = new String[10];

                    ArrayList<String> answerList = new ArrayList<String>();

                    if (survey.getQuestionDatas() != null && survey.getQuestionDatas().size() > 0) {
                        for (int i = 0; i < survey.getQuestionDatas().size(); i++) {
                            qs = survey.getQuestionDatas().get(i).getText();
                            qid = survey.getQuestionDatas().get(i).getQuestionId();
                            if (survey.getQuestionDatas().get(i).getResponseDatas().size() > 0) {
                                for (int j = 0; j < survey.getQuestionDatas().get(i).getResponseDatas().size(); j++) {
                                    answer[j] = survey.getQuestionDatas().get(i).getResponseDatas().get(j).getText();

                                    answerList.add(answer[j]);
                                }
                            }
                            int qSize = survey.getQuestionDatas().size();
                            String[] answerArr = new String[answerList.size()];
                            answerArr = answerList.toArray(answerArr);
                            fragments.add(SurveyQuestionFragment.newInstance(i, qs, surveyId, answerArr, qSize));
                            answerList.clear();
                        }

                        fragments.add(SurveyLastFragment.newInstance(1, "You have reached the end of Survey"));
                        mPagerAdapter.notifyDataSetChanged();

                    }

                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(SurveyQuestion.this, "Survey not found.", Toast.LENGTH_SHORT).show();

            }
        });

        this.mPagerAdapter = new SurveyPagerAdapter(super.getSupportFragmentManager(), fragments);

        pager = (ViewPager) super.findViewById(R.id.surveyPager);

        pager.setAdapter(this.mPagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                ScorecardValues scorevalue = new ScorecardValues();

                scorevalue.setQuestionId(pfqid);
                scorevalue.setResponseId(pfrid);
                scorevalue.setValue(pfrvalue);

                if (myPreference.checkScoreQid(context, scorevalue)) {

                    Log.i("previous removed: ", String.valueOf(pfrid));

                } else {
                    myPreference.addScorecard(context, scorevalue);
                }

                if (pager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
                    btnNext.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.GONE);
                }
                if (pager.getCurrentItem() == 0) {
                    btnPrevious.setVisibility(View.GONE);

                } else {
                    btnPrevious.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        Button btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ScorecardValues scorevalue = new ScorecardValues();

                scorevalue.setQuestionId(pfqid);
                scorevalue.setResponseId(pfrid);
                scorevalue.setValue(pfrvalue);

                if (myPreference.checkScoreQid(context, scorevalue)) {

                    Log.i("previous removed: ", String.valueOf(pfrid));

                } else {
                    myPreference.addScorecard(context, scorevalue);
                }
                int current = pager.getCurrentItem();
                final Toast toast = Toast.makeText(context, "You have not selected any option.", Toast.LENGTH_SHORT);

                if (pchk == 1 & current < fragments.size()) {
                    pager.setCurrentItem(current + 1, true);
                    toast.cancel();
                }
                {
                    toast.show();
                    pchk = 0;
                }


            }
        });
        Button btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int current = pager.getCurrentItem();

                if (current < fragments.size())
                    pager.setCurrentItem(current - 1, true);


            }
        });

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scorecardValues = myPreference.getScorecards(context);
                int listSize = scorecardValues.size();

                for (int i = 0; i < listSize; i++) {
                    Log.i("Response Value: ", scorecardValues.get(i).getValue().toString());
                    Log.i("Question Id: ", scorecardValues.get(i).getQuestionId().toString());
                    Log.i("Response Id: ", scorecardValues.get(i).getResponseId().toString());
                }

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                int clientId = sharedPreferences.getInt("CLIENT_ID", 0);
                int userId = sharedPreferences.getInt("USER_ID", 0);
                int surveyId = sharedPreferences.getInt("SURVEY_ID", 0);
                Date date = new Date();
                ScorecardPayload scorecardPayload = new ScorecardPayload();

                scorecardPayload.setUserId(userId);
                scorecardPayload.setClientId(clientId);
                scorecardPayload.setCreatedOn(date);
                scorecardPayload.setScorecardValues(scorecardValues);


                App.apiManager.submitScore(surveyId, scorecardPayload, new Callback<Scorecard>() {
                    @Override
                    public void success(Scorecard scorecard, Response response) {

                        Toast.makeText(context, "Scorecard created successfully", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(context, "Try again", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }


    @Override
    public void answer(int id, int fqid, int frid, int frValue, int chk) {
        pfqid = fqid;
        pfrid = frid;
        pfrvalue = frValue;
        pchk = chk;


    }
}
