/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountAction

import androidclient.core.ui.generated.resources.add_collateral
import androidclient.core.ui.generated.resources.add_interest_pause
import androidclient.core.ui.generated.resources.add_loan_charge
import androidclient.core.ui.generated.resources.approve
import androidclient.core.ui.generated.resources.assign_loan_officer
import androidclient.core.ui.generated.resources.buy_down_fee
import androidclient.core.ui.generated.resources.capitalized_income
import androidclient.core.ui.generated.resources.change_loan_officer
import androidclient.core.ui.generated.resources.charge_off
import androidclient.core.ui.generated.resources.close
import androidclient.core.ui.generated.resources.close_as_rescheduled
import androidclient.core.ui.generated.resources.contract_termination
import androidclient.core.ui.generated.resources.create_guarantors
import androidclient.core.ui.generated.resources.credit_balance_refund
import androidclient.core.ui.generated.resources.delete
import androidclient.core.ui.generated.resources.disburse
import androidclient.core.ui.generated.resources.disburse_to_savings
import androidclient.core.ui.generated.resources.edit_repayment_schedule
import androidclient.core.ui.generated.resources.foreclosure
import androidclient.core.ui.generated.resources.goodwill_credit
import androidclient.core.ui.generated.resources.interest_payment_wavier
import androidclient.core.ui.generated.resources.loan_screen_report
import androidclient.core.ui.generated.resources.make_repayment
import androidclient.core.ui.generated.resources.merchant_issued_refund
import androidclient.core.ui.generated.resources.modify_application
import androidclient.core.ui.generated.resources.payment_refund
import androidclient.core.ui.generated.resources.payments
import androidclient.core.ui.generated.resources.prepay_loan
import androidclient.core.ui.generated.resources.re_age
import androidclient.core.ui.generated.resources.re_anortize
import androidclient.core.ui.generated.resources.recover_from_guarantor
import androidclient.core.ui.generated.resources.recovery_payment
import androidclient.core.ui.generated.resources.reject
import androidclient.core.ui.generated.resources.reschedule
import androidclient.core.ui.generated.resources.sell_loan
import androidclient.core.ui.generated.resources.transfer_funds
import androidclient.core.ui.generated.resources.undo_approval
import androidclient.core.ui.generated.resources.undo_charge_off
import androidclient.core.ui.generated.resources.undo_disbursal
import androidclient.core.ui.generated.resources.undo_re_age
import androidclient.core.ui.generated.resources.undo_re_anortize
import androidclient.core.ui.generated.resources.undo_write_off
import androidclient.core.ui.generated.resources.view_guarantors
import androidclient.core.ui.generated.resources.withdrawn_by_client
import androidclient.core.ui.generated.resources.write_off
import androidclient.feature.loan.generated.resources.feature_loan_action_add_charge
import androidclient.feature.loan.generated.resources.feature_loan_action_add_charge_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_add_collateral
import androidclient.feature.loan.generated.resources.feature_loan_action_add_collateral_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_add_interest_pause
import androidclient.feature.loan.generated.resources.feature_loan_action_add_interest_pause_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_add_prepay_loan
import androidclient.feature.loan.generated.resources.feature_loan_action_add_prepay_loan_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_approve
import androidclient.feature.loan.generated.resources.feature_loan_action_approve_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_assign_officer
import androidclient.feature.loan.generated.resources.feature_loan_action_assign_officer_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_buy_down_fee
import androidclient.feature.loan.generated.resources.feature_loan_action_buy_down_fee_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_capitalized_income
import androidclient.feature.loan.generated.resources.feature_loan_action_capitalized_income_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_change_officer
import androidclient.feature.loan.generated.resources.feature_loan_action_change_officer_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_charge_off
import androidclient.feature.loan.generated.resources.feature_loan_action_charge_off_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_close
import androidclient.feature.loan.generated.resources.feature_loan_action_close_rescheduled
import androidclient.feature.loan.generated.resources.feature_loan_action_close_rescheduled_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_close_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_contract_termination
import androidclient.feature.loan.generated.resources.feature_loan_action_contract_termination_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_credit_balance_refund
import androidclient.feature.loan.generated.resources.feature_loan_action_credit_balance_refund_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_delete
import androidclient.feature.loan.generated.resources.feature_loan_action_delete_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_disburse
import androidclient.feature.loan.generated.resources.feature_loan_action_disburse_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_disburse_to_savings
import androidclient.feature.loan.generated.resources.feature_loan_action_disburse_to_savings_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_edit_goodwill_credit_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_edit_payment_refund_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_edit_repayment_schedule
import androidclient.feature.loan.generated.resources.feature_loan_action_edit_repayment_schedule_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_foreclosure
import androidclient.feature.loan.generated.resources.feature_loan_action_foreclosure_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_goodwill_credit_schedule
import androidclient.feature.loan.generated.resources.feature_loan_action_interest_payment_waiver
import androidclient.feature.loan.generated.resources.feature_loan_action_interest_payment_waiver_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_loan_screen_report
import androidclient.feature.loan.generated.resources.feature_loan_action_loan_screen_report_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_make_repayment
import androidclient.feature.loan.generated.resources.feature_loan_action_make_repayment_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_merchant_issued_refund_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_modify_application
import androidclient.feature.loan.generated.resources.feature_loan_action_modify_application_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_payment_refund_schedule
import androidclient.feature.loan.generated.resources.feature_loan_action_payments
import androidclient.feature.loan.generated.resources.feature_loan_action_payments_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_re_age
import androidclient.feature.loan.generated.resources.feature_loan_action_re_age_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_re_amortize
import androidclient.feature.loan.generated.resources.feature_loan_action_re_amortize_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_recover_from_guarantor
import androidclient.feature.loan.generated.resources.feature_loan_action_recover_from_guarantor_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_recovery_payment
import androidclient.feature.loan.generated.resources.feature_loan_action_recovery_payment_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_reject
import androidclient.feature.loan.generated.resources.feature_loan_action_reject_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_reschedule
import androidclient.feature.loan.generated.resources.feature_loan_action_reschedule_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_sell_loan
import androidclient.feature.loan.generated.resources.feature_loan_action_sell_loan_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_transfer_funds
import androidclient.feature.loan.generated.resources.feature_loan_action_transfer_funds_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_approval
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_approval_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_charge_off
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_charge_off_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_disbursal
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_disbursal_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_last_disbursal
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_last_disbursal_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_re_age
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_re_age_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_re_amortize
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_re_amortize_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_write_off
import androidclient.feature.loan.generated.resources.feature_loan_action_undo_write_off_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_view_guarantors
import androidclient.feature.loan.generated.resources.feature_loan_action_view_guarantors_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_waive_interest
import androidclient.feature.loan.generated.resources.feature_loan_action_waive_interest_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_withdrawn_by_client
import androidclient.feature.loan.generated.resources.feature_loan_action_withdrawn_by_client_sub
import androidclient.feature.loan.generated.resources.feature_loan_action_write_off
import androidclient.feature.loan.generated.resources.feature_loan_action_write_off_sub
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantors
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantors_sub
import androidclient.feature.loan.generated.resources.feature_loan_merchant_issued_refund_schedule
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import androidclient.core.ui.generated.resources.Res as UiRes
import androidclient.feature.loan.generated.resources.Res as LoanRes

sealed class LoanAccountActionItem(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {
    data object Payments : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_payments,
        subTitle = LoanRes.string.feature_loan_action_payments_sub,
        icon = UiRes.drawable.payments,
    )

    data object AddLoanCharge : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_add_charge,
        subTitle = LoanRes.string.feature_loan_action_add_charge_sub,
        icon = UiRes.drawable.add_loan_charge,
    )

    data object Foreclosure : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_foreclosure,
        subTitle = LoanRes.string.feature_loan_action_foreclosure_sub,
        icon = UiRes.drawable.foreclosure,
    )

    data object MakeRepayment : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_make_repayment,
        subTitle = LoanRes.string.feature_loan_action_make_repayment_sub,
        icon = UiRes.drawable.make_repayment,
    )

    data object WaiveInterest : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_waive_interest,
        subTitle = LoanRes.string.feature_loan_action_waive_interest_sub,
        icon = UiRes.drawable.interest_payment_wavier,
    )

    data object UndoDisbursal : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_undo_disbursal,
        subTitle = LoanRes.string.feature_loan_action_undo_disbursal_sub,
        icon = UiRes.drawable.undo_disbursal,
    )

    data object UndoLastDisbursal : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_undo_last_disbursal,
        subTitle = LoanRes.string.feature_loan_action_undo_last_disbursal_sub,
        icon = UiRes.drawable.undo_disbursal,
    )

    data object InterestPaymentWaiver : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_interest_payment_waiver,
        subTitle = LoanRes.string.feature_loan_action_interest_payment_waiver_sub,
        icon = UiRes.drawable.interest_payment_wavier,
    )

    data object Reschedule : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_reschedule,
        subTitle = LoanRes.string.feature_loan_action_reschedule_sub,
        icon = UiRes.drawable.reschedule,
    )

    data object AssignLoanOfficer : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_assign_officer,
        subTitle = LoanRes.string.feature_loan_action_assign_officer_sub,
        icon = UiRes.drawable.assign_loan_officer,
    )

    data object ChangeLoanOfficer : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_change_officer,
        subTitle = LoanRes.string.feature_loan_action_change_officer_sub,
        icon = UiRes.drawable.change_loan_officer,
    )

    data object WriteOff : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_write_off,
        subTitle = LoanRes.string.feature_loan_action_write_off_sub,
        icon = UiRes.drawable.write_off,
    )

    data object Close : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_close,
        subTitle = LoanRes.string.feature_loan_action_close_sub,
        icon = UiRes.drawable.close,
    )

    data object CloseAsRescheduled : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_close_rescheduled,
        subTitle = LoanRes.string.feature_loan_action_close_rescheduled_sub,
        icon = UiRes.drawable.close_as_rescheduled,
    )

    data object LoanScreenReport : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_loan_screen_report,
        subTitle = LoanRes.string.feature_loan_action_loan_screen_report_sub,
        icon = UiRes.drawable.loan_screen_report,
    )

    data object ViewGuarantors : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_view_guarantors,
        subTitle = LoanRes.string.feature_loan_action_view_guarantors_sub,
        icon = UiRes.drawable.view_guarantors,
    )

    data object CreateGuarantors : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_create_guarantors,
        subTitle = LoanRes.string.feature_loan_create_guarantors_sub,
        icon = UiRes.drawable.create_guarantors,
    )

    data object RecoverFromGuarantor : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_recover_from_guarantor,
        subTitle = LoanRes.string.feature_loan_action_recover_from_guarantor_sub,
        icon = UiRes.drawable.recover_from_guarantor,
    )

    data object SellLoan : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_sell_loan,
        subTitle = LoanRes.string.feature_loan_action_sell_loan_sub,
        icon = UiRes.drawable.sell_loan,
    )

    data object AddInterestPause : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_add_interest_pause,
        subTitle = LoanRes.string.feature_loan_action_add_interest_pause_sub,
        icon = UiRes.drawable.add_interest_pause,
    )

    data object PrepayLoan : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_add_prepay_loan,
        subTitle = LoanRes.string.feature_loan_action_add_prepay_loan_sub,
        icon = UiRes.drawable.prepay_loan,
    )

    data object ContractTermination : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_contract_termination,
        subTitle = LoanRes.string.feature_loan_action_contract_termination_sub,
        icon = UiRes.drawable.contract_termination,
    )

    data object Approve : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_approve,
        subTitle = LoanRes.string.feature_loan_action_approve_sub,
        icon = UiRes.drawable.approve,
    )

    data object ModifyApplication : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_modify_application,
        subTitle = LoanRes.string.feature_loan_action_modify_application_sub,
        icon = UiRes.drawable.modify_application,
    )

    data object Reject : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_reject,
        subTitle = LoanRes.string.feature_loan_action_reject_sub,
        icon = UiRes.drawable.reject,
    )

    data object WithdrawnByClient : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_withdrawn_by_client,
        subTitle = LoanRes.string.feature_loan_action_withdrawn_by_client_sub,
        icon = UiRes.drawable.withdrawn_by_client,
    )

    data object Delete : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_delete,
        subTitle = LoanRes.string.feature_loan_action_delete_sub,
        icon = UiRes.drawable.delete,
    )

    data object AddCollateral : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_add_collateral,
        subTitle = LoanRes.string.feature_loan_action_add_collateral_sub,
        icon = UiRes.drawable.add_collateral,
    )

    data object Disburse : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_disburse,
        subTitle = LoanRes.string.feature_loan_action_disburse_sub,
        icon = UiRes.drawable.disburse,
    )

    data object DisburseToSavings : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_disburse_to_savings,
        subTitle = LoanRes.string.feature_loan_action_disburse_to_savings_sub,
        icon = UiRes.drawable.disburse_to_savings,
    )

    data object UndoApproval : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_undo_approval,
        subTitle = LoanRes.string.feature_loan_action_undo_approval_sub,
        icon = UiRes.drawable.undo_approval,
    )

    data object TransferFunds : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_transfer_funds,
        subTitle = LoanRes.string.feature_loan_action_transfer_funds_sub,
        icon = UiRes.drawable.transfer_funds,
    )

    data object CreditBalanceRefund : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_credit_balance_refund,
        subTitle = LoanRes.string.feature_loan_action_credit_balance_refund_sub,
        icon = UiRes.drawable.credit_balance_refund,
    )

    data object RecoveryPayment : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_recovery_payment,
        subTitle = LoanRes.string.feature_loan_action_recovery_payment_sub,
        icon = UiRes.drawable.recovery_payment,
    )

    data object UndoWriteOff : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_undo_write_off,
        subTitle = LoanRes.string.feature_loan_action_undo_write_off_sub,
        icon = UiRes.drawable.undo_write_off,
    )

    data object BuyDownFee : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_buy_down_fee,
        subTitle = LoanRes.string.feature_loan_action_buy_down_fee_sub,
        icon = UiRes.drawable.buy_down_fee,
    )

    data object CapitalizedIncome : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_capitalized_income,
        subTitle = LoanRes.string.feature_loan_action_capitalized_income_sub,
        icon = UiRes.drawable.capitalized_income,
    )

    data object UndoChargeOff : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_undo_charge_off,
        subTitle = LoanRes.string.feature_loan_action_undo_charge_off_sub,
        icon = UiRes.drawable.undo_charge_off,
    )

    data object ChargeOff : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_charge_off,
        subTitle = LoanRes.string.feature_loan_action_charge_off_sub,
        icon = UiRes.drawable.charge_off,
    )

    data object UndoReAge : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_undo_re_age,
        subTitle = LoanRes.string.feature_loan_action_undo_re_age_sub,
        icon = UiRes.drawable.undo_re_age,
    )

    data object ReAge : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_re_age,
        subTitle = LoanRes.string.feature_loan_action_re_age_sub,
        icon = UiRes.drawable.re_age,
    )

    data object UndoReAmortize : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_undo_re_amortize,
        subTitle = LoanRes.string.feature_loan_action_undo_re_amortize_sub,
        icon = UiRes.drawable.undo_re_anortize,
    )

    data object ReAmortize : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_re_amortize,
        subTitle = LoanRes.string.feature_loan_action_re_amortize_sub,
        icon = UiRes.drawable.re_anortize,
    )

    data object EditRepaymentSchedule : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_edit_repayment_schedule,
        subTitle = LoanRes.string.feature_loan_action_edit_repayment_schedule_sub,
        icon = UiRes.drawable.edit_repayment_schedule,
    )

    data object GoodwillCredit : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_goodwill_credit_schedule,
        subTitle = LoanRes.string.feature_loan_action_edit_goodwill_credit_sub,
        icon = UiRes.drawable.goodwill_credit,
    )

    data object PaymentRefund : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_action_payment_refund_schedule,
        subTitle = LoanRes.string.feature_loan_action_edit_payment_refund_sub,
        icon = UiRes.drawable.payment_refund,
    )

    data object MerchantIssuedRefund : LoanAccountActionItem(
        title = LoanRes.string.feature_loan_merchant_issued_refund_schedule,
        subTitle = LoanRes.string.feature_loan_action_merchant_issued_refund_sub,
        icon = UiRes.drawable.merchant_issued_refund,
    )
}
