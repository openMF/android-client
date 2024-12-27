/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSurveyQuestion

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.gson.Gson
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.White
import com.mifos.core.modelobjects.surveys.Scorecard
import com.mifos.core.modelobjects.surveys.ScorecardValues
import com.mifos.core.objects.survey.Survey
import com.mifos.feature.client.R
import com.mifos.feature.client.clientSurveySubmit.SurveySubmitScreen
import com.mifos.feature.client.clientSurveySubmit.SurveySubmitUiState
import com.mifos.feature.client.clientSurveySubmit.SurveySubmitViewModel
import java.util.Date

@SuppressLint("MutableCollectionMutableState")
@Composable
internal fun SurveyQuestionScreen(
    navigateBack: () -> Unit,
    survey: Survey?,
    viewModel: SurveySubmitViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.surveySubmitUiState.collectAsStateWithLifecycle()
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()
    val userId by viewModel.userId.collectAsStateWithLifecycle()
    val scoreCardData: MutableList<ScorecardValues> by rememberSaveable {
        mutableStateOf(mutableListOf())
    }
    var currentQuestionNumber by rememberSaveable { mutableIntStateOf(0) }
    var showSubmitScreen by rememberSaveable { mutableStateOf(false) }

    if (survey != null) {
        val (questionData, optionsData) = processSurveyData(survey)

        SurveyQuestionScreen(
            uiState = uiState,
            navigateBack = navigateBack,
            questionNumber = currentQuestionNumber,
            currentQuestionData = questionData,
            currentOptionsData = optionsData,
            currentScoreCardData = scoreCardData,
            showSubmitScreen = showSubmitScreen,
            gotoNextQuestion = { index ->
                if (index != -1) {
                    val scoreCardValue = ScorecardValues(
                        questionId = survey.questionDatas[currentQuestionNumber].questionId,
                        responseId = survey.questionDatas[currentQuestionNumber].responseDatas[index].responseId,
                        value = survey.questionDatas[currentQuestionNumber].responseDatas[index].value,
                    )
                    scoreCardData.add(scoreCardValue)
                }
                if (currentQuestionNumber < questionData.size - 1) {
                    currentQuestionNumber += 1
                } else {
                    showSubmitScreen = true
                }
            },
            submitSurvey = {
                if (scoreCardData.isNotEmpty()) {
                    viewModel.submitSurvey(
                        survey = survey.id,
                        scorecardPayload = Scorecard(
                            userId = userId,
                            clientId = clientId,
                            createdOn = Date(),
                            scorecardValues = scoreCardData,
                        ),
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.feature_client_please_attempt_at_least_one_question),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
        )
    }
}

private fun processSurveyData(survey: Survey): Pair<List<String>, List<List<String>>> {
    val questionData = mutableListOf<String>()
    val optionsData = mutableListOf<List<String>>()

    survey.questionDatas.forEach { question ->
        val questionText = Gson().toJson(question.text).replace("\"", "")
        val optionsList = question.responseDatas.map { it.text!! }

        questionData.add(questionText)
        optionsData.add(optionsList)
    }

    return questionData to optionsData
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SurveyQuestionScreen(
    uiState: SurveySubmitUiState,
    navigateBack: () -> Unit,
    questionNumber: Int,
    currentQuestionData: List<String>,
    currentOptionsData: List<List<String>>,
    currentScoreCardData: List<ScorecardValues>,
    gotoNextQuestion: (Int) -> Unit,
    showSubmitScreen: Boolean,
    submitSurvey: () -> Unit,
) {
    val questionData = currentQuestionData.toMutableList()
    val optionsData = remember {
        mutableStateListOf<MutableList<String>>().also {
            it.addAll(currentOptionsData.map { innerList -> innerList.toMutableList() })
        }
    }
    val scoreCardData = currentScoreCardData.toMutableList()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val pagerState = rememberPagerState(pageCount = { 3 })
    LaunchedEffect(questionNumber) {
        pagerState.scrollToPage(questionNumber)
    }

    MifosScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            SurveyQuestionTopBar(
                onBackPressed = { navigateBack.invoke() },
                title = (questionNumber + 1).toString() + "/" + questionData.size,
                showSubmitScreen = showSubmitScreen,
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                pageContent = { page ->
                    if (!showSubmitScreen) {
                        SurveyQuestionContent(
                            questionData = questionData[page],
                            currentOptionsData = optionsData[page],
                            gotoNextQuestion = gotoNextQuestion,
                        )
                    } else {
                        SurveySubmitScreen(
                            uiState = uiState,
                            submitSurvey = submitSurvey,
                            noOfQuestions = scoreCardData.size,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun SurveyQuestionContent(
    questionData: String,
    currentOptionsData: List<String>,
    gotoNextQuestion: (Int) -> Unit,
) {
    val optionsData = currentOptionsData.toMutableList()
    val scrollState = rememberScrollState()
    var selectedOption by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BluePrimary)
                .padding(24.dp),
        ) {
            Text(
                text = questionData,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.Start),
            )
        }

        Column(
            modifier = Modifier
                .background(Color.White),
        ) {
            RadioGroup(
                options = optionsData,
                selectedOptionIndex = selectedOption,
                onOptionSelected = {
                    selectedOption = it
                },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                gotoNextQuestion.invoke(selectedOption)
            },
            modifier = Modifier
                .width(160.dp)
                .align(alignment = Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary,
                contentColor = White,
                disabledContainerColor = Color.DarkGray,
                disabledContentColor = White,
            ),
        ) {
            Text(text = stringResource(id = R.string.feature_client_next))
        }
    }
}

@Composable
private fun RadioGroup(options: List<String>, selectedOptionIndex: Int, onOptionSelected: (Int) -> Unit) {
    Column {
        options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = index == selectedOptionIndex,
                    onClick = { onOptionSelected(index) },
                    colors = RadioButtonDefaults.colors(BluePrimary),
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurveyQuestionTopBar(
    onBackPressed: () -> Unit,
    title: String,
    showSubmitScreen: Boolean,
) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
        navigationIcon = {
            IconButton(
                onClick = { onBackPressed() },
            ) {
                Icon(
                    imageVector = MifosIcons.arrowBack,
                    contentDescription = null,
                    tint = Black,
                )
            }
        },
        title = {
            Column {
                Text(
                    text = stringResource(id = R.string.feature_client_survey),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Normal,
                    ),
                    color = Black,
                    textAlign = TextAlign.Start,
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (!showSubmitScreen) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 16.sp,
                        ),
                        color = Black,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        },
    )
}

@Composable
@Preview(showSystemUi = true)
private fun PreviewSurveyQuestionScreen() {
    SurveyQuestionScreen(
        uiState = SurveySubmitUiState.Initial,
        navigateBack = { },
        questionNumber = 1,
        currentQuestionData = mutableListOf(),
        currentOptionsData = mutableListOf(),
        currentScoreCardData = listOf(),
        gotoNextQuestion = { },
        showSubmitScreen = false,
        submitSurvey = { },
    )
}
