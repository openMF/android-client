package com.mifos.feature.settings.syncSurvey

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.White
import com.mifos.feature.settings.R
import kotlinx.coroutines.launch

@Composable
fun SyncSurveysDialog(
    viewModel: SyncSurveysDialogViewModel = hiltViewModel(),
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

@SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
@Composable
fun SyncSurveysDialog(
    uiState: SyncSurveysDialogUiState,
    closeDialog: () -> Unit,
) {
    val context = LocalContext.current
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
        responseName = responseName
    )

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
                    duration = SnackbarDuration.Short
                )
            }
            closeDialog.invoke()
        }

        is SyncSurveysDialogUiState.ShowNetworkIsNotAvailable -> {
            LaunchedEffect(key1 = true) {
                Toast.makeText(
                    context,
                    context.getString(R.string.feature_settings_error_network_not_available),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        is SyncSurveysDialogUiState.ShowProgressbar -> {
            MifosCircularProgress()
        }

        is SyncSurveysDialogUiState.ShowSurveysSyncSuccessfully -> {
            showCancelButton = false
            LaunchedEffect(key1 = true) {
                Toast.makeText(context, R.string.feature_settings_sync_success, Toast.LENGTH_SHORT).show()
            }
        }

        is SyncSurveysDialogUiState.ShowSyncedFailedSurveys -> {
            syncFailedText = uiState.failedCount.toString()
        }

        is SyncSurveysDialogUiState.ShowUI -> {
            totalListSize = uiState.total
            surveySyncProgressMax = uiState.total
            totalSyncProgressMax = uiState.total
            val totalSurveys = uiState.total.toString() + stringResource(R.string.feature_settings_space) +
                    stringResource(R.string.feature_settings_surveys)
            totalSurveysText = totalSurveys
            syncFailedText = 0.toString()
        }

        is SyncSurveysDialogUiState.UpdateSingleSyncSurvey -> {
            surveySyncProgress = uiState.index
            totalSyncProgress = uiState.index
            surveyName = uiState.name
            questionSyncProgressMax = uiState.questionTotal
            val totalSyncCount =
                context.getString(R.string.feature_settings_space) + uiState.index + context.getString(R.string.feature_settings_slash) + totalListSize
            totalProgressText = totalSyncCount
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
            Log.d("TAG", "SyncDialogSurveysScreen: $surveySyncProgressMax")
        }
    }
}


@Composable
fun SyncSurveysDialogContent(
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
    responseName: String
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
                    .padding(5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.feature_settings_sync_surveys_full_information),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.feature_settings_name))
                        Text(text = surveyName)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.feature_settings_total))
                        Text(text = totalSurveysText)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.feature_settings_syncing_survey))
                        Text(text = surveyName)
                    }

                    LinearProgressIndicator(
                        progress = surveySyncProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.feature_settings_syncing_question))
                        Text(text = questionName)
                    }

                    LinearProgressIndicator(
                        progress = questionSyncProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.feature_settings_syncing_response))
                        Text(text = responseName)
                    }

                    LinearProgressIndicator(
                        progress = responseSyncProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.feature_settings_total_sync_progress))
                        Text(text = totalProgressText)
                    }

                    LinearProgressIndicator(
                        progress = totalSyncProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = stringResource(id = R.string.feature_settings_failed_sync))
                        Text(text = syncFailedText)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    SyncSurveyButton(
                        onClick = { closeDialog.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        text = stringResource(id = R.string.feature_settings_cancel),
                        isEnabled = showCancelButton
                    )

                    SyncSurveyButton(
                        onClick = { closeDialog.invoke() },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        text = stringResource(id = R.string.feature_settings_hide),
                        isEnabled = true
                    )
                }
            }
        }
    }
}

@Composable
fun SyncSurveyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BluePrimary,
            contentColor = White,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = White
        ),
        enabled = isEnabled
    ) {
        Text(text = text)
    }
}

class SyncSurveysDialogPreviewProvider : PreviewParameterProvider<SyncSurveysDialogUiState> {
    override val values: Sequence<SyncSurveysDialogUiState>
        get() = sequenceOf(
            SyncSurveysDialogUiState.DismissDialog,
            SyncSurveysDialogUiState.ShowError("Error"),
            SyncSurveysDialogUiState.ShowProgressbar,
            SyncSurveysDialogUiState.ShowSurveysSyncSuccessfully,
            SyncSurveysDialogUiState.ShowSyncedFailedSurveys(1),
        )
}

@Preview(showBackground = true)
@Composable
private fun SyncSurveysDialogPreview(
    @PreviewParameter(SyncSurveysDialogPreviewProvider::class) state: SyncSurveysDialogUiState
) {
    SyncSurveysDialog(
        uiState = state,
        closeDialog = { }
    )
}
