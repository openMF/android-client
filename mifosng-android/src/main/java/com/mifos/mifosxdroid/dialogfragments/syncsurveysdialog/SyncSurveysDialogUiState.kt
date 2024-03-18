package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

import com.mifos.core.objects.survey.Survey

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncSurveysDialogUiState {

    data object ShowNetworkIsNotAvailable : SyncSurveysDialogUiState()

    data object DismissDialog : SyncSurveysDialogUiState()

    data object ShowSurveysSyncSuccessfully : SyncSurveysDialogUiState()

    data class UpdateSingleSyncSurveyProgressBar(val index: Int) : SyncSurveysDialogUiState()

    data class UpdateQuestionSyncProgressBar(val index: Int) : SyncSurveysDialogUiState()

    data class ShowSyncedFailedSurveys(val failedCount: Int) : SyncSurveysDialogUiState()

    data class ShowError(val message: String) : SyncSurveysDialogUiState()

    data class SetQuestionSyncProgressBarMax(val total: Int) : SyncSurveysDialogUiState()

    data class SetResponseSyncProgressBarMax(val total: Int) : SyncSurveysDialogUiState()

    data class UpdateResponseSyncProgressBar(val index: Int) : SyncSurveysDialogUiState()

    data object ShowProgressbar : SyncSurveysDialogUiState()

    data object ShowUI : SyncSurveysDialogUiState()

    data class UpdateTotalSyncSurveyProgressBarAndCount(val total: Int) : SyncSurveysDialogUiState()

    data class UpdateSurveyList(val surveys: List<Survey>) : SyncSurveysDialogUiState()

    data class ShowSyncingSurvey(val surveyName: String) : SyncSurveysDialogUiState()
}