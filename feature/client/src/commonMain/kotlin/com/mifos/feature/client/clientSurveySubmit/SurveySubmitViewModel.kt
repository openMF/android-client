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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SurveySubmitRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.surveys.Scorecard
import com.mifos.room.entities.survey.SurveyEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SurveySubmitViewModel(
    private val repository: SurveySubmitRepository,
    private val prefManager: UserPreferencesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = -1)
    private val surveyItem =
        savedStateHandle.getStateFlow(key = Constants.CLIENT_SURVEY, initialValue = "")
    val survey: SurveyEntity =
        Json.decodeFromString<SurveyEntity>(surveyItem.value)

    private val _surveySubmitUiState =
        MutableStateFlow<SurveySubmitUiState>(SurveySubmitUiState.Initial)

    val surveySubmitUiState: StateFlow<SurveySubmitUiState>
        get() = _surveySubmitUiState

    val userId: StateFlow<Int> = prefManager.userData
        .map { it.userId.toInt() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0,
        )

    fun submitSurvey(survey: Int, scorecardPayload: Scorecard?) {
        viewModelScope.launch {
            repository.submitScore(survey, scorecardPayload).collect { result ->
                when (result) {
                    is DataState.Loading ->
                        _surveySubmitUiState.value =
                            SurveySubmitUiState.ShowProgressbar

                    is DataState.Success ->
                        _surveySubmitUiState.value =
                            SurveySubmitUiState.ShowSurveySubmittedSuccessfully(result.data)

                    is DataState.Error ->
                        _surveySubmitUiState.value =
                            SurveySubmitUiState.ShowError(result.message)
                }
            }
        }
    }
}
