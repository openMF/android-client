/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.closeLoanAccount

import org.jetbrains.compose.resources.StringResource

/**
 * UI state for the Close Loan Account screen.
 *
 * @property isTemplateLoading True if the initial template or loan data is being fetched.
 * @property loadError The error resource to display if the initial fetch fails.
 * @property closedOnDateMillis The selected close date in milliseconds, or null if none is picked.
 * @property disbursementDateMillis The loan's disbursement date, used as the minimum selectable date.
 * @property note The user-provided note for the closing transaction.
 * @property showDatePicker True if the date picker dialog should be visible.
 * @property dialogState The state of any modal dialog currently displayed (Submitting, Error, or null).
 */
data class CloseLoanState(
    val isTemplateLoading: Boolean = true,
    val loadError: StringResource? = null,
    val closedOnDateMillis: Long? = null,
    val disbursementDateMillis: Long? = null,
    val note: String = "",
    val showDatePicker: Boolean = false,
    val dialogState: DialogState? = null,
) {
    /**
     * Determines if the Submit button should be enabled.
     */
    val isSubmitEnabled: Boolean
        get() = closedOnDateMillis != null && dialogState !is DialogState.Submitting

    /** Modal dialog state overlaid on top of the form. */
    sealed interface DialogState {
        /** Shown while the close request is in flight. */
        data object Submitting : DialogState

        /**
         * Shown when the close request failed; dismissable so the user can retry.
         *
         * @property message The error message resource to display.
         */
        data class Error(val message: StringResource) : DialogState
    }
}

/** One-shot events emitted by [CloseLoanViewModel] to the screen. */
sealed interface CloseLoanEvent {
    /** Loan was closed successfully — screen should show success feedback and pop. */
    data object CloseSuccess : CloseLoanEvent

    /** Navigate back/cancel without closing the loan. */
    data object NavigateBack : CloseLoanEvent
}

/** User-originated actions handled by [CloseLoanViewModel]. */
sealed interface CloseLoanAction {
    /**
     * User picked a new `Closed On` date in the date picker.
     *
     * @property millis The selected date in milliseconds.
     */
    data class OnDateChange(val millis: Long) : CloseLoanAction

    /**
     * User typed into the note field.
     *
     * @property note The new note content.
     */
    data class OnNoteChange(val note: String) : CloseLoanAction

    /** User tapped the `Closed On` field to open the date picker. */
    data object OnShowDatePicker : CloseLoanAction

    /** User dismissed the date picker without confirming. */
    data object OnHideDatePicker : CloseLoanAction

    /** User tapped the Submit button. */
    data object OnSubmit : CloseLoanAction

    /** User dismissed the error dialog. */
    data object OnDismissError : CloseLoanAction

    /** User tapped retry after the initial template/loan load failed. */
    data object OnRetryLoadTemplate : CloseLoanAction

    /** User tapped back/cancel. */
    data object NavigateBack : CloseLoanAction
}
