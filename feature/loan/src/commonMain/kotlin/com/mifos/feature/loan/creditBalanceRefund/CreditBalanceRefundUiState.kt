/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.creditBalanceRefund

import com.mifos.core.model.objects.loan.CreditBalanceRefundInput

/**
 * The single source of truth for the Credit Balance Refund UI.
 * All display data lives here; [dialogState] drives overlay dialogs (loading/error/success).
 * The refund form is always present in the composable hierarchy — overlays appear on top of it.
 */
data class CreditBalanceRefundState(
    val dialogState: DialogState? = null,
    val loanId: Int = 0,
    val clientName: String? = null,
    val loanAccountNumber: String = "",
    val overpaidAmount: Double = 0.0,
    val currencyCode: String? = null,
    val decimalPlaces: Int? = null,
    val networkAvailable: Boolean = true,
) {
    /**
     * Represents the current overlay state shown on top of the refund form.
     * Null means no overlay — the form is interactive.
     */
    sealed interface DialogState {
        data object Loading : DialogState

        /** An error occurred. [message] comes from API, [messageRes] comes from local string resources. */
        data class Error(
            val message: String? = null,
            val messageRes: org.jetbrains.compose.resources.StringResource? = null,
        ) : DialogState

        data class Success(val transactionId: String) : DialogState
    }
}

/**
 * All user intentions and system-triggered actions that the ViewModel can handle.
 * The screen calls [CreditBalanceRefundViewModel.trySendAction] to dispatch these.
 */
sealed interface CreditBalanceRefundAction {
    data object NavigateBack : CreditBalanceRefundAction

    data object OnRetry : CreditBalanceRefundAction

    data object OnDismissDialog : CreditBalanceRefundAction

    data class OnSubmitRefund(val input: CreditBalanceRefundInput) : CreditBalanceRefundAction
}

/**
 * One-shot navigation events emitted by the ViewModel.
 * The Screen collects these and navigates without persisting state.
 */
sealed interface CreditBalanceRefundEvent {
    data object NavigateBack : CreditBalanceRefundEvent
    data object NavigateBackWithRefresh : CreditBalanceRefundEvent
}
