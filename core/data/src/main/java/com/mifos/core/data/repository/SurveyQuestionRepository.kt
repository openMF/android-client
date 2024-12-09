package com.mifos.core.data.repository

import com.mifos.core.objects.survey.Survey
import rx.Observable

interface SurveyQuestionRepository {

    fun getSurvey(surveyId: Int): Observable<Survey>

}