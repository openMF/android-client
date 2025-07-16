/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.helper

import com.mifos.room.dao.SurveyDao
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Pronay Sarker on 12/02/2025 (9:32 PM)
 */
class SurveyDaoHelper(
    private val surveyDao: SurveyDao,
) {
    /**
     * This Method save the single Survey in Database with SurveyId as Primary Id
     *
     * @param survey Survey
     * @return saved Survey
     */
    suspend fun saveSurvey(survey: SurveyEntity) {
        surveyDao.insertSurvey(survey)
    }

    /**
     * This Method save the single QuestionDatas in Database
     *
     * @param surveyId int, questionDatas QuestionDatas
     * @return saved QuestionDatas
     */
    fun saveQuestionData(
        surveyId: Int,
        questionDatas: QuestionDatasEntity,
    ): Flow<QuestionDatasEntity> {
        return flow {
            val updatedQuestionData = questionDatas.copy(surveyId = surveyId)
            surveyDao.insertQuestionData(updatedQuestionData)
            emit(updatedQuestionData)
        }
    }

    /**
     * This Method save the single ResponseDatas in Database
     *
     * @param questionId int, responseDatas ResponseDatas
     * @return saved ResponseDatas
     */
    fun saveResponseData(
        questionId: Int,
        responseDatas: ResponseDatasEntity,
    ): Flow<ResponseDatasEntity> {
        return flow {
            val updatedResponseData = responseDatas.copy(id = questionId)
            surveyDao.insertResponseData(updatedResponseData)
            emit(updatedResponseData)
        }
    }

    /**
     * Reading All surveys from Database table of Survey and return the SurveyList
     *
     * @return List Of Surveys
     */
    fun readAllSurveys(): Flow<List<SurveyEntity>> {
        return surveyDao.getAllSurveys()
    }

    /**
     * Reading All QuestionDatas from Database table of QuestionDatas
     * and return the QuestionDatasList
     * @return List Of QuestionDatas
     */
    fun getQuestionDatas(surveyId: Int): Flow<List<QuestionDatasEntity>> {
        return surveyDao.getQuestionDatas(surveyId)
    }

    /**
     * Reading All ResponseDatas from Database table of ResponseDatas
     * and return the ResponseDatasList
     * @return List Of ResponseDatas
     */
    fun getResponseDatas(questionId: Int): Flow<List<ResponseDatasEntity>> {
        return surveyDao.getResponseDatas(questionId)
    }
}
