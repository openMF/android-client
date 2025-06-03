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

import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import kotlinx.coroutines.flow.Flow

class SyncSurveysDialogRepositoryImp(private val dataManagerSurvey: DataManagerSurveys) :
    SyncSurveysDialogRepository {

    override suspend fun syncSurveyInDatabase(survey: SurveyEntity) {
        dataManagerSurvey.syncSurveyInDatabase(survey)
    }

    override fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatasEntity,
    ): Flow<QuestionDatasEntity> {
        return dataManagerSurvey.syncQuestionDataInDatabase(surveyId, questionDatas)
    }

    override fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatasEntity,
    ): Flow<ResponseDatasEntity> {
        return dataManagerSurvey.syncResponseDataInDatabase(questionId, responseDatas)
    }

    override fun allSurvey(): Flow<List<SurveyEntity>> {
        return dataManagerSurvey.allSurvey
    }
}
