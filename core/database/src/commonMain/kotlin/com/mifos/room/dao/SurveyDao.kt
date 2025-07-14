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

import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import com.mifos.room.utils.Dao
import com.mifos.room.utils.Insert
import com.mifos.room.utils.OnConflictStrategy
import com.mifos.room.utils.Query
import kotlinx.coroutines.flow.Flow

/**
 * Created by Pronay Sarker on 12/02/2025 (9:33 PM)
 */
@Dao
interface SurveyDao {

    @Insert(entity = SurveyEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurvey(survey: SurveyEntity)

    @Insert(entity = QuestionDatasEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionData(questionData: QuestionDatasEntity)

    @Insert(entity = ResponseDatasEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponseData(responseDatas: ResponseDatasEntity)

    @Query("SELECT * FROM Survey")
    fun getAllSurveys(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM QuestionDatas WHERE surveyId = :surveyId ORDER BY sequenceNo ASC")
    fun getQuestionDatas(surveyId: Int): Flow<List<QuestionDatasEntity>>

    @Query("SELECT * FROM ResponseDatas WHERE id = :questionId ORDER BY sequenceNo ASC")
    fun getResponseDatas(questionId: Int): Flow<List<ResponseDatasEntity>>
}
