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
import com.mifos.feature.savings.account.SavingsAccountScreen
import com.mifos.feature.savings.account_activate.SavingsAccountActivateScreen
import com.mifos.feature.savings.account_approval.SavingsAccountApprovalScreen
import com.mifos.feature.savings.account_summary.SavingsAccountSummaryScreen
import com.mifos.feature.savings.account_transaction.SavingsAccountTransactionScreen
import com.mifos.feature.savings.sync_account_transaction.SyncSavingsAccountTransactionScreenRoute

/**
 * Created by Pronay Sarker on 14/08/2024 (1:10 PM)
 */

const val SAVINGS_SUMMARY_ROUTE = "SAVINGS_SUMMARY_ROUTE"

fun NavGraphBuilder.savingsNavGraph(
    navController: NavController,
    onBackPressed: () -> Unit,
    loadMoreSavingsAccountInfo: (Int) -> Unit,
    loadDocuments: (Int) -> Unit,
    onDepositClick: (SavingsAccountWithAssociations, DepositType?) -> Unit,
    onWithdrawButtonClicked: (SavingsAccountWithAssociations, DepositType?) -> Unit,
    approveSavings: (savingsAccountType: DepositType?, savingsAccountNumber: Int) -> Unit,
    activateSavings: (savingsAccountType: DepositType?, savingsAccountNumber: Int) -> Unit
) {
    navigation(
        startDestination = SavingsScreens.SavingsAccountSummary.route,
        route = SAVINGS_SUMMARY_ROUTE
    ) {
        savingsSummaryScreen(
            onBackPressed = navController::popBackStack,
            loadMoreSavingsAccountInfo = {},
            loadDocuments = loadDocuments,
            onDepositClick = { _, _ -> TODO() }, // savings transaction
            onWithdrawButtonClicked = { _, _ -> },
            approveSavings = { _, _ -> },
            activateSavings = { _, _ -> }
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

        syncSavingsAccountTransactionScreen {
            onBackPressed()
        }
    }
}


fun NavGraphBuilder.addSavingsAccountScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = SavingsScreens.SavingsAccount.route
    ) {
        SavingsAccountScreen(
            navigateBack = onBackPressed
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
    activateSavings: (savingsAccountType: DepositType?, savingsAccountNumber: Int) -> Unit
) {
    composable(
        route = SavingsScreens.SavingsAccountSummary.route
    ) {
        SavingsAccountSummaryScreen(
            navigateBack = onBackPressed,
            loadMoreSavingsAccountInfo = loadMoreSavingsAccountInfo,
            loadDocuments = loadDocuments,
            onDepositClick = onDepositClick,
            onWithdrawButtonClicked = onWithdrawButtonClicked,
            approveSavings = approveSavings,
            activateSavings = activateSavings
        )
    }
}

fun NavGraphBuilder.savingsAccountActivateScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = SavingsScreens.SavingsAccountActivate.route
    ) {
        SavingsAccountActivateScreen(
            navigateBack = onBackPressed
        )
    }
}

fun NavGraphBuilder.savingsAccountApprovalScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = SavingsScreens.SavingsAccountApproval.route
    ) {
        SavingsAccountApprovalScreen(
            navigateBack = onBackPressed
        )
    }
}


fun NavGraphBuilder.savingsAccountTransactionScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = SavingsScreens.SavingsAccountTransaction.route,
        arguments = listOf(
            navArgument(Constants.SAVINGS_ACCOUNT_ID, builder = { type = NavType.IntType }),
            navArgument(Constants.CLIENT_NAME, builder = { type = NavType.StringType }),
            navArgument(Constants.SAVINGS_ACCOUNT_NUMBER, builder = { type = NavType.IntType}),
            navArgument(Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE, builder = { type = NavType.StringType}),
            navArgument("depositType" , builder = { type = NavType.ParcelableType(DepositType::class.java) })
        )
    ) {
        SavingsAccountTransactionScreen(
            navigateBack = onBackPressed
        )
    }
}

fun NavGraphBuilder.syncSavingsAccountTransactionScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = SavingsScreens.SavingsSyncAccountTransaction.route
    ) {
        SyncSavingsAccountTransactionScreenRoute(
            onBackPressed = onBackPressed
        )
    }
}