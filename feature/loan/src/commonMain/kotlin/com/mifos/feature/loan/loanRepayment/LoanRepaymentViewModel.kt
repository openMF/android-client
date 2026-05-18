/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoanRepaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanRepaymentRepository,
    private val loanChargeRepository: LoanChargeRepository,
    private val summaryRepository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanRepaymentState, LoanRepaymentEvent, LoanRepaymentAction>(LoanRepaymentState()) {

    private val route = savedStateHandle.toRoute<LoanRepaymentScreenRoute>()

    init {
        getLoan()
    }

    private fun getLoan() {
        summaryRepository.getLoanById(route.loanId).onEach { result ->
            sendAction(LoanRepaymentAction.Internal.GetLoanResult(result))
        }.launchIn(viewModelScope)
    }

    private fun handleGetLoanResult(action: LoanRepaymentAction.Internal.GetLoanResult) {
        when (val result = action.getLoanResult) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentState.DialogState.Loading(),
                    )
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        loanId = result.data?.id,
                        clientName = result.data?.clientName,
                        loanProductName = result.data?.loanProductName,
                        amountInArrears = result.data?.summary?.totalOverdue,
                        loanAccountNumber = result.data?.accountNo,
                    )
                }

                getLoanRepaymentTemplate()
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentState.DialogState.Error(
                            result.message,
                        ),
                    )
                }
            }
        }
    }

    private fun getLoanRepaymentTemplate() {
        repository.getLoanRepayTemplate(route.loanId).onEach { result ->
            sendAction(LoanRepaymentAction.Internal.GetLoanRepaymentTemplateResult(result))
        }.launchIn(viewModelScope)
    }

    private fun handleGetLoanRepaymentTemplateResult(action: LoanRepaymentAction.Internal.GetLoanRepaymentTemplateResult) {
        when (val result = action.getLoanRepaymentTemplateResult) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentState.DialogState.Error(result.message),
                    )
                }
            }

            DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentState.DialogState.Loading(),
                    )
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        loanRepaymentTemplate = result.data,
                        transactionDate = result.data?.date?.let { integersOfDate ->
                            DateHelper.getDateAsString(
                                integersOfDate = integersOfDate,
                                pattern = DateHelper.SHORT_MONTH,

                            )
                        },
                        penaltyAmount = result.data?.penaltyChargesPortion,
                        dialogState = null,
                        transactionAmount = CurrencyFormatter.format(
                            balance = result.data?.amount,
                            currencyCode = result.data?.currency?.code,
                            maximumFractionDigits = result.data?.currency?.decimalPlaces,
                        ),
                    )
                }

                getLoanCharges()
            }
        }
    }

    private fun submitRepayment(request: LoanRepaymentRequestEntity) {
        mutableStateFlow.update {
            it.copy(
                dialogState = LoanRepaymentState.DialogState.Loading(true),
            )
        }
        viewModelScope.launch {
            val result = repository.submitPayment(route.loanId, request)
            sendAction(LoanRepaymentAction.Internal.SubmitRepaymentResult(result))
        }
    }

    private fun handleSubmitRepaymentResult(action: LoanRepaymentAction.Internal.SubmitRepaymentResult) {
        when (val result = action.submitRepaymentResult) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentState.DialogState.Error(result.message),
                    )
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
                sendEvent(LoanRepaymentEvent.NavigationBack)
            }

            else -> Unit
        }
    }

    private fun getLoanCharges() {
        loanChargeRepository.getListOfLoanCharges(route.loanId).onEach {
            sendAction(LoanRepaymentAction.Internal.LoanChargesResult(it))
        }.launchIn(
            viewModelScope,
        )
    }

    private fun handleLoanChargesResult(action: LoanRepaymentAction.Internal.LoanChargesResult) {
        when (val result = action.loanChargesResult) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentState.DialogState.Error(result.message),
                    )
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                        loanCharges = result.data.filter { penaltyData ->
                            penaltyData.penalty == true
                        },
                    )
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = LoanRepaymentState.DialogState.Loading(),
                    )
                }
            }
        }
    }

    private fun currencyFormatter(amount: Double): String {
        return CurrencyFormatter.format(
            balance = amount,
            currencyCode = state.loanRepaymentTemplate?.currency?.code,
            maximumFractionDigits = state.loanRepaymentTemplate?.currency?.decimalPlaces,
        )
    }

    private fun calculatePenaltyState(
        selectedPenaltyIds: Set<Int>,
    ): Pair<Double, String> {
        val totalPenalty =
            state.loanRepaymentTemplate
                ?.penaltyChargesPortion
                ?: 0.0

        val totalAmount =
            state.loanRepaymentTemplate
                ?.amount
                ?: 0.0

        val selectedPenaltyAmount =
            state.loanCharges
                .filter {
                    it.id in selectedPenaltyIds
                }
                .sumOf {
                    it.amountOutstanding ?: 0.0
                }

        val transactionAmount =
            totalAmount -
                (totalPenalty - selectedPenaltyAmount)

        return selectedPenaltyAmount to
            currencyFormatter(transactionAmount)
    }

    override fun handleAction(action: LoanRepaymentAction) {
        when (action) {
            LoanRepaymentAction.SubmitRepayment -> {
                submitRepayment(
                    request = LoanRepaymentRequestEntity(
                        transactionDate = state.transactionDate,
                        transactionAmount = state.transactionAmount,
                        paymentTypeId = state.paymentTypeId,
                        note = state.note,
                        locale = Constants.LOCALE_EN,
                        dateFormat = DateHelper.SHORT_MONTH,
                        accountNumber = state.accountNumber,
                        checkNumber = state.checkNumber,
                        routingCode = state.routingCode,
                        receiptNumber = state.receiptNumber,
                        bankNumber = state.bankNumber,
                        externalId = state.externalId,
                    ),
                )
            }

            is LoanRepaymentAction.Internal.GetLoanResult -> {
                handleGetLoanResult(action)
            }

            is LoanRepaymentAction.Internal.GetLoanRepaymentTemplateResult -> {
                handleGetLoanRepaymentTemplateResult(action)
            }

            is LoanRepaymentAction.Internal.SubmitRepaymentResult -> {
                handleSubmitRepaymentResult(action)
            }

            is LoanRepaymentAction.UpdateAccountNumber -> {
                mutableStateFlow.update {
                    it.copy(
                        accountNumber = action.accountNumber,
                    )
                }
            }

            is LoanRepaymentAction.UpdateBankNumber -> {
                mutableStateFlow.update {
                    it.copy(
                        bankNumber = action.bankNumber,
                    )
                }
            }

            is LoanRepaymentAction.UpdateCheckNumber -> {
                mutableStateFlow.update {
                    it.copy(
                        checkNumber = action.checkNumber,
                    )
                }
            }

            is LoanRepaymentAction.UpdateNote -> {
                mutableStateFlow.update {
                    it.copy(
                        note = action.note,
                    )
                }
            }

            is LoanRepaymentAction.UpdatePaymentTypeIdIndex -> {
                mutableStateFlow.update {
                    it.copy(
                        paymentTypeIdIndex = action.index,
                        paymentTypeId = action.index?.let { index ->
                            state.loanRepaymentTemplate?.paymentTypeOptions?.get(index)
                        }?.id?.toString(),
                    )
                }
            }

            is LoanRepaymentAction.UpdateReceiptNumber -> {
                mutableStateFlow.update {
                    it.copy(
                        receiptNumber = action.receiptNumber,
                    )
                }
            }

            is LoanRepaymentAction.UpdateRoutingCode -> {
                mutableStateFlow.update {
                    it.copy(
                        routingCode = action.routingCode,
                    )
                }
            }

            is LoanRepaymentAction.UpdateTransactionAmount -> {
                val amount =
                    action.transactionAmount
                        ?.replace(Regex("[^\\d.-]"), "")
                        ?.toDoubleOrNull()
                        ?: 0.0

                mutableStateFlow.update {
                    it.copy(
                        transactionAmount =
                        amount.takeIf { amount > 0.0 }
                            ?.let(::currencyFormatter)
                            ?: "",
                    )
                }
            }

            is LoanRepaymentAction.UpdateTransactionDate -> {
                mutableStateFlow.update {
                    it.copy(
                        transactionDate = action.transactionDate,
                    )
                }
            }

            is LoanRepaymentAction.UpdateIsShowPaymentDetails -> {
                mutableStateFlow.update {
                    it.copy(
                        isShowPaymentDetail = action.isShowPaymentDetails,
                    )
                }
            }

            is LoanRepaymentAction.UpdateIsShowPenalties -> {
                mutableStateFlow.update {
                    it.copy(
                        isShowPenalties = action.isShowPenalties,
                    )
                }
            }

            LoanRepaymentAction.NavigateBack -> {
                sendEvent(LoanRepaymentEvent.NavigationBack)
            }

            is LoanRepaymentAction.UpdateExternalId -> {
                mutableStateFlow.update {
                    it.copy(
                        externalId = action.externalId,
                    )
                }
            }

            is LoanRepaymentAction.UpdateRepaymentDate -> {
                mutableStateFlow.update {
                    it.copy(
                        showRepaymentDate = action.repaymentDate,
                    )
                }
            }

            is LoanRepaymentAction.Internal.LoanChargesResult -> {
                handleLoanChargesResult(action)
            }

            is LoanRepaymentAction.TogglePenalty -> {
                val updatedSelectedPenaltyIds =
                    state.selectedPenaltyIds
                        .toMutableSet()
                        .apply {
                            if (contains(action.penaltyId)) {
                                remove(action.penaltyId)
                            } else {
                                add(action.penaltyId)
                            }
                        }

                val (
                    selectedPenaltyAmount,
                    transactionAmount,
                ) = calculatePenaltyState(
                    updatedSelectedPenaltyIds,
                )

                mutableStateFlow.update {
                    it.copy(
                        selectedPenaltyIds = updatedSelectedPenaltyIds,
                        penaltyAmount = selectedPenaltyAmount,
                        transactionAmount = transactionAmount,
                    )
                }
            }

            is LoanRepaymentAction.ToggleAllPenalty -> {
                val selectedPenaltyIds =
                    if (action.selectAllPenalties) {
                        state.loanCharges
                            .map {
                                it.id
                            }
                            .toSet()
                    } else {
                        emptySet()
                    }

                val (
                    selectedPenaltyAmount,
                    transactionAmount,
                ) = calculatePenaltyState(
                    selectedPenaltyIds = selectedPenaltyIds,
                )

                mutableStateFlow.update {
                    it.copy(
                        selectAllPenalties = action.selectAllPenalties,
                        selectedPenaltyIds = selectedPenaltyIds,
                        penaltyAmount = selectedPenaltyAmount,
                        transactionAmount = transactionAmount,
                    )
                }
            }
        }
    }
}

data class LoanRepaymentState(
    val clientName: String? = null,
    val loanId: Int? = null,
    val loanAccountNumber: String? = null,
    val loanProductName: String? = null,
    val amountInArrears: Double? = 0.0,
    val dialogState: DialogState? = null,
    val loanRepaymentTemplate: LoanRepaymentTemplateEntity? = null,
    // payload for repayment
    val locale: String? = null,
    val transactionDate: String? = null,
    val transactionAmount: String? = null,
    val paymentTypeId: String? = null,
    val paymentTypeIdIndex: Int? = null,
    val note: String? = null,
    val accountNumber: String? = null,
    val checkNumber: String? = null,
    val routingCode: String? = null,
    val receiptNumber: String? = null,
    val bankNumber: String? = null,
    val externalId: String? = null,
    val penaltyAmount: Double? = null,

    val isShowPaymentDetail: Boolean = false,
    val isShowPenalties: Boolean = false,
    val showRepaymentDate: Boolean = false,
    val loanCharges: List<ChargesEntity> = emptyList(),
    val penaltyId: Int? = null,
    val selectAllPenalties: Boolean = true,
    val selectedPenaltyIds: Set<Int> = emptySet(),

) {

    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data class Loading(val isOverlayLoading: Boolean = false) : DialogState
    }
}

sealed interface LoanRepaymentEvent {
    object NavigationBack : LoanRepaymentEvent
}

sealed interface LoanRepaymentAction {
    object SubmitRepayment : LoanRepaymentAction
    object NavigateBack : LoanRepaymentAction

    sealed interface Internal : LoanRepaymentAction {
        data class GetLoanResult(
            val getLoanResult: DataState<LoanWithAssociationsEntity?>,
        ) : Internal

        data class GetLoanRepaymentTemplateResult(
            val getLoanRepaymentTemplateResult: DataState<LoanRepaymentTemplateEntity?>,
        ) : Internal

        data class SubmitRepaymentResult(
            val submitRepaymentResult: DataState<LoanRepaymentResponseEntity>,
        ) : Internal

        data class LoanChargesResult(
            val loanChargesResult: DataState<List<ChargesEntity>>,
        ) : Internal
    }

    data class UpdateTransactionDate(
        val transactionDate: String?,
    ) : LoanRepaymentAction

    data class UpdateTransactionAmount(
        val transactionAmount: String?,
    ) : LoanRepaymentAction

    data class UpdatePaymentTypeIdIndex(
        val index: Int?,
    ) : LoanRepaymentAction

    data class UpdateNote(
        val note: String?,
    ) : LoanRepaymentAction

    data class UpdateAccountNumber(
        val accountNumber: String?,
    ) : LoanRepaymentAction

    data class UpdateCheckNumber(
        val checkNumber: String?,
    ) : LoanRepaymentAction

    data class UpdateRoutingCode(
        val routingCode: String?,
    ) : LoanRepaymentAction

    data class UpdateReceiptNumber(
        val receiptNumber: String?,
    ) : LoanRepaymentAction

    data class UpdateBankNumber(
        val bankNumber: String?,
    ) : LoanRepaymentAction

    data class UpdateExternalId(
        val externalId: String?,
    ) : LoanRepaymentAction

    data class UpdateRepaymentDate(
        val repaymentDate: Boolean,
    ) : LoanRepaymentAction

    data class UpdateIsShowPaymentDetails(
        val isShowPaymentDetails: Boolean,
    ) : LoanRepaymentAction

    data class UpdateIsShowPenalties(
        val isShowPenalties: Boolean,
    ) : LoanRepaymentAction

    data class TogglePenalty(
        val penaltyId: Int,
    ) : LoanRepaymentAction

    data class ToggleAllPenalty(
        val selectAllPenalties: Boolean,
    ) : LoanRepaymentAction
}
