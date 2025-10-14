package com.mifos.feature.client.NewFixedDepositAccount

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlin.time.ExperimentalTime

internal class NewFixedDepositAccountViewModel (
    val savedStateHandle: SavedStateHandle,
):
    BaseViewModel<FixedDepositAccountState, FixedDepositAccountEvent, FixedDepositAccountAction>( initialState = run {
        FixedDepositAccountState(clientId = savedStateHandle.toRoute<FixedDepositAccountRoute>().clientId)

    }){
    override fun handleAction(action: FixedDepositAccountAction) {
        when(action){
            FixedDepositAccountAction.Finish -> sendEvent(FixedDepositAccountEvent.Finish)
            FixedDepositAccountAction.NavigateBack -> sendEvent(FixedDepositAccountEvent.NavigateBack)
            FixedDepositAccountAction.NextStep -> moveToNextStep()
            is FixedDepositAccountAction.OnStepChange -> handleStepChange(action)

        }
    }
    private fun handleStepChange(action: FixedDepositAccountAction.OnStepChange) {
        mutableStateFlow.update { it.copy(currentStep = action.newIndex) }
    }
    private fun moveToNextStep() {
        val current = state.currentStep
        if (current < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = current + 1,
                )
            }
        } else {
            sendEvent(FixedDepositAccountEvent.Finish)
        }
    }


}




data class FixedDepositAccountState
@OptIn(ExperimentalTime::class)
constructor(
    val clientId: Int,
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val isOverLayLoadingActive: Boolean = false,
    val screenState: ScreenState = ScreenState.Loading,
){

sealed interface DialogState {
    data class Error(val message: String) : DialogState
}

sealed interface ScreenState {
    data object Loading : ScreenState
    data object Success : ScreenState
    data object NetworkError : ScreenState
}
}
sealed interface FixedDepositAccountEvent {
    data object NavigateBack : FixedDepositAccountEvent
    data object Finish : FixedDepositAccountEvent
}
sealed interface FixedDepositAccountAction {
    data object NavigateBack : FixedDepositAccountAction
    data object NextStep : FixedDepositAccountAction
    data object Finish : FixedDepositAccountAction
    data class OnStepChange(val newIndex: Int) : FixedDepositAccountAction

}