package com.mifos.repositories

import com.mifos.objects.survey.QuestionDatas
import com.mifos.objects.survey.ResponseDatas
import com.mifos.objects.survey.Survey
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface SurveyListRepository {

    fun allSurvey(): Observable<List<Survey>>

    fun databaseSurveys(): Observable<List<Survey>>

    fun getDatabaseQuestionData(surveyId: Int): Observable<List<QuestionDatas>>

    fun getDatabaseResponseDatas(questionId: Int): Observable<List<ResponseDatas>>


}