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
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

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
        loadAllLoans()
    }

    override fun handleAction(action: NewLoanAccountAction) {
        when (action) {
            NewLoanAccountAction.Retry -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                        loanTemplate = null,
                    )
                }
                observeNetwork()
                loadAllLoans()
            }
            NewLoanAccountAction.NavigateBack -> sendEvent(NewLoanAccountEvent.NavigateBack)
            NewLoanAccountAction.NextStep -> moveToNextStep()
            NewLoanAccountAction.PreviousStep -> moveToPreviousStep()
            NewLoanAccountAction.Finish -> sendEvent(NewLoanAccountEvent.Finish)
            is NewLoanAccountAction.OnStepChange -> {
                mutableStateFlow.update {
                    it.copy(
                        currentStep = action.newIndex,
                    )
                }
            }

            is NewLoanAccountAction.OnProductNameChange -> {
                mutableStateFlow.update {
                    it.copy(
                        loanProductSelected = action.index,
                    )
                }
                loadLoanAccountTemplate(state.productLoans[action.index].id ?: -1)
            }

            is NewLoanAccountAction.OnExternalIdChange -> {
                mutableStateFlow.update {
                    it.copy(
                        externalId = action.value,
                    )
                }
            }

            is NewLoanAccountAction.OnFundChange -> {
                mutableStateFlow.update {
                    it.copy(
                        fundIndex = action.index,
                    )
                }
            }
            is NewLoanAccountAction.OnLoanOfficerChange -> {
                mutableStateFlow.update {
                    it.copy(
                        loanOfficerIndex = action.index,
                    )
                }
            }
            is NewLoanAccountAction.OnLoanPurposeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        loanPurposeIndex = action.index,
                    )
                }
            }

            is NewLoanAccountAction.OnExpectedDisbursementDateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        expectedDisbursementDate = action.date,
                    )
                }
            }
            is NewLoanAccountAction.OnExpectedDisbursementDatePick -> {
                mutableStateFlow.update {
                    it.copy(
                        showExpectedDisbursementDatePick = action.state,
                    )
                }
            }
            is NewLoanAccountAction.OnSubmissionDateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        submissionDate = action.date,
                    )
                }
            }
            is NewLoanAccountAction.OnSubmissionDatePick -> {
                mutableStateFlow.update {
                    it.copy(
                        showSubmissionDatePick = action.state,
                    )
                }
            }

            is NewLoanAccountAction.OnLinkSavingsChange -> {
                mutableStateFlow.update {
                    it.copy(
                        linkSavingsIndex = action.index,
                    )
                }
            }

            is NewLoanAccountAction.OnStandingInstructionsChange -> {
                mutableStateFlow.update {
                    it.copy(
                        isCheckedStandingInstructions = action.state,
                    )
                }
            }

            is NewLoanAccountAction.OnPrincipalAmountChange ->{
                mutableStateFlow.update {
                    it.copy(
                        principalAmount = action.amount,
                    )
                }
            }

            is NewLoanAccountAction.OnNoOfRepaymentsChange -> {
                mutableStateFlow.update {
                    it.copy(
                        noOfRepayments = action.number,
                    )
                }
            }

            is NewLoanAccountAction.OnTermFrequencyIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        termFrequencyIndex = action.index,
                    )
                }
            }

            is NewLoanAccountAction.OnFirstRepaymentDateChange ->{
                mutableStateFlow.update {
                    it.copy(
                        firstRepaymentDate = action.date,
                    )
                }
            }
            is NewLoanAccountAction.OnInterestChargedFromChange ->{
                mutableStateFlow.update {
                    it.copy(
                        interestChargedFromDate = action.date,
                    )
                }
            }

            is NewLoanAccountAction.OnFirstRepaymentDatePick -> {
                mutableStateFlow.update {
                    it.copy(
                        showFirstRepaymentDatePick = action.state,
                    )
                }
            }
            is NewLoanAccountAction.OnInterestChargedFromDatePick -> {
                mutableStateFlow.update {
                    it.copy(
                        showInterestChargedFromDatePick = action.state,
                    )
                }
            }
        }
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

    private fun moveToPreviousStep() {
        val current = state.currentStep
            mutableStateFlow.update {
                it.copy(
                    currentStep = current - 1,
                )
            }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update {
                    it.copy(networkConnection = isConnected)
                }
            }
        }
    }

    private fun loadAllLoans() = viewModelScope.launch {
        getAllLoanUseCase().collect { result ->
            when (result) {
                is DataState.Error -> mutableStateFlow.update {
                    it.copy(dialogState = NewLoanAccountState.DialogState.Error(result.message))
                }

                is DataState.Loading -> mutableStateFlow.update {
                    it.copy(dialogState = NewLoanAccountState.DialogState.Loading)
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(dialogState = null, productLoans = result.data)
                    }
                }
            }
        }
    }

    fun loadLoanAccountTemplate(productId: Int) =
        viewModelScope.launch {
            getLoansAccountTemplateUseCase(state.clientId, productId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        mutableStateFlow.update {
                            it.copy(dialogState = NewLoanAccountState.DialogState.Error(result.message))
                        }

                    is DataState.Loading -> Unit

                    is DataState.Success ->
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                                loanTemplate = result.data,
                            )
                        }
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
    val externalId: String = "",
    val loanOfficerIndex: Int = -1,
    val loanPurposeIndex: Int = -1,
    val fundIndex: Int = -1,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showSubmissionDatePick: Boolean = false,
    val expectedDisbursementDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showExpectedDisbursementDatePick: Boolean = false,
    val linkSavingsIndex: Int = -1,
    val isCheckedStandingInstructions: Boolean = false,

    val principalAmount:Int=0,
    val noOfRepayments:Int=0,
    val termFrequencyIndex: Int = -1,
    val firstRepaymentDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val interestChargedFromDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showFirstRepaymentDatePick: Boolean = false,
    val showInterestChargedFromDatePick: Boolean = false,

) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
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
    data object PreviousStep: NewLoanAccountAction
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

    data class OnPrincipalAmountChange(val amount: Int) : NewLoanAccountAction
    data class OnNoOfRepaymentsChange(val number: Int) : NewLoanAccountAction
    data class OnTermFrequencyIndexChange(val index: Int) : NewLoanAccountAction
    data class OnFirstRepaymentDateChange(val date: String) : NewLoanAccountAction
    data class OnInterestChargedFromChange(val date: String) : NewLoanAccountAction
    data class OnFirstRepaymentDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnInterestChargedFromDatePick(val state: Boolean) : NewLoanAccountAction
}
