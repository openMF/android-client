package com.mifos.feature.client.clientSurveySubmit

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.survey.Scorecard
import com.mifos.feature.client.R


@Composable
fun SurveySubmitScreen(
    uiState: SurveySubmitUiState,
    submitSurvey: () -> Unit,
    noOfQuestions: Int = 0
) {
    val context = LocalContext.current

    when (uiState) {

        SurveySubmitUiState.Initial -> {
            SurveySubmitContent(
                showButton = true,
                displayText = "Attempt Questions: $noOfQuestions",
                submitSurvey = submitSurvey
            )
        }

        is SurveySubmitUiState.ShowSurveySubmittedSuccessfully -> {

            SurveySubmitContent(
                showButton = false,
                displayText = stringResource(id = R.string.feature_client_survey_successfully_submitted),
                submitSurvey = submitSurvey
            )

            LaunchedEffect(key1 = true) {
                Toast.makeText(
                    context,
                    context.getString(R.string.feature_client_scorecard_created_successfully),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        is SurveySubmitUiState.ShowError -> {
            SurveySubmitContent(
                showButton = false,
                displayText = stringResource(id = R.string.feature_client_failed_to_submit_survey),
                submitSurvey = submitSurvey
            )
            LaunchedEffect(key1 = true) {
                Toast.makeText(
                    context, uiState.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        SurveySubmitUiState.ShowProgressbar -> {
            SurveySubmitContent(
                showButton = false,
                displayText = stringResource(id = R.string.feature_client_survey_successfully_submitted),
                submitSurvey = submitSurvey
            )
            MifosCircularProgress()
        }
    }

}

@Composable
fun SurveySubmitContent(
    showButton: Boolean = true,
    displayText: String,
    submitSurvey: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            Card(
                modifier = Modifier.padding(horizontal = 40.dp),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = BluePrimary)
            ) {
                Card(
                    modifier = Modifier
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.dp)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        contentColor = White,
                        disabledContainerColor = Color.DarkGray,
                        disabledContentColor = White
                    ),
                ) {
                    Text(text = stringResource(id = R.string.feature_client_submit_survey))
                }
            }
        }
    }
}

class SurveySubmitPreviewProvider : PreviewParameterProvider<SurveySubmitUiState> {

    override val values: Sequence<SurveySubmitUiState>
        get() = sequenceOf(
            SurveySubmitUiState.Initial,
            SurveySubmitUiState.ShowProgressbar,
            SurveySubmitUiState.ShowSurveySubmittedSuccessfully(Scorecard()),
            SurveySubmitUiState.ShowError("Error")
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewSurveyListScreen(
    @PreviewParameter(SurveySubmitPreviewProvider::class) surveySubmitUiState: SurveySubmitUiState
) {
    SurveySubmitScreen(
        uiState = surveySubmitUiState,
        submitSurvey = { },
        noOfQuestions = 0
    )
}