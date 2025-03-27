/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.SurveyListRepository
import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SurveyListRepositoryImp(
    private val dataManagerSurveys: DataManagerSurveys,
) : SurveyListRepository {

    override fun allSurvey(): Flow<List<SurveyEntity>> {
        return dataManagerSurveys.allSurvey
    }

    override fun databaseSurveys(): Flow<List<SurveyEntity>> {
        return dataManagerSurveys.databaseSurveys
    }

    override fun getDatabaseQuestionData(surveyId: Int): Flow<List<QuestionDatasEntity>> {
        return dataManagerSurveys.getDatabaseQuestionData(surveyId)
    }

    override fun getDatabaseResponseDatas(questionId: Int): Flow<List<ResponseDatasEntity>> {
        return dataManagerSurveys.getDatabaseResponseDatas(questionId)
    }
}
