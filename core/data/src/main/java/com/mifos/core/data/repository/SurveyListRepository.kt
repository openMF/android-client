/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.ResponseDatas
import com.mifos.room.entities.survey.Survey
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface SurveyListRepository {

    fun allSurvey(): Flow<List<Survey>>

    fun databaseSurveys(): Flow<List<Survey>>

    fun getDatabaseQuestionData(surveyId: Int): Flow<List<QuestionDatas>>

    fun getDatabaseResponseDatas(questionId: Int): Flow<List<ResponseDatas>>
}
