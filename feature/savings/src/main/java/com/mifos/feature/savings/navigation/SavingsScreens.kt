/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.navigation

import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.accounts.savings.SavingsSummaryData
import com.mifos.core.objects.accounts.savings.SavingsTransactionData

/**
 * Created by Pronay Sarker on 14/08/2024 (1:11 PM)
 */
sealed class SavingsScreens(val route: String) {

    data object SavingsAccount :
        SavingsScreens(route = "savings_account_screen/{${Constants.GROUP_ID}}/{${Constants.CLIENT_ID}}/{${Constants.GROUP_ACCOUNT}}") {
        fun argument(groupId: Int, clientId: Int, isGroupAccount: Boolean) =
            "savings_account_screen/$groupId/$clientId/$isGroupAccount"
    }

    data object SavingsAccountActivate :
        SavingsScreens(route = "savings_account_activate_screen/{${Constants.SAVINGS_ACCOUNT_ID}}") {
        fun argument(savingsAccountId: Int) = "savings_account_activate_screen/$savingsAccountId"
    }

    data object SavingsAccountApproval :
        SavingsScreens(route = "savings_account_approval_screen/{${Constants.SAVINGS_ACCOUNT_ID}}") {
        fun argument(savingsAccountId: Int) = "savings_account_approval_screen/$savingsAccountId"
    }

    data object SavingsAccountSummary :
        SavingsScreens(route = "savings_account_summary_screen/{arg}") {
        fun argument(savingsAccountId: Int, savingsAccountType: DepositType): String {
            val gson = Gson()
            val arg = SavingsSummaryData(id = savingsAccountId, type = savingsAccountType)
            val savingsSummaryDataToJson = gson.toJson(arg)

            return "savings_account_summary_screen/$savingsSummaryDataToJson"
        }
    }

    data object SavingsAccountTransaction :
        SavingsScreens(route = "savings_account_transaction_screen/{arg}") {

        fun argument(
            savingsAccountWithAssociations: SavingsAccountWithAssociations,
            transactionType: String,
            depositType: DepositType?,
        ): String {
            val gson = Gson()
            val arg = SavingsTransactionData(savingsAccountWithAssociations, depositType, transactionType)
            val savingsTransactionDataToJson = gson.toJson(arg)

            return "savings_account_transaction_screen/$savingsTransactionDataToJson"
        }
    }

    data object SavingsSyncAccountTransaction :
        SavingsScreens("savings_sync_account_transaction")
}
