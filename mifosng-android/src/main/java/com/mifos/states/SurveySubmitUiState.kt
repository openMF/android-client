package com.mifos.states

import com.mifos.objects.survey.Scorecard

sealed class SurveySubmitUiState {

    object ShowProgressbar : SurveySubmitUiState()

    data class ShowError(val message : String) : SurveySubmitUiState()

    data class ShowSurveySubmittedSuccessfully(val scorecard: Scorecard) : SurveySubmitUiState()
}