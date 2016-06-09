/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.Survey;

import java.util.List;

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
    Observable<Survey> getSurvey(@Path("surveyId") int surveyId);

    @POST(APIEndPoint.SURVEYS + "/{surveyId}/scorecards")
    Observable<Scorecard> submitScore(@Path("surveyId") int surveyId,
                                      @Body Scorecard scorecardPayload);
}
