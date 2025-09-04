/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.model.objects.organisations.LoanProducts
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.StringResource

internal class NewLoanAccountViewModel(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val getLoansAccountTemplateUseCase: GetLoansAccountTemplateUseCase,
    private val networkMonitor: NetworkMonitor,
    val savedStateHandle: SavedStateHandle,
) :
    BaseViewModel<NewLoanAccountState, NewLoanAccountEvent, NewLoanAccountAction>(
        initialState = run {
            NewLoanAccountState(clientId = savedStateHandle.toRoute<NewLoanAccountRoute>().clientId)
        },
    ) {

    init {
        observeNetwork()
    }

    override fun handleAction(action: NewLoanAccountAction) {
        when (action) {
            is NewLoanAccountAction.Retry -> handleRetry()

            is NewLoanAccountAction.NavigateBack -> handleNavigateBack()

            is NewLoanAccountAction.NextStep -> moveToNextStep()

            is NewLoanAccountAction.Finish -> handleFinish()

            is NewLoanAccountAction.OnStepChange -> handleStepChange(action)

            is NewLoanAccountAction.OnProductNameChange -> handleProductNameChange(action)

            is NewLoanAccountAction.OnExternalIdChange -> handleExternalIdChange(action)

            is NewLoanAccountAction.OnFundChange -> handleFundChange(action)

            is NewLoanAccountAction.OnLoanOfficerChange -> handleLoanOfficerChange(action)

            is NewLoanAccountAction.OnLoanPurposeChange -> handleLoanPurposeChange(action)

            is NewLoanAccountAction.OnExpectedDisbursementDateChange -> handleExpectedDisbursementDateChange(action)

            is NewLoanAccountAction.OnExpectedDisbursementDatePick -> handleExpectedDisbursementDatePick(action)

            is NewLoanAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)

            is NewLoanAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)

            is NewLoanAccountAction.OnLinkSavingsChange -> handleLinkSavingsChange(action)

            is NewLoanAccountAction.OnStandingInstructionsChange -> handleStandingInstructionsChange(action)

            is NewLoanAccountAction.OnDetailsSubmit -> handleOnDetailsSubmit()

            is NewLoanAccountAction.Internal.OnReceivingLoanAccounts -> handleAllLoansResponse(action.loans)

            is NewLoanAccountAction.Internal.OnReceivingLoanTemplate -> handleLoanTemplateResponse(action.template)
        }
    }

    private fun handleRetry() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
                loanTemplate = null,
            )
        }
        observeNetwork()
    }

    private fun handleNavigateBack() {
        sendEvent(NewLoanAccountEvent.NavigateBack)
    }

    private fun handleOnDetailsSubmit() {
        mutableStateFlow.update {
            it.copy(externalIdError = null)
        }
        val externalIdError = TextFieldsValidator.stringValidator(state.externalId)
        if (externalIdError == null) {
            moveToNextStep()
        } else {
            mutableStateFlow.update {
                it.copy(externalIdError = externalIdError)
            }
        }
    }

    private fun handleFinish() {
        sendEvent(NewLoanAccountEvent.Finish)
    }

    private fun handleStepChange(action: NewLoanAccountAction.OnStepChange) {
        mutableStateFlow.update { it.copy(currentStep = action.newIndex) }
    }

    private fun handleProductNameChange(action: NewLoanAccountAction.OnProductNameChange) {
        mutableStateFlow.update { it.copy(loanProductSelected = action.index) }
        loadLoanAccountTemplate(state.productLoans[action.index].id ?: -1)
    }

    private fun handleExternalIdChange(action: NewLoanAccountAction.OnExternalIdChange) {
        mutableStateFlow.update { it.copy(externalId = action.value) }
    }

    private fun handleFundChange(action: NewLoanAccountAction.OnFundChange) {
        mutableStateFlow.update { it.copy(fundIndex = action.index) }
    }

    private fun handleLoanOfficerChange(action: NewLoanAccountAction.OnLoanOfficerChange) {
        mutableStateFlow.update { it.copy(loanOfficerIndex = action.index) }
    }

    private fun handleLoanPurposeChange(action: NewLoanAccountAction.OnLoanPurposeChange) {
        mutableStateFlow.update { it.copy(loanPurposeIndex = action.index) }
    }

    private fun handleExpectedDisbursementDateChange(action: NewLoanAccountAction.OnExpectedDisbursementDateChange) {
        mutableStateFlow.update { it.copy(expectedDisbursementDate = action.date) }
    }

    private fun handleExpectedDisbursementDatePick(action: NewLoanAccountAction.OnExpectedDisbursementDatePick) {
        mutableStateFlow.update { it.copy(showExpectedDisbursementDatePick = action.state) }
    }

    private fun handleSubmissionDateChange(action: NewLoanAccountAction.OnSubmissionDateChange) {
        mutableStateFlow.update { it.copy(submissionDate = action.date) }
    }

    private fun handleSubmissionDatePick(action: NewLoanAccountAction.OnSubmissionDatePick) {
        mutableStateFlow.update { it.copy(showSubmissionDatePick = action.state) }
    }

    private fun handleLinkSavingsChange(action: NewLoanAccountAction.OnLinkSavingsChange) {
        mutableStateFlow.update { it.copy(linkSavingsIndex = action.index) }
    }

    private fun handleStandingInstructionsChange(action: NewLoanAccountAction.OnStandingInstructionsChange) {
        mutableStateFlow.update { it.copy(isCheckedStandingInstructions = action.state) }
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
            sendEvent(NewLoanAccountEvent.Finish)
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update {
                    it.copy(networkConnection = isConnected)
                }
                if (isConnected) {
                    loadAllLoans()
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = NewLoanAccountState.ScreenState.NetworkError,
                        )
                    }
                }
            }
        }
    }

    private fun loadAllLoans() = viewModelScope.launch {
        getAllLoanUseCase().collect { result ->
            sendAction(NewLoanAccountAction.Internal.OnReceivingLoanAccounts(result))
        }
    }

    fun loadLoanAccountTemplate(productId: Int) = viewModelScope.launch {
        getLoansAccountTemplateUseCase(state.clientId, productId).collect { result ->
            sendAction(NewLoanAccountAction.Internal.OnReceivingLoanTemplate(result))
        }
    }

    private fun handleAllLoansResponse(result: DataState<List<LoanProducts>>) {
        when (result) {
            is DataState.Error -> mutableStateFlow.update {
                it.copy(dialogState = NewLoanAccountState.DialogState.Error(result.message))
            }

            is DataState.Loading -> mutableStateFlow.update {
                it.copy(screenState = NewLoanAccountState.ScreenState.Loading)
            }

            is DataState.Success -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                    screenState = NewLoanAccountState.ScreenState.Success,
                    productLoans = result.data,
                )
            }
        }
    }

    private fun handleLoanTemplateResponse(result: DataState<LoanTemplate>) {
        when (result) {
            is DataState.Error -> mutableStateFlow.update {
                it.copy(
                    dialogState = NewLoanAccountState.DialogState.Error(result.message),
                    isOverLayLoadingActive = false,
                )
            }

            is DataState.Loading -> mutableStateFlow.update {
                it.copy(isOverLayLoadingActive = true)
            }

            is DataState.Success -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                    isOverLayLoadingActive = false,
                    loanTemplate = result.data,
                )
            }
        }
    }
}

data class NewLoanAccountState(
    val networkConnection: Boolean = false,
    val clientId: Int,
    val productLoans: List<LoanProducts> = emptyList(),
    val loanProductSelected: Int = -1,
    val loanTemplate: LoanTemplate? = null,
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val dialogState: DialogState? = null,
    val screenState: ScreenState = ScreenState.Loading,
    val isOverLayLoadingActive: Boolean = false,
    val externalId: String = "",
    val externalIdError: StringResource? = null,
    val loanOfficerIndex: Int = -1,
    val loanPurposeIndex: Int = -1,
    val fundIndex: Int = -1,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showSubmissionDatePick: Boolean = false,
    val expectedDisbursementDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showExpectedDisbursementDatePick: Boolean = false,
    val linkSavingsIndex: Int = -1,
    val isCheckedStandingInstructions: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
    }
    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data object NetworkError : ScreenState
    }
    val isDetailsNextEnabled = loanProductSelected != -1 && externalId.isNotEmpty() && loanOfficerIndex != -1 && submissionDate.isNotEmpty() && expectedDisbursementDate.isNotEmpty()
}

sealed interface NewLoanAccountEvent {
    data object NavigateBack : NewLoanAccountEvent
    data object Finish : NewLoanAccountEvent
}

sealed interface NewLoanAccountAction {
    data object Retry : NewLoanAccountAction
    data object NavigateBack : NewLoanAccountAction
    data object NextStep : NewLoanAccountAction
    data object Finish : NewLoanAccountAction
    data class OnStepChange(val newIndex: Int) : NewLoanAccountAction
    data class OnProductNameChange(val index: Int) : NewLoanAccountAction
    data class OnExternalIdChange(val value: String) : NewLoanAccountAction
    data class OnLoanOfficerChange(val index: Int) : NewLoanAccountAction
    data class OnLoanPurposeChange(val index: Int) : NewLoanAccountAction
    data class OnFundChange(val index: Int) : NewLoanAccountAction
    data class OnSubmissionDateChange(val date: String) : NewLoanAccountAction
    data class OnExpectedDisbursementDateChange(val date: String) : NewLoanAccountAction
    data class OnSubmissionDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnExpectedDisbursementDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnLinkSavingsChange(val index: Int) : NewLoanAccountAction
    data class OnStandingInstructionsChange(val state: Boolean) : NewLoanAccountAction
    data object OnDetailsSubmit : NewLoanAccountAction

    sealed interface Internal : NewLoanAccountAction {
        data class OnReceivingLoanAccounts(val loans: DataState<List<LoanProducts>>) : Internal
        data class OnReceivingLoanTemplate(val template: DataState<LoanTemplate>) : Internal
    }
}
