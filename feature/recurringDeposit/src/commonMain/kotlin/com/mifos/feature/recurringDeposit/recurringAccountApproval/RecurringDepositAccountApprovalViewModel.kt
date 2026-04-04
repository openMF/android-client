/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.recurringAccountApproval

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.domain.useCases.ApproveRecurringDepositUseCase
import com.mifos.core.model.objects.responses.RecurringDepositApprovalResponse
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApproval
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.recurringDeposit.navigation.RecurringDepositAccountApprovalRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class RecurringDepositAccountApprovalViewModel(
    savedStateHandle: SavedStateHandle,
    private val approveRecurringDepositUseCase: ApproveRecurringDepositUseCase,
) : BaseViewModel<
    RecurringDepositAccountApprovalState,
    RecurringDepositAccountApprovalEvent,
    RecurringDepositAccountApprovalAction,
    >(
    initialState = RecurringDepositAccountApprovalState(),
) {

    private val route = savedStateHandle.toRoute<RecurringDepositAccountApprovalRoute>()

    override fun handleAction(action: RecurringDepositAccountApprovalAction) {
        when (action) {
            is RecurringDepositAccountApprovalAction.Approve -> approveRecurringDeposit(action.approval)
            is RecurringDepositAccountApprovalAction.ApprovalDateChanged -> {
                updateState {
                    it.copy(approvalDate = action.approvalDate)
                }
            }
            RecurringDepositAccountApprovalAction.NavigateBack -> {
                sendEvent(RecurringDepositAccountApprovalEvent.NavigateBack)
            }
            is RecurringDepositAccountApprovalAction.ReasonForApprovalChanged -> {
                updateState {
                    it.copy(reasonForApproval = action.reason)
                }
            }
            is RecurringDepositAccountApprovalAction.Internal.ApprovalFailed -> {
                updateState {
                    it.copy(
                        isLoading = false,
                        approvalResponse = null,
                        errorMessage = action.message,
                    )
                }
            }
            is RecurringDepositAccountApprovalAction.Internal.ApprovalSucceeded -> {
                val status = action.response.changes?.status
                if (status?.approved == true) {
                    updateState {
                        it.copy(
                            isLoading = false,
                            approvalResponse = action.response,
                            errorMessage = null,
                        )
                    }
                } else {
                    updateState {
                        it.copy(
                            isLoading = false,
                            approvalResponse = null,
                            errorMessage = status?.value ?: status?.code,
                        )
                    }
                }
            }
        }
    }

    private fun updateState(
        update: (RecurringDepositAccountApprovalState) -> RecurringDepositAccountApprovalState,
    ) {
        mutableStateFlow.update(update)
    }

    private fun approveRecurringDeposit(recurringDepositApproval: RecurringDepositApproval) {
        updateState {
            it.copy(
                isLoading = true,
                approvalResponse = null,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            try {
                val response = approveRecurringDepositUseCase(
                    accountId = route.accountId,
                    approval = recurringDepositApproval,
                )
                sendAction(RecurringDepositAccountApprovalAction.Internal.ApprovalSucceeded(response))
            } catch (e: Exception) {
                sendAction(RecurringDepositAccountApprovalAction.Internal.ApprovalFailed(e.message))
            }
        }
    }
}

data class RecurringDepositAccountApprovalState(
    val isLoading: Boolean = false,
    val approvalDate: Long = Clock.System.now().toEpochMilliseconds(),
    val reasonForApproval: String = "",
    val approvalResponse: RecurringDepositApprovalResponse? = null,
    val errorMessage: String? = null,
)

sealed interface RecurringDepositAccountApprovalEvent {
    data object NavigateBack : RecurringDepositAccountApprovalEvent
}

sealed interface RecurringDepositAccountApprovalAction {
    data object NavigateBack : RecurringDepositAccountApprovalAction
    data class ApprovalDateChanged(val approvalDate: Long) : RecurringDepositAccountApprovalAction
    data class Approve(val approval: RecurringDepositApproval) :
        RecurringDepositAccountApprovalAction
    data class ReasonForApprovalChanged(val reason: String) : RecurringDepositAccountApprovalAction

    sealed interface Internal : RecurringDepositAccountApprovalAction {
        data class ApprovalSucceeded(val response: RecurringDepositApprovalResponse) : Internal
        data class ApprovalFailed(val message: String?) : Internal
    }
}
