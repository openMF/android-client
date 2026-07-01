/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountAction.payments

import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.loan.loanAccountAction.LoanAccountActionItem
import kotlinx.coroutines.flow.update
internal class LoanPaymentsActionViewModel() : BaseViewModel<LoanPaymentsActionState, LoanPaymentsActionEvent, LoanPaymentsAction>(
    initialState = LoanPaymentsActionState(),
) {
    init {
        mutableStateFlow.update {
            it.copy(
                actions = listOf(
                    LoanAccountActionItem.GoodwillCredit,
                    LoanAccountActionItem.InterestPaymentWaiver,
                    LoanAccountActionItem.PaymentRefund,
                    LoanAccountActionItem.MerchantIssuedRefund,
                ),
            )
        }
    }

    override fun handleAction(action: LoanPaymentsAction) {
        when (action) {
            LoanPaymentsAction.NavigateBack -> sendEvent(LoanPaymentsActionEvent.NavigateBack)
            is LoanPaymentsAction.OnActionClickLoan -> {
                when (action.action) {
                    LoanAccountActionItem.GoodwillCredit -> sendEvent(LoanPaymentsActionEvent.NavigateToGoodwillCredit)
                    LoanAccountActionItem.InterestPaymentWaiver -> sendEvent(LoanPaymentsActionEvent.NavigateToInterestPaymentWaiver)
                    LoanAccountActionItem.PaymentRefund -> sendEvent(LoanPaymentsActionEvent.NavigateToPaymentRefund)
                    LoanAccountActionItem.MerchantIssuedRefund -> sendEvent(LoanPaymentsActionEvent.NavigateToMerchantIssuedRefund)
                    else -> { }
                }
            }
        }
    }
}

data class LoanPaymentsActionState(
    val actions: List<LoanAccountActionItem> = emptyList(),
)

sealed interface LoanPaymentsActionEvent {
    data object NavigateBack : LoanPaymentsActionEvent
    data object NavigateToGoodwillCredit : LoanPaymentsActionEvent
    data object NavigateToInterestPaymentWaiver : LoanPaymentsActionEvent
    data object NavigateToPaymentRefund : LoanPaymentsActionEvent
    data object NavigateToMerchantIssuedRefund : LoanPaymentsActionEvent
}

sealed interface LoanPaymentsAction {
    data object NavigateBack : LoanPaymentsAction
    data class OnActionClickLoan(val action: LoanAccountActionItem) : LoanPaymentsAction
}
