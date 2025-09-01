/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.savingsAccounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SavingsAccountsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ClientDetailsRepository,
) : BaseViewModel<SavingsAccountState, SavingsAccountEvent, SavingsAccountAction>(
    initialState = SavingsAccountState(),
) {
    private val route = savedStateHandle.toRoute<SavingsAccountsRoute>()

    override fun handleAction(action: SavingsAccountAction) {
        when (action) {
            is SavingsAccountAction.ApproveAccount -> sendEvent(SavingsAccountEvent.ApproveAccount)

            SavingsAccountAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(isFilterDialogOpen = !it.isFilterDialogOpen)
                }
            }

            SavingsAccountAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(isSearchBarActive = !it.isSearchBarActive)
                }
            }

            is SavingsAccountAction.ViewAccount -> {
                sendEvent(SavingsAccountEvent.ViewAccount(state.clientId))
            }

            SavingsAccountAction.Refresh -> {
                getSavingsAccount()
            }

            SavingsAccountAction.NavigateBack -> sendEvent(SavingsAccountEvent.NavigateBack)

            SavingsAccountAction.OnSearchClick -> {
                getSavingsAccount()
            }

            is SavingsAccountAction.UpdateSearchValue -> {
                mutableStateFlow.update {
                    it.copy(searchText = action.query)
                }
            }

            SavingsAccountAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
        }
    }

    init {
        getSavingsAccount()
    }

    private fun getSavingsAccount() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = SavingsAccountState.DialogState.Loading)
            }
            try {
                // Todo modify search accordingly
                val savingsAccounts = repository.getClientAccounts(route.clientId)
                    .savingsAccounts
                    .filter { accountEntity ->
                        accountEntity.depositType?.serverType == SavingAccountDepositTypeEntity.ServerTypes.SAVINGS &&
                            accountEntity.status?.closed == false
                    }
                    .filter { it.accountNo?.contains(state.searchText.trim()) == true }

                mutableStateFlow.update {
                    it.copy(
                        savingsAccounts = savingsAccounts,
                        dialogState = null,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = SavingsAccountState.DialogState.Error(
                            e.message ?: "Unknown error",
                        ),
                    )
                }
            }
        }
    }
}

data class SavingsAccountState(
    val clientId: Int = -1,
    val isSearchBarActive: Boolean = false,
    val searchText: String = "",
    val savingsAccounts: List<SavingsAccountEntity> = emptyList(),
    val isFilterDialogOpen: Boolean = false,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed interface SavingsAccountEvent {
    data object NavigateBack : SavingsAccountEvent
    data object ApproveAccount : SavingsAccountEvent
    data class ViewAccount(val id: Int) : SavingsAccountEvent
}

sealed interface SavingsAccountAction {
    data object ToggleSearch : SavingsAccountAction
    data object NavigateBack : SavingsAccountAction
    data object ToggleFilter : SavingsAccountAction
    data object Refresh : SavingsAccountAction
    data class ApproveAccount(val accountId: Int) : SavingsAccountAction
    data class ViewAccount(val accountId: Int) : SavingsAccountAction
    data class UpdateSearchValue(val query: String) : SavingsAccountAction
    data object OnSearchClick : SavingsAccountAction
    data object CloseDialog : SavingsAccountAction
}
