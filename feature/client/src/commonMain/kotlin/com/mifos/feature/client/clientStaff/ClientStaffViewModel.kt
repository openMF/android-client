/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientStaff

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.network.model.StaffOption
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ClientStaffViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientStaffState, ClientStaffEvent, ClientStaffAction>(
    initialState = ClientStaffState(),
) {

    private val route = savedStateHandle.toRoute<ClientStaffRoute>()

    init {
        getStaffOptionsAndObserveNetwork(route.id)
    }

    private fun getStaffOptionsAndObserveNetwork(clientId: Int) {
        observeNetwork()
        loadStaffOptions(clientId)
    }

    private fun loadStaffOptions(clientId: Int) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = ClientStaffState.DialogState.Loading)
            }

            try {
                val options = repo.getClientStaffOptions(clientId)

                mutableStateFlow.update {
                    it.copy(
                        staffOptions = options,
                        dialogState = null,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientStaffState.DialogState.Error(
                            e.message ?: "Unknown error",
                        ),
                    )
                }
            }
        }
    }

    private suspend fun assignStaff() {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientStaffState.DialogState.Loading,
            )
        }
        val result = repo.assignStaff(clientId = route.id, staffId = state.staffOptions[state.currentSelectedIndex].id)
        when {
            result is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientStaffState.DialogState.ShowStatusDialog(ResultStatus.SUCCESS),
                    )
                }
            }
            result is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientStaffState.DialogState.ShowStatusDialog(ResultStatus.FAILURE, result.message),
                    )
                }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update {
                    it.copy(networkConnection = isConnected)
                }
            }
        }
    }

    override fun handleAction(action: ClientStaffAction) {
        when (action) {
            ClientStaffAction.NavigateBack -> sendEvent(ClientStaffEvent.NavigateBack)
            ClientStaffAction.OnRetry -> getStaffOptionsAndObserveNetwork(route.id)
            ClientStaffAction.OnNext -> sendEvent(ClientStaffEvent.NavigateNext)
            is ClientStaffAction.OptionChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        currentSelectedIndex = action.index,
                    )
                }
            }

            ClientStaffAction.OnSubmit -> {
                viewModelScope.launch {
                    assignStaff()
                }
            }
        }
    }
}

data class ClientStaffState(
    val staffOptions: List<StaffOption> = emptyList(),
    val currentSelectedIndex: Int = 0,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
}

sealed interface ClientStaffEvent {
    data object NavigateBack : ClientStaffEvent
    data object NavigateNext : ClientStaffEvent
}

sealed interface ClientStaffAction {
    data object NavigateBack : ClientStaffAction
    data object OnRetry : ClientStaffAction
    data object OnNext : ClientStaffAction
    data class OptionChanged(val index: Int) : ClientStaffAction
    data object OnSubmit : ClientStaffAction
}
