/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.loan.chargeform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.launch
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.common.utils.DateHelper
import kpt.core.data.charge.ChargeCommandRepository
import kpt.core.data.loan.LoanRepository
import kpt.core.model.objects.payloads.ChargesPayload
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Levy a charge on a loan — ported from
 * `6b66e8a43:feature/loan/loanChargeForm/{LoanChargeSheetViewModel,LoanChargeFormSheet}.kt`.
 *
 * This is the charges list's only action, and the reason that screen's "+" was not drawn until now.
 *
 * ## A screen, not a sheet-inside-a-screen
 *
 * The original rendered the form as a `ModalBottomSheet` composed INSIDE the charges list, driven
 * by a `showLoanChargeForm` boolean the list owned — and the list reported the outcome by firing
 * snackbars from the sheet's `onSuccess`/`onError` callbacks. So the list owned the form's
 * visibility, the form owned its own state, and the outcome travelled back up through two
 * lambdas.
 *
 * It is its own destination here, which is what every other write in this feature is. The list
 * refreshes when the officer comes back, because its stream re-reads — no callback, no boolean, no
 * state shared across a sheet boundary.
 *
 * ## The template read decides what is SELECTABLE, and drops what cannot be sent
 *
 * `chargeOptions` is filtered to entries with an id, a name, an amount, and both a recognised
 * charge-time and charge-calculation type — the original's `mapNotNull`, kept, because a charge
 * missing any of those cannot be submitted. An unrecognised `chargeTimeType` is the common case:
 * Fineract has many more than the three this screen knows.
 */
internal class LoanChargeFormViewModel(
    private val repository: LoanRepository,
    private val commands: ChargeCommandRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoanChargeFormState, LoanChargeFormEvent, LoanChargeFormAction>(
    LoanChargeFormState(loanId = savedStateHandle.get<Int>(LOAN_ID_KEY) ?: -1),
) {

    init {
        loadChargeTypes()
    }

    override fun handleAction(action: LoanChargeFormAction) {
        when (action) {
            LoanChargeFormAction.NavigateBack -> sendEvent(LoanChargeFormEvent.NavigateBack)
            LoanChargeFormAction.OnRetry -> loadChargeTypes()

            is LoanChargeFormAction.OnChargeSelected -> mutableStateFlow.value = state.copy(
                selectedCharge = action.charge,
                // The chosen charge's DEFAULT amount, which the officer may then edit — the
                // original did the same.
                amount = action.charge.amount.toString(),
                amountError = false,
            )

            is LoanChargeFormAction.OnAmountChanged ->
                mutableStateFlow.value = state.copy(amount = action.amount, amountError = false)

            is LoanChargeFormAction.OnDueDateChanged ->
                mutableStateFlow.value = state.copy(dueDate = action.millis)

            is LoanChargeFormAction.UpdateDatePicker ->
                mutableStateFlow.value = state.copy(showDatePicker = action.status)

            LoanChargeFormAction.CloseDialog ->
                mutableStateFlow.value = state.copy(dialogState = null)

            LoanChargeFormAction.OnSubmit -> submit()
        }
    }

    private fun loadChargeTypes() {
        mutableStateFlow.value = state.copy(isLoadingTypes = true, typesError = false)

        viewModelScope.launch {
            val template = repository.loanChargeTemplate(state.loanId)

            mutableStateFlow.value = if (template == null) {
                state.copy(isLoadingTypes = false, typesError = true)
            } else {
                state.copy(
                    isLoadingTypes = false,
                    typesError = false,
                    chargeTypes = template.chargeOptions.mapNotNull { option ->
                        LoanChargeType(
                            id = option.id ?: return@mapNotNull null,
                            name = option.name ?: return@mapNotNull null,
                            amount = option.amount ?: return@mapNotNull null,
                            time = ChargeTimeKind.fromValue(option.chargeTimeType?.value)
                                ?: return@mapNotNull null,
                            calculation = ChargeCalculationKind
                                .fromValue(option.chargeCalculationType?.value)
                                ?: return@mapNotNull null,
                        )
                    },
                )
            }
        }
    }

    private fun submit() {
        if (state.isSubmitting) return
        val charge = state.selectedCharge
        if (charge == null) {
            mutableStateFlow.value = state.copy(chargeError = true)
            return
        }
        // The original validated the amount only as a side effect of TYPING
        // (`amountError = amount.toDoubleOrNull() == null` in `updateAmount`) and never re-checked
        // it at submit — so a charge whose default amount was somehow unparseable, or a field left
        // exactly as prefilled, could be submitted unvalidated.
        val amount = state.amount.trim().toDoubleOrNull()
        if (amount == null || amount <= 0) {
            mutableStateFlow.value = state.copy(amountError = true)
            return
        }

        mutableStateFlow.value = state.copy(isSubmitting = true, chargeError = false, dialogState = null)

        viewModelScope.launch {
            val payload = ChargesPayload(
                chargeId = charge.id,
                loanId = state.loanId,
                amount = state.amount.trim(),
                // `getDateAsStringFromLong` emits "dd-MM-yyyy" and this declares the same — one of
                // the few payloads in the original where the date and its format agreed. Kept
                // verbatim rather than moved to `ApiDateFormatter`, because Fineract's charge
                // endpoint is what defines this contract and the pairing here is already correct.
                dateFormat = DateHelper.SHORT_MONTH,
                locale = LOCALE,
                // ONLY for a specified-due-date charge. A disbursement or installment-fee charge
                // has no due date of its own, and sending one makes Fineract reject the whole
                // request — the original's conditional, kept.
                dueDate = DateHelper.getDateAsStringFromLong(state.dueDate)
                    .takeIf { charge.time == ChargeTimeKind.SPECIFIED_DUE_DATE },
            )

            when (val result = commands.createCharges(LOAN_RESOURCE, state.loanId, payload)) {
                is MutationResult.Applied -> mutableStateFlow.value = state.copy(
                    isSubmitting = false,
                    dialogState = LoanChargeFormState.DialogState.Submitted,
                )

                is MutationResult.Blocked -> mutableStateFlow.value = state.copy(
                    isSubmitting = false,
                    dialogState = LoanChargeFormState.DialogState.Failed(
                        if (result.reason == BlockReason.OFFLINE) OFFLINE_MSG else FAILED_MSG,
                    ),
                )

                is MutationResult.Failed -> {
                    Logger.e(tag = TAG, throwable = result.cause) { "create loan charge failed" }
                    mutableStateFlow.value = state.copy(
                        isSubmitting = false,
                        dialogState = LoanChargeFormState.DialogState.Failed(
                            result.cause.message ?: FAILED_MSG,
                        ),
                    )
                }

                is MutationResult.Conflicted -> mutableStateFlow.value = state.copy(
                    isSubmitting = false,
                    dialogState = LoanChargeFormState.DialogState.Failed(FAILED_MSG),
                )
            }
        }
    }

    private companion object {
        const val TAG = "LoanChargeForm"
        const val LOAN_ID_KEY = "loanId"
        const val LOAN_RESOURCE = "loans"
        const val LOCALE = "en"

        const val OFFLINE_MSG = "No connection — the charge was not added."
        const val FAILED_MSG = "Could not add this charge."
    }
}

/** A selectable charge type, with everything the payload and the form both need. */
internal data class LoanChargeType(
    val id: Int,
    val name: String,
    val amount: Double,
    val time: ChargeTimeKind,
    val calculation: ChargeCalculationKind,
)

/**
 * The three charge-time kinds this screen handles.
 *
 * Fineract has many more (overdue installment, tranche disbursement, …); an option whose
 * `chargeTimeType` is none of these is DROPPED rather than offered, because the form has no way to
 * ask for whatever that kind needs. The original did the same.
 *
 * Named `…Kind` to avoid colliding with `core/model`'s `ChargeTimeType`, which is the wire shape.
 */
internal enum class ChargeTimeKind(val wireValue: String) {
    DISBURSEMENT("Disbursement"),
    INSTALLMENT_FEE("Installment Fee"),
    SPECIFIED_DUE_DATE("Specified due date"),
    ;

    companion object {
        fun fromValue(value: String?) = entries.find { it.wireValue.equals(value, ignoreCase = true) }
    }
}

/** Only flat charges are handled; a percentage charge needs a base the form does not collect. */
internal enum class ChargeCalculationKind(val wireValue: String) {
    FLAT("Flat"),
    ;

    companion object {
        fun fromValue(value: String?) = entries.find { it.wireValue.equals(value, ignoreCase = true) }
    }
}

@OptIn(ExperimentalTime::class)
internal data class LoanChargeFormState(
    val loanId: Int = -1,
    val chargeTypes: List<LoanChargeType> = emptyList(),
    val selectedCharge: LoanChargeType? = null,
    val amount: String = "",
    val dueDate: Long = Clock.System.now().toEpochMilliseconds(),
    val showDatePicker: Boolean = false,
    val isLoadingTypes: Boolean = false,
    val typesError: Boolean = false,
    val chargeError: Boolean = false,
    val amountError: Boolean = false,
    val isSubmitting: Boolean = false,
    val dialogState: DialogState? = null,
) {
    /** A due date is collected only for the one charge kind that has one. */
    val needsDueDate: Boolean get() = selectedCharge?.time == ChargeTimeKind.SPECIFIED_DUE_DATE

    sealed interface DialogState {
        data object Submitted : DialogState
        data class Failed(val msg: String) : DialogState
    }
}

internal sealed interface LoanChargeFormEvent {
    data object NavigateBack : LoanChargeFormEvent
}

internal sealed interface LoanChargeFormAction {
    data object NavigateBack : LoanChargeFormAction
    data object OnRetry : LoanChargeFormAction
    data object OnSubmit : LoanChargeFormAction
    data object CloseDialog : LoanChargeFormAction
    data class OnChargeSelected(val charge: LoanChargeType) : LoanChargeFormAction
    data class OnAmountChanged(val amount: String) : LoanChargeFormAction
    data class OnDueDateChanged(val millis: Long) : LoanChargeFormAction
    data class UpdateDatePicker(val status: Boolean) : LoanChargeFormAction
}
