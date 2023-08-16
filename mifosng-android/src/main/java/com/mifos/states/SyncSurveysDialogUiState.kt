package com.mifos.states

import com.mifos.objects.survey.Survey

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncSurveysDialogUiState {

    object ShowNetworkIsNotAvailable : SyncSurveysDialogUiState()

    object DismissDialog : SyncSurveysDialogUiState()

    object ShowSurveysSyncSuccessfully : SyncSurveysDialogUiState()

    data class UpdateSingleSyncSurveyProgressBar(val index: Int) : SyncSurveysDialogUiState()

    data class UpdateQuestionSyncProgressBar(val index: Int) : SyncSurveysDialogUiState()

    data class ShowSyncedFailedSurveys(val failedCount: Int) : SyncSurveysDialogUiState()

    data class ShowError(val message: String) : SyncSurveysDialogUiState()

    data class SetQuestionSyncProgressBarMax(val total: Int) : SyncSurveysDialogUiState()

    data class SetResponseSyncProgressBarMax(val total: Int) : SyncSurveysDialogUiState()

    data class UpdateResponseSyncProgressBar(val index: Int) : SyncSurveysDialogUiState()

    object ShowProgressbar : SyncSurveysDialogUiState()

    object ShowUI : SyncSurveysDialogUiState()

    data class UpdateTotalSyncSurveyProgressBarAndCount(val total: Int) : SyncSurveysDialogUiState()

    data class UpdateSurveyList(val surveys: List<Survey>) : SyncSurveysDialogUiState()

    data class ShowSyncingSurvey(val surveyName: String) : SyncSurveysDialogUiState()
}