/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.recurringDepositAccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecurringDepositAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
) : BaseViewModel<
    RecurringDepositAccountState,
    RecurringDepositAccountEvent,
    RecurringDepositAccountAction,
    >
    (initialState = RecurringDepositAccountState()) {

    val route = savedStateHandle.toRoute<RecurringDepositAccountRoute>()

    init {
        checkNetworkAndGetLoanAccounts()
    }

    override fun handleAction(action: RecurringDepositAccountAction) {
        when (action) {
            RecurringDepositAccountAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is RecurringDepositAccountAction.NavigateBack -> {
                sendEvent(RecurringDepositAccountEvent.OnNavigateBack)
            }

            is RecurringDepositAccountAction.Refresh -> {
                checkNetworkAndGetLoanAccounts()
            }

            is RecurringDepositAccountAction.Search -> {
                checkNetworkAndGetLoanAccounts()
            }

            is RecurringDepositAccountAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(
                        isFilterDialogOpen = true,
                    )
                }
            }

            is RecurringDepositAccountAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(
                        isSearchBarActive = true,
                    )
                }
            }

            is RecurringDepositAccountAction.UpdateSearch -> {
                mutableStateFlow.update {
                    it.copy(
                        searchText = action.query,
                    )
                }
            }

            is RecurringDepositAccountAction.ViewAccount -> {
                sendEvent(
                    RecurringDepositAccountEvent.OnViewAccount(action.accountNumber),
                )
            }

            is RecurringDepositAccountAction.ApproveAccount -> {
                sendEvent(
                    RecurringDepositAccountEvent.OnApproveAccount(action.accountNumber),
                )
            }
        }
    }

    private fun checkNetworkAndGetLoanAccounts() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                when (isConnected) {
                    true -> getRecurringDepositAccounts()
                    false -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = RecurringDepositAccountState
                                    .DialogState.Error("No internet connection, Try Again"),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getRecurringDepositAccounts() {
        viewModelScope.launch {
            getClientDetailsUseCase.invoke(route.clientId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = RecurringDepositAccountState.DialogState.Error(
                                    result.message,
                                ),
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = RecurringDepositAccountState.DialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val recurringDepositAccount =
                            result.data.clientAccounts?.savingsAccounts?.let {
                                it.filter { accountEntity ->
                                    accountEntity.depositType?.serverType == SavingAccountDepositTypeEntity.ServerTypes.RECURRING && accountEntity.status?.closed == false
                                }.apply {
                                    // Todo modify search accordingly
                                    searchRecurringDepositAccounts(state.searchText, this)
                                }
                            } ?: emptyList()

                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                                clientId = route.clientId,
                                recurringDepositAccounts = recurringDepositAccount,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun searchRecurringDepositAccounts(
        query: String,
        recurringDepositAccounts: List<SavingsAccountEntity>,
    ): List<SavingsAccountEntity> {
        if (query.isNotBlank()) {
            return recurringDepositAccounts.filter { accountEntity ->
                accountEntity.accountNo.toString().contains(state.searchText.trim())
            }
        }
        return recurringDepositAccounts
    }
}

data class RecurringDepositAccountState(
    val clientId: Int = -1,
    val recurringDepositAccounts: List<SavingsAccountEntity> = emptyList(),
    val searchText: String = "",
    val dialogState: DialogState? = null,
    val isSearchBarActive: Boolean = false,
    val isFilterDialogOpen: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed class RecurringDepositAccountAction {
    data object NavigateBack : RecurringDepositAccountAction()
    data class ViewAccount(val accountNumber: String) : RecurringDepositAccountAction()
    data class ApproveAccount(val accountNumber: String) : RecurringDepositAccountAction()
    data object Refresh : RecurringDepositAccountAction()
    data object ToggleFilter : RecurringDepositAccountAction()
    data object ToggleSearch : RecurringDepositAccountAction()
    data class Search(val query: String) : RecurringDepositAccountAction()
    data class UpdateSearch(val query: String) : RecurringDepositAccountAction()
    data object CloseDialog : RecurringDepositAccountAction()
}

sealed class RecurringDepositAccountEvent {
    data object OnNavigateBack : RecurringDepositAccountEvent()
    data class OnViewAccount(val accountNumber: String) : RecurringDepositAccountEvent()
    data class OnApproveAccount(val accountNumber: String) : RecurringDepositAccountEvent()
}
