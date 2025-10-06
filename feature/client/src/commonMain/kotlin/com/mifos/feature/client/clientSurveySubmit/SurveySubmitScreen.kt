/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSurveySubmit

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_submit_survey
import androidclient.feature.client.generated.resources.feature_client_scorecard_created_successfully
import androidclient.feature.client.generated.resources.feature_client_submit_survey
import androidclient.feature.client.generated.resources.feature_client_survey_successfully_submitted
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.mifos.core.model.objects.surveys.Scorecard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
internal fun SurveySubmitScreen(
    uiState: SurveySubmitUiState,
    submitSurvey: () -> Unit,
    noOfQuestions: Int = 0,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    when (uiState) {
        SurveySubmitUiState.Initial -> {
            SurveySubmitContent(
                showButton = true,
                displayText = "Attempt Questions: $noOfQuestions",
                submitSurvey = submitSurvey,
            )
        }

        is SurveySubmitUiState.ShowSurveySubmittedSuccessfully -> {
            SurveySubmitContent(
                showButton = false,
                displayText = stringResource(Res.string.feature_client_survey_successfully_submitted),
                submitSurvey = submitSurvey,
            )

            val scorecardCreatedSuccess = stringResource(Res.string.feature_client_scorecard_created_successfully)

            LaunchedEffect(key1 = true) {
                snackbarHostState.showSnackbar(
                    message = scorecardCreatedSuccess,
                    duration = SnackbarDuration.Long,
                )
            }
        }

        is SurveySubmitUiState.ShowError -> {
            SurveySubmitContent(
                showButton = false,
                displayText = stringResource(Res.string.feature_client_failed_to_submit_survey),
                submitSurvey = submitSurvey,
            )
            LaunchedEffect(key1 = true) {
                snackbarHostState.showSnackbar(
                    message = uiState.message,
                    duration = SnackbarDuration.Long,
                )
            }
        }

        SurveySubmitUiState.ShowProgressbar -> {
            SurveySubmitContent(
                showButton = false,
                displayText = stringResource(Res.string.feature_client_survey_successfully_submitted),
                submitSurvey = submitSurvey,
            )
            MifosProgressIndicator()
        }
    }
}

@Composable
internal fun SurveySubmitContent(
    displayText: String,
    submitSurvey: () -> Unit,
    showButton: Boolean = true,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Card(
                modifier = Modifier.padding(horizontal = 40.dp),
                shape = RoundedCornerShape(4.dp),
            ) {
                Card(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.dp)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

            if (showButton) {
                Button(
                    enabled = showButton,
                    onClick = {
                        submitSurvey.invoke()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                ) {
                    Text(text = stringResource(Res.string.feature_client_submit_survey))
                }
            }
        }
    }
}

private class SurveySubmitPreviewProvider : PreviewParameterProvider<SurveySubmitUiState> {

    override val values: Sequence<SurveySubmitUiState>
        get() = sequenceOf(
            SurveySubmitUiState.Initial,
            SurveySubmitUiState.ShowProgressbar,
            SurveySubmitUiState.ShowSurveySubmittedSuccessfully(Scorecard()),
            SurveySubmitUiState.ShowError("Error"),
        )
}

@Composable
@DevicePreview
private fun PreviewSurveyListScreen(
    @PreviewParameter(SurveySubmitPreviewProvider::class) surveySubmitUiState: SurveySubmitUiState,
) {
    SurveySubmitScreen(
        uiState = surveySubmitUiState,
        submitSurvey = { },
        noOfQuestions = 0,
    )
}
