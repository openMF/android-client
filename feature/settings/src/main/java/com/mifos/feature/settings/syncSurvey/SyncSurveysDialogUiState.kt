package com.mifos.feature.settings.syncSurvey

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncSurveysDialogUiState {

    data object Initial : SyncSurveysDialogUiState()

    data object ShowNetworkIsNotAvailable : SyncSurveysDialogUiState()

    data object DismissDialog : SyncSurveysDialogUiState()

    data object ShowSurveysSyncSuccessfully : SyncSurveysDialogUiState()

    data class UpdateSingleSyncSurvey(val index: Int, val name: String, val questionTotal: Int) : SyncSurveysDialogUiState()

    data class UpdateQuestionSync(val index: Int, val name: Int, val responseTotal: Int) : SyncSurveysDialogUiState()

    data class UpdateResponseSync(val index: Int, val name: Int) : SyncSurveysDialogUiState()

    data class UpdateTotalSyncSurveyProgressBarMax(val total: Int) : SyncSurveysDialogUiState()

    data class ShowSyncedFailedSurveys(val failedCount: Int) : SyncSurveysDialogUiState()

    data class ShowError(val message: String) : SyncSurveysDialogUiState()

    data object ShowProgressbar : SyncSurveysDialogUiState()

    data class ShowUI(val total: Int) : SyncSurveysDialogUiState()

}