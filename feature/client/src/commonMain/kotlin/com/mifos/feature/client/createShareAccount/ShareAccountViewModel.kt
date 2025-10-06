package com.mifos.feature.client.createShareAccount

import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update



class ShareAccountViewModel : BaseViewModel <ShareAccountState, ShareAccountEvent, ShareAccountAction> (ShareAccountState()) {

    override fun handleAction(action: ShareAccountAction) {
        when (action) {
            ShareAccountAction.NextStep -> {
                mutableStateFlow.update { state ->
                    val maxIndex = 3 // total steps - 1
                    state.copy(currentStep = (state.currentStep + 1).coerceAtMost(maxIndex))
                }
            }
            is ShareAccountAction.OnStepChange -> {
                mutableStateFlow.update { it.copy(currentStep = action.index) }
            }
            ShareAccountAction.NavigateBack -> {
               sendEvent(ShareAccountEvent.NavigateBack)
            }
            ShareAccountAction.Finish -> {
                sendEvent(ShareAccountEvent.Finish)
            }
        }
    }
}

data class ShareAccountState(
    val currentStep: Int = 0,
    val dialogState: Any? = null
)

sealed class ShareAccountAction {
    object NextStep : ShareAccountAction()
    data class OnStepChange(val index: Int) : ShareAccountAction()
    object NavigateBack : ShareAccountAction()
    object Finish : ShareAccountAction()
}

sealed class ShareAccountEvent {
    object NavigateBack : ShareAccountEvent()
    object Finish : ShareAccountEvent()
}