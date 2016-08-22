package com.mifos.mifosxdroid.online.surveysubmit;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.survey.Scorecard;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface SurveySubmitMvpView extends MvpView {

    void showSurveySubmittedSuccessfully(Scorecard scorecard);

    void showError(int errorMessage);
}

