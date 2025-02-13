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
import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.ResponseDatas
import com.mifos.room.entities.survey.Survey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncSurveysDialogRepositoryImp @Inject constructor(private val dataManagerSurvey: DataManagerSurveys) :
    SyncSurveysDialogRepository {

    override suspend fun syncSurveyInDatabase(survey: Survey) {
        dataManagerSurvey.syncSurveyInDatabase(survey)
    }

    override fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatas,
    ): Flow<QuestionDatas> {
        return dataManagerSurvey.syncQuestionDataInDatabase(surveyId, questionDatas)
    }

    override fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatas,
    ): Flow<ResponseDatas> {
        return dataManagerSurvey.syncResponseDataInDatabase(questionId, responseDatas)
    }

    override fun allSurvey(): Flow<List<Survey>> {
        return dataManagerSurvey.allSurvey
    }
}
