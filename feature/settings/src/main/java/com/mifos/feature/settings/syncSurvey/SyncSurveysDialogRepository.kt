/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.syncSurvey

import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.ResponseDatas
import com.mifos.room.entities.survey.Survey
import kotlinx.coroutines.flow.Flow

interface SyncSurveysDialogRepository {

    suspend fun syncSurveyInDatabase(survey: Survey)

    fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatas,
    ): Flow<QuestionDatas>

    fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatas,
    ): Flow<ResponseDatas>

    fun allSurvey(): Flow<List<Survey>>
}
