/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_network_not_available
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.ShareAccountRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.network.model.share.ProductOption
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateShareAccountViewModel(
    private val repository: ShareAccountRepository,
    private val networkMonitor: NetworkMonitor,
    val savedStateHandle: SavedStateHandle,

) : BaseViewModel<ShareAccountState, ShareAccountEvent, ShareAccountAction>
    (ShareAccountState()) {

    val route = savedStateHandle.toRoute<CreateShareAccountRoute>()

    init {
        loadShareTemplate(route.clientId)
    }

    private fun loadShareTemplate(client: Int) {
        viewModelScope.launch {
            val online = networkMonitor.isOnline.first()
            if (online) {
                repository.getShareTemplate(client).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Error(dataState.message),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Loading,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ShareAccountState.ScreenState.Success,
                                    productOption = dataState.data.productOptions,
                                )
                            }
                        }
                    }
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ShareAccountState.ScreenState.Error(getString(Res.string.feature_client_error_network_not_available)),
                    )
                }
            }
        }
    }

    private fun moveToNextStep() {
        val current = state.currentStep
        if (current < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = current + 1,
                )
            }
        }
    }

    private fun moveToPreviousStep() {
        val current = state.currentStep
        mutableStateFlow.update {
            it.copy(
                currentStep = current - 1,
            )
        }
    }

    private fun handleOnDetailNext() {
        if (state.shareProductIndex == null) {
            mutableStateFlow.update {
                it.copy(
                    shareProductError = TextFieldsValidator.stringValidator(""),
                )
            }
        } else {
            moveToNextStep()
        }
    }

    override fun handleAction(action: ShareAccountAction) {
        when (action) {
            ShareAccountAction.NextStep -> {
                moveToNextStep()
            }
            is ShareAccountAction.OnStepChange -> {
                mutableStateFlow.update { it.copy(currentStep = action.index) }
            }

            ShareAccountAction.NavigateBack -> {
                sendEvent(ShareAccountEvent.NavigateBack)
            }

            ShareAccountAction.Finish -> {
                sendEvent(ShareAccountEvent.Finish)
            }

            is ShareAccountAction.OnDateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        submissionDate = action.date,
                    )
                }
            }

            is ShareAccountAction.OnOpenDatePicker -> {
                mutableStateFlow.update {
                    it.copy(
                        showDatePicker = action.state,
                    )
                }
            }

            is ShareAccountAction.OnShareProductChange -> {
                mutableStateFlow.update {
                    it.copy(
                        shareProductIndex = action.index,
                        shareProductError = null,
                    )
                }
            }

            is ShareAccountAction.OnExternalIdChange -> {
                mutableStateFlow.update {
                    it.copy(
                        externalId = action.value,
                    )
                }
            }

            ShareAccountAction.Retry -> {
                loadShareTemplate(route.clientId)
            }

            ShareAccountAction.OnDetailNext -> {
                handleOnDetailNext()
            }

            ShareAccountAction.PreviousStep -> {
                moveToPreviousStep()
            }
        }
    }
}

data class ShareAccountState
@OptIn(ExperimentalTime::class)
constructor(
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val dialogState: Any? = null,
    val externalId: String? = null,
    val shareProductIndex: Int? = null,
    val shareProductError: StringResource? = null,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showDatePicker: Boolean = false,
    val productOption: List<ProductOption> = emptyList(),
    val screenState: ScreenState = ScreenState.Loading,
) {
    interface ScreenState {
        object Loading : ScreenState
        object Success : ScreenState
        data class Error(val message: String) : ScreenState
    }
}

sealed interface ShareAccountAction {
    object NextStep : ShareAccountAction
    data object PreviousStep : ShareAccountAction
    data class OnStepChange(val index: Int) : ShareAccountAction
    object NavigateBack : ShareAccountAction
    object Finish : ShareAccountAction
    data class OnShareProductChange(val index: Int) : ShareAccountAction
    data class OnDateChange(val date: String) : ShareAccountAction
    data class OnOpenDatePicker(val state: Boolean) : ShareAccountAction
    data class OnExternalIdChange(val value: String?) : ShareAccountAction
    object OnDetailNext : ShareAccountAction
    object Retry : ShareAccountAction
}

sealed interface ShareAccountEvent {
    object NavigateBack : ShareAccountEvent
    object Finish : ShareAccountEvent
}
