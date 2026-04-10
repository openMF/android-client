/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.createLoanReschedules

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_must_select_reason
import androidclient.feature.loan.generated.resources.feature_loan_reschedule_ok
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanReschedulesRepository
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleRequest
import com.mifos.core.model.objects.account.loan.reschedules.RescheduleReasonOption
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock

class LoanRescheduleFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanReschedulesRepository,
) : BaseViewModel<LoanRescheduleFormUiState, LoanRescheduleFormEvent, LoanRescheduleFormAction>(
    initialState = LoanRescheduleFormUiState(),
) {
    private val route = savedStateHandle.toRoute<RescheduleScreenRoute>()

    init {
        fetchRescheduleTemplate()
    }

    override fun handleAction(action: LoanRescheduleFormAction) {
        when (action) {
            is LoanRescheduleFormAction.ToggleChangeRepaymentDate -> mutableStateFlow.update {
                it.copy(
                    changeRepaymentDateSelected = action.isSelected,
                    adjustedDueDate = if (!action.isSelected) "" else it.adjustedDueDate,
                )
            }
            is LoanRescheduleFormAction.ToggleIntroduceGracePeriods -> mutableStateFlow.update {
                it.copy(
                    introduceGracePeriodsSelected = action.isSelected,
                    graceOnPrincipal = if (!action.isSelected) "" else it.graceOnPrincipal,
                    graceOnInterest = if (!action.isSelected) "" else it.graceOnInterest,
                )
            }
            is LoanRescheduleFormAction.ToggleExtendRepaymentPeriod -> mutableStateFlow.update {
                it.copy(
                    extendRepaymentPeriodSelected = action.isSelected,
                    extraTerms = if (!action.isSelected) "" else it.extraTerms,
                )
            }
            is LoanRescheduleFormAction.ToggleAdjustInterestRate -> mutableStateFlow.update {
                it.copy(
                    adjustInterestRateSelected = action.isSelected,
                    newInterestRate = if (!action.isSelected) "" else it.newInterestRate,
                )
            }
            is LoanRescheduleFormAction.ToggleWaivePenalties -> mutableStateFlow.update {
                it.copy(waivePenaltiesSelected = action.isSelected)
            }
            is LoanRescheduleFormAction.OnRescheduleReasonChange -> mutableStateFlow.update {
                it.copy(
                    selectedReasonId = action.id,
                    selectedReasonName = action.name,
                    reasonIdError = null,
                )
            }
            is LoanRescheduleFormAction.OnRescheduleFromDateChange -> mutableStateFlow.update {
                it.copy(rescheduleFromDate = action.date)
            }
            is LoanRescheduleFormAction.OnSubmittedOnDateChange -> mutableStateFlow.update {
                it.copy(submittedOnDate = action.date)
            }
            is LoanRescheduleFormAction.OnCommentsChange -> mutableStateFlow.update {
                it.copy(comments = action.comments)
            }
            is LoanRescheduleFormAction.OnAdjustedDueDateChange -> mutableStateFlow.update {
                it.copy(adjustedDueDate = action.date)
            }
            is LoanRescheduleFormAction.OnGraceOnPrincipalChange -> mutableStateFlow.update {
                it.copy(graceOnPrincipal = action.value)
            }
            is LoanRescheduleFormAction.OnGraceOnInterestChange -> mutableStateFlow.update {
                it.copy(graceOnInterest = action.value)
            }
            is LoanRescheduleFormAction.OnExtraTermsChange -> mutableStateFlow.update {
                it.copy(extraTerms = action.value)
            }
            is LoanRescheduleFormAction.OnNewInterestRateChange -> mutableStateFlow.update {
                it.copy(newInterestRate = action.rate)
            }
            is LoanRescheduleFormAction.ShowDatePicker -> mutableStateFlow.update {
                it.copy(activeDatePicker = action.picker)
            }
            LoanRescheduleFormAction.HideDatePicker -> mutableStateFlow.update {
                it.copy(activeDatePicker = ActiveDatePicker.NONE)
            }
            LoanRescheduleFormAction.OnSubmitClicked -> validateAndSubmit()
            LoanRescheduleFormAction.OnRetrySubmitClick -> validateAndSubmit()
            LoanRescheduleFormAction.CloseDialog -> mutableStateFlow.update {
                it.copy(dialogState = null)
            }
        }
    }

    private fun fetchRescheduleTemplate() {
        viewModelScope.launch {
            repository.getLoanRescheduleTemplate().collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> mutableStateFlow.update {
                        it.copy(dialogState = LoanRescheduleFormUiState.DialogState.Loading)
                    }
                    is DataState.Success -> mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                            reasons = dataState.data.rescheduleReasons.sortedBy { r -> r.name },
                        )
                    }
                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(
                            dialogState = LoanRescheduleFormUiState.DialogState.Error(
                                errorMessage = dataState.message,
                                confirmBtnRes = Res.string.feature_loan_reschedule_ok,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun validateAndSubmit() {
        if (state.selectedReasonId == null) {
            mutableStateFlow.update {
                it.copy(reasonIdError = Res.string.feature_loan_must_select_reason)
            }
            return
        }
        if (!state.isSubmitEnabled) return
        mutableStateFlow.update { it.copy(reasonIdError = null) }
        submitReschedule()
    }

    private fun submitReschedule() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = LoanRescheduleFormUiState.DialogState.Loading)
            }

            val request = LoanRescheduleRequest(
                loanId = route.loanId,
                rescheduleFromDate = state.rescheduleFromDate,
                rescheduleReasonId = state.selectedReasonId!!,
                submittedOnDate = state.submittedOnDate,
                dateFormat = ApiDateFormatter.DATE_FORMAT,
                locale = ApiDateFormatter.LOCALE,
                rescheduleReasonComment = state.comments,
                adjustedDueDate = if (state.changeRepaymentDateSelected) state.adjustedDueDate else "",
                graceOnPrincipal = if (state.introduceGracePeriodsSelected) state.graceOnPrincipal else "",
                graceOnInterest = if (state.introduceGracePeriodsSelected) state.graceOnInterest else "",
                extraTerms = if (state.extendRepaymentPeriodSelected) state.extraTerms else "",
                newInterestRate = if (state.adjustInterestRateSelected) state.newInterestRate else "",
                waivePenalties = if (state.waivePenaltiesSelected) true else null,
            )

            when (val result = repository.submitLoanReschedule(request)) {
                is DataState.Success -> {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    sendEvent(LoanRescheduleFormEvent.NavigateBack)
                }
                is DataState.Error -> mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRescheduleFormUiState.DialogState.Error(
                            errorMessage = result.message,
                            confirmBtnRes = Res.string.feature_loan_reschedule_ok,
                        ),
                    )
                }
                DataState.Loading -> Unit
            }
        }
    }
}

enum class ActiveDatePicker {
    NONE,
    RESCHEDULE_FROM,
    SUBMITTED_ON,
    ADJUSTED_DUE_DATE,
}

data class LoanRescheduleFormUiState(
    val dialogState: DialogState? = null,
    val activeDatePicker: ActiveDatePicker = ActiveDatePicker.NONE,
    val reasonIdError: StringResource? = null,

    val selectedReasonId: Int? = null,
    val selectedReasonName: String = "",
    val rescheduleFromDate: String = ApiDateFormatter.formatForApi(Clock.System.now().toEpochMilliseconds()),
    val submittedOnDate: String = ApiDateFormatter.formatForApi(Clock.System.now().toEpochMilliseconds()),
    val comments: String = "",
    val changeRepaymentDateSelected: Boolean = false,
    val introduceGracePeriodsSelected: Boolean = false,
    val extendRepaymentPeriodSelected: Boolean = false,
    val adjustInterestRateSelected: Boolean = false,
    val waivePenaltiesSelected: Boolean = false,
    val adjustedDueDate: String = "",
    val graceOnPrincipal: String = "",
    val graceOnInterest: String = "",
    val extraTerms: String = "",
    val newInterestRate: String = "",
    val reasons: List<RescheduleReasonOption> = emptyList(),
) {
    val isSubmitEnabled: Boolean
        get() {
            if (selectedReasonId == null) return false
            if (rescheduleFromDate.isBlank()) return false
            if (submittedOnDate.isBlank()) return false
            if (changeRepaymentDateSelected && adjustedDueDate.isBlank()) return false
            if (introduceGracePeriodsSelected &&
                graceOnPrincipal.isBlank() &&
                graceOnInterest.isBlank()
            ) {
                return false
            }
            if (extendRepaymentPeriodSelected && extraTerms.isBlank()) return false
            if (adjustInterestRateSelected && newInterestRate.isBlank()) return false
            return true
        }

    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(
            val errorMessage: String = "",
            val confirmBtnRes: StringResource? = null,
        ) : DialogState
    }
}

sealed interface LoanRescheduleFormAction {
    data class ToggleChangeRepaymentDate(val isSelected: Boolean) : LoanRescheduleFormAction
    data class ToggleIntroduceGracePeriods(val isSelected: Boolean) : LoanRescheduleFormAction
    data class ToggleExtendRepaymentPeriod(val isSelected: Boolean) : LoanRescheduleFormAction
    data class ToggleAdjustInterestRate(val isSelected: Boolean) : LoanRescheduleFormAction
    data class ToggleWaivePenalties(val isSelected: Boolean) : LoanRescheduleFormAction
    data class OnRescheduleReasonChange(val id: Int, val name: String) : LoanRescheduleFormAction
    data class OnRescheduleFromDateChange(val date: String) : LoanRescheduleFormAction
    data class OnSubmittedOnDateChange(val date: String) : LoanRescheduleFormAction
    data class OnCommentsChange(val comments: String) : LoanRescheduleFormAction
    data class OnAdjustedDueDateChange(val date: String) : LoanRescheduleFormAction
    data class OnGraceOnPrincipalChange(val value: String) : LoanRescheduleFormAction
    data class OnGraceOnInterestChange(val value: String) : LoanRescheduleFormAction
    data class OnExtraTermsChange(val value: String) : LoanRescheduleFormAction
    data class OnNewInterestRateChange(val rate: String) : LoanRescheduleFormAction
    data class ShowDatePicker(val picker: ActiveDatePicker) : LoanRescheduleFormAction
    data object HideDatePicker : LoanRescheduleFormAction
    data object OnSubmitClicked : LoanRescheduleFormAction
    data object OnRetrySubmitClick : LoanRescheduleFormAction
    data object CloseDialog : LoanRescheduleFormAction
}

sealed interface LoanRescheduleFormEvent {
    data object NavigateBack : LoanRescheduleFormEvent
}
