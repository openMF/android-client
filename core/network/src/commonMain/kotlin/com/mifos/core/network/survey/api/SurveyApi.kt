/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.survey.api

import com.mifos.core.model.objects.surveys.Scorecard
import com.mifos.core.network.APIEndPoint
import com.mifos.room.survey.entity.SurveyEntity
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

/** Fineract `survey` domain endpoints. */
interface SurveyApi {

    @GET(APIEndPoint.SURVEYS)
    suspend fun getAllSurveys(): List<SurveyEntity>

    @GET(APIEndPoint.SURVEYS + "/{surveyId}")
    suspend fun getSurvey(@Path("surveyId") surveyId: Int): SurveyEntity

    /** Submit a completed scorecard for the given survey. */
    @POST(APIEndPoint.SURVEYS + "/{surveyId}/scorecards")
    suspend fun submitScore(
        @Path("surveyId") surveyId: Int,
        @Body scorecardPayload: Scorecard?,
    ): Scorecard
}
