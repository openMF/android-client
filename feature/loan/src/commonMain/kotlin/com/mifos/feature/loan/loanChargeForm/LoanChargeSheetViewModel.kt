/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanChargeForm

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_charge_disbursement
import androidclient.feature.loan.generated.resources.feature_loan_charge_flat
import androidclient.feature.loan.generated.resources.feature_loan_charge_installment
import androidclient.feature.loan.generated.resources.feature_loan_charge_specified_due_date
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.domain.useCases.CreateLoanChargesUseCase
import com.mifos.core.domain.useCases.GetChargeTemplateUseCase
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class LoanChargeSheetViewModel(
    private val getChargesUseCase: GetChargeTemplateUseCase,
    private val createLoanChargesUseCase: CreateLoanChargesUseCase,
) : BaseViewModel<LoanChargeFormState, LoanChargeFormEvent, LoanChargeFormAction>(
    initialState = LoanChargeFormState(),
) {

    override fun handleAction(action: LoanChargeFormAction) {
        when (action) {
            is LoanChargeFormAction.LoadCharges -> loadCharges(action.loanId)
            is LoanChargeFormAction.ChargeSelected -> selectCharge(action.index)
            is LoanChargeFormAction.AmountChanged -> updateAmount(action.amount)
            is LoanChargeFormAction.DueDateChanged -> updateDueDate(action.dueDateMillis)
            is LoanChargeFormAction.Submit -> createLoanCharges(action.loanId)
            LoanChargeFormAction.ShowDatePicker -> mutableStateFlow.update { it.copy(showDatePicker = true) }
            LoanChargeFormAction.HideDatePicker -> mutableStateFlow.update { it.copy(showDatePicker = false) }
            LoanChargeFormAction.Reset -> resetState()
        }
    }

    private fun loadCharges(loanId: Int) {
        if (stateFlow.value.chargeTypesState is LoanChargeFormState.ChargeTypesState.Success) return

        viewModelScope.launch {
            getChargesUseCase("loans", loanId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        sendEvent(LoanChargeFormEvent.FailedToLoadChargeTypes)
                        mutableStateFlow.update {
                            it.copy(
                                chargeTypesState = LoanChargeFormState.ChargeTypesState.Error,
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                chargeTypesState = LoanChargeFormState.ChargeTypesState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        val options = result.data.chargeOptions

                        val validCharges = options.mapNotNull { option ->
                            val id = option.id
                            val name = option.name
                            val amount = option.amount
                            val chargeTimeType =
                                ChargeTimeType.fromValue(option.chargeTimeType?.value)
                            val chargeCalculationType =
                                ChargeCalculationType.fromValue(option.chargeCalculationType?.value)

                            if (id != null && name != null && amount != null && chargeTimeType != null && chargeCalculationType != null) {
                                ChargeTypes(
                                    id = id,
                                    name = name,
                                    amount = amount,
                                    chargeTimeType = chargeTimeType,
                                    chargeCalculationType = chargeCalculationType,
                                )
                            } else {
                                null
                            }
                        }

                        mutableStateFlow.update {
                            it.copy(
                                chargeTypesState = LoanChargeFormState.ChargeTypesState.Success(
                                    validCharges,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun selectCharge(index: Int) {
        val chargeState = stateFlow.value.chargeTypesState

        if (chargeState is LoanChargeFormState.ChargeTypesState.Success) {
            val charges = chargeState.chargeTypes

            if (index in charges.indices) {
                val selectedCharge = charges[index]
                mutableStateFlow.update {
                    it.copy(
                        selectedChargeId = selectedCharge.id,
                        name = selectedCharge.name,
                        chargeTime = selectedCharge.chargeTimeType,
                        chargeCalculation = selectedCharge.chargeCalculationType,
                        amount = selectedCharge.amount.toString(),
                        amountError = false,
                    )
                }
            }
        }
    }

    private fun updateAmount(amount: String) {
        mutableStateFlow.update {
            it.copy(
                amount = amount,
                amountError = amount.toDoubleOrNull() == null,
            )
        }
    }

    private fun updateDueDate(dueDateMillis: Long) {
        mutableStateFlow.update { it.copy(dueDate = dueDateMillis) }
    }

    private fun createLoanCharges(loanId: Int) {
        val currentState = mutableStateFlow.value
        if (currentState.selectedChargeId == null || currentState.isCreatingCharge) return

        val payload = ChargesPayload().apply {
            this.amount = currentState.amount
            this.dateFormat = "dd-MM-yyyy"
            this.chargeId = currentState.selectedChargeId
            this.locale = "en"
            if (currentState.chargeTime == ChargeTimeType.SPECIFIED_DUE_DATE) {
                this.dueDate = DateHelper.getDateAsStringFromLong(currentState.dueDate)
            }
        }

        viewModelScope.launch {
            createLoanChargesUseCase("loans", loanId, payload).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        sendEvent(LoanChargeFormEvent.ChargeCreationFailed)
                        mutableStateFlow.update { it.copy(isCreatingCharge = false) }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update { it.copy(isCreatingCharge = true) }
                    }

                    is DataState.Success -> {
                        sendEvent(LoanChargeFormEvent.ChargeCreationSuccess)
                        mutableStateFlow.update { it.copy(isCreatingCharge = false) }
                    }
                }
            }
        }
    }

    private fun resetState() {
        mutableStateFlow.update { currentState ->
            LoanChargeFormState(
                chargeTypesState = currentState.chargeTypesState as? LoanChargeFormState.ChargeTypesState.Success
                    ?: LoanChargeFormState.ChargeTypesState.Loading,
            )
        }
    }
}

data class ChargeTypes(
    val id: Int,
    val name: String,
    val chargeTimeType: ChargeTimeType,
    val chargeCalculationType: ChargeCalculationType,
    val amount: Double,
)

enum class ChargeTimeType(
    val value: String,
    val labelRes: StringResource,
) {
    DISBURSEMENT("Disbursement", Res.string.feature_loan_charge_disbursement),
    INSTALLMENT_FEE("Installment Fee", Res.string.feature_loan_charge_installment),
    SPECIFIED_DUE_DATE("Specified due date", Res.string.feature_loan_charge_specified_due_date),
    ;

    companion object {
        fun fromValue(value: String?) = entries.find { it.value.equals(value, true) }
    }
}

enum class ChargeCalculationType(
    val value: String,
    val labelRes: StringResource,
) {
    FLAT("Flat", Res.string.feature_loan_charge_flat),
    ;

    companion object {
        fun fromValue(value: String?) = entries.find { it.value.equals(value, true) }
    }
}

data class LoanChargeFormState
@OptIn(ExperimentalTime::class)
constructor(
    val selectedChargeId: Int? = null,
    val name: String = "",
    val amount: String = "",
    val amountError: Boolean = false,
    val chargeTime: ChargeTimeType? = null,
    val chargeCalculation: ChargeCalculationType? = null,
    val dueDate: Long = Clock.System.now().toEpochMilliseconds(),
    val showDatePicker: Boolean = false,
    val chargeTypesState: ChargeTypesState = ChargeTypesState.Loading,
    val isCreatingCharge: Boolean = false,
) {
    sealed interface ChargeTypesState {
        data object Loading : ChargeTypesState
        data object Error : ChargeTypesState
        data class Success(
            val chargeTypes: List<ChargeTypes> = emptyList(),
        ) : ChargeTypesState
    }
}

sealed interface LoanChargeFormAction {
    data class LoadCharges(val loanId: Int) : LoanChargeFormAction
    data class ChargeSelected(val index: Int) : LoanChargeFormAction
    data class AmountChanged(val amount: String) : LoanChargeFormAction
    data class DueDateChanged(val dueDateMillis: Long) : LoanChargeFormAction
    data class Submit(val loanId: Int) : LoanChargeFormAction
    data object ShowDatePicker : LoanChargeFormAction
    data object HideDatePicker : LoanChargeFormAction
    data object Reset : LoanChargeFormAction
}

sealed interface LoanChargeFormEvent {
    data object FailedToLoadChargeTypes : LoanChargeFormEvent
    data object ChargeCreationFailed : LoanChargeFormEvent
    data object ChargeCreationSuccess : LoanChargeFormEvent
}
