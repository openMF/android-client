/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.objects.surveys.Scorecard
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.survey.Survey
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import rx.Observable

/**
 * @author
 */
interface SurveyService {
    @GET(APIEndPoint.SURVEYS)
    suspend fun allSurveys(): List<Survey>

    @GET(APIEndPoint.SURVEYS + "/{surveyId}")
    fun getSurvey(@Path("surveyId") surveyId: Int): Observable<Survey>

    @POST(APIEndPoint.SURVEYS + "/{surveyId}/scorecards")
    fun submitScore(
        @Path("surveyId") surveyId: Int,
        @Body scorecardPayload: Scorecard?,
    ): Observable<Scorecard>
}
