package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

import com.mifos.api.datamanager.DataManagerSurveys
import com.mifos.objects.survey.QuestionDatas
import com.mifos.objects.survey.ResponseDatas
import com.mifos.objects.survey.Survey
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