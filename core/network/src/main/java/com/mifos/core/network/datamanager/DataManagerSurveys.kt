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

import com.mifos.core.model.objects.surveys.Scorecard
import com.mifos.core.network.BaseApiManager
import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.ResponseDatas
import com.mifos.room.entities.survey.Survey
import com.mifos.room.helper.SurveyDaoHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Survey API, In which Request is going to Server
 * and In Response, We are getting Survey API Observable Response using Retrofit 2 .
 *
 * Created by Rajan Maurya on 22/08/16.
 */
@Singleton
class DataManagerSurveys @Inject constructor(
    val mBaseApiManager: BaseApiManager,
//    private val mDatabaseHelperSurveys: DatabaseHelperSurveys,
    private val surveyDatabaseHelper: SurveyDaoHelper,
    private val prefManager: com.mifos.core.datastore.PrefManager,
) {
    /**
     * This Method sending the Request to REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys and fetch the list of surveys and
     * returns the Observable<List></List><Survey>> to the Presenter.
     *
     * @return Observable<List></List><Survey>>
     </Survey></Survey> */
    val allSurvey: Flow<List<Survey>>
        get() = when (prefManager.userStatus) {
            false -> flow { mBaseApiManager.surveyApi.allSurveys() }
            true -> surveyDatabaseHelper.readAllSurveys()
        }

    /**
     * This method call the DatabaseHelperSurveys Helper and mDatabaseHelperSurveys.readAllSurveys()
     * read the all Surveys from the Database Survey table and returns the List<Survey>.
     *
     * @return List<Survey>
     </Survey></Survey> */
    val databaseSurveys: Flow<List<Survey>>
        get() = surveyDatabaseHelper.readAllSurveys()

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getQuestionDatas() read the all QuestionDatas
     * from the Database QuestionDatas table and returns the List<QuestionDatas>.
     * @return List<QuestionDatas>
     </QuestionDatas></QuestionDatas> */
    fun getDatabaseQuestionData(surveyId: Int): Flow<List<QuestionDatas>> {
        return surveyDatabaseHelper.getQuestionDatas(surveyId)
    }

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getResponseDatas() read the all ResponseDatas
     * from the Database ResponseDatas table and returns the List<ResponseDatas>.
     * @return List<ResponseDatas>
     </ResponseDatas></ResponseDatas> */
    fun getDatabaseResponseDatas(questionId: Int): Flow<List<ResponseDatas>> {
        return surveyDatabaseHelper.getResponseDatas(questionId)
    }

    /**
     * This Method sending the request to the REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys/{surveyId}/scorecards
     * @param surveyId Survey Id
     * @param scorecardPayload Scorecard Payload
     * @return Scorecard
     */
    fun submitScore(surveyId: Int, scorecardPayload: Scorecard?): Observable<Scorecard> {
        return mBaseApiManager.surveyApi.submitScore(surveyId, scorecardPayload)
    }

    fun getSurvey(surveyId: Int): Observable<Survey> {
        return mBaseApiManager.surveyApi.getSurvey(surveyId)
    }

    /**
     * This method save the single Survey in Database.
     *
     * @param survey Survey
     * @return Survey
     */
    suspend fun syncSurveyInDatabase(survey: Survey) {
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
        questionDatas: QuestionDatas,
    ): Flow<QuestionDatas> {
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
        responseDatas: ResponseDatas,
    ): Flow<ResponseDatas> {
        return surveyDatabaseHelper.saveResponseData(questionId, responseDatas)
    }
}
