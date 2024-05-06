/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.survey.Scorecard
import com.mifos.core.objects.survey.Survey
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import rx.Observable

/**
 * @author
 */
interface SurveyService {
    @get:GET(APIEndPoint.SURVEYS)
    val allSurveys: Observable<List<Survey>>

    @GET(APIEndPoint.SURVEYS + "/{surveyId}")
    fun getSurvey(@Path("surveyId") surveyId: Int): Observable<Survey>

    @POST(APIEndPoint.SURVEYS + "/{surveyId}/scorecards")
    fun submitScore(
        @Path("surveyId") surveyId: Int,
        @Body scorecardPayload: Scorecard?
    ): Observable<Scorecard>
}