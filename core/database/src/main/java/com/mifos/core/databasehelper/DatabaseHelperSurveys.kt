package com.mifos.core.databasehelper

import com.mifos.core.objects.survey.QuestionDatas
import com.mifos.core.objects.survey.QuestionDatas_Table
import com.mifos.core.objects.survey.ResponseDatas
import com.mifos.core.objects.survey.ResponseDatas_Table
import com.mifos.core.objects.survey.Survey
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 22/08/16.
 */
@Singleton
class DatabaseHelperSurveys @Inject constructor() {
    /**
     * This Method save the single Survey in Database with SurveyId as Primary Id
     *
     * @param survey Survey
     * @return saved Survey
     */
    fun saveSurvey(survey: Survey): Observable<Survey> {
        return Observable.defer { //Saving Survey in Database
            survey.save()
            Observable.just(survey)
        }
    }

    /**
     * This Method save the single QuestionDatas in Database
     *
     * @param surveyId int, questionDatas QuestionDatas
     * @return saved QuestionDatas
     */
    fun saveQuestionData(
        surveyId: Int,
        questionDatas: QuestionDatas
    ): Observable<QuestionDatas> {
        return Observable.defer { //Saving QuestionDatas in Database
            questionDatas.surveyId = surveyId
            questionDatas.save()
            Observable.just(questionDatas)
        }
    }

    /**
     * This Method save the single ResponseDatas in Database
     *
     * @param questionId int, responseDatas ResponseDatas
     * @return saved ResponseDatas
     */
    fun saveResponseData(
        questionId: Int,
        responseDatas: ResponseDatas
    ): Observable<ResponseDatas> {
        return Observable.defer { //Saving ResponseDatas in Database
            responseDatas.questionId = questionId
            responseDatas.save()
            Observable.just(responseDatas)
        }
    }

    /**
     * Reading All surveys from Database table of Survey and return the SurveyList
     *
     * @return List Of Surveys
     */
    fun readAllSurveys(): Observable<List<Survey>> {
        return Observable.defer {
            val surveyList = SQLite.select()
                .from(Survey::class.java)
                .queryList()
            Observable.just(surveyList)
        }
    }

    /**
     * Reading All QuestionDatas from Database table of QuestionDatas
     * and return the QuestionDatasList
     * @return List Of QuestionDatas
     */
    fun getQuestionDatas(surveyId: Int): Observable<List<QuestionDatas>> {
        return Observable.defer {
            val questionDatas = SQLite.select()
                .from(QuestionDatas::class.java)
                .where(QuestionDatas_Table.surveyId.eq(surveyId))
                .orderBy(QuestionDatas_Table.sequenceNo, true)
                .queryList()
            Observable.just(questionDatas)
        }
    }

    /**
     * Reading All ResponseDatas from Database table of ResponseDatas
     * and return the ResponseDatasList
     * @return List Of ResponseDatas
     */
    fun getResponseDatas(questionId: Int): Observable<List<ResponseDatas>> {
        return Observable.defer {
            val responseDatas = SQLite.select()
                .from(ResponseDatas::class.java)
                .where(ResponseDatas_Table.questionId.eq(questionId))
                .orderBy(ResponseDatas_Table.sequenceNo, true)
                .queryList()
            Observable.just(responseDatas)
        }
    }
}