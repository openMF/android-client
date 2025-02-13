/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.ResponseDatas
import com.mifos.room.entities.survey.Survey
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 12/02/2025 (9:33 PM)
 */
@Dao
interface SurveyDao {

    @Insert
    suspend fun insertSurvey(survey: Survey)

    @Insert
    suspend fun insertQuestionData(questionData: QuestionDatas)

    @Insert
    suspend fun insertResponseData(responseDatas: ResponseDatas)

    @Query("SELECT * FROM Survey")
    fun getAllSurveys(): Flow<List<Survey>>

    @Query("SELECT * FROM QuestionDatas WHERE surveyId = :surveyId ORDER BY sequenceNo ASC")
    fun getQuestionDatas(surveyId: Int): Flow<List<QuestionDatas>>

    @Query("SELECT * FROM ResponseDatas WHERE questionId = :questionId ORDER BY sequenceNo ASC")
    fun getResponseDatas(questionId: Int): Flow<List<ResponseDatas>>
}
