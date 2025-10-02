package com.mifos.feature.client.createShareAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class ShareAccountViewModel : ViewModel() {
    private val _stateFlow = MutableStateFlow(ShareAccountState())
    val stateFlow = _stateFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ShareAccountEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun trySendAction(action: ShareAccountAction) {
        when (action) {
            ShareAccountAction.NextStep -> {
                _stateFlow.update { state ->
                    val maxIndex = 3 // total steps - 1
                    state.copy(currentStep = (state.currentStep + 1).coerceAtMost(maxIndex))
                }
            }
            is ShareAccountAction.OnStepChange -> {
                _stateFlow.update { it.copy(currentStep = action.index) }
            }
            ShareAccountAction.NavigateBack -> {
                viewModelScope.launch { _eventFlow.emit(ShareAccountEvent.NavigateBack) }
            }
            ShareAccountAction.Finish -> {
                viewModelScope.launch { _eventFlow.emit(ShareAccountEvent.Finish) }
            }
        }
    }
}
