package com.mifos.mifosxdroid.online.surveysubmit

import com.mifos.core.objects.survey.Scorecard
import rx.Observable

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface SurveySubmitRepository {

    fun submitScore(surveyId: Int, scorecardPayload: Scorecard?): Observable<Scorecard>
}