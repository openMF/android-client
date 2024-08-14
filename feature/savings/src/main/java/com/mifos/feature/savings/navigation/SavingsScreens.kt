package com.mifos.feature.savings.navigation

import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType

/**
 * Created by Pronay Sarker on 14/08/2024 (1:11 PM)
 */
sealed class SavingsScreens(val route: String) {

    data object SavingsAccount :
        SavingsScreens(route = "savings_account_screen/{${Constants.GROUP_ID}}/{${Constants.CLIENT_ID}}/{${Constants.GROUP_ACCOUNT}}") {

        fun argument(groupId: Int, clientId: Int, isGroupAccount: Boolean): String {
            return "savings_account_screen/${groupId}/${clientId}/${isGroupAccount}"
        }
    }

    data object SavingsAccountActivate : SavingsScreens(route = "savings_account_activate_screen")

    data object SavingsAccountApproval : SavingsScreens(route = "savings_account_approval_screen")

    data object SavingsAccountSummary : SavingsScreens(route = "savings_account_summary_screen")

    data object SavingsAccountTransaction :
        SavingsScreens(route = "savings_account_transaction_screen/{${Constants.SAVINGS_ACCOUNT_ID}}/{${Constants.CLIENT_NAME}}/{${Constants.SAVINGS_ACCOUNT_NUMBER}}/{${Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE}}/{depositType}") {

        fun argument(
            savingsAccountId: Int,
            clientName: String,
            savingsAccountNumber: Int,
            transactionType: String,
            depositType: DepositType
        ): String {
            val gson = Gson()
            val depositTypeJson = gson.toJson(depositType)

            return "savings_account_transaction_screen/${savingsAccountId}/${clientName}/${savingsAccountNumber}/${transactionType}/${depositTypeJson}"
        }
    }

    data object SavingsSyncAccountTransaction :
        SavingsScreens("savings_sync_account_transaction")
}

