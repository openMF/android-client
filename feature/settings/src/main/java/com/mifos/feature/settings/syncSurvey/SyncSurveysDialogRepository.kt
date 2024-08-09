package com.mifos.feature.settings.syncSurvey

import com.mifos.core.objects.survey.QuestionDatas
import com.mifos.core.objects.survey.ResponseDatas
import com.mifos.core.objects.survey.Survey
import rx.Observable

interface SyncSurveysDialogRepository {

    fun syncSurveyInDatabase(survey: Survey): Observable<Survey>

    fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatas
    ): Observable<QuestionDatas>

    fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatas
    ): Observable<ResponseDatas>

    fun allSurvey(): Observable<List<Survey>>


}