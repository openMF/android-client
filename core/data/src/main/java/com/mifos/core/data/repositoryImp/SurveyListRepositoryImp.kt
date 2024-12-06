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
import com.mifos.core.objects.survey.QuestionDatas
import com.mifos.core.objects.survey.ResponseDatas
import com.mifos.core.objects.survey.Survey
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SurveyListRepositoryImp @Inject constructor(private val dataManagerSurveys: DataManagerSurveys) :
    SurveyListRepository {

    override fun allSurvey(): Observable<List<Survey>> {
        return dataManagerSurveys.allSurvey
    }

    override fun databaseSurveys(): Observable<List<Survey>> {
        return dataManagerSurveys.databaseSurveys
    }

    override fun getDatabaseQuestionData(surveyId: Int): Observable<List<QuestionDatas>> {
        return dataManagerSurveys.getDatabaseQuestionData(surveyId)
    }

    override fun getDatabaseResponseDatas(questionId: Int): Observable<List<ResponseDatas>> {
        return dataManagerSurveys.getDatabaseResponseDatas(questionId)
    }
}
