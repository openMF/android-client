package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerSurveys
import com.mifos.objects.survey.Scorecard
import rx.Observable
import javax.inject.Inject

class SurveySubmitRepositoryImp @Inject constructor(private val dataManagerSurveys: DataManagerSurveys) :
    SurveySubmitRepository {

    override fun submitScore(surveyId: Int, scorecardPayload: Scorecard?): Observable<Scorecard> {
        return dataManagerSurveys.submitScore(surveyId, scorecardPayload)
    }
}