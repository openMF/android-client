/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanForeclosure

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanForeclosureRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureInput
import com.mifos.core.model.objects.account.loan.foreclosure.LoanForeclosureTemplate
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Clock

internal class LoanForeclosureViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanForeclosureRepository,
    private val prefRepository: UserPreferencesRepository,
) : BaseViewModel<ForeclosureState, ForeclosureEvent, ForeclosureAction>(
    initialState = ForeclosureState(
        minTransactionDateMillis = LocalDate(2000, 1, 1)
            .atStartOfDayIn(TimeZone.currentSystemDefault())
            .toEpochMilliseconds(),
        maxTransactionDateMillis = Clock.System.now().toEpochMilliseconds(),
    ),
) {
    val loanId = savedStateHandle.toRoute<LoanForeclosureScreenRoute>().loanAccountNumber

    private var fetchTemplateJob: Job? = null

    init {
        fetchTemplate()
    }

    override fun handleAction(action: ForeclosureAction) {
        when (action) {
            ForeclosureAction.NavigateBack -> sendEvent(ForeclosureEvent.NavigateBack)

            ForeclosureAction.Retry -> fetchTemplate()

            ForeclosureAction.SubmitForeclosure -> submit()

            is ForeclosureAction.OnTransactionDatePick ->
                mutableStateFlow.update { it.copy(showTransactionDatePick = action.show) }

            is ForeclosureAction.OnTransactionDateSelected -> {
                mutableStateFlow.update {
                    it.copy(
                        showTransactionDatePick = false,
                        transactionDate = DateHelper.getDateAsStringFromLong(action.millis),
                    )
                }
                fetchTemplate()
            }

            is ForeclosureAction.OnNoteChange ->
                mutableStateFlow.update { it.copy(note = action.note, isNoteError = false) }

            is ForeclosureAction.Internal.ReceiveSubmitResult -> handleSubmitResult(action)

            is ForeclosureAction.Internal.ReceiveTemplateResult -> handleTemplateResult(action.result)
        }
    }

    private fun fetchTemplate() {
        fetchTemplateJob?.cancel()

        fetchTemplateJob = viewModelScope.launch {
            val locale = prefRepository.settingsInfo.first().language.localName ?: "en"

            val dataState = repository.getLoanForeclosureTemplate(
                loanId = loanId,
                transactionDate = state.transactionDate,
                dateFormat = DateHelper.SHORT_MONTH,
                locale = locale,
            )
            sendAction(ForeclosureAction.Internal.ReceiveTemplateResult(dataState))
        }
    }

    private fun submit() {
        if (state.note.isBlank()) {
            mutableStateFlow.update { it.copy(isNoteError = true) }
            return
        }

        mutableStateFlow.update { it.copy(isOverLayLoadingActive = true) }

        viewModelScope.launch {
            val locale = prefRepository.settingsInfo.first().language.localName ?: "en"
            val result = repository.submitLoanForeclosure(
                loanId = loanId,
                input = LoanForeclosureInput(
                    transactionDate = state.transactionDate,
                    note = state.note,
                    dateFormat = DateHelper.SHORT_MONTH,
                    locale = locale,
                ),
            )
            sendAction(ForeclosureAction.Internal.ReceiveSubmitResult(result))
        }
    }

    private fun handleTemplateResult(dataState: DataState<LoanForeclosureTemplate>) {
        when (dataState) {
            DataState.Loading -> mutableStateFlow.update {
                it.copy(screenState = ForeclosureState.ScreenState.Loading)
            }

            is DataState.Success -> {
                val template = dataState.data
                mutableStateFlow.update {
                    it.copy(
                        screenState = ForeclosureState.ScreenState.Success,
                        transactionAmount = (template.amount ?: 0.0).toString(),
                        principalPortion = (template.principalPortion ?: 0.0).toString(),
                        interestPortion = (template.interestPortion ?: 0.0).toString(),
                        feeChargesPortion = (template.feeChargesPortion ?: 0.0).toString(),
                        penaltyChargesPortion = (template.penaltyChargesPortion ?: 0.0).toString(),
                    )
                }
            }
            is DataState.Error -> {
                if (dataState.exception is IllegalStateException) {
                    mutableStateFlow.update {
                        it.copy(screenState = ForeclosureState.ScreenState.Success)
                    }
                    sendEvent(ForeclosureEvent.ShowSnackbar(dataState.message))
                } else {
                    mutableStateFlow.update {
                        it.copy(screenState = ForeclosureState.ScreenState.Error(dataState.message))
                    }
                }
            }
        }
    }

    private fun handleSubmitResult(action: ForeclosureAction.Internal.ReceiveSubmitResult) {
        mutableStateFlow.update { it.copy(isOverLayLoadingActive = false) }

        when (val result = action.result) {
            is DataState.Success -> sendEvent(ForeclosureEvent.Finish)

            is DataState.Error -> {
                if (result.exception is IllegalStateException) {
                    sendEvent(ForeclosureEvent.ShowSnackbar(result.message))
                } else {
                    mutableStateFlow.update {
                        it.copy(screenState = ForeclosureState.ScreenState.Error(result.message))
                    }
                }
            }

            else -> Unit
        }
    }
}

data class ForeclosureState(
    val minTransactionDateMillis: Long,
    val maxTransactionDateMillis: Long,

    val transactionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showTransactionDatePick: Boolean = false,

    val note: String = "",
    val isNoteError: Boolean = false,

    val principalPortion: String = "",
    val interestPortion: String = "",
    val feeChargesPortion: String = "",
    val penaltyChargesPortion: String = "",
    val transactionAmount: String = "",

    val screenState: ScreenState = ScreenState.Loading,
    val isOverLayLoadingActive: Boolean = false,
) {
    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data class Error(val message: String) : ScreenState
    }

    val isSubmitEnabled: Boolean =
        screenState is ScreenState.Success &&
            note.isNotBlank() &&
            !isOverLayLoadingActive
}

sealed interface ForeclosureEvent {
    data object NavigateBack : ForeclosureEvent
    data object Finish : ForeclosureEvent
    data class ShowSnackbar(val message: String) : ForeclosureEvent
}

sealed interface ForeclosureAction {
    data object NavigateBack : ForeclosureAction
    data object Retry : ForeclosureAction
    data object SubmitForeclosure : ForeclosureAction

    data class OnTransactionDatePick(val show: Boolean) : ForeclosureAction
    data class OnTransactionDateSelected(val millis: Long) : ForeclosureAction
    data class OnNoteChange(val note: String) : ForeclosureAction

    sealed interface Internal : ForeclosureAction {
        data class ReceiveSubmitResult(
            val result: DataState<Unit>,
        ) : Internal
        data class ReceiveTemplateResult(
            val result: DataState<LoanForeclosureTemplate>,
        ) : Internal
    }
}
