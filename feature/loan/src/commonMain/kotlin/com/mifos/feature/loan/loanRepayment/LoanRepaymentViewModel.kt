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

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_repayment
import androidclient.feature.loan.generated.resources.feature_loan_payment_failed
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_profile_failed_to_load_loan
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.math.round
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal class LoanRepaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanRepaymentRepository,
    private val summaryRepository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanRepaymentState, LoanRepaymentEvent, LoanRepaymentAction>(
    initialState = LoanRepaymentState(),
) {

    private val args = savedStateHandle.toRoute<LoanRepaymentScreenRoute>()

    init {
        mutableStateFlow.update {
            it.copy(repaymentDate = Clock.System.now().toEpochMilliseconds())
        }

        if (args.loanAccountNumber.isEmpty()) {
            loadLoanById()
        } else {
            mutableStateFlow.update {
                it.copy(
                    loanAccountNumber = args.loanAccountNumber,
                    loanId = args.loanId,
                    clientName = args.clientName,
                    loanProductName = args.loanProductName,
                    amountInArrears = args.amountInArrears,
                )
            }
            checkDatabaseLoanRepaymentByLoanId()
        }
    }

    override fun handleAction(action: LoanRepaymentAction) {
        when (action) {
            is LoanRepaymentAction.RepaymentDateChanged ->
                mutableStateFlow.update { it.copy(repaymentDate = action.date) }

            is LoanRepaymentAction.PaymentTypeChanged -> {
                val id = state.paymentTypeOptions.getOrNull(action.index)?.id ?: 0
                mutableStateFlow.update {
                    it.copy(paymentType = action.value, paymentTypeId = id)
                }
            }

            is LoanRepaymentAction.TransactionAmountChanged ->
                mutableStateFlow.update { it.copy(transactionAmount = action.value) }

            is LoanRepaymentAction.ShowPaymentDetailsToggled ->
                mutableStateFlow.update { it.copy(showPaymentDetails = action.show) }

            is LoanRepaymentAction.WaivePenaltiesToggled -> {
                mutableStateFlow.update { it.copy(waivePenalties = action.waive) }
                recalculateTransactionAmount()
            }

            is LoanRepaymentAction.AccountNumberChanged ->
                mutableStateFlow.update { it.copy(accountNumber = action.value) }

            is LoanRepaymentAction.ChequeNumberChanged ->
                mutableStateFlow.update { it.copy(chequeNumber = action.value) }

            is LoanRepaymentAction.RoutingCodeChanged ->
                mutableStateFlow.update { it.copy(routingCode = action.value) }

            is LoanRepaymentAction.ReceiptNumberChanged ->
                mutableStateFlow.update { it.copy(receiptNumber = action.value) }

            is LoanRepaymentAction.BankNumberChanged ->
                mutableStateFlow.update { it.copy(bankNumber = action.value) }

            is LoanRepaymentAction.NoteChanged ->
                mutableStateFlow.update { it.copy(note = action.value) }

            LoanRepaymentAction.ShowDatePicker ->
                mutableStateFlow.update { it.copy(showDatePicker = true) }

            LoanRepaymentAction.DismissDatePicker ->
                mutableStateFlow.update { it.copy(showDatePicker = false) }

            LoanRepaymentAction.ReviewPaymentClicked ->
                mutableStateFlow.update { it.copy(showConfirmationSheet = true) }

            LoanRepaymentAction.DismissConfirmationSheet ->
                mutableStateFlow.update { it.copy(showConfirmationSheet = false) }

            LoanRepaymentAction.ConfirmPayment -> submitPayment()

            LoanRepaymentAction.CancelClicked ->
                sendEvent(LoanRepaymentEvent.NavigateBack)

            LoanRepaymentAction.DismissDialog ->
                mutableStateFlow.update { it.copy(dialogState = null) }

            LoanRepaymentAction.RetryClicked -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
                if (state.loanAccountNumber.isEmpty()) {
                    loadLoanById()
                } else if (state.loanRepaymentTemplate == null) {
                    loadLoanRepaymentTemplate()
                } else {
                    checkDatabaseLoanRepaymentByLoanId()
                }
            }

            LoanRepaymentAction.DismissSuccessDialog ->
                sendEvent(LoanRepaymentEvent.NavigateBack)

            LoanRepaymentAction.SyncDialogDismissed ->
                sendEvent(LoanRepaymentEvent.NavigateBack)
        }
    }

    private fun loadLoanById() {
        viewModelScope.launch {
            summaryRepository.getLoanById(args.loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading ->
                        mutableStateFlow.update { it.copy(isLoading = true) }

                    is DataState.Success -> {
                        val loan = dataState.data
                        if (loan == null) {
                            mutableStateFlow.update {
                                it.copy(
                                    isLoading = false,
                                    dialogState = LoanRepaymentState.DialogState.Error(
                                        Res.string.feature_loan_profile_error_details_not_found,
                                    ),
                                )
                            }
                            return@collect
                        }
                        mutableStateFlow.update {
                            it.copy(
                                loanId = loan.id,
                                clientName = loan.clientName,
                                loanProductName = loan.loanProductName,
                                amountInArrears = loan.summary.totalOverdue,
                                loanAccountNumber = loan.accountNo,
                            )
                        }
                        checkDatabaseLoanRepaymentByLoanId()
                    }

                    is DataState.Error ->
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                dialogState = LoanRepaymentState.DialogState.Error(
                                    Res.string.feature_loan_profile_failed_to_load_loan,
                                ),
                            )
                        }
                }
            }
        }
    }

    private fun loadLoanRepaymentTemplate() {
        viewModelScope.launch {
            repository.getLoanRepayTemplate(state.loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading ->
                        mutableStateFlow.update { it.copy(isLoading = true) }

                    is DataState.Success -> {
                        val template = dataState.data
                        if (template == null) {
                            mutableStateFlow.update {
                                it.copy(
                                    isLoading = false,
                                    dialogState = LoanRepaymentState.DialogState.Error(
                                        Res.string.feature_loan_failed_to_load_loan_repayment,
                                    ),
                                )
                            }
                            return@collect
                        }
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                loanRepaymentTemplate = template,
                                transactionAmount = calculateDefaultTransactionAmount(
                                    template = template,
                                    waivePenalties = it.waivePenalties,
                                ),
                            )
                        }
                    }

                    is DataState.Error ->
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                dialogState = LoanRepaymentState.DialogState.Error(
                                    Res.string.feature_loan_failed_to_load_loan_repayment,
                                ),
                            )
                        }
                }
            }
        }
    }

    private fun checkDatabaseLoanRepaymentByLoanId() {
        viewModelScope.launch {
            repository.getDatabaseLoanRepaymentByLoanId(state.loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading ->
                        mutableStateFlow.update { it.copy(isLoading = true) }

                    is DataState.Success -> {
                        if (dataState.data != null) {
                            mutableStateFlow.update {
                                it.copy(
                                    isLoading = false,
                                    dialogState = LoanRepaymentState.DialogState.SyncPreviousTransaction,
                                )
                            }
                        } else {
                            loadLoanRepaymentTemplate()
                        }
                    }

                    is DataState.Error ->
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                dialogState = LoanRepaymentState.DialogState.Error(
                                    Res.string.feature_loan_failed_to_load_loan_repayment,
                                ),
                            )
                        }
                }
            }
        }
    }

    private fun submitPayment() {
        if (state.isLoading) return
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(isLoading = true, showConfirmationSheet = false)
            }

            try {
                val request = LoanRepaymentRequestEntity(
                    accountNumber = state.loanAccountNumber,
                    paymentTypeId = state.paymentTypeId.toString(),
                    dateFormat = DateHelper.SHORT_MONTH,
                    locale = "en",
                    transactionAmount = state.transactionAmount,
                    transactionDate = DateHelper.getDateAsStringFromLong(state.repaymentDate),
                    note = state.note.ifBlank { null },
                    checkNumber = state.chequeNumber.ifBlank { null },
                    routingCode = state.routingCode.ifBlank { null },
                    receiptNumber = state.receiptNumber.ifBlank { null },
                    bankNumber = state.bankNumber.ifBlank { null },
                )
                val response = repository.submitPayment(state.loanId, request)
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        dialogState = LoanRepaymentState.DialogState.PaymentSuccess(response),
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        dialogState = LoanRepaymentState.DialogState.Error(
                            Res.string.feature_loan_payment_failed,
                        ),
                    )
                }
            }
        }
    }

    private fun recalculateTransactionAmount() {
        val template = state.loanRepaymentTemplate ?: return
        mutableStateFlow.update {
            it.copy(
                transactionAmount = calculateDefaultTransactionAmount(
                    template = template,
                    waivePenalties = it.waivePenalties,
                ),
            )
        }
    }

    companion object {
        fun calculateDefaultTransactionAmount(
            template: LoanRepaymentTemplateEntity,
            waivePenalties: Boolean,
        ): String {
            val principal = template.principalPortion ?: 0.0
            val interest = template.interestPortion ?: 0.0
            val fees = template.feeChargesPortion ?: 0.0
            val penalties = if (waivePenalties) 0.0 else (template.penaltyChargesPortion ?: 0.0)
            val total = round((principal + interest + fees + penalties) * 100) / 100.0
            return if (total == 0.0) "" else total.toString()
        }

        fun formatCurrency(amount: Double?, code: String?, decimalPlaces: Int?): String {
            return CurrencyFormatter.format(
                balance = amount,
                currencyCode = code,
                maximumFractionDigits = decimalPlaces ?: 2,
            )
        }
    }
}

@Immutable
internal data class LoanRepaymentState(
    val clientName: String = "",
    val loanId: Int = 0,
    val loanAccountNumber: String = "",
    val loanProductName: String = "",
    val amountInArrears: Double? = 0.0,
    val loanRepaymentTemplate: LoanRepaymentTemplateEntity? = null,
    val repaymentDate: Long = 0L,
    val paymentType: String = "",
    val paymentTypeId: Int = 0,
    val transactionAmount: String = "",
    val showPaymentDetails: Boolean = false,
    val waivePenalties: Boolean = false,
    val accountNumber: String = "",
    val chequeNumber: String = "",
    val routingCode: String = "",
    val receiptNumber: String = "",
    val bankNumber: String = "",
    val note: String = "",
    val isLoading: Boolean = true,
    val showDatePicker: Boolean = false,
    val showConfirmationSheet: Boolean = false,
    val dialogState: DialogState? = null,
) {
    val currencyCode: String? get() = loanRepaymentTemplate?.currency?.code
    val decimalPlaces: Int? get() = loanRepaymentTemplate?.currency?.decimalPlaces
    val principalPortion: Double? get() = loanRepaymentTemplate?.principalPortion
    val interestPortion: Double? get() = loanRepaymentTemplate?.interestPortion
    val feeChargesPortion: Double? get() = loanRepaymentTemplate?.feeChargesPortion
    val penaltyChargesPortion: Double?
        get() = if (waivePenalties) 0.0 else loanRepaymentTemplate?.penaltyChargesPortion
    val paymentTypeOptions: List<PaymentTypeOptionEntity>
        get() = loanRepaymentTemplate?.paymentTypeOptions ?: emptyList()

    val isFormValid: Boolean
        get() = transactionAmount.toDoubleOrNull()?.let { it > 0 } == true &&
            paymentType.isNotBlank()

    sealed interface DialogState {
        data class Error(val message: StringResource) : DialogState
        data object SyncPreviousTransaction : DialogState
        data class PaymentSuccess(val response: LoanRepaymentResponseEntity) : DialogState
    }
}

internal sealed interface LoanRepaymentEvent {
    data object NavigateBack : LoanRepaymentEvent
}

internal sealed interface LoanRepaymentAction {
    data class RepaymentDateChanged(val date: Long) : LoanRepaymentAction
    data class PaymentTypeChanged(val index: Int, val value: String) : LoanRepaymentAction
    data class TransactionAmountChanged(val value: String) : LoanRepaymentAction
    data class ShowPaymentDetailsToggled(val show: Boolean) : LoanRepaymentAction
    data class WaivePenaltiesToggled(val waive: Boolean) : LoanRepaymentAction
    data class AccountNumberChanged(val value: String) : LoanRepaymentAction
    data class ChequeNumberChanged(val value: String) : LoanRepaymentAction
    data class RoutingCodeChanged(val value: String) : LoanRepaymentAction
    data class ReceiptNumberChanged(val value: String) : LoanRepaymentAction
    data class BankNumberChanged(val value: String) : LoanRepaymentAction
    data class NoteChanged(val value: String) : LoanRepaymentAction
    data object ShowDatePicker : LoanRepaymentAction
    data object DismissDatePicker : LoanRepaymentAction
    data object ReviewPaymentClicked : LoanRepaymentAction
    data object DismissConfirmationSheet : LoanRepaymentAction
    data object ConfirmPayment : LoanRepaymentAction
    data object CancelClicked : LoanRepaymentAction
    data object DismissDialog : LoanRepaymentAction
    data object RetryClicked : LoanRepaymentAction
    data object DismissSuccessDialog : LoanRepaymentAction
    data object SyncDialogDismissed : LoanRepaymentAction
}
