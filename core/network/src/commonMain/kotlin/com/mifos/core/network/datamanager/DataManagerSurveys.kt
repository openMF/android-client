/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.surveys.Scorecard
import com.mifos.core.network.BaseApiManager
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import com.mifos.room.helper.SurveyDaoHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * This DataManager is for Managing Survey API, In which Request is going to Server
 * and In Response, We are getting Survey API Observable Response using Retrofit 2 .
 *
 * Created by Rajan Maurya on 22/08/16.
 */
class DataManagerSurveys(
    val mBaseApiManager: BaseApiManager,
//    private val mDatabaseHelperSurveys: DatabaseHelperSurveys,
    private val surveyDatabaseHelper: SurveyDaoHelper,
    private val prefManager: UserPreferencesRepository,
) {
    /**
     * This Method sending the Request to REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys and fetch the list of surveys and
     * returns the Observable<List></List><Survey>> to the Presenter.
     *
     * @return Observable<List></List><Survey>>
     </Survey></Survey> */
    @OptIn(ExperimentalCoroutinesApi::class)
    val allSurvey: Flow<List<SurveyEntity>>
        get() = prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> mBaseApiManager.surveyService.allSurveys()
                true -> surveyDatabaseHelper.readAllSurveys()
            }
        }

    /**
     * This method call the DatabaseHelperSurveys Helper and mDatabaseHelperSurveys.readAllSurveys()
     * read the all Surveys from the Database Survey table and returns the List<Survey>.
     *
     * @return List<Survey>
     </Survey></Survey> */
    val databaseSurveys: Flow<List<SurveyEntity>>
        get() = surveyDatabaseHelper.readAllSurveys()

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getQuestionDatas() read the all QuestionDatas
     * from the Database QuestionDatas table and returns the List<QuestionDatas>.
     * @return List<QuestionDatas>
     </QuestionDatas></QuestionDatas> */
    fun getDatabaseQuestionData(surveyId: Int): Flow<List<QuestionDatasEntity>> {
        return surveyDatabaseHelper.getQuestionDatas(surveyId)
    }

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getResponseDatas() read the all ResponseDatas
     * from the Database ResponseDatas table and returns the List<ResponseDatas>.
     * @return List<ResponseDatas>
     </ResponseData></ResponseDatas> */
    fun getDatabaseResponseDatas(questionId: Int): Flow<List<ResponseDatasEntity>> {
        return surveyDatabaseHelper.getResponseDatas(questionId)
    }

    /**
     * This Method sending the request to the REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys/{surveyId}/scorecards
     * @param surveyId Survey Id
     * @param scorecardPayload Scorecard Payload
     * @return Scorecard
     */
    fun submitScore(surveyId: Int, scorecardPayload: Scorecard?): Flow<Scorecard> {
        return mBaseApiManager.surveyService.submitScore(surveyId, scorecardPayload)
    }

    fun getSurvey(surveyId: Int): Flow<SurveyEntity> {
        return mBaseApiManager.surveyService.getSurvey(surveyId)
    }

    /**
     * This method save the single Survey in Database.
     *
     * @param survey Survey
     * @return Survey
     */
    suspend fun syncSurveyInDatabase(survey: SurveyEntity) {
        return surveyDatabaseHelper.saveSurvey(survey)
    }

    /**
     * This method save the single QuestionDatas in Database.
     *
     * @param questionDatas QuestionDatas
     * @return QuestionDatas
     */
    fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatasEntity,
    ): Flow<QuestionDatasEntity> {
        return surveyDatabaseHelper.saveQuestionData(surveyId, questionDatas)
    }

    /**
     * This method save the single ResponseDatas in Database.
     *
     * @param responseDatas ResponseDatas
     * @return ResponseDatas
     */
    fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatasEntity,
    ): Flow<ResponseDatasEntity> {
        return surveyDatabaseHelper.saveResponseData(questionId, responseDatas)
    }
}
