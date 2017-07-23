package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperSurveys;
import com.mifos.objects.survey.QuestionDatas;
import com.mifos.objects.survey.ResponseDatas;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * This DataManager is for Managing Survey API, In which Request is going to Server
 * and In Response, We are getting Survey API Observable Response using Retrofit 2 .
 *
 * Created by Rajan Maurya on 22/08/16.
 */
@Singleton
public class DataManagerSurveys {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperSurveys mDatabaseHelperSurveys;

    @Inject
    public DataManagerSurveys(BaseApiManager baseApiManager,
                              DatabaseHelperSurveys databaseHelperSurveys) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperSurveys = databaseHelperSurveys;
    }


    /**
     * This Method sending the Request to REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys and fetch the list of surveys and
     * returns the Observable<List<Survey>> to the Presenter.
     *
     * @return Observable<List<Survey>>
     */
    public Observable<List<Survey>> getAllSurvey() {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getSurveyApi().getAllSurveys();
            case 1:
                return mDatabaseHelperSurveys.readAllSurveys();
            default:
                List<Survey> defaultSurveyList = new ArrayList<Survey>();
                return Observable.just(defaultSurveyList);
        }
    }

    /**
     * This method call the DatabaseHelperSurveys Helper and mDatabaseHelperSurveys.readAllSurveys()
     * read the all Surveys from the Database Survey table and returns the List<Survey>.
     *
     * @return List<Survey>
     */
    public Observable<List<Survey>> getDatabaseSurveys() {
        return mDatabaseHelperSurveys.readAllSurveys();
    }

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getQuestionDatas() read the all QuestionDatas
     * from the Database QuestionDatas table and returns the List<QuestionDatas>.
     * @return List<QuestionDatas>
     */
    public Observable<List<QuestionDatas>> getDatabaseQuestionDatas(int surveyId) {
        return mDatabaseHelperSurveys.getQuestionDatas(surveyId);
    }

    /**
     * This method call the DatabaseHelperSurveys Helper and
     * mDatabaseHelperSurveys.getResponseDatas() read the all ResponseDatas
     * from the Database ResponseDatas table and returns the List<ResponseDatas>.
     * @return List<ResponseDatas>
     */
    public Observable<List<ResponseDatas>> getDatabaseResponseDatas(int questionId) {
        return mDatabaseHelperSurveys.getResponseDatas(questionId);
    }


    /**
     * This Method sending the request to the REST API :
     * https://demo.openmf.org/fineract-provider/api/v1/surveys/{surveyId}/scorecards
     * @param surveyId Survey Id
     * @param scorecardPayload Scorecard Payload
     * @return Scorecard
     */
    public Observable<Scorecard> submitScore(int surveyId, Scorecard scorecardPayload) {
        return mBaseApiManager.getSurveyApi().submitScore(surveyId, scorecardPayload);
    }

    public Observable<Survey> getSurvey(int surveyId) {
        return mBaseApiManager.getSurveyApi().getSurvey(surveyId);
    }

    /**
     * This method save the single Survey in Database.
     *
     * @param survey Survey
     * @return Survey
     */
    public Observable<Survey> syncSurveyInDatabase(Survey survey) {
        return mDatabaseHelperSurveys.saveSurvey(survey);
    }

    /**
     * This method save the single QuestionDatas in Database.
     *
     * @param questionDatas QuestionDatas
     * @return QuestionDatas
     */
    public Observable<QuestionDatas> syncQuestionDataInDatabase(int surveyId,
                                                                QuestionDatas questionDatas) {
        return mDatabaseHelperSurveys.saveQuestionData(surveyId, questionDatas);
    }

    /**
     * This method save the single ResponseDatas in Database.
     *
     * @param responseDatas ResponseDatas
     * @return ResponseDatas
     */
    public Observable<ResponseDatas> syncResponseDataInDatabase(int questionId,
                                                                ResponseDatas responseDatas) {
        return mDatabaseHelperSurveys.saveResponseData(questionId, responseDatas);
    }
}
