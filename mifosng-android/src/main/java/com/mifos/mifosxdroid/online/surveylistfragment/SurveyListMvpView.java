package com.mifos.mifosxdroid.online.surveylistfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.survey.Survey;

import java.util.List;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface SurveyListMvpView extends MvpView {

    void showAllSurvey(List<Survey> surveys);

    void ResponseError(String s);
}
