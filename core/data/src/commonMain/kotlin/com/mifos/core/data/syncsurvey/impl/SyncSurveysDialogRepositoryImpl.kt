/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.syncsurvey.impl

import com.mifos.core.data.syncsurvey.SyncSurveysDialogRepository
import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Default [SyncSurveysDialogRepository] backed by [DataManagerSurveys]. Mirrors
 * the legacy feature-local `SyncSurveysDialogRepositoryImp` byte-for-byte —
 * this move is package-only, not behaviour change.
 */
class SyncSurveysDialogRepositoryImpl(
    private val dataManagerSurveys: DataManagerSurveys,
) : SyncSurveysDialogRepository {

    override suspend fun syncSurveyInDatabase(survey: SurveyEntity) {
        dataManagerSurveys.syncSurveyInDatabase(survey)
    }

    override fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatasEntity,
    ): Flow<QuestionDatasEntity> {
        return dataManagerSurveys.syncQuestionDataInDatabase(surveyId, questionDatas)
    }

    override fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatasEntity,
    ): Flow<ResponseDatasEntity> {
        return dataManagerSurveys.syncResponseDataInDatabase(questionId, responseDatas)
    }

    override fun allSurvey(): Flow<List<SurveyEntity>> {
        return dataManagerSurveys.allSurvey
    }
}
