package com.mifos.mifosxdroid.online.surveylist

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.survey.Survey

/**
 * Created by Rajan Maurya on 08/06/16.
 */
interface SurveyListMvpView : MvpView {
    fun showAllSurvey(surveys: List<Survey?>?)
    fun showFetchingError(errorMessage: Int)
}