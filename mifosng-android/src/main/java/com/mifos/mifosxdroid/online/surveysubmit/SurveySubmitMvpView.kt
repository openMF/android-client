package com.mifos.mifosxdroid.online.surveysubmit

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.survey.Scorecard

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface SurveySubmitMvpView : MvpView {
    fun showSurveySubmittedSuccessfully(scorecard: Scorecard?)
    fun showError(errorMessage: Int)
}