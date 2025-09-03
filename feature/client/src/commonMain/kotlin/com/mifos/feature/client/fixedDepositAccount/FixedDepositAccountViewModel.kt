/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.fixedDepositAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.no_internet_message
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
import org.jetbrains.compose.resources.getString

class FixedDepositAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
) : BaseViewModel<
    FixedDepositAccountState,
    FixedDepositAccountEvent,
    FixedDepositAccountAction,
    >
    (initialState = FixedDepositAccountState()) {

    val route = savedStateHandle.toRoute<FixedDepositAccountRoute>()

    init {
        checkNetworkAndGetFixedDepositAccounts()
    }

    override fun handleAction(action: FixedDepositAccountAction) {
        when (action) {
            FixedDepositAccountAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is FixedDepositAccountAction.NavigateBack -> {
                sendEvent(FixedDepositAccountEvent.OnNavigateBack)
            }

            is FixedDepositAccountAction.Refresh -> {
                checkNetworkAndGetFixedDepositAccounts()
            }

            is FixedDepositAccountAction.Search -> {
                checkNetworkAndGetFixedDepositAccounts()
            }

            is FixedDepositAccountAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(
                        isFilterDialogOpen = !it.isFilterDialogOpen,
                    )
                }
            }

            is FixedDepositAccountAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(isSearchBarActive = !it.isSearchBarActive)
                }
            }

            is FixedDepositAccountAction.UpdateSearch -> {
                mutableStateFlow.update {
                    it.copy(searchText = action.query)
                }
            }

            is FixedDepositAccountAction.ViewAccount -> {
                sendEvent(
                    FixedDepositAccountEvent.OnViewAccount(action.accountNumber),
                )
            }

            is FixedDepositAccountAction.ApproveAccount -> {
                sendEvent(
                    FixedDepositAccountEvent.OnApproveAccount(action.accountNumber),
                )
            }
        }
    }

    private fun checkNetworkAndGetFixedDepositAccounts() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                when (isConnected) {
                    true -> getFixedDepositAccounts()
                    false -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = FixedDepositAccountState
                                    .DialogState.Error(getString(Res.string.no_internet_message)),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getFixedDepositAccounts() {
        viewModelScope.launch {
            getClientDetailsUseCase.invoke(route.clientId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = FixedDepositAccountState.DialogState.Error(
                                    result.message,
                                ),
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = FixedDepositAccountState.DialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val fixedDepositAccount =
                            result.data.clientAccounts?.savingsAccounts?.let {
                                it.filter { accountEntity ->
                                    accountEntity.depositType?.serverType ==
                                        SavingAccountDepositTypeEntity.ServerTypes.FIXED &&
                                        accountEntity.status?.closed == false
                                }.filter { accountEntity ->
                                    accountEntity.accountNo.toString().contains(state.searchText.trim())
                                }
                            } ?: emptyList()

                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                                clientId = route.clientId,
                                fixedDepositAccount = fixedDepositAccount,
                            )
                        }
                    }
                }
            }
        }
    }
}

data class FixedDepositAccountState(
    val clientId: Int = -1,
    val fixedDepositAccount: List<SavingsAccountEntity> = emptyList(),
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

sealed class FixedDepositAccountAction {
    data object NavigateBack : FixedDepositAccountAction()
    data class ViewAccount(val accountNumber: String) : FixedDepositAccountAction()
    data class ApproveAccount(val accountNumber: String) : FixedDepositAccountAction()
    data object Refresh : FixedDepositAccountAction()
    data object ToggleFilter : FixedDepositAccountAction()
    data object ToggleSearch : FixedDepositAccountAction()
    data object Search : FixedDepositAccountAction()
    data class UpdateSearch(val query: String) : FixedDepositAccountAction()
    data object CloseDialog : FixedDepositAccountAction()
}

sealed class FixedDepositAccountEvent {
    data object OnNavigateBack : FixedDepositAccountEvent()
    data class OnViewAccount(val accountNumber: String) : FixedDepositAccountEvent()
    data class OnApproveAccount(val accountNumber: String) : FixedDepositAccountEvent()
}
