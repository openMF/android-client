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

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.feature.savings.savingsAccount.SavingsAccountScreen
import com.mifos.feature.savings.savingsAccountActivate.SavingsAccountActivateScreen
import com.mifos.feature.savings.savingsAccountApproval.SavingsAccountApprovalScreen
import com.mifos.feature.savings.savingsAccountSummary.SavingsAccountSummaryScreen
import com.mifos.feature.savings.savingsAccountTransaction.SavingsAccountTransactionScreen

/**
 * Created by Pronay Sarker on 14/08/2024 (1:10 PM)
 */

fun NavGraphBuilder.savingsNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
    loadMoreSavingsAccountInfo: (String, Int) -> Unit,
    loadDocuments: (Int, String) -> Unit,
) {
    navigation(
        startDestination = SavingsScreens.SavingsAccountSummary.route,
        route = "savings_summary_route",
    ) {
        savingsSummaryScreen(
            onBackPressed = navController::popBackStack,
            loadMoreSavingsAccountInfo = { loadMoreSavingsAccountInfo(Constants.DATA_TABLE_NAME_SAVINGS, it) },
            loadDocuments = { loadDocuments(it, Constants.ENTITY_TYPE_SAVINGS) },
            onDepositClick = { savingsAccountWithAssociations, depositType ->
                navController.navigateToSavingsAccountTransactionScreen(
                    savingsAccountWithAssociations = savingsAccountWithAssociations,
                    depositType = depositType,
                    transactionType = Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT,
                )
            },
            onWithdrawButtonClicked = { savingsAccountWithAssociations, depositType ->
                navController.navigateToSavingsAccountTransactionScreen(
                    savingsAccountWithAssociations = savingsAccountWithAssociations,
                    depositType = depositType,
                    transactionType = Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL,
                )
            },
            approveSavings = { _, accountNumber ->
                navController.navigateToSavingsAccountApproval(
                    accountNumber,
                )
            },
            activateSavings = { _, accountNumber ->
                navController.navigateToSavingsAccountActivate(
                    accountNumber,
                )
            },
        )

        addSavingsAccountScreen {
            onBackPressed()
        }

        savingsAccountActivateScreen {
            onBackPressed()
        }

        savingsAccountApprovalScreen {
            onBackPressed()
        }

        savingsAccountTransactionScreen {
            onBackPressed()
        }
    }
}

fun NavGraphBuilder.addSavingsAccountScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = SavingsScreens.SavingsAccount.route,
        arguments = listOf(
            navArgument(name = Constants.GROUP_ID, builder = { type = NavType.IntType }),
            navArgument(name = Constants.CLIENT_ID, builder = { type = NavType.IntType }),
            navArgument(name = Constants.GROUP_ACCOUNT, builder = { type = NavType.BoolType }),
        ),
    ) {
        SavingsAccountScreen(
            navigateBack = onBackPressed,
        )
    }
}

fun NavGraphBuilder.savingsSummaryScreen(
    onBackPressed: () -> Unit,
    loadMoreSavingsAccountInfo: (Int) -> Unit,
    loadDocuments: (Int) -> Unit,
    onDepositClick: (SavingsAccountWithAssociations, DepositType?) -> Unit,
    onWithdrawButtonClicked: (SavingsAccountWithAssociations, DepositType?) -> Unit,
    approveSavings: (savingsAccountType: DepositType?, savingsAccountNumber: Int) -> Unit,
    activateSavings: (savingsAccountType: DepositType?, savingsAccountNumber: Int) -> Unit,
) {
    composable(
        route = SavingsScreens.SavingsAccountSummary.route,
        arguments = listOf(
            navArgument(name = "arg", builder = { type = NavType.StringType }),
        ),
    ) {
        SavingsAccountSummaryScreen(
            navigateBack = onBackPressed,
            loadMoreSavingsAccountInfo = loadMoreSavingsAccountInfo,
            loadDocuments = loadDocuments,
            onDepositClick = onDepositClick,
            onWithdrawButtonClicked = onWithdrawButtonClicked,
            approveSavings = approveSavings,
            activateSavings = activateSavings,
        )
    }
}

fun NavGraphBuilder.savingsAccountActivateScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = SavingsScreens.SavingsAccountActivate.route,
        arguments = listOf(
            navArgument(name = Constants.SAVINGS_ACCOUNT_ID, builder = { type = NavType.IntType }),
        ),
    ) {
        SavingsAccountActivateScreen(
            navigateBack = onBackPressed,
        )
    }
}

fun NavGraphBuilder.savingsAccountApprovalScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = SavingsScreens.SavingsAccountApproval.route,
        arguments = listOf(
            navArgument(name = Constants.SAVINGS_ACCOUNT_ID, builder = { type = NavType.IntType }),
        ),
    ) {
        SavingsAccountApprovalScreen(
            navigateBack = onBackPressed,
        )
    }
}

fun NavGraphBuilder.savingsAccountTransactionScreen(
    onBackPressed: () -> Unit,
) {
    composable(
        route = SavingsScreens.SavingsAccountTransaction.route,
        arguments = listOf(
            navArgument(name = "arg", builder = { type = NavType.StringType }),
        ),
    ) {
        SavingsAccountTransactionScreen(
            navigateBack = onBackPressed,
        )
    }
}

fun NavController.navigateToAddSavingsAccount(
    groupId: Int,
    clientId: Int,
    isGroupAccount: Boolean,
) {
    navigate(SavingsScreens.SavingsAccount.argument(groupId, clientId, isGroupAccount))
}

fun NavController.navigateToSavingsAccountApproval(savingsAccountId: Int) {
    navigate(SavingsScreens.SavingsAccountApproval.argument(savingsAccountId))
}

fun NavController.navigateToSavingsAccountActivate(savingsAccountId: Int) {
    navigate(SavingsScreens.SavingsAccountActivate.argument(savingsAccountId))
}

fun NavController.navigateToSavingsAccountSummaryScreen(id: Int, type: DepositType) {
    navigate(SavingsScreens.SavingsAccountSummary.argument(id, type))
}

fun NavController.navigateToSavingsAccountTransactionScreen(
    savingsAccountWithAssociations: SavingsAccountWithAssociations,
    transactionType: String,
    depositType: DepositType?,
) {
    navigate(
        SavingsScreens.SavingsAccountTransaction.argument(
            savingsAccountWithAssociations,
            transactionType,
            depositType,
        ),
    )
}
