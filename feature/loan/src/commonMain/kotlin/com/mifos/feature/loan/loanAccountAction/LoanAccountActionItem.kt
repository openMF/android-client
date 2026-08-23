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

import kpt.core.ui.generated.resources.add_collateral
import kpt.core.ui.generated.resources.add_interest_pause
import kpt.core.ui.generated.resources.add_loan_charge
import kpt.core.ui.generated.resources.approve
import kpt.core.ui.generated.resources.assign_loan_officer
import kpt.core.ui.generated.resources.buy_down_fee
import kpt.core.ui.generated.resources.capitalized_income
import kpt.core.ui.generated.resources.change_loan_officer
import kpt.core.ui.generated.resources.charge_off
import kpt.core.ui.generated.resources.close
import kpt.core.ui.generated.resources.close_as_rescheduled
import kpt.core.ui.generated.resources.contract_termination
import kpt.core.ui.generated.resources.create_guarantors
import kpt.core.ui.generated.resources.credit_balance_refund
import kpt.core.ui.generated.resources.delete
import kpt.core.ui.generated.resources.disburse
import kpt.core.ui.generated.resources.disburse_to_savings
import kpt.core.ui.generated.resources.edit_repayment_schedule
import kpt.core.ui.generated.resources.foreclosure
import kpt.core.ui.generated.resources.goodwill_credit
import kpt.core.ui.generated.resources.interest_payment_wavier
import kpt.core.ui.generated.resources.loan_screen_report
import kpt.core.ui.generated.resources.make_repayment
import kpt.core.ui.generated.resources.merchant_issued_refund
import kpt.core.ui.generated.resources.modify_application
import kpt.core.ui.generated.resources.payment_refund
import kpt.core.ui.generated.resources.payments
import kpt.core.ui.generated.resources.prepay_loan
import kpt.core.ui.generated.resources.re_age
import kpt.core.ui.generated.resources.re_anortize
import kpt.core.ui.generated.resources.recover_from_guarantor
import kpt.core.ui.generated.resources.recovery_payment
import kpt.core.ui.generated.resources.reject
import kpt.core.ui.generated.resources.reschedule
import kpt.core.ui.generated.resources.sell_loan
import kpt.core.ui.generated.resources.transfer_funds
import kpt.core.ui.generated.resources.undo_approval
import kpt.core.ui.generated.resources.undo_charge_off
import kpt.core.ui.generated.resources.undo_disbursal
import kpt.core.ui.generated.resources.undo_re_age
import kpt.core.ui.generated.resources.undo_re_anortize
import kpt.core.ui.generated.resources.undo_write_off
import kpt.core.ui.generated.resources.view_guarantors
import kpt.core.ui.generated.resources.withdrawn_by_client
import kpt.core.ui.generated.resources.write_off
import kpt.feature.loan.generated.resources.feature_loan_action_add_charge
import kpt.feature.loan.generated.resources.feature_loan_action_add_charge_sub
import kpt.feature.loan.generated.resources.feature_loan_action_add_collateral
import kpt.feature.loan.generated.resources.feature_loan_action_add_collateral_sub
import kpt.feature.loan.generated.resources.feature_loan_action_add_interest_pause
import kpt.feature.loan.generated.resources.feature_loan_action_add_interest_pause_sub
import kpt.feature.loan.generated.resources.feature_loan_action_add_prepay_loan
import kpt.feature.loan.generated.resources.feature_loan_action_add_prepay_loan_sub
import kpt.feature.loan.generated.resources.feature_loan_action_approve
import kpt.feature.loan.generated.resources.feature_loan_action_approve_sub
import kpt.feature.loan.generated.resources.feature_loan_action_assign_officer
import kpt.feature.loan.generated.resources.feature_loan_action_assign_officer_sub
import kpt.feature.loan.generated.resources.feature_loan_action_buy_down_fee
import kpt.feature.loan.generated.resources.feature_loan_action_buy_down_fee_sub
import kpt.feature.loan.generated.resources.feature_loan_action_capitalized_income
import kpt.feature.loan.generated.resources.feature_loan_action_capitalized_income_sub
import kpt.feature.loan.generated.resources.feature_loan_action_change_officer
import kpt.feature.loan.generated.resources.feature_loan_action_change_officer_sub
import kpt.feature.loan.generated.resources.feature_loan_action_charge_off
import kpt.feature.loan.generated.resources.feature_loan_action_charge_off_sub
import kpt.feature.loan.generated.resources.feature_loan_action_close
import kpt.feature.loan.generated.resources.feature_loan_action_close_rescheduled
import kpt.feature.loan.generated.resources.feature_loan_action_close_rescheduled_sub
import kpt.feature.loan.generated.resources.feature_loan_action_close_sub
import kpt.feature.loan.generated.resources.feature_loan_action_contract_termination
import kpt.feature.loan.generated.resources.feature_loan_action_contract_termination_sub
import kpt.feature.loan.generated.resources.feature_loan_action_credit_balance_refund
import kpt.feature.loan.generated.resources.feature_loan_action_credit_balance_refund_sub
import kpt.feature.loan.generated.resources.feature_loan_action_delete
import kpt.feature.loan.generated.resources.feature_loan_action_delete_sub
import kpt.feature.loan.generated.resources.feature_loan_action_disburse
import kpt.feature.loan.generated.resources.feature_loan_action_disburse_sub
import kpt.feature.loan.generated.resources.feature_loan_action_disburse_to_savings
import kpt.feature.loan.generated.resources.feature_loan_action_disburse_to_savings_sub
import kpt.feature.loan.generated.resources.feature_loan_action_edit_goodwill_credit_sub
import kpt.feature.loan.generated.resources.feature_loan_action_edit_payment_refund_sub
import kpt.feature.loan.generated.resources.feature_loan_action_edit_repayment_schedule
import kpt.feature.loan.generated.resources.feature_loan_action_edit_repayment_schedule_sub
import kpt.feature.loan.generated.resources.feature_loan_action_foreclosure
import kpt.feature.loan.generated.resources.feature_loan_action_foreclosure_sub
import kpt.feature.loan.generated.resources.feature_loan_action_goodwill_credit_schedule
import kpt.feature.loan.generated.resources.feature_loan_action_interest_payment_waiver
import kpt.feature.loan.generated.resources.feature_loan_action_interest_payment_waiver_sub
import kpt.feature.loan.generated.resources.feature_loan_action_loan_screen_report
import kpt.feature.loan.generated.resources.feature_loan_action_loan_screen_report_sub
import kpt.feature.loan.generated.resources.feature_loan_action_make_repayment
import kpt.feature.loan.generated.resources.feature_loan_action_make_repayment_sub
import kpt.feature.loan.generated.resources.feature_loan_action_merchant_issued_refund_sub
import kpt.feature.loan.generated.resources.feature_loan_action_modify_application
import kpt.feature.loan.generated.resources.feature_loan_action_modify_application_sub
import kpt.feature.loan.generated.resources.feature_loan_action_payment_refund_schedule
import kpt.feature.loan.generated.resources.feature_loan_action_payments
import kpt.feature.loan.generated.resources.feature_loan_action_payments_sub
import kpt.feature.loan.generated.resources.feature_loan_action_re_age
import kpt.feature.loan.generated.resources.feature_loan_action_re_age_sub
import kpt.feature.loan.generated.resources.feature_loan_action_re_amortize
import kpt.feature.loan.generated.resources.feature_loan_action_re_amortize_sub
import kpt.feature.loan.generated.resources.feature_loan_action_recover_from_guarantor
import kpt.feature.loan.generated.resources.feature_loan_action_recover_from_guarantor_sub
import kpt.feature.loan.generated.resources.feature_loan_action_recovery_payment
import kpt.feature.loan.generated.resources.feature_loan_action_recovery_payment_sub
import kpt.feature.loan.generated.resources.feature_loan_action_reject
import kpt.feature.loan.generated.resources.feature_loan_action_reject_sub
import kpt.feature.loan.generated.resources.feature_loan_action_reschedule
import kpt.feature.loan.generated.resources.feature_loan_action_reschedule_sub
import kpt.feature.loan.generated.resources.feature_loan_action_sell_loan
import kpt.feature.loan.generated.resources.feature_loan_action_sell_loan_sub
import kpt.feature.loan.generated.resources.feature_loan_action_transfer_funds
import kpt.feature.loan.generated.resources.feature_loan_action_transfer_funds_sub
import kpt.feature.loan.generated.resources.feature_loan_action_undo_approval
import kpt.feature.loan.generated.resources.feature_loan_action_undo_approval_sub
import kpt.feature.loan.generated.resources.feature_loan_action_undo_charge_off
import kpt.feature.loan.generated.resources.feature_loan_action_undo_charge_off_sub
import kpt.feature.loan.generated.resources.feature_loan_action_undo_disbursal
import kpt.feature.loan.generated.resources.feature_loan_action_undo_disbursal_sub
import kpt.feature.loan.generated.resources.feature_loan_action_undo_last_disbursal
import kpt.feature.loan.generated.resources.feature_loan_action_undo_last_disbursal_sub
import kpt.feature.loan.generated.resources.feature_loan_action_undo_re_age
import kpt.feature.loan.generated.resources.feature_loan_action_undo_re_age_sub
import kpt.feature.loan.generated.resources.feature_loan_action_undo_re_amortize
import kpt.feature.loan.generated.resources.feature_loan_action_undo_re_amortize_sub
import kpt.feature.loan.generated.resources.feature_loan_action_undo_write_off
import kpt.feature.loan.generated.resources.feature_loan_action_undo_write_off_sub
import kpt.feature.loan.generated.resources.feature_loan_action_view_guarantors
import kpt.feature.loan.generated.resources.feature_loan_action_view_guarantors_sub
import kpt.feature.loan.generated.resources.feature_loan_action_waive_interest
import kpt.feature.loan.generated.resources.feature_loan_action_waive_interest_sub
import kpt.feature.loan.generated.resources.feature_loan_action_withdrawn_by_client
import kpt.feature.loan.generated.resources.feature_loan_action_withdrawn_by_client_sub
import kpt.feature.loan.generated.resources.feature_loan_action_write_off
import kpt.feature.loan.generated.resources.feature_loan_action_write_off_sub
import kpt.feature.loan.generated.resources.feature_loan_create_guarantors
import kpt.feature.loan.generated.resources.feature_loan_create_guarantors_sub
import kpt.feature.loan.generated.resources.feature_loan_merchant_issued_refund_schedule
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import kpt.core.ui.generated.resources.Res as UiRes
import kpt.feature.loan.generated.resources.Res as LoanRes

sealed class LoanAccountActionItem(
    val id: String,
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {
    data object Payments : LoanAccountActionItem(
        id = "payments",
        title = LoanRes.string.feature_loan_action_payments,
        subTitle = LoanRes.string.feature_loan_action_payments_sub,
        icon = UiRes.drawable.payments,
    )

    data object AddLoanCharge : LoanAccountActionItem(
        id = "add_loan_charge",
        title = LoanRes.string.feature_loan_action_add_charge,
        subTitle = LoanRes.string.feature_loan_action_add_charge_sub,
        icon = UiRes.drawable.add_loan_charge,
    )

    data object Foreclosure : LoanAccountActionItem(
        id = "foreclosure",
        title = LoanRes.string.feature_loan_action_foreclosure,
        subTitle = LoanRes.string.feature_loan_action_foreclosure_sub,
        icon = UiRes.drawable.foreclosure,
    )

    data object MakeRepayment : LoanAccountActionItem(
        id = "make_repayment",
        title = LoanRes.string.feature_loan_action_make_repayment,
        subTitle = LoanRes.string.feature_loan_action_make_repayment_sub,
        icon = UiRes.drawable.make_repayment,
    )

    data object WaiveInterest : LoanAccountActionItem(
        id = "waive_interest",
        title = LoanRes.string.feature_loan_action_waive_interest,
        subTitle = LoanRes.string.feature_loan_action_waive_interest_sub,
        icon = UiRes.drawable.interest_payment_wavier,
    )

    data object UndoDisbursal : LoanAccountActionItem(
        id = "undo_disbursal",
        title = LoanRes.string.feature_loan_action_undo_disbursal,
        subTitle = LoanRes.string.feature_loan_action_undo_disbursal_sub,
        icon = UiRes.drawable.undo_disbursal,
    )

    data object UndoLastDisbursal : LoanAccountActionItem(
        id = "undo_last_disbursal",
        title = LoanRes.string.feature_loan_action_undo_last_disbursal,
        subTitle = LoanRes.string.feature_loan_action_undo_last_disbursal_sub,
        icon = UiRes.drawable.undo_disbursal,
    )

    data object InterestPaymentWaiver : LoanAccountActionItem(
        id = "interest_payment_waiver",
        title = LoanRes.string.feature_loan_action_interest_payment_waiver,
        subTitle = LoanRes.string.feature_loan_action_interest_payment_waiver_sub,
        icon = UiRes.drawable.interest_payment_wavier,
    )

    data object Reschedule : LoanAccountActionItem(
        id = "reschedule",
        title = LoanRes.string.feature_loan_action_reschedule,
        subTitle = LoanRes.string.feature_loan_action_reschedule_sub,
        icon = UiRes.drawable.reschedule,
    )

    data object AssignLoanOfficer : LoanAccountActionItem(
        id = "assign_loan_officer",
        title = LoanRes.string.feature_loan_action_assign_officer,
        subTitle = LoanRes.string.feature_loan_action_assign_officer_sub,
        icon = UiRes.drawable.assign_loan_officer,
    )

    data object ChangeLoanOfficer : LoanAccountActionItem(
        id = "change_loan_officer",
        title = LoanRes.string.feature_loan_action_change_officer,
        subTitle = LoanRes.string.feature_loan_action_change_officer_sub,
        icon = UiRes.drawable.change_loan_officer,
    )

    data object WriteOff : LoanAccountActionItem(
        id = "write_off",
        title = LoanRes.string.feature_loan_action_write_off,
        subTitle = LoanRes.string.feature_loan_action_write_off_sub,
        icon = UiRes.drawable.write_off,
    )

    data object Close : LoanAccountActionItem(
        id = "close",
        title = LoanRes.string.feature_loan_action_close,
        subTitle = LoanRes.string.feature_loan_action_close_sub,
        icon = UiRes.drawable.close,
    )

    data object CloseAsRescheduled : LoanAccountActionItem(
        id = "close_as_rescheduled",
        title = LoanRes.string.feature_loan_action_close_rescheduled,
        subTitle = LoanRes.string.feature_loan_action_close_rescheduled_sub,
        icon = UiRes.drawable.close_as_rescheduled,
    )

    data object LoanScreenReport : LoanAccountActionItem(
        id = "loan_screen_report",
        title = LoanRes.string.feature_loan_action_loan_screen_report,
        subTitle = LoanRes.string.feature_loan_action_loan_screen_report_sub,
        icon = UiRes.drawable.loan_screen_report,
    )

    data object ViewGuarantors : LoanAccountActionItem(
        id = "view_guarantors",
        title = LoanRes.string.feature_loan_action_view_guarantors,
        subTitle = LoanRes.string.feature_loan_action_view_guarantors_sub,
        icon = UiRes.drawable.view_guarantors,
    )

    data object CreateGuarantors : LoanAccountActionItem(
        id = "create_guarantors",
        title = LoanRes.string.feature_loan_create_guarantors,
        subTitle = LoanRes.string.feature_loan_create_guarantors_sub,
        icon = UiRes.drawable.create_guarantors,
    )

    data object RecoverFromGuarantor : LoanAccountActionItem(
        id = "recover_from_guarantor",
        title = LoanRes.string.feature_loan_action_recover_from_guarantor,
        subTitle = LoanRes.string.feature_loan_action_recover_from_guarantor_sub,
        icon = UiRes.drawable.recover_from_guarantor,
    )

    data object SellLoan : LoanAccountActionItem(
        id = "sell_loan",
        title = LoanRes.string.feature_loan_action_sell_loan,
        subTitle = LoanRes.string.feature_loan_action_sell_loan_sub,
        icon = UiRes.drawable.sell_loan,
    )

    data object AddInterestPause : LoanAccountActionItem(
        id = "add_interest_pause",
        title = LoanRes.string.feature_loan_action_add_interest_pause,
        subTitle = LoanRes.string.feature_loan_action_add_interest_pause_sub,
        icon = UiRes.drawable.add_interest_pause,
    )

    data object PrepayLoan : LoanAccountActionItem(
        id = "prepay_loan",
        title = LoanRes.string.feature_loan_action_add_prepay_loan,
        subTitle = LoanRes.string.feature_loan_action_add_prepay_loan_sub,
        icon = UiRes.drawable.prepay_loan,
    )

    data object ContractTermination : LoanAccountActionItem(
        id = "contract_termination",
        title = LoanRes.string.feature_loan_action_contract_termination,
        subTitle = LoanRes.string.feature_loan_action_contract_termination_sub,
        icon = UiRes.drawable.contract_termination,
    )

    data object Approve : LoanAccountActionItem(
        id = "approve",
        title = LoanRes.string.feature_loan_action_approve,
        subTitle = LoanRes.string.feature_loan_action_approve_sub,
        icon = UiRes.drawable.approve,
    )

    data object ModifyApplication : LoanAccountActionItem(
        id = "modify_application",
        title = LoanRes.string.feature_loan_action_modify_application,
        subTitle = LoanRes.string.feature_loan_action_modify_application_sub,
        icon = UiRes.drawable.modify_application,
    )

    data object Reject : LoanAccountActionItem(
        id = "reject",
        title = LoanRes.string.feature_loan_action_reject,
        subTitle = LoanRes.string.feature_loan_action_reject_sub,
        icon = UiRes.drawable.reject,
    )

    data object WithdrawnByClient : LoanAccountActionItem(
        id = "withdrawn_by_client",
        title = LoanRes.string.feature_loan_action_withdrawn_by_client,
        subTitle = LoanRes.string.feature_loan_action_withdrawn_by_client_sub,
        icon = UiRes.drawable.withdrawn_by_client,
    )

    data object Delete : LoanAccountActionItem(
        id = "delete",
        title = LoanRes.string.feature_loan_action_delete,
        subTitle = LoanRes.string.feature_loan_action_delete_sub,
        icon = UiRes.drawable.delete,
    )

    data object AddCollateral : LoanAccountActionItem(
        id = "add_collateral",
        title = LoanRes.string.feature_loan_action_add_collateral,
        subTitle = LoanRes.string.feature_loan_action_add_collateral_sub,
        icon = UiRes.drawable.add_collateral,
    )

    data object Disburse : LoanAccountActionItem(
        id = "disburse",
        title = LoanRes.string.feature_loan_action_disburse,
        subTitle = LoanRes.string.feature_loan_action_disburse_sub,
        icon = UiRes.drawable.disburse,
    )

    data object DisburseToSavings : LoanAccountActionItem(
        id = "disburse_to_savings",
        title = LoanRes.string.feature_loan_action_disburse_to_savings,
        subTitle = LoanRes.string.feature_loan_action_disburse_to_savings_sub,
        icon = UiRes.drawable.disburse_to_savings,
    )

    data object UndoApproval : LoanAccountActionItem(
        id = "undo_approval",
        title = LoanRes.string.feature_loan_action_undo_approval,
        subTitle = LoanRes.string.feature_loan_action_undo_approval_sub,
        icon = UiRes.drawable.undo_approval,
    )

    data object TransferFunds : LoanAccountActionItem(
        id = "transfer_funds",
        title = LoanRes.string.feature_loan_action_transfer_funds,
        subTitle = LoanRes.string.feature_loan_action_transfer_funds_sub,
        icon = UiRes.drawable.transfer_funds,
    )

    data object CreditBalanceRefund : LoanAccountActionItem(
        id = "credit_balance_refund",
        title = LoanRes.string.feature_loan_action_credit_balance_refund,
        subTitle = LoanRes.string.feature_loan_action_credit_balance_refund_sub,
        icon = UiRes.drawable.credit_balance_refund,
    )

    data object RecoveryPayment : LoanAccountActionItem(
        id = "recovery_payment",
        title = LoanRes.string.feature_loan_action_recovery_payment,
        subTitle = LoanRes.string.feature_loan_action_recovery_payment_sub,
        icon = UiRes.drawable.recovery_payment,
    )

    data object UndoWriteOff : LoanAccountActionItem(
        id = "undo_write_off",
        title = LoanRes.string.feature_loan_action_undo_write_off,
        subTitle = LoanRes.string.feature_loan_action_undo_write_off_sub,
        icon = UiRes.drawable.undo_write_off,
    )

    data object BuyDownFee : LoanAccountActionItem(
        id = "buy_down_fee",
        title = LoanRes.string.feature_loan_action_buy_down_fee,
        subTitle = LoanRes.string.feature_loan_action_buy_down_fee_sub,
        icon = UiRes.drawable.buy_down_fee,
    )

    data object CapitalizedIncome : LoanAccountActionItem(
        id = "capitalized_income",
        title = LoanRes.string.feature_loan_action_capitalized_income,
        subTitle = LoanRes.string.feature_loan_action_capitalized_income_sub,
        icon = UiRes.drawable.capitalized_income,
    )

    data object UndoChargeOff : LoanAccountActionItem(
        id = "undo_charge_off",
        title = LoanRes.string.feature_loan_action_undo_charge_off,
        subTitle = LoanRes.string.feature_loan_action_undo_charge_off_sub,
        icon = UiRes.drawable.undo_charge_off,
    )

    data object ChargeOff : LoanAccountActionItem(
        id = "charge_off",
        title = LoanRes.string.feature_loan_action_charge_off,
        subTitle = LoanRes.string.feature_loan_action_charge_off_sub,
        icon = UiRes.drawable.charge_off,
    )

    data object UndoReAge : LoanAccountActionItem(
        id = "undo_re_age",
        title = LoanRes.string.feature_loan_action_undo_re_age,
        subTitle = LoanRes.string.feature_loan_action_undo_re_age_sub,
        icon = UiRes.drawable.undo_re_age,
    )

    data object ReAge : LoanAccountActionItem(
        id = "re_age",
        title = LoanRes.string.feature_loan_action_re_age,
        subTitle = LoanRes.string.feature_loan_action_re_age_sub,
        icon = UiRes.drawable.re_age,
    )

    data object UndoReAmortize : LoanAccountActionItem(
        id = "undo_re_amortize",
        title = LoanRes.string.feature_loan_action_undo_re_amortize,
        subTitle = LoanRes.string.feature_loan_action_undo_re_amortize_sub,
        icon = UiRes.drawable.undo_re_anortize,
    )

    data object ReAmortize : LoanAccountActionItem(
        id = "re_amortize",
        title = LoanRes.string.feature_loan_action_re_amortize,
        subTitle = LoanRes.string.feature_loan_action_re_amortize_sub,
        icon = UiRes.drawable.re_anortize,
    )

    data object EditRepaymentSchedule : LoanAccountActionItem(
        id = "edit_repayment_schedule",
        title = LoanRes.string.feature_loan_action_edit_repayment_schedule,
        subTitle = LoanRes.string.feature_loan_action_edit_repayment_schedule_sub,
        icon = UiRes.drawable.edit_repayment_schedule,
    )

    data object GoodwillCredit : LoanAccountActionItem(
        id = "goodwill_credit",
        title = LoanRes.string.feature_loan_action_goodwill_credit_schedule,
        subTitle = LoanRes.string.feature_loan_action_edit_goodwill_credit_sub,
        icon = UiRes.drawable.goodwill_credit,
    )

    data object PaymentRefund : LoanAccountActionItem(
        id = "payment_refund",
        title = LoanRes.string.feature_loan_action_payment_refund_schedule,
        subTitle = LoanRes.string.feature_loan_action_edit_payment_refund_sub,
        icon = UiRes.drawable.payment_refund,
    )

    data object MerchantIssuedRefund : LoanAccountActionItem(
        id = "merchant_issued_refund",
        title = LoanRes.string.feature_loan_merchant_issued_refund_schedule,
        subTitle = LoanRes.string.feature_loan_action_merchant_issued_refund_sub,
        icon = UiRes.drawable.merchant_issued_refund,
    )
}
