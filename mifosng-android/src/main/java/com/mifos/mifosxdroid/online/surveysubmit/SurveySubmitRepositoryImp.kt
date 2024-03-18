package com.mifos.mifosxdroid.online.surveysubmit

import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.core.objects.survey.Scorecard
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SurveySubmitRepositoryImp @Inject constructor(private val dataManagerSurveys: DataManagerSurveys) :
    SurveySubmitRepository {

    override fun submitScore(surveyId: Int, scorecardPayload: Scorecard?): Observable<Scorecard> {
        return dataManagerSurveys.submitScore(surveyId, scorecardPayload)
    }
}