/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.assignLoanOfficer

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_assign_officer_failed_to_load
import kpt.feature.loan.generated.resources.feature_loan_assign_officer_failure
import kpt.feature.loan.generated.resources.feature_loan_assign_officer_success
import kpt.feature.loan.generated.resources.feature_loan_message_field_required
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.domain.useCases.assignLoanOfficer.AssignLoanOfficerUseCase
import com.mifos.core.domain.useCases.assignLoanOfficer.GetLoanOfficerOptionsUseCase
import com.mifos.core.model.objects.account.loan.assignLoanOfficer.AssignLoanOfficerInput
import com.mifos.core.model.objects.template.loan.LoanOfficerOption
import kpt.core.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock

internal class AssignLoanOfficerViewModel(
    savedStateHandle: SavedStateHandle,
    private val getLoanOfficerOptionsUseCase: GetLoanOfficerOptionsUseCase,
    private val assignLoanOfficerUseCase: AssignLoanOfficerUseCase,
) : BaseViewModel<AssignLoanOfficerState, AssignLoanOfficerEvent, AssignLoanOfficerAction>(
    initialState = AssignLoanOfficerState(
        loanId = savedStateHandle.toRoute<AssignLoanOfficerRoute>().loanId,
    ),
) {

    init {
        loadLoanOfficers()
    }

    private fun loadLoanOfficers() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(viewState = AssignLoanOfficerState.ViewState.Loading) }
            try {
                val result = getLoanOfficerOptionsUseCase(state.loanId)
                sendAction(AssignLoanOfficerAction.Internal.ReceiveOptionsResult(result))
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(viewState = AssignLoanOfficerState.ViewState.Error(Res.string.feature_loan_assign_officer_failed_to_load))
                }
            }
        }
    }

    private fun submitAssignment() {
        val currentState = mutableStateFlow.value
        val selectedOfficerId =
            currentState.loanOfficerOptions.getOrNull(currentState.selectedOfficerIndex)?.id

        val officerError =
            if (selectedOfficerId == null) Res.string.feature_loan_message_field_required else null

        if (officerError != null) {
            mutableStateFlow.update {
                it.copy(
                    officerError = officerError,
                )
            }
            return
        }
        selectedOfficerId?.let { officerId ->
            mutableStateFlow.update {
                it.copy(
                    officerError = null,
                    submitInProgress = true,
                )
            }

            val formattedDate =
                DateHelper.getDateAsStringFromLong(currentState.assignmentDateMillis)

            val request = AssignLoanOfficerInput(
                assignmentDate = formattedDate,
                toLoanOfficerId = officerId,
            )

            viewModelScope.launch {
                try {
                    assignLoanOfficerUseCase(state.loanId, request)
                    sendAction(AssignLoanOfficerAction.Internal.ReceiveSubmitResult)
                } catch (e: Exception) {
                    mutableStateFlow.update {
                        it.copy(
                            submitInProgress = false,
                            dialogMessage = Res.string.feature_loan_assign_officer_failure,
                        )
                    }
                }
            }
        }
    }

    private fun handleOptionsResult(result: List<LoanOfficerOption>) {
        mutableStateFlow.update {
            it.copy(
                viewState = AssignLoanOfficerState.ViewState.Success(result),
                loanOfficerOptions = result,
            )
        }
    }

    private fun handleSubmitResult() {
        mutableStateFlow.update {
            it.copy(
                submitInProgress = false,
                dialogMessage = Res.string.feature_loan_assign_officer_success,
                isAssignmentSuccessful = true,
            )
        }
    }

    private fun handleDismissDialog() {
        val currentState = mutableStateFlow.value
        mutableStateFlow.update { it.copy(dialogMessage = null) }

        if (currentState.isAssignmentSuccessful) {
            sendEvent(AssignLoanOfficerEvent.AssignLoanOfficerSuccess)
        }
    }

    override fun handleAction(action: AssignLoanOfficerAction) {
        when (action) {
            AssignLoanOfficerAction.Load,
            AssignLoanOfficerAction.Retry,
            -> loadLoanOfficers()

            AssignLoanOfficerAction.DismissDialog -> handleDismissDialog()
            AssignLoanOfficerAction.NavigateBack -> sendEvent(AssignLoanOfficerEvent.NavigateBack)

            is AssignLoanOfficerAction.SelectOfficer -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedOfficerIndex = action.index,
                        officerError = null,
                    )
                }
            }

            is AssignLoanOfficerAction.UpdateAssignmentDate -> {
                mutableStateFlow.update {
                    it.copy(
                        assignmentDateMillis = action.millis,
                        assignmentDateText = DateHelper.getDateAsStringFromLong(action.millis),
                    )
                }
            }

            AssignLoanOfficerAction.ShowDatePicker -> mutableStateFlow.update {
                it.copy(
                    showDatePicker = true,
                )
            }

            AssignLoanOfficerAction.HideDatePicker -> mutableStateFlow.update {
                it.copy(
                    showDatePicker = false,
                )
            }

            AssignLoanOfficerAction.Submit -> submitAssignment()

            is AssignLoanOfficerAction.Internal.ReceiveOptionsResult -> handleOptionsResult(action.result)
            AssignLoanOfficerAction.Internal.ReceiveSubmitResult -> handleSubmitResult()
        }
    }
}

private val defaultAssignmentDate = Clock.System.now().toEpochMilliseconds()

data class AssignLoanOfficerState(
    val loanId: Int,
    val viewState: ViewState = ViewState.Loading,
    val loanOfficerOptions: List<LoanOfficerOption> = emptyList(),
    val selectedOfficerIndex: Int = -1,
    val assignmentDateMillis: Long = defaultAssignmentDate,
    val assignmentDateText: String = DateHelper.getDateAsStringFromLong(defaultAssignmentDate),
    val showDatePicker: Boolean = false,
    val officerError: StringResource? = null,
    val submitInProgress: Boolean = false,
    val dialogMessage: StringResource? = null,
    val isAssignmentSuccessful: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Success(val officers: List<LoanOfficerOption>) : ViewState
        data class Error(val message: StringResource) : ViewState
    }

    val canSubmit = selectedOfficerIndex != -1
}

internal sealed interface AssignLoanOfficerEvent {
    data object NavigateBack : AssignLoanOfficerEvent
    data object AssignLoanOfficerSuccess : AssignLoanOfficerEvent
}

internal sealed interface AssignLoanOfficerAction {
    data object Load : AssignLoanOfficerAction
    data object Retry : AssignLoanOfficerAction
    data object NavigateBack : AssignLoanOfficerAction
    data object DismissDialog : AssignLoanOfficerAction
    data object ShowDatePicker : AssignLoanOfficerAction
    data object HideDatePicker : AssignLoanOfficerAction
    data object Submit : AssignLoanOfficerAction

    data class SelectOfficer(val index: Int) : AssignLoanOfficerAction
    data class UpdateAssignmentDate(val millis: Long) : AssignLoanOfficerAction

    sealed interface Internal : AssignLoanOfficerAction {
        data class ReceiveOptionsResult(val result: List<LoanOfficerOption>) : Internal
        data object ReceiveSubmitResult : Internal
    }
}
