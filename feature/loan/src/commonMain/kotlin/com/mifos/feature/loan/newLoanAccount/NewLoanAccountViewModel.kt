/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_account_created_successfully
import androidclient.feature.loan.generated.resources.feature_loan_account_number
import androidclient.feature.loan.generated.resources.feature_loan_disbursed_date
import androidclient.feature.loan.generated.resources.installment_paid
import androidclient.feature.loan.generated.resources.principal_paid_off
import androidclient.feature.loan.generated.resources.total_installments
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.domain.useCases.CalculateLoanScheduleUseCase
import com.mifos.core.domain.useCases.CreateLoanAccountUseCase
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.model.objects.organisations.LoanProducts
import com.mifos.core.network.model.CollateralItem
import com.mifos.core.network.model.LoansPayload
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState.DialogState
import com.mifos.feature.loan.newLoanAccount.pages.DatatableFieldValue
import com.mifos.room.entities.noncore.ColumnHeader
import com.mifos.room.entities.noncore.DataTablePayload
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class NewLoanAccountViewModel(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val repo: ClientDetailsRepository,
    private val getLoansAccountTemplateUseCase: GetLoansAccountTemplateUseCase,
    private val loanUseCase: CreateLoanAccountUseCase,
    private val calculateLoanScheduleUseCase: CalculateLoanScheduleUseCase,
    val savedStateHandle: SavedStateHandle,
) : BaseViewModel<NewLoanAccountState, NewLoanAccountEvent, NewLoanAccountAction>(
    initialState = run {
        val route = savedStateHandle.toRoute<NewLoanAccountRoute>()
        NewLoanAccountState(clientId = route.clientId, accountNo = route.accountNo)
    },
) {

    init {
        viewModelScope.launch {
            loadAllLoans()
            loadCollaterals()
        }
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

            is NewLoanAccountAction.OnExpectedDisbursementDateChange -> handleExpectedDisbursementDateChange(
                action,
            )

            is NewLoanAccountAction.OnExpectedDisbursementDatePick -> handleExpectedDisbursementDatePick(
                action,
            )

            is NewLoanAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)

            is NewLoanAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)

            is NewLoanAccountAction.OnLinkSavingsChange -> handleLinkSavingsChange(action)

            is NewLoanAccountAction.OnStandingInstructionsChange -> handleStandingInstructionsChange(
                action,
            )

            is NewLoanAccountAction.OnFirstRepaymentDateChange -> handleFirstRepaymentDateChange(
                action,
            )

            is NewLoanAccountAction.OnFirstRepaymentDatePick -> handleFirstRepaymentDatePick(action)

            is NewLoanAccountAction.OnInterestChargedFromChange -> handleInterestChargedFromChange(
                action,
            )

            is NewLoanAccountAction.OnInterestChargedFromDatePick -> handleInterestChargedFromDatePick(
                action,
            )

            is NewLoanAccountAction.OnNoOfRepaymentsChange -> handleNoOfRepaymentsChange(action)

            is NewLoanAccountAction.OnPrincipalAmountChange -> handlePrincipalAmountChange(action)

            is NewLoanAccountAction.OnTermFrequencyIndexChange -> handleTermFrequencyIndexChange(
                action,
            )

            is NewLoanAccountAction.OnRepaidEveryChange -> handleRepaidEveryChange(action)

            is NewLoanAccountAction.OnSelectedDayIndexChange -> handleSelectedDayIndexChange(action)

            is NewLoanAccountAction.OnSelectedOnIndexChange -> handleSelectedOnIndexChange(action)

            is NewLoanAccountAction.OnNominalInterestRateChange -> handleNominalInterestRateChange(
                action,
            )

            is NewLoanAccountAction.OnNominalFrequencyIndexChange -> handleNominalFrequencyIndexChange(
                action,
            )

            is NewLoanAccountAction.OnNominalMethodIndexChange -> handleNominalMethodIndexChange(
                action,
            )

            is NewLoanAccountAction.OnNominalAmortizationIndexChange -> handleNominalAmortizationIndexChange(
                action,
            )

            is NewLoanAccountAction.OnEqualAmortizationCheckChange -> handleEqualAmortizationCheckChange(
                action,
            )

            is NewLoanAccountAction.OnRepaymentStrategyIndexChange -> handleRepaymentStrategyIndexChange(
                action,
            )

            is NewLoanAccountAction.OnBalloonRepaymentAmountChange -> handleBalloonRepaymentAmountChange(
                action,
            )

            is NewLoanAccountAction.OnInterestCalculationPeriodIndexChange -> handleInterestCalculationPeriodIndexChange(
                action,
            )

            is NewLoanAccountAction.OnInterestPartialPeriodCheckChange -> handleInterestPartialPeriodCheckChange(
                action,
            )

            is NewLoanAccountAction.OnArrearsToleranceChange -> handleArrearsToleranceChange(action)

            is NewLoanAccountAction.OnInterestFreePeriodChange -> handleInterestFreePeriodChange(
                action,
            )

            is NewLoanAccountAction.OnMoratoriumGraceOnInterestPaymentChange -> handleMoratoriumGraceOnInterestPaymentChange(
                action,
            )

            is NewLoanAccountAction.OnMoratoriumGraceOnPrincipalPaymentChange -> handleMoratoriumGraceOnPrincipalPaymentChange(
                action,
            )

            is NewLoanAccountAction.OnMoratoriumOnArrearsAgeingChange -> handleMoratoriumOnArrearsAgeingChange(
                action,
            )

            is NewLoanAccountAction.DismissDialog -> handleDismissAddCollateralDialog()

            is NewLoanAccountAction.ShowAddCollateralDialog -> handleShowAddCollateralDialog()

            is NewLoanAccountAction.AddCollateralToList -> handleAddCollateralToList()

            is NewLoanAccountAction.OnCollateralQuantityChanged -> handleCollateralQuantityChanged(
                action,
            )

            is NewLoanAccountAction.SelectedCollateralIndexChange -> handleSelectedCollateralIndexChange(
                action,
            )

            is NewLoanAccountAction.HideCollaterals -> handleHideCollaterals()

            is NewLoanAccountAction.ShowCollaterals -> handleShowCollaterals()

            is NewLoanAccountAction.AddChargeToList -> handleAddChargeToList()

            is NewLoanAccountAction.OnChooseChargeIndexChange -> handleChooseChargeIndexChange(
                action,
            )

            is NewLoanAccountAction.ShowAddChargeDialog -> handleShowAddChargeDialog()

            is NewLoanAccountAction.ShowCharges -> handleShowChargesDialog()

            is NewLoanAccountAction.ShowOverDueCharges -> handleShowOverDueChargesDialog()

            is NewLoanAccountAction.OnChargesDatePick -> handleChargesDatePick(action)

            is NewLoanAccountAction.OnChargesDateChange -> handleChargesDateChange(action)

            is NewLoanAccountAction.OnChargesAmountChange -> handleChargesAmountChange(action)

            is NewLoanAccountAction.DeleteChargeFromSelectedCharges -> handleDeleteCharge(action.index)

            is NewLoanAccountAction.EditChargeDialog -> handleEditChargeDialog(action.index)

            is NewLoanAccountAction.EditCharge -> handleEditCharge(action.index)

            is NewLoanAccountAction.RepaymentScheduler -> {
                moveToNextStep()
                if (state.noOfRepaymentsPreviousState != state.noOfRepayments) {
                    viewModelScope.launch {
                        repaymentScheduler()
                    }

                    mutableStateFlow.update {
                        it.copy(
                            noOfRepaymentsPreviousState = state.noOfRepayments,
                        )
                    }
                }
            }

            NewLoanAccountAction.SubmitLoanApplication -> submitLoanApplication()

            // GAP-DT-013: inline datatable form input — merge into state.datatableValues
            is NewLoanAccountAction.UpdateDatatableField -> {
                mutableStateFlow.update { current ->
                    val tableMap = current.datatableValues[action.tableIndex].orEmpty().toMutableMap()
                    tableMap[action.columnName] = action.value
                    current.copy(
                        datatableValues = current.datatableValues + (action.tableIndex to tableMap),
                    )
                }
            }
        }
    }

    private fun submitLoanApplication() {
        viewModelScope.launch {
            val payload = LoansPayload(
                loanOfficerId =
                if (state.loanOfficerIndex == -1) {
                    null
                } else {
                    state.loanTemplate?.loanOfficerOptions?.getOrNull(state.loanOfficerIndex)?.id
                },
                principal = state.principalAmount.toDouble(),
                clientId = state.clientId,
                allowPartialPeriodInterestCalculation = state.isCheckedInterestPartialPeriod,
                amortizationType = state.loanTemplate?.amortizationTypeOptions?.getOrNull(state.nominalAmortizationIndex)?.id,
                dateFormat = DateHelper.SHORT_MONTH,
                interestCalculationPeriodType = state.loanTemplate
                    ?.interestCalculationPeriodTypeOptions?.getOrNull(state.interestCalculationPeriodIndex)?.id,
                interestRatePerPeriod = state.nominalInterestRate.toDouble(),
                interestType = state.loanTemplate
                    ?.interestTypeOptions?.getOrNull(state.nominalInterestMethodIndex)?.id,
                loanTermFrequency = state.noOfRepayments * state.repaidEvery,
                loanTermFrequencyType = state.loanTemplate?.termFrequencyTypeOptions?.getOrNull(state.termFrequencyIndex)?.id,
                loanType = "individual",
                locale = "en",
                numberOfRepayments = state.noOfRepayments,
                productId = state.productId,
                repaymentEvery = state.repaidEvery,
                repaymentFrequencyDayOfWeekType =
                if (state.selectedDayIndex == -1) {
                    null
                } else {
                    state.loanTemplate?.repaymentFrequencyDaysOfWeekTypeOptions?.getOrNull(state.selectedDayIndex)?.id
                },
                repaymentFrequencyNthDayType =
                if (state.selectedOnIndex == -1) {
                    null
                } else {
                    state.loanTemplate?.repaymentFrequencyNthDayTypeOptions?.getOrNull(state.selectedOnIndex)?.id
                },
                repaymentFrequencyType = state.loanTemplate?.termFrequencyTypeOptions?.getOrNull(state.termFrequencyIndex)?.id,
                expectedDisbursementDate = state.expectedDisbursementDate,
                submittedOnDate = state.submissionDate,
                loanPurposeId =
                if (state.loanPurposeIndex == -1) {
                    null
                } else {
                    state.loanTemplate?.loanPurposeOptions?.getOrNull(state.loanPurposeIndex)?.id
                },
                fundId =
                if (state.fundIndex == -1) {
                    null
                } else {
                    state.loanTemplate?.fundOptions?.getOrNull(state.fundIndex)?.id
                },
                linkAccountId =
                if (state.linkSavingsIndex == -1) {
                    null
                } else {
                    state.loanTemplate?.accountLinkingOptions?.getOrNull(state.linkSavingsIndex)?.id
                },
                transactionProcessingStrategyCode = state.loanTemplate
                    ?.transactionProcessingStrategyOptions?.getOrNull(state.repaymentStrategyIndex)?.code,
                externalId = state.externalId,
            )
            val realDataTables = state.loanTemplate?.dataTables?.filterNotNull().orEmpty()

            Logger.d(
                tag = "DataTableGate",
                messageString =
                "submit: productId=${state.productId} " +
                    "rawSize=${state.loanTemplate?.dataTables?.size ?: 0} " +
                    "realSize=${realDataTables.size} " + "names=${realDataTables.map { it.registeredTableName }} " +
                    "valuesFilled=${state.datatableValues.values.sumOf { it.size }}",
            )

            if (realDataTables.isNotEmpty()) {
                val datatablePayloads = realDataTables.mapIndexed { idx, table ->
                    DataTablePayload(
                        registeredTableName = table.registeredTableName,
                        data = buildDatatablePayloadMap(
                            headers = table.columnHeaderData,
                            rawValues = state.datatableValues[idx].orEmpty(),
                        ),
                    )
                }
                payload.dataTables = ArrayList(datatablePayloads)
            }

            loanUseCase(payload).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        if (dataState.exception is IllegalStateException) {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = NewLoanAccountState.DialogState.SuccessResponseStatus(
                                        successStatus = false,
                                        msg = dataState.message,
                                    ),
                                    launchEffectKey = Random.nextInt(),
                                    isOverLayLoadingActive = false,
                                )
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = NewLoanAccountState.ScreenState.Error(dataState.message),
                                    isOverLayLoadingActive = false,
                                )
                            }
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                isOverLayLoadingActive = true,
                            )
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                isOverLayLoadingActive = false,
                                launchEffectKey = Random.nextInt(),
                                dialogState = NewLoanAccountState.DialogState.SuccessResponseStatus(
                                    successStatus = true,
                                    msg = getString(Res.string.feature_loan_account_created_successfully),
                                ),
                            )
                        }
                        delay(1000)
                        sendEvent(NewLoanAccountEvent.LoanCreationSuccess(state.clientId))
                    }
                }
            }
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
        val chooseChargeIndex =
            state.loanTemplate?.chargeOptions?.indexOfFirst { it.id == selectedEditCharge.id } ?: -1

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
        mutableStateFlow.update {
            it.copy(
                selectedDayIndex = action.index,
            )
        }
    }

    private fun handleSelectedOnIndexChange(action: NewLoanAccountAction.OnSelectedOnIndexChange) {
        mutableStateFlow.update {
            it.copy(
                selectedOnIndex = action.index,
            )
        }
    }

    private fun handleNominalInterestRateChange(action: NewLoanAccountAction.OnNominalInterestRateChange) {
        mutableStateFlow.update {
            it.copy(
                nominalInterestRate = action.rate,
            )
        }
    }

    private fun handleNominalFrequencyIndexChange(action: NewLoanAccountAction.OnNominalFrequencyIndexChange) {
        mutableStateFlow.update { it.copy(nominalFrequencyIndex = action.index) }
    }

    private fun handleNominalMethodIndexChange(action: NewLoanAccountAction.OnNominalMethodIndexChange) {
        mutableStateFlow.update {
            it.copy(
                nominalInterestMethodIndex = action.index,
            )
        }
    }

    private fun handleNominalAmortizationIndexChange(action: NewLoanAccountAction.OnNominalAmortizationIndexChange) {
        mutableStateFlow.update {
            it.copy(
                nominalAmortizationIndex = action.index,
            )
        }
    }

    private fun handleEqualAmortizationCheckChange(action: NewLoanAccountAction.OnEqualAmortizationCheckChange) {
        mutableStateFlow.update { it.copy(isCheckedEqualAmortization = action.boolean) }
    }

    private fun handleRepaymentStrategyIndexChange(action: NewLoanAccountAction.OnRepaymentStrategyIndexChange) {
        mutableStateFlow.update {
            it.copy(
                repaymentStrategyIndex = action.index,
            )
        }
    }

    private fun handleBalloonRepaymentAmountChange(action: NewLoanAccountAction.OnBalloonRepaymentAmountChange) {
        mutableStateFlow.update { it.copy(balloonRepaymentAmount = action.amount) }
    }

    private fun handleInterestCalculationPeriodIndexChange(action: NewLoanAccountAction.OnInterestCalculationPeriodIndexChange) {
        mutableStateFlow.update {
            it.copy(
                interestCalculationPeriodIndex = action.index,
            )
        }
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
            )
        }
    }

    private fun handleTermFrequencyIndexChange(action: NewLoanAccountAction.OnTermFrequencyIndexChange) {
        mutableStateFlow.update {
            it.copy(
                termFrequencyIndex = action.index,
            )
        }
    }

    private fun handleRetry() {
        viewModelScope.launch {
            loadAllLoans()
            loadCollaterals()
        }
    }

    private fun handleNavigateBack() {
        sendEvent(NewLoanAccountEvent.NavigateBack)
    }

    private fun handleFinish() {
        sendEvent(NewLoanAccountEvent.Finish)
    }

    private fun handleStepChange(action: NewLoanAccountAction.OnStepChange) {
        mutableStateFlow.update {
            it.copy(
                currentStep = action.newIndex,
            )
        }
    }

    private fun handleProductNameChange(action: NewLoanAccountAction.OnProductNameChange) {
        mutableStateFlow.update {
            it.copy(
                loanProductSelected = action.index,
            )
        }
        loadLoanAccountTemplate(state.productLoans[action.index].id ?: -1)
    }

    private fun handleExternalIdChange(action: NewLoanAccountAction.OnExternalIdChange) {
        mutableStateFlow.update { it.copy(externalId = action.value) }
    }

    private fun handleFundChange(action: NewLoanAccountAction.OnFundChange) {
        mutableStateFlow.update {
            it.copy(
                fundIndex = action.index,
            )
        }
    }

    private fun handleLoanOfficerChange(action: NewLoanAccountAction.OnLoanOfficerChange) {
        mutableStateFlow.update {
            it.copy(
                loanOfficerIndex = action.index,
            )
        }
    }

    private fun handleLoanPurposeChange(action: NewLoanAccountAction.OnLoanPurposeChange) {
        mutableStateFlow.update {
            it.copy(
                loanPurposeIndex = action.index,
            )
        }
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
        mutableStateFlow.update {
            it.copy(
                linkSavingsIndex = action.index,
            )
        }
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

    private suspend fun loadAllLoans() {
        getAllLoanUseCase().collect { result ->
            when (result) {
                is DataState.Error -> mutableStateFlow.update {
                    it.copy(
                        screenState = NewLoanAccountState.ScreenState.Error(result.message),
                    )
                }

                is DataState.Loading -> mutableStateFlow.update {
                    it.copy(screenState = NewLoanAccountState.ScreenState.Loading)
                }

                is DataState.Success -> mutableStateFlow.update {
                    it.copy(
                        screenState = NewLoanAccountState.ScreenState.Success,
                        productLoans = result.data,
                    )
                }
            }
        }
    }

    private fun loadLoanAccountTemplate(productId: Int) = viewModelScope.launch {
        mutableStateFlow.update {
            it.copy(productId = productId)
        }
        getLoansAccountTemplateUseCase(state.clientId, productId).collect { result ->
            when (result) {
                is DataState.Error -> mutableStateFlow.update {
                    it.copy(
                        screenState = NewLoanAccountState.ScreenState.Error(result.message),
                        isOverLayLoadingActive = false,
                    )
                }

                is DataState.Loading -> mutableStateFlow.update {
                    it.copy(
                        isOverLayLoadingActive = true,
                    )
                }

                is DataState.Success -> mutableStateFlow.update {
                    it.copy(
                        screenState = NewLoanAccountState.ScreenState.Success,
                        isOverLayLoadingActive = false,
                        loanTemplate = result.data,
                        principalAmount = (result.data.principal ?: 0).toString(),
                        noOfRepayments = result.data.numberOfRepayments ?: 0,
                        repaidEvery = result.data.repaymentEvery ?: 0,
                        nominalInterestRate = (
                            result.data.interestRatePerPeriod
                                ?: 0
                            ).toString(),
                        nominalAmortizationIndex = result.data.amortizationTypeOptions.indexOfFirst { item -> item.value == result.data.amortizationType?.value },
                        termFrequencyIndex = result.data.termFrequencyTypeOptions.indexOfFirst { item -> item.value == result.data.termPeriodFrequencyType?.value },
                        nominalFrequencyIndex = result.data.interestRateFrequencyTypeOptions.indexOfFirst { item -> item.value == result.data.interestRateFrequencyType?.value },
                        nominalInterestMethodIndex = result.data.interestTypeOptions.indexOfFirst { item -> item.value == result.data.interestType?.value },
                        repaymentStrategyIndex = result.data.transactionProcessingStrategyOptions.indexOfFirst { item -> item.code == result.data.transactionProcessingStrategyCode },
                        interestCalculationPeriodIndex = result.data.interestCalculationPeriodTypeOptions.indexOfFirst { item -> item.value == result.data.interestCalculationPeriodType?.value },
                    )
                }
            }
        }
    }

    private suspend fun repaymentScheduler() {
        val payload = buildLoansPayloadForSchedulePreview()

        calculateLoanScheduleUseCase(payload).collect { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    if (dataState.exception is IllegalStateException) {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = NewLoanAccountState.DialogState.SuccessResponseStatus(
                                    successStatus = false,
                                    msg = dataState.message,
                                ),
                                launchEffectKey = Random.nextInt(),
                                isOverLayLoadingActive = false,
                            )
                        }
                    } else {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = NewLoanAccountState.ScreenState.Error(dataState.message),
                                isOverLayLoadingActive = false,
                            )
                        }
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            isOverLayLoadingActive = true,
                        )
                    }
                }

                is DataState.Success -> {
                    val schedulerDetails = mapOf(
                        Res.string.feature_loan_account_number to (state.accountNo),
                        Res.string.feature_loan_disbursed_date to state.expectedDisbursementDate.ifEmpty { "N/A" },
                        Res.string.principal_paid_off to CurrencyFormatter.format(
                            balance = state.repaymentSchedule.totalPrincipalPaid,
                            currencyCode = dataState.data.currency?.code ?: "N/A",
                            maximumFractionDigits = dataState.data.currency?.decimalPlaces ?: 0,
                        ),
                        Res.string.installment_paid to "0",
                        Res.string.total_installments to state.noOfRepayments.toString(),
                    )

                    mutableStateFlow.update {
                        it.copy(
                            repaymentSchedulesSummary = schedulerDetails,
                            screenState = NewLoanAccountState.ScreenState.Success,
                            repaymentSchedule = dataState.data,
                            isOverLayLoadingActive = false,
                            launchEffectKey = Random.nextInt(),
                        )
                    }
                }
            }
        }
    }

    /**
     * Build LoansPayload from current form state for schedule preview calculation.
     * Uses the same data that will be sent when submitting the loan application.
     */
    private fun buildLoansPayloadForSchedulePreview(): LoansPayload {
        return LoansPayload(
            loanOfficerId = if (state.loanOfficerIndex == -1) {
                null
            } else {
                state.loanTemplate?.loanOfficerOptions?.getOrNull(state.loanOfficerIndex)?.id
            },
            principal = state.principalAmount.toDoubleOrNull(),
            clientId = state.clientId,
            allowPartialPeriodInterestCalculation = state.isCheckedInterestPartialPeriod,
            amortizationType = state.loanTemplate?.amortizationTypeOptions
                ?.getOrNull(state.nominalAmortizationIndex)?.id,
            dateFormat = DateHelper.SHORT_MONTH,
            interestCalculationPeriodType = state.loanTemplate?.interestCalculationPeriodTypeOptions
                ?.getOrNull(state.interestCalculationPeriodIndex)?.id,
            interestRatePerPeriod = state.nominalInterestRate.toDoubleOrNull(),
            interestType = state.loanTemplate?.interestTypeOptions
                ?.getOrNull(state.nominalInterestMethodIndex)?.id,
            loanTermFrequency = state.noOfRepayments * state.repaidEvery,
            loanTermFrequencyType = state.loanTemplate?.termFrequencyTypeOptions
                ?.getOrNull(state.termFrequencyIndex)?.id,
            loanType = "individual",
            locale = "en",
            numberOfRepayments = state.noOfRepayments,
            productId = state.productId,
            repaymentEvery = state.repaidEvery,
            repaymentFrequencyDayOfWeekType = if (state.selectedDayIndex == -1) {
                null
            } else {
                state.loanTemplate?.repaymentFrequencyDaysOfWeekTypeOptions
                    ?.getOrNull(state.selectedDayIndex)?.id
            },
            repaymentFrequencyNthDayType = if (state.selectedOnIndex == -1) {
                null
            } else {
                state.loanTemplate?.repaymentFrequencyNthDayTypeOptions
                    ?.getOrNull(state.selectedOnIndex)?.id
            },
            repaymentFrequencyType = state.loanTemplate?.termFrequencyTypeOptions
                ?.getOrNull(state.termFrequencyIndex)?.id,
            expectedDisbursementDate = state.expectedDisbursementDate,
            submittedOnDate = state.submissionDate,
            loanPurposeId = if (state.loanPurposeIndex == -1) {
                null
            } else {
                state.loanTemplate?.loanPurposeOptions?.getOrNull(state.loanPurposeIndex)?.id
            },
            fundId = if (state.fundIndex == -1) {
                null
            } else {
                state.loanTemplate?.fundOptions?.getOrNull(state.fundIndex)?.id
            },
            linkAccountId = if (state.linkSavingsIndex == -1) {
                null
            } else {
                state.loanTemplate?.accountLinkingOptions?.getOrNull(state.linkSavingsIndex)?.id
            },
            transactionProcessingStrategyCode = state.loanTemplate?.transactionProcessingStrategyOptions
                ?.getOrNull(state.repaymentStrategyIndex)?.code,
            externalId = state.externalId,
        )
    }
}

@OptIn(ExperimentalTime::class)
data class NewLoanAccountState(
    val launchEffectKey: Int? = null,
    val accountNo: String = "",
    val clientId: Int,
    val productId: Int? = null,
    val productLoans: List<LoanProducts> = emptyList(),
    val repaymentSchedule: RepaymentSchedule = RepaymentSchedule(),
    val repaymentSchedulesSummary: Map<StringResource, String> = emptyMap(),
    val loanTemplate: LoanTemplate? = null,
    val currentStep: Int = 0,
    /**
     * - Per-loan datatable values keyed by table-index → (columnName → value).
     * - Populated by the inline stepper's DatatableStepPage instances.
     */
    val datatableValues: Map<Int, Map<String, DatatableFieldValue>> = emptyMap(),
    val dialogState: DialogState? = null,
    val screenState: ScreenState? = null,
    val isOverLayLoadingActive: Boolean = false,
    val externalId: String = "",
    val externalIdError: StringResource? = null,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showSubmissionDatePick: Boolean = false,
    val expectedDisbursementDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showExpectedDisbursementDatePick: Boolean = false,
    val isCheckedStandingInstructions: Boolean = false,
    val principalAmount: String = "0",
    val noOfRepayments: Int = 0,
    val noOfRepaymentsPreviousState: Int = 0,
    val firstRepaymentDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val interestChargedFromDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showFirstRepaymentDatePick: Boolean = false,
    val showInterestChargedFromDatePick: Boolean = false,
    val repaidEvery: Int = 0,
    val nominalInterestRate: String = "0",
    val isCheckedEqualAmortization: Boolean = false,
    val isCheckedInterestPartialPeriod: Boolean = false,
    val arrearsTolerance: Int = 0,
    val interestFreePeriod: Int = 0,
    val moratoriumGraceOnPrincipalPayment: Int = 0,
    val moratoriumGraceOnInterestPayment: Int = 0,
    val moratoriumOnArrearsAgeing: Int = 0,
    val collaterals: List<CollateralItem> = emptyList(),
    val addedCollaterals: List<CreatedCollateral> = emptyList(),
    val collateralQuantity: Int = 0,
    val collateralTotal: Double = 0.0,
    val totalCollateral: Double = 0.0,
    val addedCharges: List<CreatedCharges> = emptyList(),
    val chargeDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val balloonRepaymentAmount: Int = 0,
    val showChargesDatePick: Boolean = false,
    val chargeAmount: String = "",

    /**
     * These are used in dropDown field for changing the value,
     */
    val loanProductSelected: Int = -1,
    val chooseChargeIndex: Int = -1,
    val collateralSelectedIndex: Int = -1,
    val repaymentStrategyIndex: Int = -1,
    val interestCalculationPeriodIndex: Int = -1,
    val nominalFrequencyIndex: Int = -1,
    val nominalInterestMethodIndex: Int = -1,
    val nominalAmortizationIndex: Int = -1,
    val selectedOnIndex: Int = -1,
    val selectedDayIndex: Int = -1,
    val termFrequencyIndex: Int = -1,
    val linkSavingsIndex: Int = -1,
    val loanOfficerIndex: Int = -1,
    val loanPurposeIndex: Int = -1,
    val fundIndex: Int = -1,

) {
    sealed interface DialogState {
        data object AddNewCollateral : DialogState
        data class AddNewCharge(val edit: Boolean, val index: Int = -1) : DialogState
        data object ShowCollaterals : DialogState
        data object ShowCharges : DialogState
        data object ShowOverDueCharges : DialogState
        data class SuccessResponseStatus(val successStatus: Boolean, val msg: String = "") :
            DialogState
    }

    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data class Error(val message: String) : ScreenState
    }

    val isDetailsNextEnabled =
        loanProductSelected != -1 && submissionDate.isNotEmpty() && expectedDisbursementDate.isNotEmpty()
    val isCollateralBtnEnabled = collateralQuantity != 0 && collateralSelectedIndex != -1

    val totalSteps: Int
        get() = 4 + (loanTemplate?.dataTables?.filterNotNull()?.size ?: 0)
}

sealed interface NewLoanAccountEvent {
    data object NavigateBack : NewLoanAccountEvent
    data object Finish : NewLoanAccountEvent
    data class LoanCreationSuccess(val clientId: Int) : NewLoanAccountEvent
}

/**
 * Build the Fineract datatable-payload `data` map for one table.
 * Mirrors `DataTableListViewModel.buildPayloadMap` (kept in sync intentionally —
 * both serve the same Fineract contract). Filters system columns (PK, created_at,
 * updated_at, FKs) and converts each typed [DatatableFieldValue] to the
 * JSON-encodable primitive the API expects (with column-aware numeric coercion).
 */
private fun buildDatatablePayloadMap(
    headers: List<ColumnHeader?>,
    rawValues: Map<String, DatatableFieldValue>,
): Map<String, Any> {
    // Fineract parses datatable date columns using `dateFormat` from the same map.
    // The DatatableStepPage date widget renders via `DateHelper.getDateAsStringFromLong`
    // which formats as `dd-MM-yyyy` (i.e. DateHelper.SHORT_MONTH). The two MUST match
    // — using ApiDateFormatter.DATE_FORMAT ("dd MMMM yyyy") here was rejected by the
    // server with "invalid value" for product 7's CONDICION.FECHA field.
    val payload = mutableMapOf<String, Any>(
        "dateFormat" to DateHelper.SHORT_MONTH,
        "locale" to Constants.LOCALE_EN,
    )
    headers.filterNotNull().forEach { header ->
        if (header.columnPrimaryKey == true) return@forEach
        val name = header.dataTableColumnName ?: return@forEach
        if (name in SYSTEM_COLUMNS) return@forEach
        val raw = rawValues[name] ?: return@forEach
        payload[name] = header.encodePayloadValue(raw)
    }
    return payload
}

/**
 * Columns Fineract auto-populates server-side on insert — never user input.
 * Audit timestamps + foreign keys to the parent entity (m_loan auto-fills
 * `loan_id`, m_client auto-fills `client_id`, etc). Kept in sync with the
 * same set in DatatableStepPage.SYSTEM_COLUMNS. (Two definitions because
 * the page is in a different module; consider extracting to core/database
 * if a third consumer appears.)
 */
private val SYSTEM_COLUMNS = setOf(
    "created_at", "updated_at", "createdAt", "updatedAt",
    "loan_id", "client_id", "group_id", "savings_id", "share_id", "office_id",
)

/**
 * Convert a typed form value into the primitive shape Fineract expects for this
 * column. Numeric coercion lives here (and only here) — the form layer stores all
 * text inputs as [DatatableFieldValue.Text]; we parse to Int/Double per column
 * display type at submit time. Unparseable input falls back to 0 / 0.0 (same
 * behaviour as the prior `coerceDatatableValue` helper).
 */
private fun ColumnHeader.encodePayloadValue(value: DatatableFieldValue): Any = when (value) {
    is DatatableFieldValue.Text -> when (columnDisplayType) {
        "INTEGER" -> value.text.toIntOrNull() ?: 0
        "DECIMAL", "FLOAT" -> value.text.toDoubleOrNull() ?: 0.0
        else -> value.text
    }
    is DatatableFieldValue.Bool -> value.checked
    is DatatableFieldValue.Code -> value.id
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
    data class OnPrincipalAmountChange(val amount: String) : NewLoanAccountAction
    data class OnNoOfRepaymentsChange(val number: Int) : NewLoanAccountAction
    data class OnTermFrequencyIndexChange(val index: Int) : NewLoanAccountAction
    data class OnFirstRepaymentDateChange(val date: String) : NewLoanAccountAction
    data class OnInterestChargedFromChange(val date: String) : NewLoanAccountAction
    data class OnFirstRepaymentDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnInterestChargedFromDatePick(val state: Boolean) : NewLoanAccountAction
    data class OnRepaidEveryChange(val number: Int) : NewLoanAccountAction
    data class OnSelectedOnIndexChange(val index: Int) : NewLoanAccountAction
    data class OnSelectedDayIndexChange(val index: Int) : NewLoanAccountAction
    data class OnNominalInterestRateChange(val rate: String) : NewLoanAccountAction

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
    data object RepaymentScheduler : NewLoanAccountAction
    data object SubmitLoanApplication : NewLoanAccountAction

    /**
     * GAP-DT-013: a field on one of the inline datatable steps changed.
     * `tableIndex` is into `loanTemplate.dataTables.filterNotNull()`.
     * `columnName` matches the JSON `columnName` (used verbatim in submit payload).
     * `value` carries the typed input via [DatatableFieldValue] — Text / Bool / Code.
     */
    data class UpdateDatatableField(
        val tableIndex: Int,
        val columnName: String,
        val value: DatatableFieldValue,
    ) : NewLoanAccountAction
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
