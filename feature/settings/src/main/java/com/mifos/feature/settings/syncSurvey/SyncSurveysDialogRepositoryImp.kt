package com.mifos.feature.settings.syncSurvey

import com.mifos.core.network.datamanager.DataManagerSurveys
import com.mifos.core.objects.survey.QuestionDatas
import com.mifos.core.objects.survey.ResponseDatas
import com.mifos.core.objects.survey.Survey
import rx.Observable
import javax.inject.Inject

class SyncSurveysDialogRepositoryImp @Inject constructor(private val dataManagerSurvey: DataManagerSurveys) :
    SyncSurveysDialogRepository {

    override fun syncSurveyInDatabase(survey: Survey): Observable<Survey> {
        return dataManagerSurvey.syncSurveyInDatabase(survey)
    }

    override fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatas
    ): Observable<QuestionDatas> {
        return dataManagerSurvey.syncQuestionDataInDatabase(surveyId, questionDatas)
    }

    override fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatas
    ): Observable<ResponseDatas> {
        return dataManagerSurvey.syncResponseDataInDatabase(questionId, responseDatas)
    }

    override fun allSurvey(): Observable<List<Survey>> {
        return dataManagerSurvey.allSurvey
    }


}