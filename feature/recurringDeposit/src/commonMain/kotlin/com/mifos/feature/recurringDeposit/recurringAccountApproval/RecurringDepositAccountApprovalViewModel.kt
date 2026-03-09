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
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.ApproveRecurringDepositUseCase
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApproval
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.recurringDeposit.navigation.RecurringDepositAccountApprovalRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecurringDepositAccountApprovalViewModel(
    savedStateHandle: SavedStateHandle,
    private val approveRecurringDepositUseCase: ApproveRecurringDepositUseCase,
) : BaseViewModel<
    RecurringDepositAccountApprovalUiState,
    Unit,
    RecurringDepositAccountApprovalAction,
    >(
    initialState = RecurringDepositAccountApprovalUiState.Initial,
) {

    private val route = savedStateHandle.toRoute<RecurringDepositAccountApprovalRoute>()

    fun approveRecurringDepositApplication(recurringDepositApproval: RecurringDepositApproval) {
        trySendAction(RecurringDepositAccountApprovalAction.Approve(recurringDepositApproval))
    }

    override fun handleAction(action: RecurringDepositAccountApprovalAction) {
        when (action) {
            is RecurringDepositAccountApprovalAction.Approve -> approveRecurringDeposit(action.approval)
        }
    }

    private fun approveRecurringDeposit(recurringDepositApproval: RecurringDepositApproval) {
        mutableStateFlow.update { RecurringDepositAccountApprovalUiState.ShowProgressbar }
        viewModelScope.launch {
            approveRecurringDepositUseCase.invoke(
                accountId = route.accountId,
                approval = recurringDepositApproval,
            ).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            RecurringDepositAccountApprovalUiState.ShowError(result.message)
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            RecurringDepositAccountApprovalUiState.ShowProgressbar
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            RecurringDepositAccountApprovalUiState
                                .ShowRecurringDepositAccountApprovedSuccessfully
                        }
                    }
                }
            }
        }
    }
}

sealed interface RecurringDepositAccountApprovalAction {
    data class Approve(val approval: RecurringDepositApproval) :
        RecurringDepositAccountApprovalAction
}
