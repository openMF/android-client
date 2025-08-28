package com.mifos.feature.client.clientLoanAccounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.savingsAccounts.SavingsAccountState
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientLoanAccountsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ClientDetailsRepository,
) : BaseViewModel<ClientLoanAccountsState, ClientLoanAccountsEvent, ClientLoanAccountsAction>(
    initialState = ClientLoanAccountsState(),
) {
    override fun handleAction(action: ClientLoanAccountsAction) {
        when(action){
            ClientLoanAccountsAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is ClientLoanAccountsAction.MakeRepayment -> {
                sendEvent(ClientLoanAccountsEvent.MakeRepayment(state.clientId))
            }

            ClientLoanAccountsAction.NavigateBack -> {
                // implement if needed, else remove
            }

            ClientLoanAccountsAction.OnSearchClick -> {

            }

            ClientLoanAccountsAction.Refresh -> {

            }

            ClientLoanAccountsAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(isFilterDialogOpen =! it.isFilterDialogOpen)
                }
            }

            ClientLoanAccountsAction.ToggleSearch -> TODO()
            is ClientLoanAccountsAction.UpdateSearchValue -> TODO()
            is ClientLoanAccountsAction.ViewAccount -> TODO()
            ClientLoanAccountsEvent.NavigateBack -> TODO()
        }
    }

    init {
        getLoanAccounts()
    }

    fun getLoanAccounts () {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = ClientLoanAccountsState.DialogState.Loading)
            }

            try {
                // Todo modify search accordingly
                // currently only supporting searching by account no
                val loanAccounts = repository.getClientAccounts(3)
                    .loanAccounts
                    .filter { it.accountNo?.contains(state.searchText.trim()) == true }

                mutableStateFlow.update {
                    it.copy(
                        loanAccounts = loanAccounts,
                        dialogState = null,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientLoanAccountsState.DialogState.Error(
                            e.message ?: "Unknown error",
                        ),
                    )
                }
            }
        }

    }

}

data class ClientLoanAccountsState(
    val clientId: Int = -1,
    val isSearchBarActive: Boolean = false,
    val searchText: String = "",
    val loanAccounts: List<LoanAccountEntity> = emptyList(),
    val isFilterDialogOpen: Boolean = false,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed interface ClientLoanAccountsEvent {
    data object NavigateBack : ClientLoanAccountsEvent
    data class MakeRepayment(val id: Int) : ClientLoanAccountsEvent
    data class ViewAccount(val id: Int) : ClientLoanAccountsEvent
}

sealed interface ClientLoanAccountsAction {
    data object ToggleSearch : ClientLoanAccountsAction
    data object NavigateBack : ClientLoanAccountsAction
    data object ToggleFilter : ClientLoanAccountsAction
    data object Refresh : ClientLoanAccountsAction
    data class MakeRepayment(val accountId: Int) : ClientLoanAccountsAction
    data class ViewAccount(val accountId: Int) : ClientLoanAccountsAction
    data class UpdateSearchValue(val query: String) : ClientLoanAccountsAction
    data object OnSearchClick : ClientLoanAccountsAction
    data object CloseDialog : ClientLoanAccountsAction
}

