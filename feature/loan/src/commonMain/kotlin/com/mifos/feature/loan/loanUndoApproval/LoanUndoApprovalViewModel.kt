package com.mifos.feature.loan.loanUndoApproval

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.core.model.objects.account.loan.LoanUndoApprovalRequest
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoanUndoApprovalViewModel(
    private val repository: LoanAccountApprovalRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanUndoApprovalState, LoanUndoApprovalEvent, LoanUndoApprovalAction>(
    LoanUndoApprovalState(),
) {

    val route = savedStateHandle.toRoute<LoanUndoApprovalRoute>()

    override fun handleAction(action: LoanUndoApprovalAction) {
        when (action) {
            is LoanUndoApprovalAction.OnNoteChange -> {
                mutableStateFlow.update {
                    it.copy(
                        note = action.note,
                    )
                }
            }

            LoanUndoApprovalAction.OnRetry -> {
                undoLoanApproval()
            }

            LoanUndoApprovalAction.OnSubmit -> {
                undoLoanApproval()
            }

            LoanUndoApprovalAction.OnNavigateBack -> {
                sendEvent(LoanUndoApprovalEvent.NavigationBack)
            }
        }
    }

    fun undoLoanApproval() {
        mutableStateFlow.update {
            it.copy(
                dialogState = LoanUndoApprovalState.DialogState.Loading
            )
        }
        viewModelScope.launch {
            runCatching {
                repository.undoLoanApproval(route.loanId, LoanUndoApprovalRequest(note = state.note))
            }.onFailure { error ->
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanUndoApprovalState.DialogState.Error(error.message.toString()),
                    )
                }
            }.onSuccess {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null
                    )
                }
                sendEvent(LoanUndoApprovalEvent.NavigationBack)
            }
        }
    }
}

data class LoanUndoApprovalState(
    val note: String = "",
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        object Loading : DialogState
    }
}

sealed interface LoanUndoApprovalEvent {
    object NavigationBack : LoanUndoApprovalEvent
}

sealed interface LoanUndoApprovalAction {
    data class OnNoteChange(val note: String) : LoanUndoApprovalAction
    object OnSubmit : LoanUndoApprovalAction
    object OnRetry : LoanUndoApprovalAction
    object OnNavigateBack : LoanUndoApprovalAction
}