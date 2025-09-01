package com.mifos.feature.client.fixedDepositAccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.recurringDepositAccount.RecurringDepositAccountRoute
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecurringDepositAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
) : BaseViewModel<
        FixedDepositAccountState,
        FixedDepositAccountEvent,
        FixedDepositAccountAction,
        >
    (initialState = FixedDepositAccountState())
{

    val route = savedStateHandle.toRoute<RecurringDepositAccountRoute>()

    init {
        checkNetworkAndGetLoanAccounts()
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
                checkNetworkAndGetLoanAccounts()
            }

            is FixedDepositAccountAction.Search -> {
                checkNetworkAndGetLoanAccounts()
            }

            is FixedDepositAccountAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(
                        isFilterDialogOpen = true,
                    )
                }
            }

            is FixedDepositAccountAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(
                        isSearchBarActive = true,
                    )
                }
            }

            is FixedDepositAccountAction.UpdateSearch -> {
                mutableStateFlow.update {
                    it.copy(
                        searchText = action.query,
                    )
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

    private fun checkNetworkAndGetLoanAccounts() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                when (isConnected) {
                    true -> getRecurringDepositAccounts()
                    false -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = FixedDepositAccountState
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
                        val recurringDepositAccount =
                            result.data.clientAccounts?.savingsAccounts?.let {
                                it.filter { accountEntity ->
                                    accountEntity.depositType?.serverType ==
                                            SavingAccountDepositTypeEntity.ServerTypes.FIXED &&
                                            accountEntity.status?.closed == false
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

data class FixedDepositAccountState(
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

sealed class FixedDepositAccountAction {
    data object NavigateBack : FixedDepositAccountAction()
    data class ViewAccount(val accountNumber: String) : FixedDepositAccountAction()
    data class ApproveAccount(val accountNumber: String) : FixedDepositAccountAction()
    data object Refresh : FixedDepositAccountAction()
    data object ToggleFilter : FixedDepositAccountAction()
    data object ToggleSearch : FixedDepositAccountAction()
    data class Search(val query: String) : FixedDepositAccountAction()
    data class UpdateSearch(val query: String) : FixedDepositAccountAction()
    data object CloseDialog : FixedDepositAccountAction()
}

sealed class FixedDepositAccountEvent {
    data object OnNavigateBack : FixedDepositAccountEvent()
    data class OnViewAccount(val accountNumber: String) : FixedDepositAccountEvent()
    data class OnApproveAccount(val accountNumber: String) : FixedDepositAccountEvent()
}
