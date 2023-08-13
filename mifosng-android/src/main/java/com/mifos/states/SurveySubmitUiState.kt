package com.mifos.states

import com.mifos.objects.survey.Scorecard

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class SurveySubmitUiState {

    object ShowProgressbar : SurveySubmitUiState()

    data class ShowError(val message: String) : SurveySubmitUiState()

    data class ShowSurveySubmittedSuccessfully(val scorecard: Scorecard) : SurveySubmitUiState()
}