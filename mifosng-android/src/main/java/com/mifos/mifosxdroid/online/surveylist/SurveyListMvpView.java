package com.mifos.mifosxdroid.online.surveylist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.survey.Survey;

import java.util.List;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface SurveyListMvpView extends MvpView {

    void showAllSurvey(List<Survey> surveys);

    void showFetchingError(int errorMessage);
}
