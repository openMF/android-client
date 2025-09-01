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

import com.mifos.room.entities.accounts.loans.LoanApprovalData
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import kotlinx.serialization.json.Json

// TODO : Migrate it to type safe while implementing this screen
sealed class LoanScreens(val route: String) {

    data object LoanApprovalScreen : LoanScreens("loan_approval_screen/{arg}") {
        fun argument(loanAccountNumber: Int, loanWithAssociations: LoanWithAssociationsEntity): String {
            val arg = LoanApprovalData(loanAccountNumber, loanWithAssociations)
            val loanApprovalDataInJson = Json.encodeToString(arg)

            return "loan_approval_screen/$loanApprovalDataInJson"
        }
    }
}
