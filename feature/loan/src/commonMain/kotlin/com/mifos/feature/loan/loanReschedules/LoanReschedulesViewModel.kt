/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReschedules

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_approve_failed
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_delete_failed
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_fetch_failed
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanReschedulesRepository
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleApprovalRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRejectionRequest
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.model.objects.account.loan.reschedules.RescheduleStatus
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock

class LoanReschedulesViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanReschedulesRepository,
) : BaseViewModel<LoanReschedulesUiState, LoanReschedulesEvent, LoanReschedulesAction>(
    initialState = LoanReschedulesUiState(),
) {
    private val route = savedStateHandle.toRoute<LoanReschedulesRoute>()
    val loanId: Int get() = route.loanId
    private var fetchHistoryJob: Job? = null

    init {
        fetchRescheduleHistory()
    }

    override fun handleAction(action: LoanReschedulesAction) {
        when (action) {
            LoanReschedulesAction.OnRetryFetching -> fetchRescheduleHistory()
            LoanReschedulesAction.RefreshList -> fetchRescheduleHistory()
            LoanReschedulesAction.DismissDialog -> mutableStateFlow.update {
                it.copy(dialogState = null)
            }

            is LoanReschedulesAction.OnDeleteIconClick -> mutableStateFlow.update {
                if (action.item.statusEnum.toRescheduleStatus() != RescheduleStatusCode.PENDING) return@update it
                it.copy(dialogState = LoanReschedulesUiState.DialogState.ConfirmDelete(action.item.id))
            }

            is LoanReschedulesAction.OnApproveIconClick -> mutableStateFlow.update {
                if (action.item.statusEnum.toRescheduleStatus() != RescheduleStatusCode.PENDING) return@update it
                it.copy(dialogState = LoanReschedulesUiState.DialogState.ConfirmApprove(action.item.id))
            }

            is LoanReschedulesAction.ConfirmDelete -> {
                val item = state.history.firstOrNull { it.id == action.rescheduleId }
                if (item?.statusEnum.toRescheduleStatus() == RescheduleStatusCode.PENDING) {
                    deleteReschedule(action.rescheduleId)
                }
            }

            is LoanReschedulesAction.ConfirmApprove -> {
                val item = state.history.firstOrNull { it.id == action.rescheduleId }
                if (item?.statusEnum.toRescheduleStatus() == RescheduleStatusCode.PENDING) {
                    approveReschedule(action.rescheduleId)
                }
            }
        }
    }

    private fun fetchRescheduleHistory() {
        fetchHistoryJob?.cancel()
        fetchHistoryJob = viewModelScope.launch {
            repository.getLoanReschedules(route.loanId).collectLatest { dataState ->
                when (dataState) {
                    is DataState.Loading -> mutableStateFlow.update {
                        it.copy(dialogState = LoanReschedulesUiState.DialogState.Loading)
                    }
                    is DataState.Success -> mutableStateFlow.update {
                        it.copy(dialogState = null, history = dataState.data)
                    }
                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(
                            dialogState = LoanReschedulesUiState.DialogState.FetchingFailed(
                                messageRes = Res.string.feature_loan_reschedule_fetch_failed,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun approveReschedule(rescheduleId: Int) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = LoanReschedulesUiState.DialogState.Loading)
            }

            val today = ApiDateFormatter.formatForApi(Clock.System.now().toEpochMilliseconds())
            val request = LoanRescheduleApprovalRequest(
                approvedOnDate = today,
                dateFormat = ApiDateFormatter.DATE_FORMAT,
                locale = ApiDateFormatter.LOCALE,
            )

            when (val result = repository.approveReschedule(rescheduleId, request)) {
                is DataState.Success -> {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    fetchRescheduleHistory()
                }
                is DataState.Error -> mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanReschedulesUiState.DialogState.ActionError(
                            messageRes = Res.string.feature_loan_reschedule_approve_failed,
                        ),
                    )
                }
                DataState.Loading -> Unit
            }
        }
    }

    private fun deleteReschedule(rescheduleId: Int) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = LoanReschedulesUiState.DialogState.Loading)
            }

            val today = ApiDateFormatter.formatForApi(Clock.System.now().toEpochMilliseconds())
            val request = LoanRescheduleRejectionRequest(
                rejectedOnDate = today,
                dateFormat = ApiDateFormatter.DATE_FORMAT,
                locale = ApiDateFormatter.LOCALE,
            )

            when (val result = repository.deleteReschedule(rescheduleId, request)) {
                is DataState.Success -> {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    fetchRescheduleHistory()
                }
                is DataState.Error -> mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanReschedulesUiState.DialogState.ActionError(
                            messageRes = Res.string.feature_loan_reschedule_delete_failed,
                        ),
                    )
                }
                DataState.Loading -> Unit
            }
        }
    }
}

data class LoanReschedulesUiState(
    val dialogState: DialogState? = null,
    val history: List<LoanRescheduleResponse> = emptyList(),
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class FetchingFailed(val messageRes: StringResource) : DialogState
        data class ConfirmDelete(val rescheduleId: Int) : DialogState
        data class ConfirmApprove(val rescheduleId: Int) : DialogState
        data class ActionError(val messageRes: StringResource) : DialogState
    }
}

sealed interface LoanReschedulesAction {
    data object OnRetryFetching : LoanReschedulesAction
    data object RefreshList : LoanReschedulesAction
    data object DismissDialog : LoanReschedulesAction
    data class OnDeleteIconClick(val item: LoanRescheduleResponse) : LoanReschedulesAction
    data class OnApproveIconClick(val item: LoanRescheduleResponse) : LoanReschedulesAction
    data class ConfirmDelete(val rescheduleId: Int) : LoanReschedulesAction
    data class ConfirmApprove(val rescheduleId: Int) : LoanReschedulesAction
}

sealed interface LoanReschedulesEvent {
    data object NavigateBack : LoanReschedulesEvent
}

enum class RescheduleStatusCode {
    PENDING,
    APPROVED,
    REJECTED,
    UNKNOWN,
}

fun RescheduleStatus?.toRescheduleStatus(): RescheduleStatusCode {
    return when {
        this?.pendingApproval == true -> RescheduleStatusCode.PENDING
        this?.approved == true -> RescheduleStatusCode.APPROVED
        this?.rejected == true -> RescheduleStatusCode.REJECTED
        else -> RescheduleStatusCode.UNKNOWN
    }
}
