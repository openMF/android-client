/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.newFixedDepositAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_network_not_available
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.FixedDepositRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.template.recurring.FieldOfficerOption
import com.mifos.core.network.model.FixedDepositTemplate
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.feature.client.fixedDepositAccount.FixedDepositAccountRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateFixedDepositAccountViewmodel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val fixedDepositRepository: FixedDepositRepository,
) :
    BaseViewModel<
        NewFixedDepositAccountState,
        NewFixedDepositAccountEvent,
        NewFixedDepositAccountAction,
        >(
        NewFixedDepositAccountState(
            clientId = savedStateHandle.toRoute<FixedDepositAccountRoute>().clientId,
        ),
    ) {

    init {
        loadFixedDepositTemplate()
    }

    suspend fun isOnline(
        content: suspend () -> Unit,
    ) {
        if (networkMonitor.isOnline.first()) {
            content()
        } else {
            mutableStateFlow.update {
                it.copy(
                    screenState = NewFixedDepositAccountState.ScreenState.Error(getString(Res.string.feature_client_error_network_not_available)),
                )
            }
        }
    }

    override fun handleAction(action: NewFixedDepositAccountAction) {
        when (action) {
            is NewFixedDepositAccountAction.OnNextPress -> moveToNextStep()
            is NewFixedDepositAccountAction.OnStepChange -> handleStepChange(action)
            is NewFixedDepositAccountAction.NavigateBack -> sendEvent(NewFixedDepositAccountEvent.NavigateBack)
            is NewFixedDepositAccountAction.Finish -> sendEvent(NewFixedDepositAccountEvent.Finish)
            is NewFixedDepositAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)
            is NewFixedDepositAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(
                action,
            )

            is NewFixedDepositAccountAction.OnProductNameChange -> handleOnProductNameChange(action)
            is NewFixedDepositAccountAction.OnFieldOfficerChange -> handleFieldOfficerChange(action)
            is NewFixedDepositAccountAction.OnExternalIdChange -> handleExternalIdChange(action)
            NewFixedDepositAccountAction.Retry -> handleRetry()
            is NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositAmount -> handleSetFixedDepositAmount(
                action,
            )

            is NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositPeriod -> handleSetFixedDepositPeriod(
                action,
            )

            is NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositPeriodType -> handleSetFixedDepositPeriodType(
                action,
            )

            is NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCompoundingPeriod -> handleSetInterestCompoundingPeriod(
                action,
            )

            is NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestPostingPeriod -> handleSetInterestPostingPeriod(
                action,
            )

            is NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCalculationType -> handleInterestCalculationType(
                action,
            )

            is NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCalculationDaysInYearType -> handleSetInterestCalculationDaysInYearType(
                action,
            )

            NewFixedDepositAccountAction.OnDetailNext -> {
                val productError =
                    TextFieldsValidator.dropDownEmptyValidator(state.fixedDepositAccountDetail.productSelected == -1)
                if (productError != null) {
                    mutableStateFlow.update {
                        it.copy(
                            fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                                productError = productError,
                            ),
                        )
                    }
                } else {
                    moveToNextStep()
                }
            }

            NewFixedDepositAccountAction.OnTermNext -> {
                val depositAmountError =
                    TextFieldsValidator.doubleNumberValidator(state.fixedDepositAccountTerms.depositAmount)
                val depositPeriodError =
                    TextFieldsValidator.numberValidator(state.fixedDepositAccountTerms.depositPeriod)
                val depositPeriodTypeError =
                    TextFieldsValidator.dropDownEmptyValidator(state.fixedDepositAccountTerms.depositPeriodTypeIndex == -1)

                if (depositAmountError != null || depositPeriodError != null || depositPeriodTypeError != null) {
                    mutableStateFlow.update {
                        it.copy(
                            fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                                depositAmountError = depositAmountError,
                                depositPeriodError = depositPeriodError,
                                depositPeriodTypeError = depositPeriodTypeError,
                            ),
                        )
                    }
                } else {
                    moveToNextStep()
                }
            }

            NewFixedDepositAccountAction.PreviousStep -> {
                moveToPreviousStep()
            }

            is NewFixedDepositAccountAction.OnApplyPenalInterestChange -> {
                mutableStateFlow.update {
                    it.copy(
                        applyPenalInterest = action.checked,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnLockInPeriodFrequencyChange -> {
                mutableStateFlow.update {
                    it.copy(
                        lockInPeriodFrequency = action.value,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnLockInPeriodTypeIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        lockInPeriodTypeIndex = action.index,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnMaturityInstructionIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        maturityInstructionsIndex = action.index,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnMaximumDepositFrequencyChange -> {
                mutableStateFlow.update {
                    it.copy(
                        maximumDispositFrequency = action.value,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnMaximumDepositTypeIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        maximumDispositTypeIndex = action.index,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnMinimumDepositTermFrequencyChange -> {
                mutableStateFlow.update {
                    it.copy(
                        minimumDispositTermFrequency = action.value,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnMinimumDepositTermTypeIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        minimumDispositTermTypeIndex = action.index,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnMultiplesFrequencyChange -> {
                mutableStateFlow.update {
                    it.copy(
                        multiplesFrequency = action.value,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnMultiplesTypeIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        multiplesTypeIndex = action.index,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnPenalInterestChange -> {
                mutableStateFlow.update {
                    it.copy(
                        penalInterest = action.value,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnPeriodIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        periodIndex = action.index,
                    )
                }
            }

            is NewFixedDepositAccountAction.OnTransferLinkedSavingsAccountInterestChange -> {
                mutableStateFlow.update {
                    it.copy(
                        transferLinkedSavingAccountInterest = action.checked,
                    )
                }
            }

            NewFixedDepositAccountAction.OnDismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            NewFixedDepositAccountAction.OnShowRateChart -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = NewFixedDepositAccountState.DialogState.RateChartDialog,
                    )
                }
            }
        }
    }

    private fun handleRetry() {
        loadFixedDepositTemplate()
    }

    private fun loadFixedDepositTemplate() = viewModelScope.launch {
        isOnline {
            fixedDepositRepository.getFixedDepositTemplate(
                clientId = state.clientId,
            ).collect { state ->
                when (state) {
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = NewFixedDepositAccountState.ScreenState.Success,
                                isOverlayLoading = false,
                                template = state.data,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = NewFixedDepositAccountState.ScreenState.Error(state.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = NewFixedDepositAccountState.ScreenState.Loading,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadRecurringAccountTemplateWithProduct() = viewModelScope.launch {
        isOnline {
            fixedDepositRepository.getFixedDepositTemplate(
                clientId = state.clientId,
                productId = state.template.productOptions?.get(state.fixedDepositAccountDetail.productSelected)?.id,
            ).collect { state ->
                when (state) {
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = NewFixedDepositAccountState.ScreenState.Success,
                                isOverlayLoading = false,
                                template = state.data,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = NewFixedDepositAccountState.ScreenState.Error(state.message),
                                isOverlayLoading = false,
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                isOverlayLoading = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleSubmissionDateChange(action: NewFixedDepositAccountAction.OnSubmissionDateChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    submissionDate = action.date,
                ),
            )
        }
    }

    private fun handleStepChange(action: NewFixedDepositAccountAction.OnStepChange) {
        mutableStateFlow.update { it.copy(currentStep = action.newIndex) }
    }

    private fun handleSubmissionDatePick(action: NewFixedDepositAccountAction.OnSubmissionDatePick) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    showSubmissionDatePick = action.state,
                ),
            )
        }
    }

    private fun handleOnProductNameChange(action: NewFixedDepositAccountAction.OnProductNameChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    productSelected = action.index,
                    productError = null,
                ),
            )
        }
        loadRecurringAccountTemplateWithProduct()
    }

    private fun handleFieldOfficerChange(action: NewFixedDepositAccountAction.OnFieldOfficerChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    fieldOfficerIndex = action.index,
                ),
            )
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

    private fun handleExternalIdChange(action: NewFixedDepositAccountAction.OnExternalIdChange) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountDetail = it.fixedDepositAccountDetail.copy(
                    externalId = action.value,
                ),
            )
        }
    }

    private fun handleSetFixedDepositAmount(action: NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositAmount) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                    depositAmount = action.depositAmount,
                    depositAmountError = null,
                ),
            )
        }
    }

    private fun handleSetFixedDepositPeriod(action: NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositPeriod) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                    depositPeriod = action.period,
                    depositPeriodError = null,
                ),
            )
        }
    }

    private fun handleSetFixedDepositPeriodType(action: NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetFixedDepositPeriodType) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                    depositPeriodTypeIndex = action.depositPeriodTypeIndex,
                    depositPeriodTypeError = null,
                ),
            )
        }
    }

    private fun handleSetInterestCompoundingPeriod(action: NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCompoundingPeriod) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                    interestCompoundingPeriodTypeIndex = action.interestCompoundingPeriodTypeIndex,
                ),
            )
        }
    }

    private fun handleInterestCalculationType(action: NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCalculationType) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                    interestCalculationTypeIndex = action.interestCalculationPeriodTypeIndex,
                ),
            )
        }
    }

    private fun handleSetInterestPostingPeriod(action: NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestPostingPeriod) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                    interestPostingPeriodTypeIndex = action.interestPostingPeriodTypeIndex,
                ),
            )
        }
    }

    private fun handleSetInterestCalculationDaysInYearType(action: NewFixedDepositAccountAction.NewFixedDepositAccountTermsAction.SetInterestCalculationDaysInYearType) {
        mutableStateFlow.update {
            it.copy(
                fixedDepositAccountTerms = it.fixedDepositAccountTerms.copy(
                    interestCalculationDaysInYearTypeIndex = action.periodTypeIndex,
                ),
            )
        }
    }

    private fun moveToNextStep() {
        val current = state.currentStep
        if (current < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = current + 1,
                    dialogState = null,
                )
            }
        } else {
            sendEvent(NewFixedDepositAccountEvent.Finish)
        }
    }
}

data class NewFixedDepositAccountState(
    val clientId: Int = -1,
    val currentStep: Int = 0,
    val dialogState: DialogState? = null,
    val totalSteps: Int = 4,
    val screenState: ScreenState = ScreenState.Loading,
    val fixedDepositAccountDetail: FixedDepositAccountDetailsState = FixedDepositAccountDetailsState(),
    val fixedDepositAccountTerms: FixedDepositAccountTermsState = FixedDepositAccountTermsState(),
    val template: FixedDepositTemplate = FixedDepositTemplate(),
    val isOverlayLoading: Boolean = false,

    val lockInPeriodFrequency: String = "",
    val lockInPeriodTypeIndex: Int = -1,
    val minimumDispositTermFrequency: String = "",
    val minimumDispositTermTypeIndex: Int = -1,
    val maximumDispositFrequency: String = "",
    val maximumDispositTypeIndex: Int = -1,
    val multiplesFrequency: String = "",
    val multiplesTypeIndex: Int = -1,

    val maturityInstructionsIndex: Int = -1,
    val periodIndex: Int = -1,
    val applyPenalInterest: Boolean = false,
    val penalInterest: String = "",

    val transferLinkedSavingAccountInterest: Boolean = false,
) {
    sealed interface ScreenState {
        data class Error(val message: String) : ScreenState
        data object Loading : ScreenState
        data object Success : ScreenState
    }

    sealed interface DialogState {
        data object RateChartDialog : DialogState
    }

    val isRateChartEmpty = !template.accountChart?.chartSlabs.isNullOrEmpty()
}

data class FixedDepositAccountTermsState(
    val depositAmount: String = "",
    val depositPeriod: String = "",
    val depositPeriodTypeIndex: Int = -1,
    val interestCompoundingPeriodTypeIndex: Int = -1,
    val interestPostingPeriodTypeIndex: Int = -1,
    val interestCalculationTypeIndex: Int = -1,
    val interestCalculationDaysInYearTypeIndex: Int = -1,
    val depositAmountError: StringResource? = null,
    val depositPeriodError: StringResource? = null,
    val depositPeriodTypeError: StringResource? = null,
)

data class FixedDepositAccountDetailsState
@OptIn(ExperimentalTime::class)
constructor(
    val submittedOnDate: String = "",
    val fieldOfficer: FieldOfficerOption? = null,
    val showSubmissionDatePick: Boolean = false,
    val productSelected: Int = -1,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val fieldOfficerIndex: Int = -1,
    val productError: StringResource? = null,
    val externalId: String = "",
    val externalIdError: StringResource? = null,
    val isMiniLoaderActive: Boolean = false,
    val fieldOfficerOptions: List<FieldOfficerOption>? = null,
)

sealed class NewFixedDepositAccountAction {
    data object OnNextPress : NewFixedDepositAccountAction()
    data object OnDetailNext : NewFixedDepositAccountAction()
    data object OnTermNext : NewFixedDepositAccountAction()
    data object OnDismissDialog : NewFixedDepositAccountAction()
    data class OnStepChange(val newIndex: Int) : NewFixedDepositAccountAction()
    data object PreviousStep : NewFixedDepositAccountAction()
    data object NavigateBack : NewFixedDepositAccountAction()
    data class OnSubmissionDatePick(val state: Boolean) : NewFixedDepositAccountAction()
    data class OnSubmissionDateChange(val date: String) : NewFixedDepositAccountAction()
    data class OnProductNameChange(val index: Int) : NewFixedDepositAccountAction()
    data object Finish : NewFixedDepositAccountAction()
    data class OnFieldOfficerChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnExternalIdChange(val value: String) : NewFixedDepositAccountAction()
    data object Retry : NewFixedDepositAccountAction()
    data object OnShowRateChart : NewFixedDepositAccountAction()
    data class OnLockInPeriodFrequencyChange(val value: String) : NewFixedDepositAccountAction()
    data class OnLockInPeriodTypeIndexChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnMinimumDepositTermFrequencyChange(val value: String) :
        NewFixedDepositAccountAction()

    data class OnMinimumDepositTermTypeIndexChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnMaximumDepositFrequencyChange(val value: String) : NewFixedDepositAccountAction()
    data class OnMaximumDepositTypeIndexChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnMultiplesFrequencyChange(val value: String) : NewFixedDepositAccountAction()
    data class OnMultiplesTypeIndexChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnMaturityInstructionIndexChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnPeriodIndexChange(val index: Int) : NewFixedDepositAccountAction()
    data class OnApplyPenalInterestChange(val checked: Boolean) : NewFixedDepositAccountAction()
    data class OnPenalInterestChange(val value: String) : NewFixedDepositAccountAction()
    data class OnTransferLinkedSavingsAccountInterestChange(val checked: Boolean) :
        NewFixedDepositAccountAction()

    sealed class NewFixedDepositAccountTermsAction : NewFixedDepositAccountAction() {
        data class SetFixedDepositAmount(val depositAmount: String) : NewFixedDepositAccountAction()
        data class SetFixedDepositPeriod(val period: String) : NewFixedDepositAccountAction()
        data class SetFixedDepositPeriodType(val depositPeriodTypeIndex: Int) :
            NewFixedDepositAccountAction()

        data class SetInterestCompoundingPeriod(val interestCompoundingPeriodTypeIndex: Int) :
            NewFixedDepositAccountAction()

        data class SetInterestPostingPeriod(val interestPostingPeriodTypeIndex: Int) :
            NewFixedDepositAccountAction()

        data class SetInterestCalculationType(val interestCalculationPeriodTypeIndex: Int) :
            NewFixedDepositAccountAction()

        data class SetInterestCalculationDaysInYearType(val periodTypeIndex: Int) :
            NewFixedDepositAccountAction()
    }
}

sealed class NewFixedDepositAccountEvent() {
    object NavigateBack : NewFixedDepositAccountEvent()
    object Finish : NewFixedDepositAccountEvent()
}
