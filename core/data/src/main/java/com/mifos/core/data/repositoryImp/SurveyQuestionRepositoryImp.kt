/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.SurveyQuestionRepository
import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.core.objects.survey.Survey
import rx.Observable
import javax.inject.Inject

class SurveyQuestionRepositoryImp @Inject constructor(private val dataManagerSurveys: DataManagerSurveys) :
    SurveyQuestionRepository {

    override fun getSurvey(surveyId: Int): Observable<Survey> {
        return dataManagerSurveys.getSurvey(surveyId)
    }
}
