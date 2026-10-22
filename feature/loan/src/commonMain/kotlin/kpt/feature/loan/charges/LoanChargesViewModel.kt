/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.loan.charges

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.emptyIfContent
import kpt.core.base.store.screen.mapContent
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.common.utils.CurrencyFormatter
import kpt.core.common.utils.DateHelper
import kpt.core.data.loan.LoanRepository
import kpt.core.database.charge.entity.ChargesEntity

/**
 * The charges levied on one loan — ported from
 * `6b66e8a43:feature/loan/loanCharge/{LoanChargeViewModel,Screen}.kt`.
 *
 * Reached from the loan SUMMARY's overflow menu and from the loan PROFILE's "Charges" row; both
 * were gated off until this screen landed.
 *
 * ## FIXED: the pull-to-refresh spinner could never appear
 *
 * ```kotlin
 * fun refreshLoanChargeList() {
 *     _isRefreshing.value = true
 *     loadLoanChargesList()      // launches a coroutine and returns immediately
 *     _isRefreshing.value = false
 * }
 * ```
 *
 * `loadLoanChargesList` returns a `Job`, so the flag went true and false within one frame and the
 * refresh indicator never rendered. The stream owns its own refreshing signal, which
 * `ScreenContent` renders — so there is no flag to get wrong.
 *
 * ## The row values are projected and FORMATTED here
 *
 * The original rendered `charges.amount.toString()` and `charges.chargeId.toString()` on nullable
 * fields — the literal text "null" on any charge Fineract sent without them — and an unformatted
 * raw double for money. They are formatted once, where the currency is in hand.
 */
internal class LoanChargesViewModel(
    repository: LoanRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<Unit, LoanChargesEvent, LoanChargesAction>(Unit) {

    private val loanId: Int = savedStateHandle.get<Int>(LOAN_ID_KEY) ?: -1

    private val stream = repository.loanChargesStream(loanId, viewModelScope)

    val charges: StateFlow<ScreenState<List<LoanChargeRow>>> = stream.state
        .mapContent { charges, _ -> charges.map { it.toRow() } }
        // A loan with no charges is EMPTY, not a list with no rows. The original checked
        // `loanCharges.isEmpty()` in the screen body; this moves the decision to where the list is
        // produced so the screen has one branch.
        .emptyIfContent { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    override fun handleAction(action: LoanChargesAction) {
        when (action) {
            LoanChargesAction.NavigateBack -> sendEvent(LoanChargesEvent.NavigateBack)
            LoanChargesAction.OnRetry -> stream.retry()
            LoanChargesAction.OnAddCharge -> sendEvent(LoanChargesEvent.AddCharge(loanId))
        }
    }

    private companion object {
        const val LOAN_ID_KEY = "loanId"
        const val STATE_TIMEOUT_MS = 5_000L
    }
}

/**
 * One charge row, fully formatted — the screen renders strings and never reaches for the entity.
 *
 * `id` is the row key. `chargeId` is Fineract's CHARGE-DEFINITION id (which charge type this is)
 * and `id` is this levied instance — the original displayed `chargeId` under the label "Charge Id",
 * which is the definition's id and so repeats across every loan carrying the same charge. Kept as
 * displayed, because the label names it and an officer comparing it against the charge catalogue
 * wants exactly that; the distinct row key is [id].
 */
internal data class LoanChargeRow(
    val id: Int,
    val chargeId: String,
    val name: String,
    val amount: String,
    val dueDate: String,
)

private fun ChargesEntity.toRow(): LoanChargeRow = LoanChargeRow(
    id = id,
    chargeId = chargeId?.toString() ?: PLACEHOLDER,
    name = name.orEmpty(),
    // Formatted with the charge's OWN currency, which the original ignored entirely — it printed
    // `amount.toString()`, so a 1500.0 charge read "1500.0" with no symbol and no grouping.
    amount = amount?.let {
        CurrencyFormatter.format(it, currency?.code, currency?.decimalPlaces)
    } ?: PLACEHOLDER,
    dueDate = formattedDueDateOrDash(),
)

/**
 * The due date, in the app's display format.
 *
 * `ChargesEntity.formattedDueDate` builds `"${dueDate[0]}-${dueDate[1]}-${dueDate[2]}"` — so a
 * March charge reads **"2026-3-14"**: year-first, unpadded, and unlike every other date the app
 * shows. `DateHelper.getDateAsString` is what the rest of the port uses ("14 Mar 2026"). The
 * entity's property is left alone (it is a `core/database` concern with other callers) and the
 * formatting happens here. Its "No Due Date" fallback is also a hardcoded English literal in
 * `core/database`; this uses a dash and lets the screen label it.
 *
 * The size guard is still needed even though `dueDate` is `List<Int>?` rather than a list OF
 * nullables (unlike `LoanTimeline.actualDisbursementDate`): `getDateAsString` indexes `[2]`
 * directly, so a short list from a tenant sending a partial date would throw.
 */
private fun ChargesEntity.formattedDueDateOrDash(): String =
    dueDate?.takeIf { it.size >= DATE_PARTS }
        ?.let { DateHelper.getDateAsString(it) }
        ?: PLACEHOLDER

private const val PLACEHOLDER = "—"
private const val DATE_PARTS = 3

internal sealed interface LoanChargesEvent {
    data object NavigateBack : LoanChargesEvent
    data class AddCharge(val loanId: Int) : LoanChargesEvent
}

internal sealed interface LoanChargesAction {
    data object NavigateBack : LoanChargesAction
    data object OnRetry : LoanChargesAction
    data object OnAddCharge : LoanChargesAction
}
