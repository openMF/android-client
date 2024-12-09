package com.mifos.feature.client.clientSurveyQuestion

import com.mifos.core.objects.survey.Survey

sealed class SurveyQuestionUiState {

    data object ShowProgressbar : SurveyQuestionUiState()

    data class ShowFetchingError(val message: Int) : SurveyQuestionUiState()

    data class ShowQuestions(val ques: Survey) : SurveyQuestionUiState()

}