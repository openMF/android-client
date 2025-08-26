/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientClosure

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.network.model.Narration
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

internal class ClientClosureViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientClosureState, ClientClosureEvent, ClientClosureAction>(
    initialState = run {
        ClientClosureState(savedStateHandle.toRoute<ClientClosureRoute>().id)
    },
) {

    init {
        viewModelScope.launch {
            observeNetwork()
            loadClosureReasons()
        }
    }

    private suspend fun loadClosureReasons() {
        mutableStateFlow.update {
            it.copy(dialogState = ClientClosureState.DialogState.Loading)
        }

        val result = repo.getClientCloseTemplate()
        when (result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        reasons = result.data.narrations,
                        dialogState = null,
                    )
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientClosureState.DialogState.Error(result.message),
                    )
                }
            }

            else -> Unit
        }
    }

    private suspend fun closeClient() {
        mutableStateFlow.update { it.copy(dialogState = ClientClosureState.DialogState.Loading) }
        val result = repo.closeClient(
            clientId = state.id,
            closureDate = DateHelper.getDateAsStringFromLong(state.date),
            closureReasonId = state.reasons[state.currentSelectedIndex].id,
        )
        when (result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientClosureState.DialogState.ShowStatusDialog(ResultStatus.SUCCESS),
                    )
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientClosureState.DialogState.ShowStatusDialog(ResultStatus.FAILURE, result.message),
                    )
                }
            }

            else -> Unit
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    override fun handleAction(action: ClientClosureAction) {
        when (action) {
            ClientClosureAction.NavigateBack -> sendEvent(ClientClosureEvent.NavigateBack)
            ClientClosureAction.OnRetry -> {
                viewModelScope.launch {
                    loadClosureReasons()
                }
            }
            ClientClosureAction.OnNext -> sendEvent(ClientClosureEvent.NavigateNext)
            is ClientClosureAction.OptionChanged -> {
                mutableStateFlow.update { it.copy(currentSelectedIndex = action.index) }
            }
            ClientClosureAction.OnSubmit -> {
                viewModelScope.launch { closeClient() }
            }
            is ClientClosureAction.UpdateDatePicker -> {
                mutableStateFlow.update { it.copy(showDatePicker = action.status) }
            }
            is ClientClosureAction.UpdateDate -> {
                mutableStateFlow.update { it.copy(date = action.date) }
            }
        }
    }
}

data class ClientClosureState(
    val id: Int = -1,
    val reasons: List<Narration> = emptyList(),
    val showDatePicker: Boolean = false,
    val currentSelectedIndex: Int = 0,
    val date: Long = Clock.System.now().toEpochMilliseconds(),
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
    val isEnabled: Boolean = reasons.isNotEmpty()
}

sealed interface ClientClosureEvent {
    data object NavigateBack : ClientClosureEvent
    data object NavigateNext : ClientClosureEvent
}

sealed interface ClientClosureAction {
    data object NavigateBack : ClientClosureAction
    data object OnRetry : ClientClosureAction
    data object OnNext : ClientClosureAction
    data class OptionChanged(val index: Int) : ClientClosureAction
    data object OnSubmit : ClientClosureAction
    data class UpdateDatePicker(val status: Boolean) : ClientClosureAction
    data class UpdateDate(val date: Long) : ClientClosureAction
}
