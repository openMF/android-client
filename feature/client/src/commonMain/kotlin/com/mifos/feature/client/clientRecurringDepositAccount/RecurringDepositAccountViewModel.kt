package com.mifos.feature.client.clientRecurringDepositAccount

import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientGeneral.ClientProfileGeneralState


class RecurringDepositAccountViewModel(

) : BaseViewModel<RecurringDepositAccountState,
    RecurringDepositAccountEvent,
    RecurringDepositAccountActions>(
        initialState = RecurringDepositAccountState()
    ){

    override fun handleAction(action: RecurringDepositAccountActions) {
        TODO("Not yet implemented")
    }

}


data class RecurringDepositAccountState(
    val recurringDepositAccounts: List<Int> = emptyList(),
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
){
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed class RecurringDepositAccountActions {
    data object NavigateBack : RecurringDepositAccountActions()
    data class ViewAccount(val accountId: Int) : RecurringDepositAccountActions()
    data class ApproveAccount(val accountId: Int): RecurringDepositAccountEvent()
    data object Refresh : RecurringDepositAccountActions()
    data object ToggleFilter : RecurringDepositAccountActions()
    data object ToggleSearch : RecurringDepositAccountActions()
    data object Search : RecurringDepositAccountActions()
    data class UpdateSearch(val query: String) : RecurringDepositAccountActions()
    data object CloseDialog: RecurringDepositAccountActions()
}

sealed class RecurringDepositAccountEvent {
    data object onNavigateBack : RecurringDepositAccountEvent()
    data class onViewAccount(val accountId: Int) : RecurringDepositAccountEvent()
    data object onApproveAccount : RecurringDepositAccountEvent()
}