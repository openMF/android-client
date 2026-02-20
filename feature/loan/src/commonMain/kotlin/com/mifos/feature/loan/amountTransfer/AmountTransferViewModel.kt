package com.mifos.feature.loan.amountTransfer

import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.loan.amountTransfer.AmountTransferUiState.DialogState
import com.mifos.feature.loan.loanAccountSummary.LoanAccountSummaryState

class AmountTransferViewModel(
) : BaseViewModel<AmountTransferUiState, AmountTransferEvent, AmountTransferAction>(
    initialState = AmountTransferUiState(
        dialogState = DialogState.Loading
    )
) {
    override fun handleAction(action: AmountTransferAction) {

    }
}

data class AmountTransferUiState(
    val isLoading: Boolean = false,
    val dialogState: DialogState? = null,

) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }

}

sealed interface AmountTransferAction {

}

sealed interface AmountTransferEvent {

}