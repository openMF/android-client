/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.ClientChooseFragment;
import com.mifos.mifosxdroid.online.SurveyListFragment;
import com.mifos.mifosxdroid.online.SurveyQuestionViewPager;
import com.mifos.objects.survey.Survey;

public class SurveyActivity extends MifosBaseActivity implements SurveyListFragment.OnFragmentInteractionListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        replaceFragment(new ClientChooseFragment(), false, R.id.container);
    }

    @Override
    public void loadSurveyQuestion(Survey survey, int Clientid) {
        Intent myIntent = new Intent(SurveyActivity.this, SurveyQuestionViewPager.class);
        myIntent.putExtra("Survey", (new Gson()).toJson(survey));
        myIntent.putExtra("ClientId", Clientid);
        startActivity(myIntent);
    }
}
