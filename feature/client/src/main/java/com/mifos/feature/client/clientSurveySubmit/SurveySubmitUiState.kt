package com.mifos.feature.client.clientSurveySubmit

import com.mifos.core.objects.survey.Scorecard

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class SurveySubmitUiState {

    data object Initial : SurveySubmitUiState()

    data object ShowProgressbar : SurveySubmitUiState()

    data class ShowError(val message: String) : SurveySubmitUiState()

    data class ShowSurveySubmittedSuccessfully(val scorecard: Scorecard) : SurveySubmitUiState()
}