/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.syncSurvey

import androidclient.feature.settings.generated.resources.Res
import androidclient.feature.settings.generated.resources.feature_settings_cancel
import androidclient.feature.settings.generated.resources.feature_settings_error_network_not_available
import androidclient.feature.settings.generated.resources.feature_settings_failed_sync
import androidclient.feature.settings.generated.resources.feature_settings_hide
import androidclient.feature.settings.generated.resources.feature_settings_name
import androidclient.feature.settings.generated.resources.feature_settings_slash
import androidclient.feature.settings.generated.resources.feature_settings_space
import androidclient.feature.settings.generated.resources.feature_settings_surveys
import androidclient.feature.settings.generated.resources.feature_settings_sync_success
import androidclient.feature.settings.generated.resources.feature_settings_sync_surveys_full_information
import androidclient.feature.settings.generated.resources.feature_settings_syncing_question
import androidclient.feature.settings.generated.resources.feature_settings_syncing_response
import androidclient.feature.settings.generated.resources.feature_settings_syncing_survey
import androidclient.feature.settings.generated.resources.feature_settings_total
import androidclient.feature.settings.generated.resources.feature_settings_total_sync_progress
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.touchlab.kermit.Logger
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.ui.util.DevicePreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SyncSurveysDialog(
    viewModel: SyncSurveysDialogViewModel = koinViewModel(),
    closeDialog: () -> Unit,
) {
    val state by viewModel.syncSurveysDialogUiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.loadSurveyList()
    }

    SyncSurveysDialog(
        uiState = state,
        closeDialog = closeDialog,
    )
}

@Composable
internal fun SyncSurveysDialog(
    uiState: SyncSurveysDialogUiState,
    closeDialog: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    var questionSyncProgress by rememberSaveable { mutableIntStateOf(0) }
    var responseSyncProgress by rememberSaveable { mutableIntStateOf(0) }
    var totalSyncProgress by rememberSaveable { mutableIntStateOf(0) }
    var surveySyncProgress by rememberSaveable { mutableIntStateOf(0) }
    var totalSyncProgressMax by rememberSaveable { mutableIntStateOf(1) }
    var questionSyncProgressMax by rememberSaveable { mutableIntStateOf(1) }
    var responseSyncProgressMax by rememberSaveable { mutableIntStateOf(1) }
    var surveySyncProgressMax by rememberSaveable { mutableIntStateOf(1) }
    var totalListSize by rememberSaveable { mutableIntStateOf(0) }
    var showCancelButton by rememberSaveable { mutableStateOf(true) }
    var surveyName by rememberSaveable { mutableStateOf("") }
    var questionName by rememberSaveable { mutableStateOf("") }
    var responseName by rememberSaveable { mutableStateOf("") }
    var totalSurveysText by rememberSaveable { mutableStateOf("") }
    var totalProgressText by rememberSaveable { mutableStateOf("") }
    var syncFailedText by rememberSaveable { mutableStateOf("") }

    SyncSurveysDialogContent(
        closeDialog = closeDialog,
        showCancelButton = showCancelButton,
        totalSurveysText = totalSurveysText,
        syncFailedText = syncFailedText,
        surveyName = surveyName,
        totalProgressText = totalProgressText,
        questionSyncProgress = questionSyncProgress.toFloat() / questionSyncProgressMax.toFloat(),
        responseSyncProgress = responseSyncProgress.toFloat() / responseSyncProgressMax.toFloat(),
        totalSyncProgress = totalSyncProgress.toFloat() / totalSyncProgressMax.toFloat(),
        surveySyncProgress = surveySyncProgress.toFloat() / surveySyncProgressMax.toFloat(),
        questionName = questionName,
        responseName = responseName,
    )

    val snackbarHostState = remember { SnackbarHostState() }

    when (uiState) {
        is SyncSurveysDialogUiState.Initial -> Unit

        is SyncSurveysDialogUiState.DismissDialog -> {
            closeDialog.invoke()
        }

        is SyncSurveysDialogUiState.ShowError -> {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = uiState.message,
                    actionLabel = "Ok",
                    duration = SnackbarDuration.Short,
                )
            }
            closeDialog.invoke()
        }

        is SyncSurveysDialogUiState.ShowNetworkIsNotAvailable -> {
            val message = stringResource(Res.string.feature_settings_error_network_not_available)
            LaunchedEffect(uiState) {
                snackbarHostState.showSnackbar(
                    message,
                )
            }
        }

        is SyncSurveysDialogUiState.ShowProgressbar -> {
            MifosCircularProgress()
        }

        is SyncSurveysDialogUiState.ShowSurveysSyncSuccessfully -> {
            showCancelButton = false
            val message = stringResource(Res.string.feature_settings_sync_success)
            LaunchedEffect(key1 = true) {
                snackbarHostState.showSnackbar(
                    message,
                )
            }
        }

        is SyncSurveysDialogUiState.ShowSyncedFailedSurveys -> {
            syncFailedText = uiState.failedCount.toString()
        }

        is SyncSurveysDialogUiState.ShowUI -> {
            totalListSize = uiState.total
            surveySyncProgressMax = uiState.total
            totalSyncProgressMax = uiState.total
            val totalSurveys =
                uiState.total.toString() + stringResource(Res.string.feature_settings_space) +
                    stringResource(Res.string.feature_settings_surveys)
            totalSurveysText = totalSurveys
            syncFailedText = 0.toString()
        }

        is SyncSurveysDialogUiState.UpdateSingleSyncSurvey -> {
            surveySyncProgress = uiState.index
            totalSyncProgress = uiState.index
            surveyName = uiState.name
            questionSyncProgressMax = uiState.questionTotal
            val space = stringResource(Res.string.feature_settings_space)
            val slash = stringResource(Res.string.feature_settings_slash)

            totalProgressText = "$space${uiState.index}$slash$totalListSize"
        }

        is SyncSurveysDialogUiState.UpdateQuestionSync -> {
            questionSyncProgress = uiState.index
            questionName = uiState.name.toString()
            responseSyncProgressMax = uiState.responseTotal
        }

        is SyncSurveysDialogUiState.UpdateResponseSync -> {
            responseSyncProgress = uiState.index
            responseName = uiState.name.toString()
        }

        is SyncSurveysDialogUiState.UpdateTotalSyncSurveyProgressBarMax -> {
            surveySyncProgressMax = uiState.total
            Logger.d("TAG", Throwable("SyncDialogSurveysScreen: $surveySyncProgressMax"))
        }
    }
}

@Composable
private fun SyncSurveysDialogContent(
    closeDialog: () -> Unit,
    questionSyncProgress: Float,
    responseSyncProgress: Float,
    surveySyncProgress: Float,
    totalSyncProgress: Float,
    surveyName: String,
    totalSurveysText: String,
    syncFailedText: String,
    totalProgressText: String,
    showCancelButton: Boolean,
    questionName: String,
    responseName: String,
) {
    Dialog(onDismissRequest = { closeDialog.invoke() }) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
            ) {
                Text(
                    text = stringResource(Res.string.feature_settings_sync_surveys_full_information),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(Res.string.feature_settings_name))
                        Text(text = surveyName)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(Res.string.feature_settings_total))
                        Text(text = totalSurveysText)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(Res.string.feature_settings_syncing_survey))
                        Text(text = surveyName)
                    }

                    LinearProgressIndicator(
                        progress = { surveySyncProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(Res.string.feature_settings_syncing_question))
                        Text(text = questionName)
                    }

                    LinearProgressIndicator(
                        progress = { questionSyncProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(Res.string.feature_settings_syncing_response))
                        Text(text = responseName)
                    }

                    LinearProgressIndicator(
                        progress = { responseSyncProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(Res.string.feature_settings_total_sync_progress))
                        Text(text = totalProgressText)
                    }

                    LinearProgressIndicator(
                        progress = { totalSyncProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(Res.string.feature_settings_failed_sync))
                        Text(text = syncFailedText)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    SyncSurveyButton(
                        onClick = { closeDialog.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        text = stringResource(Res.string.feature_settings_cancel),
                        isEnabled = showCancelButton,
                    )

                    SyncSurveyButton(
                        onClick = { closeDialog.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        text = stringResource(Res.string.feature_settings_hide),
                        isEnabled = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncSurveyButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.DarkGray,
        ),
        enabled = isEnabled,
    ) {
        Text(text = text)
    }
}

@Composable
@DevicePreview
private fun SyncSurveysDialogPreview() {
    Column {
        SyncSurveysDialog(uiState = SyncSurveysDialogUiState.DismissDialog, closeDialog = {})
        SyncSurveysDialog(uiState = SyncSurveysDialogUiState.ShowError("Error"), closeDialog = {})
        SyncSurveysDialog(uiState = SyncSurveysDialogUiState.ShowProgressbar, closeDialog = {})
        SyncSurveysDialog(uiState = SyncSurveysDialogUiState.ShowSurveysSyncSuccessfully, closeDialog = {})
        SyncSurveysDialog(uiState = SyncSurveysDialogUiState.ShowSyncedFailedSurveys(1), closeDialog = {})
    }
}
