package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.mappers.survey.ScorecardDataMapper.mapToEntity
import javax.inject.Singleton
import javax.inject.Inject
import com.mifos.api.local.databasehelper.DatabaseHelperSurveys
import com.mifos.api.mappers.survey.SurveyDataMapper
import org.apache.fineract.client.services.SpmSurveysApi
import org.apache.fineract.client.services.ScoreCardApi
import com.mifos.utils.PrefManager
import com.mifos.objects.survey.QuestionDatas
import com.mifos.objects.survey.ResponseDatas
import com.mifos.objects.survey.Scorecard
import com.mifos.objects.survey.Survey
import rx.Observable
import java.util.ArrayList

/**
 * This DataManager is for Managing Survey API, In which Request is going to Server
 * and In Response, We are getting Survey API Observable Response using Retrofit 2 .
 *
 * Created by Rajan Maurya on 22/08/16.
 */
@Singleton
class DataManagerSurveys @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    private val mDatabaseHelperSurveys: DatabaseHelperSurveys,
    private val sdkBaseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    private val surveyApi: SpmSurveysApi
        private get() = sdkBaseApiManager.getSurveyApi()
    private val scoreCardApi: ScoreCardApi
        private get() = sdkBaseApiManager.getClient().surveyScorecards

    /**
     * This Method sending the Request to REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys and fetch the list of surveys and
     * returns the Observable<List></List><Survey>> to the Presenter.
     *
     * @return Observable<List></List><Survey>>
    </Survey></Survey> */
    val allSurvey: Observable<List<Survey>>
        get() = when (PrefManager.getUserStatus()) {
            0 -> surveyApi.fetchAllSurveys1(null)
                .map { SurveyDataMapper.mapFromEntityList(it) }
            1 -> mDatabaseHelperSurveys.readAllSurveys()
            else -> {
                val defaultSurveyList: List<Survey> = ArrayList()
                Observable.just(defaultSurveyList)
            }
        }

    /**
     * This method call the DatabaseHelperSurveys Helper and mDatabaseHelperSurveys.readAllSurveys()
     * read the all Surveys from the Database Survey table and returns the List<Survey>.
     *
     * @return List<Survey>
    </Survey></Survey> */
    val databaseSurveys: Observable<List<Survey>>
        get() = mDatabaseHelperSurveys.readAllSurveys()

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getQuestionDatas() read the all QuestionDatas
     * from the Database QuestionDatas table and returns the List<QuestionDatas>.
     * @return List<QuestionDatas>
    </QuestionDatas></QuestionDatas> */
    fun getDatabaseQuestionDatas(surveyId: Int): Observable<List<QuestionDatas>> {
        return mDatabaseHelperSurveys.getQuestionDatas(surveyId)
    }

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getResponseDatas() read the all ResponseDatas
     * from the Database ResponseDatas table and returns the List<ResponseDatas>.
     * @return List<ResponseDatas>
    </ResponseDatas></ResponseDatas> */
    fun getDatabaseResponseDatas(questionId: Int): Observable<List<ResponseDatas>> {
        return mDatabaseHelperSurveys.getResponseDatas(questionId)
    }

    /**
     * This Method sending the request to the REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys/{surveyId}/scorecards
     * @param surveyId Survey Id
     * @param scorecardPayload Scorecard Payload
     * @return Scorecard
     */
    fun submitScore(surveyId: Int, scorecardPayload: Scorecard?): Observable<Scorecard?> {
        val sdkPayload = mapToEntity(scorecardPayload!!)
        return scoreCardApi.createScorecard1(surveyId.toLong(), sdkPayload)
            .map { unused: Void? -> scorecardPayload }
        //return mBaseApiManager.getSurveyApi().submitScore(surveyId, scorecardPayload);
    }

    fun getSurvey(surveyId: Int): Observable<Survey> {
        return mBaseApiManager.surveyApi.getSurvey(surveyId)
    }

    /**
     * This method save the single Survey in Database.
     *
     * @param survey Survey
     * @return Survey
     */
    fun syncSurveyInDatabase(survey: Survey?): Observable<Survey> {
        return mDatabaseHelperSurveys.saveSurvey(survey)
    }

    /**
     * This method save the single QuestionDatas in Database.
     *
     * @param questionDatas QuestionDatas
     * @return QuestionDatas
     */
    fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatas?
    ): Observable<QuestionDatas> {
        return mDatabaseHelperSurveys.saveQuestionData(surveyId, questionDatas)
    }

    /**
     * This method save the single ResponseDatas in Database.
     *
     * @param responseDatas ResponseDatas
     * @return ResponseDatas
     */
    fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatas?
    ): Observable<ResponseDatas> {
        return mDatabaseHelperSurveys.saveResponseData(questionId, responseDatas)
    }
}