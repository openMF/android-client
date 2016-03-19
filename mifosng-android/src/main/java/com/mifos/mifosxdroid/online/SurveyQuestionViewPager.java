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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SurveyPagerAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.MyPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyQuestionViewPager extends MifosBaseActivity implements SurveyQuestionFragment.OnAnswerSelectedListener {

    public Communicator fragmentCommunicator;
    private ViewPager pager = null;
    private Button btnNext;
    private PagerAdapter mPagerAdapter = null;
    public static final String ID = "id";
    public static int surveyId;
    public static int qid;
    public static int pfqid;
    public static int pfrid;
    public static int pfrvalue;
    private List<Fragment> fragments = null;
    private String qs;
    ViewPager.OnPageChangeListener mPageChangeListener;
    MyPreference myPreference;
    Context context;
    private Survey surveys;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_survey_question);
        Intent intent = getIntent();
        surveys = (new Gson()).fromJson(intent.getStringExtra("SURVEY_DETAILS"), Survey.class);
        context = SurveyQuestionViewPager.this;
        myPreference = new MyPreference();
        myPreference.resetScorecard(this);
        btnNext = (Button) findViewById(R.id.btnNext);

        fragments = new Vector<Fragment>();
        loadsurvey(surveys);

        this.mPagerAdapter = new SurveyPagerAdapter(super.getSupportFragmentManager(), fragments);
        pager = (ViewPager) super.findViewById(R.id.surveyPager);
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
        Button btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fragmentCommunicator != null)
                    fragmentCommunicator.passDataToFragment(pfqid, pfrid, pfrvalue);
                int current = pager.getCurrentItem();

                if (current < fragments.size())
                    pager.setCurrentItem(current + 1, true);

            }
        });


    }

    @Override
    public void answer(int id, int fqid, int frid, int frValue) {
        pfqid = fqid;
        pfrid = frid;
        pfrvalue = frValue;

    }

    public void loadsurvey(Survey survey){
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

                    String[] answerArr = new String[answerList.size()];
                    answerArr = answerList.toArray(answerArr);
                    fragments.add(SurveyQuestionFragment.newInstance(i, qs, surveyId, answerArr));
                    answerList.clear();
                }

                fragments.add(SurveyLastFragment.newInstance(1, "You have reached the end of Survey"));
                mPagerAdapter.notifyDataSetChanged();

            }

        }
    }

}

