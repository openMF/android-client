/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.navigation

import com.mifos.core.common.utils.Constants
import com.mifos.room.entities.accounts.loans.LoanApprovalData
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.serialization.json.Json

sealed class LoanScreens(val route: String) {

    data object LoanAccountScreen : LoanScreens("loan_account_screen/{${Constants.CLIENT_ID}}") {
        fun argument(clientId: Int) = "loan_account_screen/$clientId"
    }

    data object LoanAccountSummaryScreen : LoanScreens("loan_account_summary_screen/{${Constants.LOAN_ACCOUNT_NUMBER}}") {
        fun argument(loanAccountNumber: Int) = "loan_account_summary_screen/$loanAccountNumber"
    }

    data object LoanApprovalScreen : LoanScreens("loan_approval_screen/{arg}") {
        fun argument(loanAccountNumber: Int, loanWithAssociations: LoanWithAssociationsEntity): String {
            val arg = LoanApprovalData(loanAccountNumber, loanWithAssociations)
            val loanApprovalDataInJson = Json.encodeToString(arg)

            return "loan_approval_screen/$loanApprovalDataInJson"
        }
    }

    data object LoanChargeScreen : LoanScreens("loan_charge_screen/{${Constants.LOAN_ACCOUNT_NUMBER}}") {
        fun argument(loanAccountNumber: Int) = "loan_charge_screen/$loanAccountNumber"
    }

    data object LoanDisbursementScreen : LoanScreens("loan_disbursement_screen/{${Constants.LOAN_ACCOUNT_NUMBER}}") {
        fun argument(loanAccountNumber: Int) = "loan_disbursement_screen/$loanAccountNumber"
    }

    data object LoanRepaymentScreen : LoanScreens("loan_repayment_screen/{${Constants.LOAN_WITH_ASSOCIATIONS}}") {
        fun argument(loanWithAssociations: LoanWithAssociationsEntity): String {
            val loanWithAssociationsInJson = Json.encodeToString(loanWithAssociations)

            return "loan_repayment_screen/$loanWithAssociationsInJson"
        }
    }

    data object LoanRepaymentSchedule : LoanScreens("loan_repayment_schedule/{${Constants.LOAN_ACCOUNT_NUMBER}}") {
        fun argument(loanAccountNumber: Int) = "loan_repayment_schedule/$loanAccountNumber"
    }

    data object LoanTransactionScreen : LoanScreens("loan_transaction_screen/{${Constants.LOAN_ACCOUNT_NUMBER}}") {
        fun argument(loanAccountNumber: Int) = "loan_transaction_screen/$loanAccountNumber"
    }

    data object GroupLoanScreen : LoanScreens("group_loan_screen/{${Constants.GROUP_ID}}") {
        fun argument(groupId: Int) = "group_loan_screen/$groupId"
    }
}
