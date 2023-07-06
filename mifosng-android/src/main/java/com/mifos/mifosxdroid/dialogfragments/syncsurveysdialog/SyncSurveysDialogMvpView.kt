package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.survey.Survey

/**
 * Created by mayankjindal on 19/07/17.
 */
interface SyncSurveysDialogMvpView : MvpView {
    fun showUI()
    fun updateSurveyList(mSurveyList: List<Survey>)
    fun showSyncingSurvey(surveyName: String?)
    fun showSyncedFailedSurveys(failedCount: Int)
    fun setQuestionSyncProgressBarMax(total: Int)
    fun setResponseSyncProgressBarMax(total: Int)
    fun updateSingleSyncSurveyProgressBar(i: Int)
    fun updateQuestionSyncProgressBar(i: Int)
    fun updateResponseSyncProgressBar(i: Int)
    fun updateTotalSyncSurveyProgressBarAndCount(i: Int)
    var maxSingleSyncSurveyProgressBar: Int
    fun showNetworkIsNotAvailable()
    fun showSurveysSyncSuccessfully()
    val isOnline: Boolean?
    fun dismissDialog()
    fun showDialog()
    fun hideDialog()
    fun showError(s: Int)
}