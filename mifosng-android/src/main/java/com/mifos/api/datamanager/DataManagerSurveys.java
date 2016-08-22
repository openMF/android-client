package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperSurveys;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.Survey;

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
        return mBaseApiManager.getSurveyApi().getAllSurveys();
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
}
