/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientGeneral

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientGeneral.ClientProfileGeneralEvent.OnActionClick
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.zipmodels.ClientAndClientAccounts
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ClientProfileGeneralViewmodel(
    savedStateHandle: SavedStateHandle,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientProfileGeneralState, ClientProfileGeneralEvent, ClientProfileGeneralAction>(
    initialState = ClientProfileGeneralState(),
) {
    private val route = savedStateHandle.toRoute<ClientProfileGeneralRoute>()

    init {
        getClientAndObserveNetwork()
    }

    override fun handleAction(action: ClientProfileGeneralAction) {
        when (action) {
            ClientProfileGeneralAction.NavigateBack -> sendEvent(ClientProfileGeneralEvent.NavigateBack)
            is ClientProfileGeneralAction.OnActionClick -> sendEvent(OnActionClick(action.action))

            ClientProfileGeneralAction.OnRetry -> getClientAndObserveNetwork()
        }
    }

    private fun loadPerformanceHistory(
        clientAndClientAccounts: ClientAndClientAccounts,
    ): ClientProfileGeneralState.PerformanceHistory {
        val loanAccounts = clientAndClientAccounts.clientAccounts?.loanAccounts
        val savingAccounts = clientAndClientAccounts.clientAccounts?.savingsAccounts

        val loanCyclesCount =
            loanAccounts?.filter { it.status?.active == true }?.sumOf { it.loanCycle ?: 0 } ?: 0

        val activeLoanAccounts = loanAccounts?.count { it.status?.active == true } ?: 0

        val activeSavingsCount = savingAccounts?.count { it.status?.active == true } ?: 0

        val totalSaving =
            savingAccounts?.filter {
                it.status?.active == true &&
                    it.depositType?.serverType != SavingAccountDepositTypeEntity.ServerTypes.RECURRING &&
                    it.status?.closed == false
            }?.sumOf {
                it.accountBalance ?: 0.0
            }

        // TODO: No function yet created for calculating this value.
        val lastLoanAmount = null

        return ClientProfileGeneralState.PerformanceHistory(
            loanCyclesCount = loanCyclesCount,
            activeLoans = activeLoanAccounts,
            lastLoanAmount = lastLoanAmount,
            activeSavingsCount = activeSavingsCount,
            totalSaving = totalSaving,
        )
    }

    private fun getClientAndObserveNetwork() {
        observeNetwork()
        loadClientDetails(route.id)
    }

    /**
     * Observes the network connectivity status and updates state accordingly.
     */
    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update {
                    it.copy(networkConnection = isConnected)
                }
            }
        }
    }

    /**
     * Fetches both client details and profile image.
     *
     * @param clientId ID of the client whose details need to be fetched.
     */
    private fun loadClientDetails(clientId: Int) {
        viewModelScope.launch {
            getClientDetailsUseCase(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val performanceHistory = loadPerformanceHistory(result.data)
                        mutableStateFlow.update {
                            it.copy(
                                currency = result.data.clientAccounts?.savingsAccounts?.firstOrNull()?.currency?.displaySymbol
                                    ?: "$",
                                client = result.data.client,
                                performanceHistory = performanceHistory,
                                dialogState = null,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileGeneralState.DialogState.Error(result.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileGeneralState.DialogState.Loading,
                            )
                        }
                    }
                }
            }
        }
    }
}

data class ClientProfileGeneralState(
    val currency: String = "$",
    val client: ClientEntity? = null,
    val performanceHistory: PerformanceHistory = PerformanceHistory(),
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {

    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }

    data class PerformanceHistory(
        var loanCyclesCount: Int? = null,
        var activeLoans: Int? = null,
        var lastLoanAmount: Double? = null,
        var activeSavingsCount: Int? = null,
        var totalSaving: Double? = null,
    )
}

sealed interface ClientProfileGeneralEvent {
    /** Navigates back to the previous screen */
    data object NavigateBack : ClientProfileGeneralEvent

    /** Triggered when an action item is clicked */
    data class OnActionClick(val action: ClientProfileGeneralActionItem) : ClientProfileGeneralEvent
}

sealed interface ClientProfileGeneralAction {
    /** Navigate back from the screen */
    data object NavigateBack : ClientProfileGeneralAction

    /** User clicks on an action item */
    data class OnActionClick(val action: ClientProfileGeneralActionItem) :
        ClientProfileGeneralAction

    /** User clicks on Retry */
    data object OnRetry : ClientProfileGeneralAction
}
