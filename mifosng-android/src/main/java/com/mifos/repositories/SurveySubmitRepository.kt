package com.mifos.repositories

import com.mifos.objects.survey.Scorecard
import rx.Observable

interface SurveySubmitRepository {

    fun submitScore(surveyId: Int, scorecardPayload: Scorecard?): Observable<Scorecard>
}