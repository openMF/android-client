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
import com.mifos.room.entities.survey.SurveyEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

/**
 * @author
 */
interface SurveyService {
    @GET(APIEndPoint.SURVEYS)
    fun allSurveys(): Flow<List<SurveyEntity>>

    @GET(APIEndPoint.SURVEYS + "/{surveyId}")
    fun getSurvey(@Path("surveyId") surveyId: Int): Flow<SurveyEntity>

    @POST(APIEndPoint.SURVEYS + "/{surveyId}/scorecards")
    fun submitScore(
        @Path("surveyId") surveyId: Int,
        @Body scorecardPayload: Scorecard?,
    ): Flow<Scorecard>
}
