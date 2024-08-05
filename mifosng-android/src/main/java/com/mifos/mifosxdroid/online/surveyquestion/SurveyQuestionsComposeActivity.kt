/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.surveyquestion

import android.os.Bundle
import android.util.Log
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.survey.Scorecard
import com.mifos.core.objects.survey.ScorecardValues
import com.mifos.core.objects.survey.Survey
import com.mifos.feature.client.clientSurveyQuestion.SurveyQuestionScreen
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@AndroidEntryPoint
class SurveyQuestionActivity : MifosBaseActivity() {

    private var survey: Survey? = null
    private var mMapScores = HashMap<Int, ScorecardValues>()
    private var clientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mIntent = intent
        survey = Gson().fromJson(mIntent.getStringExtra(Constants.SURVEYS), Survey::class.java)
        clientId = mIntent.getIntExtra(Constants.CLIENT_ID, 1)

        setContentView(
            ComposeView(this).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    SurveyQuestionScreen(
                        navigateBack = { this@SurveyQuestionActivity.finish() },
                        survey = survey,
                        clientId = clientId
                    )
                }
            }
        )
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