package com.mifos.feature.client.clientSurveyList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.survey.Survey
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.feature.client.R

/**
 * Created by Pronay Sarker on 03/07/2024 (6:05 AM)
 */

@Composable
fun SurveyListScreen(
    viewModel: SurveyListViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    onCardClicked: (index: Int, surveys: List<Survey>) -> Unit
) {
    val uiState by viewModel.surveyListUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadSurveyList()
    }

    SurveyListScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewModel.loadSurveyList() },
        onCardClicked = onCardClicked
    )
}

@Composable
fun SurveyListScreen(
    uiState: SurveyListUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    onCardClicked: (index: Int, surveys: List<Survey>) -> Unit
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        icon = MifosIcons.arrowBack,
        onBackPressed = navigateBack,
        title = stringResource(id = R.string.feature_client_surveys)
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (uiState) {

                is SurveyListUiState.ShowAllSurvey -> {
                    if (uiState.syncSurvey.isEmpty()) {
                        MifosEmptyUi(text = stringResource(id = R.string.feature_client_no_survey_available_for_client))
                    } else {
                        SurveyListContent(
                            surveyList = uiState.syncSurvey,
                            onCardClicked = onCardClicked
                        )
                    }
                }

                is SurveyListUiState.ShowFetchingError -> {
                    MifosSweetError(
                        message = stringResource(id = uiState.message),
                        onclick = onRetry
                    )
                }

                SurveyListUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }
            }
        }
    }
}

@Composable
fun SurveyListContent(
    surveyList: List<Survey>,
    onCardClicked: (index: Int, surveys: List<Survey>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            text = stringResource(id = R.string.feature_client_select_one_survey),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            )
        )

        LazyColumn {
            items(surveyList) { survey ->
                SurveyCardItem(
                    surveyName = survey.name,
                    description = survey.description,
                    onCardClicked = { onCardClicked.invoke(surveyList.indexOf(survey), surveyList) }
                )
            }
        }
    }
}

@Composable
fun SurveyCardItem(
    surveyName: String?,
    description: String?,
    onCardClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onCardClicked,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(5.dp)
                    .background(color = Color(0xFF4285f6))
            )

            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Text(
                    text = surveyName ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = description ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8f)
                )
            }
        }
    }
}

class SurveyListPreviewProvider : PreviewParameterProvider<SurveyListUiState> {
    var demoSurvey = listOf(
        Survey(name = "Survey header", description = "Survey description"),
        Survey(name = "Header", description = null),
        Survey(name = "General Knowledge", description = "What is the capital of MARS"),
        Survey(name = "Favourite youtuber?", description = "Dhinkchak pooja"),
        Survey(name = "Phone survey", description = "Samsung is better than iphone"),
    )

    override val values: Sequence<SurveyListUiState>
        get() = sequenceOf(
            SurveyListUiState.ShowProgressbar,
            SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_fetch_datatable),
            SurveyListUiState.ShowAllSurvey(demoSurvey)
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewSurveyListScreen(
    @PreviewParameter(SurveyListPreviewProvider::class) surveyListUiState: SurveyListUiState
) {
    SurveyListScreen(
        uiState = surveyListUiState,
        navigateBack = { },
        onRetry = { },
        onCardClicked = { _, _ ->

        }
    )
}