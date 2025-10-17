package com.mifos.feature.client.newFixedDepositAccount

import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update


class NewFixedDepositAccountViewmodel:
    BaseViewModel<NewFixedDepositAccountState,NewFixedDepositAccountEvent,
        NewFixedDepositAccountAction>(NewFixedDepositAccountState()){
    override fun handleAction(action: NewFixedDepositAccountAction) {
        when(action){
         NewFixedDepositAccountAction.NextStep ->
             mutableStateFlow.update { state ->
                 val maxIndex = 4
                 state.copy(currentStep = (state.currentStep + 1).coerceAtMost(maxIndex))

             }
            is NewFixedDepositAccountAction.OnStepChange -> {
                mutableStateFlow.update { it.copy(currentStep = action.index) }
            }
            is NewFixedDepositAccountAction.NavigateBack -> {
                sendEvent(NewFixedDepositAccountEvent.NavigateBack)
            }
            NewFixedDepositAccountAction.Finish -> {
                sendEvent(NewFixedDepositAccountEvent.Finish)
            }



        }
    }
        }



data class NewFixedDepositAccountState(
    val currentStep : Int = 0,
    val dialogState: Any? = null
)
sealed class NewFixedDepositAccountAction(){
    object NextStep : NewFixedDepositAccountAction()
    data class OnStepChange(val index: Int) : NewFixedDepositAccountAction()
    object NavigateBack : NewFixedDepositAccountAction()
    object Finish : NewFixedDepositAccountAction()

}
sealed class NewFixedDepositAccountEvent(){
    object NavigateBack : NewFixedDepositAccountEvent()
    object Finish : NewFixedDepositAccountEvent()
}