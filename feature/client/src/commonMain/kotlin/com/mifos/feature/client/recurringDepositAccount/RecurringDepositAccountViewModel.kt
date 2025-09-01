package com.mifos.feature.client.recurringDepositAccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.recurringDepositAccount.RecurringDepositAccountEvent.*
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class RecurringDepositAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
) : BaseViewModel<RecurringDepositAccountState,
    RecurringDepositAccountEvent,
    RecurringDepositAccountAction>(
        initialState = RecurringDepositAccountState()
){

    val route = savedStateHandle.toRoute<RecurringDepositAccountRoute>()

    init {
        getRecurringDepositAccounts()
    }

    override fun handleAction(action: RecurringDepositAccountAction) {
        when(action){
            RecurringDepositAccountAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }
            is RecurringDepositAccountAction.NavigateBack -> {
                sendEvent(onNavigateBack)
            }
            is RecurringDepositAccountAction.Refresh -> {
                getRecurringDepositAccounts()
            }
            is RecurringDepositAccountAction.Search -> {
                getRecurringDepositAccounts()
            }
            is RecurringDepositAccountAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(
                        isFilterDialogOpen = true
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
                    onViewAccount(action.accountId)
                )
            }

            is RecurringDepositAccountAction.ApproveAccount -> {
                sendEvent(
                    onApproveAccount(action.accountId)
                )
            }
        }
    }

    private fun getRecurringDepositAccounts() {
        viewModelScope.launch {
            getClientDetailsUseCase.invoke(route.clientId).collect { result->
                when(result){
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = RecurringDepositAccountState.DialogState.Error(result.message))
                        }
                    }
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = RecurringDepositAccountState.DialogState.Loading)
                        }
                    }
                    is DataState.Success -> {
                        val recurringDepositAccount = result.data.clientAccounts?.savingsAccounts?.let {
                            it.filter {accountEntity ->
                                accountEntity.depositType?.serverType ==
                                    SavingAccountDepositTypeEntity.ServerTypes.RECURRING &&
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
                                recurringDepositAccounts = recurringDepositAccount
                            )
                        }
                    }
                }
            }
        }
    }

    private fun searchRecurringDepositAccounts(
        query: String,
        recurringDepositAccounts: List<SavingsAccountEntity>
    ): List<SavingsAccountEntity> {
        if(query.isNotBlank()) {
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
){
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed class RecurringDepositAccountAction {
    data object NavigateBack : RecurringDepositAccountAction()
    data class ViewAccount(val accountId: Int) : RecurringDepositAccountAction()
    data class ApproveAccount(val accountId: Int): RecurringDepositAccountAction()
    data object Refresh : RecurringDepositAccountAction()
    data object ToggleFilter : RecurringDepositAccountAction()
    data object ToggleSearch : RecurringDepositAccountAction()
    data class Search(val query: String) : RecurringDepositAccountAction()
    data class UpdateSearch(val query: String) : RecurringDepositAccountAction()
    data object CloseDialog: RecurringDepositAccountAction()
}

sealed class RecurringDepositAccountEvent {
    data object onNavigateBack : RecurringDepositAccountEvent()
    data class onViewAccount(val accountId: Int) : RecurringDepositAccountEvent()
    data class onApproveAccount(val accountId: Int) : RecurringDepositAccountEvent()
}