package com.mifos.feature.client.clientSurveyList

import com.mifos.core.objects.survey.Survey

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class SurveyListUiState {

    data object ShowProgressbar : SurveyListUiState()

    data class ShowFetchingError(val message: Int) : SurveyListUiState()

    data class ShowAllSurvey(val syncSurvey: List<Survey>) : SurveyListUiState()
}