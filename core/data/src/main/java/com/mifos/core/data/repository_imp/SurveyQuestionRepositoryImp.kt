package com.mifos.core.data.repository_imp

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