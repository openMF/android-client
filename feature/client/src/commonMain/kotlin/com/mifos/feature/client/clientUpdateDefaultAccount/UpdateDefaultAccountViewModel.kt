/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientUpdateDefaultAccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.network.model.SavingAccountOption
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class UpdateDefaultAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<UpdateDefaultAccountState, UpdateDefaultAccountEvent, UpdateDefaultAccountAction>(
    initialState = UpdateDefaultAccountState(),
) {
    private val route = savedStateHandle.toRoute<UpdateDefaultAccountRoute>()

    init {
        mutableStateFlow.update {
            it.copy(id = route.clientId)
        }
        getAccountsAndObserveNetwork()
    }

    private fun getAccountsAndObserveNetwork() {
        viewModelScope.launch {
            observeNetwork()
            loadAccounts()
        }
    }

    private suspend fun loadAccounts() {
        mutableStateFlow.update {
            it.copy(dialogState = UpdateDefaultAccountState.DialogState.Loading)
        }

        try {
            val options = repo.getSavingsAccounts(route.clientId)
            mutableStateFlow.update {
                it.copy(
                    accounts = options,
                    dialogState = null,
                )
            }
        } catch (e: Exception) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = UpdateDefaultAccountState.DialogState.Error(
                        e.message ?: "Unknown error",
                    ),
                )
            }
        }
    }

    private suspend fun updateDefaultAccount() {
        mutableStateFlow.update { it.copy(dialogState = UpdateDefaultAccountState.DialogState.Loading) }
        val accountId = state.accounts[state.currentSelectedIndex].id
        val result = repo.updateDefaultSavingsAccount(route.clientId, accountId)
        when (result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(dialogState = UpdateDefaultAccountState.DialogState.ShowStatusDialog(ResultStatus.SUCCESS))
                }
            }
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = UpdateDefaultAccountState.DialogState.ShowStatusDialog(
                            ResultStatus.FAILURE,
                            result.message,
                        ),
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

    override fun handleAction(action: UpdateDefaultAccountAction) {
        when (action) {
            UpdateDefaultAccountAction.NavigateBack -> sendEvent(UpdateDefaultAccountEvent.NavigateBack)
            UpdateDefaultAccountAction.OnRetry -> getAccountsAndObserveNetwork()
            UpdateDefaultAccountAction.OnNext -> sendEvent(UpdateDefaultAccountEvent.NavigateNext)
            is UpdateDefaultAccountAction.OptionChanged -> {
                mutableStateFlow.update { it.copy(currentSelectedIndex = action.index) }
            }
            UpdateDefaultAccountAction.OnSave -> {
                viewModelScope.launch { updateDefaultAccount() }
            }

            UpdateDefaultAccountAction.Dismiss -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
            }
        }
    }
}

data class UpdateDefaultAccountState(
    val id: Int = -1,
    val accounts: List<SavingAccountOption> = emptyList(),
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

sealed interface UpdateDefaultAccountEvent {
    data object NavigateBack : UpdateDefaultAccountEvent
    data object NavigateNext : UpdateDefaultAccountEvent
}

sealed interface UpdateDefaultAccountAction {
    data object NavigateBack : UpdateDefaultAccountAction
    data object OnRetry : UpdateDefaultAccountAction
    data object OnNext : UpdateDefaultAccountAction
    data class OptionChanged(val index: Int) : UpdateDefaultAccountAction
    data object OnSave : UpdateDefaultAccountAction
    data object Dismiss : UpdateDefaultAccountAction
}
