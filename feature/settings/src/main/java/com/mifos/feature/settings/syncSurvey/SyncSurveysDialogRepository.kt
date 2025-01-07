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

import com.mifos.core.entity.survey.QuestionDatas
import com.mifos.core.entity.survey.ResponseDatas
import com.mifos.core.entity.survey.Survey
import rx.Observable

interface SyncSurveysDialogRepository {

    fun syncSurveyInDatabase(survey: Survey): Observable<Survey>

    fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatas,
    ): Observable<QuestionDatas>

    fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatas,
    ): Observable<ResponseDatas>

    fun allSurvey(): Observable<List<Survey>>
}
