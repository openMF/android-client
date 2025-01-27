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
import com.mifos.core.data.repository.SurveySubmitRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.survey.Scorecard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SurveySubmitViewModel @Inject constructor(
    private val repository: SurveySubmitRepository,
    private val prefManager: PrefManager,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = -1)

    val userId = prefManager.userDetails.map {
        it?.userId?.toInt() ?: 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0,
    )

    private val _surveySubmitUiState =
        MutableStateFlow<SurveySubmitUiState>(SurveySubmitUiState.Initial)

    val surveySubmitUiState: StateFlow<SurveySubmitUiState>
        get() = _surveySubmitUiState

    fun submitSurvey(survey: Int, scorecardPayload: Scorecard?) {
        _surveySubmitUiState.value = SurveySubmitUiState.ShowProgressbar
        repository.submitScore(survey, scorecardPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<Scorecard>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        _surveySubmitUiState.value =
                            SurveySubmitUiState.ShowError(e.message.toString())
                    }

                    override fun onNext(scorecard: Scorecard) {
                        _surveySubmitUiState.value =
                            SurveySubmitUiState.ShowSurveySubmittedSuccessfully(scorecard)
                    }
                },
            )
    }
}
