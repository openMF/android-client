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

import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsSummaryData
import com.mifos.room.entities.accounts.savings.SavingsTransactionData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO: Convert while doing these screens
sealed class SavingsScreens(val route: String) {

    data object SavingsAccountSummary :
        SavingsScreens(route = "savings_account_summary_screen/{arg}") {
        fun argument(savingsAccountId: Int, savingsAccountType: SavingAccountDepositTypeEntity): String {
            val arg = SavingsSummaryData(id = savingsAccountId, type = savingsAccountType)
            val savingsSummaryDataToJson = Json.encodeToString(arg)

            return "savings_account_summary_screen/$savingsSummaryDataToJson"
        }
    }

    data object SavingsAccountTransaction :
        SavingsScreens(route = "savings_account_transaction_screen/{arg}") {

        fun argument(
            savingsAccountWithAssociations: SavingsAccountWithAssociationsEntity,
            transactionType: String,
            depositType: SavingAccountDepositTypeEntity?,
        ): String {
            val arg = SavingsTransactionData(savingsAccountWithAssociations, depositType, transactionType)
            val savingsTransactionDataToJson = Json.encodeToString(arg)
            return "savings_account_transaction_screen/$savingsTransactionDataToJson"
        }
    }
}
