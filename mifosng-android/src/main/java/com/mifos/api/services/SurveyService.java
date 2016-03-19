/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.Survey;
import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.ScorecardPayload;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/**
 * @author
 */
public interface SurveyService {

    @GET(APIEndPoint.SURVEYS)
    Observable<List<Survey>> getAllSurveys();

    @GET(APIEndPoint.SURVEYS + "/{surveyId}")
    void getSurvey(@Path("surveyId") int surveyId, Callback<Survey> surveyCallback);

    @POST(APIEndPoint.SURVEYS + "/{surveyId}/scorecards")
    Observable<Scorecard> submitScore(@Path("surveyId") int surveyId, @Body ScorecardPayload scorecardPayload);
}
