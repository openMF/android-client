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
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.model.objects.organisations.LoanProducts
import com.mifos.core.network.model.CollateralItem
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState.DialogState
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.StringResource

internal class NewLoanAccountViewModel(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val repo: ClientDetailsRepository,
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

            is NewLoanAccountAction.PreviousStep -> moveToPreviousStep()

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

            is NewLoanAccountAction.OnFirstRepaymentDateChange -> handleFirstRepaymentDateChange(action)

            is NewLoanAccountAction.OnFirstRepaymentDatePick -> handleFirstRepaymentDatePick(action)

            is NewLoanAccountAction.OnInterestChargedFromChange -> handleInterestChargedFromChange(action)

            is NewLoanAccountAction.OnInterestChargedFromDatePick -> handleInterestChargedFromDatePick(action)

            is NewLoanAccountAction.OnNoOfRepaymentsChange -> handleNoOfRepaymentsChange(action)

            is NewLoanAccountAction.OnPrincipalAmountChange -> handlePrincipalAmountChange(action)

            is NewLoanAccountAction.OnTermFrequencyIndexChange -> handleTermFrequencyIndexChange(action)

            is NewLoanAccountAction.OnRepaidEveryChange -> handleRepaidEveryChange(action)

            is NewLoanAccountAction.OnSelectedDayIndexChange -> handleSelectedDayIndexChange(action)

            is NewLoanAccountAction.OnSelectedOnIndexChange -> handleSelectedOnIndexChange(action)

            is NewLoanAccountAction.OnNominalInterestRateChange -> handleNominalInterestRateChange(action)

            is NewLoanAccountAction.OnNominalFrequencyIndexChange -> handleNominalFrequencyIndexChange(action)

            is NewLoanAccountAction.OnNominalMethodIndexChange -> handleNominalMethodIndexChange(action)

            is NewLoanAccountAction.OnNominalAmortizationIndexChange -> handleNominalAmortizationIndexChange(action)

            is NewLoanAccountAction.OnEqualAmortizationCheckChange -> handleEqualAmortizationCheckChange(action)

            is NewLoanAccountAction.OnRepaymentStrategyIndexChange -> handleRepaymentStrategyIndexChange(action)

            is NewLoanAccountAction.OnBalloonRepaymentAmountChange -> handleBalloonRepaymentAmountChange(action)

            is NewLoanAccountAction.OnInterestCalculationPeriodIndexChange -> handleInterestCalculationPeriodIndexChange(action)

            is NewLoanAccountAction.OnInterestPartialPeriodCheckChange -> handleInterestPartialPeriodCheckChange(action)

            is NewLoanAccountAction.OnArrearsToleranceChange -> handleArrearsToleranceChange(action)

            is NewLoanAccountAction.OnInterestFreePeriodChange -> handleInterestFreePeriodChange(action)

            is NewLoanAccountAction.OnMoratoriumGraceOnInterestPaymentChange -> handleMoratoriumGraceOnInterestPaymentChange(action)

            is NewLoanAccountAction.OnMoratoriumGraceOnPrincipalPaymentChange -> handleMoratoriumGraceOnPrincipalPaymentChange(action)

            is NewLoanAccountAction.OnMoratoriumOnArrearsAgeingChange -> handleMoratoriumOnArrearsAgeingChange(action)

            is NewLoanAccountAction.DismissDialog -> handleDismissAddCollateralDialog()

            is NewLoanAccountAction.ShowAddCollateralDialog -> handleShowAddCollateralDialog()

            is NewLoanAccountAction.AddCollateralToList -> handleAddCollateralToList()

            is NewLoanAccountAction.OnCollateralQuantityChanged -> handleCollateralQuantityChanged(action)

            is NewLoanAccountAction.SelectedCollateralIndexChange -> handleSelectedCollateralIndexChange(action)

            is NewLoanAccountAction.HideCollaterals -> handleHideCollaterals()

            is NewLoanAccountAction.ShowCollaterals -> handleShowCollaterals()

            is NewLoanAccountAction.AddChargeToList -> handleAddChargeToList()

            is NewLoanAccountAction.OnChooseChargeIndexChange -> handleChooseChargeIndexChange(action)

            is NewLoanAccountAction.ShowAddChargeDialog -> handleShowAddChargeDialog()

            is NewLoanAccountAction.ShowCharges -> handleShowChargesDialog()

            is NewLoanAccountAction.ShowOverDueCharges -> handleShowOverDueChargesDialog()

            is NewLoanAccountAction.OnChargesDatePick -> handleChargesDatePick(action)

            is NewLoanAccountAction.OnChargesDateChange -> handleChargesDateChange(action)

            is NewLoanAccountAction.OnChargesAmountChange -> handleChargesAmountChange(action)

            is NewLoanAccountAction.DeleteChargeFromSelectedCharges -> handleDeleteCharge(action.index)

            is NewLoanAccountAction.EditChargeDialog -> handleEditChargeDialog(action.index)

            is NewLoanAccountAction.EditCharge -> handleEditCharge(action.index)
        }
    }

    private fun handleChooseChargeIndexChange(action: NewLoanAccountAction.OnChooseChargeIndexChange) {
        mutableStateFlow.update {
            it.copy(chooseChargeIndex = action.index)
        }
    }

    private fun handleShowAddChargeDialog() {
        mutableStateFlow.update {
            it.copy(dialogState = DialogState.AddNewCharge(false))
        }
    }

    private fun handleShowChargesDialog() {
        mutableStateFlow.update {
            it.copy(dialogState = DialogState.ShowCharges)
        }
    }

    private fun handleShowOverDueChargesDialog() {
        mutableStateFlow.update {
            it.copy(dialogState = DialogState.ShowOverDueCharges)
        }
    }

    private fun handleChargesDatePick(action: NewLoanAccountAction.OnChargesDatePick) {
        mutableStateFlow.update {
            it.copy(showChargesDatePick = action.state)
        }
    }

    private fun handleChargesDateChange(action: NewLoanAccountAction.OnChargesDateChange) {
        mutableStateFlow.update {
            it.copy(chargeDate = action.date)
        }
    }

    private fun handleChargesAmountChange(action: NewLoanAccountAction.OnChargesAmountChange) {
        mutableStateFlow.update {
            it.copy(chargeAmount = action.amount)
        }
    }

    private fun handleDeleteCharge(index: Int) {
        val newCharges = state.addedCharges.toMutableList().apply {
            removeAt(index)
        }
        mutableStateFlow.update {
            it.copy(addedCharges = newCharges)
        }
    }

    private fun handleEditChargeDialog(index: Int) {
        val selectedEditCharge = state.addedCharges[index]
        val chooseChargeIndex = state.loanTemplate
            ?.chargeOptions
            ?.indexOfFirst { it.id == selectedEditCharge.id } ?: -1

        mutableStateFlow.update {
            it.copy(
                chargeAmount = selectedEditCharge.amount.toString(),
                chargeDate = selectedEditCharge.date,
                chooseChargeIndex = chooseChargeIndex,
                dialogState = DialogState.AddNewCharge(true, index),
            )
        }
    }

    private fun handleEditCharge(index: Int) {
        val selectedIndex = state.chooseChargeIndex
        val selectedCharge = state.loanTemplate?.chargeOptions?.getOrNull(selectedIndex)
        val amount = state.chargeAmount.toDoubleOrNull() ?: selectedCharge?.amount ?: 0.0
        if (selectedCharge != null) {
            val newCharge = CreatedCharges(
                id = selectedCharge.id,
                name = selectedCharge.name,
                amount = amount,
                date = state.chargeDate,
                type = selectedCharge.chargeCalculationType?.value ?: "",
                collectedOn = selectedCharge.chargeTimeType?.value ?: "",
            )
            val currentAddedCharges = state.addedCharges.toMutableList()
            currentAddedCharges[index] = newCharge
            mutableStateFlow.update {
                it.copy(
                    addedCharges = currentAddedCharges,
                    chooseChargeIndex = -1,
                    dialogState = DialogState.ShowCharges,
                    chargeAmount = "",
                )
            }
        }
    }

    private fun handleFirstRepaymentDateChange(action: NewLoanAccountAction.OnFirstRepaymentDateChange) {
        mutableStateFlow.update {
            it.copy(firstRepaymentDate = action.date)
        }
    }

    private fun handleFirstRepaymentDatePick(action: NewLoanAccountAction.OnFirstRepaymentDatePick) {
        mutableStateFlow.update {
            it.copy(showFirstRepaymentDatePick = action.state)
        }
    }

    private fun handleRepaidEveryChange(action: NewLoanAccountAction.OnRepaidEveryChange) {
        mutableStateFlow.update { it.copy(repaidEvery = action.number) }
    }

    private fun handleSelectedDayIndexChange(action: NewLoanAccountAction.OnSelectedDayIndexChange) {
        mutableStateFlow.update { it.copy(selectedDayIndex = action.index) }
    }

    private fun handleSelectedOnIndexChange(action: NewLoanAccountAction.OnSelectedOnIndexChange) {
        mutableStateFlow.update { it.copy(selectedOnIndex = action.index) }
    }

    private fun handleNominalInterestRateChange(action: NewLoanAccountAction.OnNominalInterestRateChange) {
        mutableStateFlow.update {
            it.copy(
                nominalInterestRate = action.rate,
                nominalInterestRateText = action.text,
            )
        }
    }

    private fun handleNominalFrequencyIndexChange(action: NewLoanAccountAction.OnNominalFrequencyIndexChange) {
        mutableStateFlow.update { it.copy(nominalFrequencyIndex = action.index) }
    }

    private fun handleNominalMethodIndexChange(action: NewLoanAccountAction.OnNominalMethodIndexChange) {
        mutableStateFlow.update { it.copy(nominalInterestMethodIndex = action.index) }
    }

    private fun handleNominalAmortizationIndexChange(action: NewLoanAccountAction.OnNominalAmortizationIndexChange) {
        mutableStateFlow.update { it.copy(nominalAmortizationIndex = action.index) }
    }

    private fun handleEqualAmortizationCheckChange(action: NewLoanAccountAction.OnEqualAmortizationCheckChange) {
        mutableStateFlow.update { it.copy(isCheckedEqualAmortization = action.boolean) }
    }

    private fun handleRepaymentStrategyIndexChange(action: NewLoanAccountAction.OnRepaymentStrategyIndexChange) {
        mutableStateFlow.update { it.copy(repaymentStrategyIndex = action.index) }
    }

    private fun handleBalloonRepaymentAmountChange(action: NewLoanAccountAction.OnBalloonRepaymentAmountChange) {
        mutableStateFlow.update { it.copy(balloonRepaymentAmount = action.amount) }
    }

    private fun handleInterestCalculationPeriodIndexChange(action: NewLoanAccountAction.OnInterestCalculationPeriodIndexChange) {
        mutableStateFlow.update { it.copy(interestCalculationPeriodIndex = action.index) }
    }

    private fun handleInterestPartialPeriodCheckChange(action: NewLoanAccountAction.OnInterestPartialPeriodCheckChange) {
        mutableStateFlow.update { it.copy(isCheckedInterestPartialPeriod = action.boolean) }
    }

    private fun handleArrearsToleranceChange(action: NewLoanAccountAction.OnArrearsToleranceChange) {
        mutableStateFlow.update { it.copy(arrearsTolerance = action.number) }
    }

    private fun handleInterestFreePeriodChange(action: NewLoanAccountAction.OnInterestFreePeriodChange) {
        mutableStateFlow.update { it.copy(interestFreePeriod = action.number) }
    }

    private fun handleMoratoriumGraceOnInterestPaymentChange(action: NewLoanAccountAction.OnMoratoriumGraceOnInterestPaymentChange) {
        mutableStateFlow.update { it.copy(moratoriumGraceOnInterestPayment = action.number) }
    }

    private fun handleMoratoriumGraceOnPrincipalPaymentChange(action: NewLoanAccountAction.OnMoratoriumGraceOnPrincipalPaymentChange) {
        mutableStateFlow.update { it.copy(moratoriumGraceOnPrincipalPayment = action.number) }
    }

    private fun handleMoratoriumOnArrearsAgeingChange(action: NewLoanAccountAction.OnMoratoriumOnArrearsAgeingChange) {
        mutableStateFlow.update { it.copy(moratoriumOnArrearsAgeing = action.number) }
    }

    private fun handleDismissAddCollateralDialog() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }

    private fun handleShowAddCollateralDialog() {
        mutableStateFlow.update { it.copy(dialogState = NewLoanAccountState.DialogState.AddNewCollateral) }
    }

    private fun handleAddCollateralToList() {
        val selectedIndex = state.collateralSelectedIndex
        val selectedCollateral = state.collaterals.getOrNull(selectedIndex)

        if (selectedCollateral != null) {
            val newCollateral = CreatedCollateral(
                id = selectedCollateral.id,
                quantity = state.collateralQuantity,
                totalValue = state.collateralTotal,
                totalCollateral = state.totalCollateral,
                name = selectedCollateral.name,
            )

            mutableStateFlow.update {
                it.copy(
                    addedCollaterals = it.addedCollaterals + newCollateral,
                    collateralQuantity = 0,
                    collateralSelectedIndex = -1,
                    totalCollateral = 0.0,
                    collateralTotal = 0.0,
                    dialogState = null,
                )
            }
        } else {
            mutableStateFlow.update {
                it.copy(
                    collateralQuantity = 0,
                    collateralSelectedIndex = -1,
                    totalCollateral = 0.0,
                    collateralTotal = 0.0,
                    dialogState = null,
                )
            }
        }
    }

    private fun handleAddChargeToList() {
        val selectedIndex = state.chooseChargeIndex
        val selectedCharge = state.loanTemplate?.chargeOptions?.getOrNull(selectedIndex)
        val amount = state.chargeAmount.toDoubleOrNull() ?: selectedCharge?.amount ?: 0.0
        if (selectedCharge != null) {
            val newCharge = CreatedCharges(
                id = selectedCharge.id,
                name = selectedCharge.name,
                amount = amount,
                date = state.chargeDate,
                type = selectedCharge.chargeCalculationType?.value ?: "",
                collectedOn = selectedCharge.chargeTimeType?.value ?: "",
            )

            mutableStateFlow.update {
                it.copy(
                    addedCharges = it.addedCharges + newCharge,
                    chooseChargeIndex = -1,
                    dialogState = null,
                    chargeAmount = "",
                )
            }
        } else {
            mutableStateFlow.update {
                it.copy(
                    chooseChargeIndex = -1,
                    dialogState = null,
                    chargeAmount = "",
                )
            }
        }
    }

    private fun handleCollateralQuantityChanged(action: NewLoanAccountAction.OnCollateralQuantityChanged) {
        val currentCollateral = state.collaterals[state.collateralSelectedIndex]
        val total = currentCollateral.basePrice * action.number
        val totalCollateral = (total * currentCollateral.pctToBase) / 100

        mutableStateFlow.update {
            it.copy(
                collateralQuantity = action.number,
                collateralTotal = total,
                totalCollateral = totalCollateral,
            )
        }
    }

    private fun handleSelectedCollateralIndexChange(action: NewLoanAccountAction.SelectedCollateralIndexChange) {
        mutableStateFlow.update { it.copy(collateralSelectedIndex = action.index) }
    }

    private fun handleHideCollaterals() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }

    private fun handleShowCollaterals() {
        mutableStateFlow.update { it.copy(dialogState = NewLoanAccountState.DialogState.ShowCollaterals) }
    }

    private fun handleInterestChargedFromChange(action: NewLoanAccountAction.OnInterestChargedFromChange) {
        mutableStateFlow.update {
            it.copy(interestChargedFromDate = action.date)
        }
    }

    private fun handleInterestChargedFromDatePick(action: NewLoanAccountAction.OnInterestChargedFromDatePick) {
        mutableStateFlow.update {
            it.copy(showInterestChargedFromDatePick = action.state)
        }
    }

    private fun handleNoOfRepaymentsChange(action: NewLoanAccountAction.OnNoOfRepaymentsChange) {
        mutableStateFlow.update {
            it.copy(noOfRepayments = action.number)
        }
    }

    private fun handlePrincipalAmountChange(action: NewLoanAccountAction.OnPrincipalAmountChange) {
        mutableStateFlow.update {
            it.copy(
                principalAmount = action.amount,
                principalAmountText = action.text,
            )
        }
    }

    private fun handleTermFrequencyIndexChange(action: NewLoanAccountAction.OnTermFrequencyIndexChange) {
        mutableStateFlow.update {
            it.copy(termFrequencyIndex = action.index)
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
                if (isConnected) {
                    loadAllLoans()
                    loadCollaterals()
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

    private suspend fun loadCollaterals() {
        val result = repo.getCollateralItems()
        when (result) {
            is DataState.Error -> {}
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        collaterals = result.data,
                    )
                }
            }
            else -> Unit
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
                    principalAmount = result.data.principal ?: 0.0,
                    principalAmountText = result.data.principal.toString(),
                    noOfRepayments = result.data.numberOfRepayments ?: 0,
                    repaidEvery = result.data.repaymentEvery ?: 0,
                    nominalInterestRate = result.data.interestRatePerPeriod ?: 0.0,
                    nominalInterestRateText = result.data.interestRatePerPeriod.toString(),
                    termFrequencyIndex = result.data.termFrequencyTypeOptions.indexOfFirst { item -> item.value == result.data.termPeriodFrequencyType?.value },
                    nominalFrequencyIndex = result.data.interestRateFrequencyTypeOptions.indexOfFirst { item -> item.value == result.data.interestRateFrequencyType?.value },
                    nominalInterestMethodIndex = result.data.interestTypeOptions.indexOfFirst { item -> item.value == result.data.interestType?.value },
                    nominalAmortizationIndex = result.data.amortizationTypeOptions.indexOfFirst { item -> item.value == result.data.amortizationType?.value },
                    repaymentStrategyIndex = result.data.transactionProcessingStrategyOptions.indexOfFirst { item -> item.code == result.data.transactionProcessingStrategyCode },
                    interestCalculationPeriodIndex = result.data.interestCalculationPeriodTypeOptions.indexOfFirst { item -> item.code == result.data.interestCalculationPeriodType?.code },
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

    val principalAmount: Double = 0.0,
    val principalAmountText: String = "",
    val noOfRepayments: Int = 0,
    val termFrequencyIndex: Int = -1,
    val firstRepaymentDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val interestChargedFromDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showFirstRepaymentDatePick: Boolean = false,
    val showInterestChargedFromDatePick: Boolean = false,
    val repaidEvery: Int = 1,
    val selectedOnIndex: Int = -1,
    val selectedDayIndex: Int = -1,
    val nominalInterestRate: Double = 0.0,
    val nominalInterestRateText: String = "",
    val nominalFrequencyIndex: Int = -1,
    val nominalInterestMethodIndex: Int = -1,
    val nominalAmortizationIndex: Int = -1,
    val isCheckedEqualAmortization: Boolean = false,
    val repaymentStrategyIndex: Int = -1,
    val balloonRepaymentAmount: Int = 0,
    val interestCalculationPeriodIndex: Int = -1,
    val isCheckedInterestPartialPeriod: Boolean = false,
    val arrearsTolerance: Int = 0,
    val interestFreePeriod: Int = 0,
    val moratoriumGraceOnPrincipalPayment: Int = 0,
    val moratoriumGraceOnInterestPayment: Int = 0,
    val moratoriumOnArrearsAgeing: Int = 0,

    val collaterals: List<CollateralItem> = emptyList(),
    val addedCollaterals: List<CreatedCollateral> = emptyList(),
    val collateralSelectedIndex: Int = -1,
    val collateralQuantity: Int = 0,
    val collateralTotal: Double = 0.0,
    val totalCollateral: Double = 0.0,

    val chooseChargeIndex: Int = -1,
    val addedCharges: List<CreatedCharges> = emptyList(),
    val chargeDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showChargesDatePick: Boolean = false,
    val chargeAmount: String = "",

) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object AddNewCollateral : DialogState
        data class AddNewCharge(val edit: Boolean, val index: Int = -1) : DialogState
        data object ShowCollaterals : DialogState
        data object ShowCharges : DialogState
        data object ShowOverDueCharges : DialogState
    }
    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data object NetworkError : ScreenState
    }
    val isDetailsNextEnabled = loanProductSelected != -1 && externalId.isNotEmpty() && submissionDate.isNotEmpty() && expectedDisbursementDate.isNotEmpty()
    val isCollateralBtnEnabled = collateralQuantity != 0 && collateralSelectedIndex != -1
}

sealed interface NewLoanAccountEvent {
    data object NavigateBack : NewLoanAccountEvent
    data object Finish : NewLoanAccountEvent
}

sealed interface NewLoanAccountAction {
    data object Retry : NewLoanAccountAction
    data object NavigateBack : NewLoanAccountAction
    data object PreviousStep : NewLoanAccountAction
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

    data class OnPrincipalAmountChange(val amount: Double, val text: String) : NewLoanAccountAction
    data class OnNoOfRepaymentsChange(val number: Int) : NewLoanAccountAction
    data class OnTermFrequencyIndexChange(val index: Int) : NewLoanAccountAction
    data class OnFirstRepaymentDateChange(val date: String) : NewLoanAccountAction
    data class OnInterestChargedFromChange(val date: String) : NewLoanAccountAction
    data class OnFirstRepaymentDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnInterestChargedFromDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnRepaidEveryChange(val number: Int) : NewLoanAccountAction
    data class OnSelectedOnIndexChange(val index: Int) : NewLoanAccountAction
    data class OnSelectedDayIndexChange(val index: Int) : NewLoanAccountAction
    data class OnNominalInterestRateChange(val rate: Double, val text: String) : NewLoanAccountAction
    data class OnNominalFrequencyIndexChange(val index: Int) : NewLoanAccountAction
    data class OnNominalMethodIndexChange(val index: Int) : NewLoanAccountAction
    data class OnNominalAmortizationIndexChange(val index: Int) : NewLoanAccountAction
    data class OnEqualAmortizationCheckChange(val boolean: Boolean) : NewLoanAccountAction
    data class OnRepaymentStrategyIndexChange(val index: Int) : NewLoanAccountAction
    data class OnBalloonRepaymentAmountChange(val amount: Int) : NewLoanAccountAction
    data class OnInterestCalculationPeriodIndexChange(val index: Int) : NewLoanAccountAction
    data class OnInterestPartialPeriodCheckChange(val boolean: Boolean) : NewLoanAccountAction
    data class OnArrearsToleranceChange(val number: Int) : NewLoanAccountAction
    data class OnInterestFreePeriodChange(val number: Int) : NewLoanAccountAction
    data class OnMoratoriumGraceOnPrincipalPaymentChange(val number: Int) : NewLoanAccountAction
    data class OnMoratoriumGraceOnInterestPaymentChange(val number: Int) : NewLoanAccountAction
    data class OnMoratoriumOnArrearsAgeingChange(val number: Int) : NewLoanAccountAction
    data object ShowAddCollateralDialog : NewLoanAccountAction
    data object AddCollateralToList : NewLoanAccountAction
    data class SelectedCollateralIndexChange(val index: Int) : NewLoanAccountAction
    data class OnCollateralQuantityChanged(val number: Int) : NewLoanAccountAction
    data object ShowCollaterals : NewLoanAccountAction
    data object HideCollaterals : NewLoanAccountAction

    data class OnChooseChargeIndexChange(val index: Int) : NewLoanAccountAction
    data object ShowAddChargeDialog : NewLoanAccountAction
    data object DismissDialog : NewLoanAccountAction
    data object ShowCharges : NewLoanAccountAction
    data object ShowOverDueCharges : NewLoanAccountAction
    data class OnChargesDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnChargesDateChange(val date: String) : NewLoanAccountAction
    data class OnChargesAmountChange(val amount: String) : NewLoanAccountAction
    data object AddChargeToList : NewLoanAccountAction
    data class DeleteChargeFromSelectedCharges(val index: Int) : NewLoanAccountAction
    data class EditChargeDialog(val index: Int) : NewLoanAccountAction
    data class EditCharge(val index: Int) : NewLoanAccountAction
}

data class CreatedCollateral(
    val id: Int = -1,
    val quantity: Int = 0,
    val name: String = "",
    val totalValue: Double = 0.0,
    val totalCollateral: Double = 0.0,
)

data class CreatedCharges(
    val id: Int? = -1,
    val name: String?,
    val date: String,
    val type: String?,
    val amount: Double? = 0.0,
    val collectedOn: String = "",
)
