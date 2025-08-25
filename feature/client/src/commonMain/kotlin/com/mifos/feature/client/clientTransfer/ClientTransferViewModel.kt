/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientTransfer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

internal class ClientTransferViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val repository: CreateNewGroupRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientTransferState, ClientTransferEvent, ClientTransferAction>(
    initialState = ClientTransferState(),
) {
    private val route = savedStateHandle.toRoute<ClientTransferRoute>()

    init {
        mutableStateFlow.update {
            it.copy(
                id = route.id,
            )
        }
        getTransferOptionsAndObserveNetwork()
    }

    private fun getTransferOptionsAndObserveNetwork() {
        viewModelScope.launch {
            observeNetwork()
            loadOffices()
        }
    }

    private suspend fun loadOffices() {
        repository.offices().collect { result ->
            when (result) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientTransferState.DialogState.Error(result.message),
                        )
                    }
                }
                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientTransferState.DialogState.Loading,
                        )
                    }
                }
                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                            offices = result.data,
                        )
                    }
                }
            }
        }
    }

    private suspend fun transferClient() {
        mutableStateFlow.update { it.copy(dialogState = ClientTransferState.DialogState.Loading) }
        val result = repo.proposeTransfer(
            clientId = route.id,
            destinationOfficeId = state.offices[state.currentSelectedIndex].id,
            transferDate = DateHelper.getDateAsStringFromLong(state.date),
            note = state.note,
        )
        when {
            result is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientTransferState.DialogState.ShowStatusDialog(ResultStatus.SUCCESS),
                    )
                }
            }
            result is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientTransferState.DialogState.ShowStatusDialog(ResultStatus.FAILURE, result.message),
                    )
                }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    override fun handleAction(action: ClientTransferAction) {
        when (action) {
            ClientTransferAction.NavigateBack -> sendEvent(ClientTransferEvent.NavigateBack)
            ClientTransferAction.OnRetry -> getTransferOptionsAndObserveNetwork()
            ClientTransferAction.OnNext -> sendEvent(ClientTransferEvent.NavigateNext)
            is ClientTransferAction.OptionChanged -> {
                mutableStateFlow.update { it.copy(currentSelectedIndex = action.index) }
            }
            is ClientTransferAction.NoteChanged -> {
                mutableStateFlow.update { it.copy(note = action.note) }
            }
            ClientTransferAction.OnSubmit -> {
                viewModelScope.launch { transferClient() }
            }

            is ClientTransferAction.UpdateDatePicker -> {
                mutableStateFlow.update {
                    it.copy(showDatePicker = action.status)
                }
            }

            is ClientTransferAction.UpdateDate -> {
                mutableStateFlow.update {
                    it.copy(date = action.date)
                }
            }

            ClientTransferAction.DismissDialogAndClearAll -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                        currentSelectedIndex = 0,
                        note = "",
                        date = Clock.System.now().toEpochMilliseconds(),
                    )
                }
            }
        }
    }
}

data class ClientTransferState(
    val id: Int = -1,
    val offices: List<OfficeEntity> = emptyList(),
    val showDatePicker: Boolean = false,
    val currentSelectedIndex: Int = 0,
    val note: String = "",
    val date: Long = Clock.System.now().toEpochMilliseconds(),
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
    val isEnabled: Boolean = note.isNotEmpty()
}

sealed interface ClientTransferEvent {
    data object NavigateBack : ClientTransferEvent
    data object NavigateNext : ClientTransferEvent
}

sealed interface ClientTransferAction {
    data object NavigateBack : ClientTransferAction
    data object OnRetry : ClientTransferAction
    data object OnNext : ClientTransferAction
    data class OptionChanged(val index: Int) : ClientTransferAction
    data class NoteChanged(val note: String) : ClientTransferAction
    data object OnSubmit : ClientTransferAction
    data class UpdateDatePicker(val status: Boolean) : ClientTransferAction
    data class UpdateDate(val date: Long) : ClientTransferAction
    data object DismissDialogAndClearAll : ClientTransferAction
}
