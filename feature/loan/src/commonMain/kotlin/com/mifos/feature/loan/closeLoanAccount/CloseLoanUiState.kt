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
 * Represents the UI state for the Close Loan Account screen.
 *
 * @property isTemplateLoading Whether the screen is currently fetching the close template or loan details.
 * @property loadError The error message resource if the initial data load fails.
 * @property closedOnDateMillis The selected closure date in milliseconds. Starts as null.
 * @property disbursementDateMillis The loan's actual disbursement date, used as the minimum selectable date.
 * @property note The user-provided note for the loan closure.
 * @property showDatePicker Whether the date picker dialog is currently visible.
 * @property dialogState The state of the overlay dialog (Submitting, Error, or null).
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

    /**
     * Represents the state of the overlay modal dialog.
     */
    sealed interface DialogState {
        /**
         * State indicating that the close request is being submitted to the server.
         */
        data object Submitting : DialogState

        /**
         * State indicating that the submission failed.
         *
         * @property message The error message to display in the dialog.
         */
        data class Error(val message: StringResource) : DialogState
    }
}

/**
 * Events sent from the ViewModel to the UI for one-time actions.
 */
sealed interface CloseLoanEvent {
    /**
     * Triggered when the loan account is closed successfully.
     */
    data object CloseSuccess : CloseLoanEvent

    /**
     * Triggered when the user chooses to navigate back without closing.
     */
    data object NavigateBack : CloseLoanEvent
}

/**
 * Actions originating from the UI to be handled by the ViewModel.
 */
sealed interface CloseLoanAction {
    /**
     * Dispatched when the user selects a date in the date picker.
     * @property millis The selected timestamp.
     */
    data class OnDateChange(val millis: Long) : CloseLoanAction

    /**
     * Dispatched when the user updates the note text field.
     * @property note The new note content.
     */
    data class OnNoteChange(val note: String) : CloseLoanAction

    /**
     * Dispatched to show the date picker dialog.
     */
    data object OnShowDatePicker : CloseLoanAction

    /**
     * Dispatched to hide the date picker dialog.
     */
    data object OnHideDatePicker : CloseLoanAction

    /**
     * Dispatched when the user taps the Submit button.
     */
    data object OnSubmit : CloseLoanAction

    /**
     * Dispatched to dismiss the error dialog.
     */
    data object OnDismissError : CloseLoanAction

    /**
     * Dispatched to retry loading the template data.
     */
    data object OnRetryLoadTemplate : CloseLoanAction

    /**
     * Dispatched to navigate back.
     */
    data object NavigateBack : CloseLoanAction
}
