package com.mifos.feature.loan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.noncore.DataTable
import com.mifos.feature.loan.group_loan_account.GroupLoanAccountScreen
import com.mifos.feature.loan.loan_account.LoanAccountScreen
import com.mifos.feature.loan.loan_account_summary.LoanAccountSummaryScreen
import com.mifos.feature.loan.loan_approval.LoanAccountApprovalScreen
import com.mifos.feature.loan.loan_charge.LoanChargeScreen
import com.mifos.feature.loan.loan_disbursement.LoanAccountDisbursementScreen
import com.mifos.feature.loan.loan_repayment.LoanRepaymentScreen
import com.mifos.feature.loan.loan_repayment_schedule.LoanRepaymentScheduleScreen
import com.mifos.feature.loan.loan_transaction.LoanTransactionsScreen

/**
 * Created by Pronay Sarker on 16/08/2024 (2:24 AM)
 */

fun NavGraphBuilder.loanNavGraph(
    navController: NavController,
    onDocumentsClicked: (Int, String) -> Unit,
    onMoreInfoClicked: (Int) -> Unit
) {
    navigation(
        startDestination = LoanScreens.LoanAccountSummaryScreen.route,
        route = "loan_route"
    ) {
        loanAccountSummary(
            onBackPressed = navController::popBackStack,
            onMoreInfoClicked = onMoreInfoClicked,
            onTransactionsClicked = navController::navigateToLoanTransactionScreen,
            onRepaymentScheduleClicked = navController::navigateToLoanRepaymentScheduleScreen,
            onDocumentsClicked = { onDocumentsClicked(it, Constants.ENTITY_TYPE_LOANS) },
            onChargesClicked = navController::navigateToLoanChargesScreen,
            approveLoan = navController::navigateToLoanApprovalScreen,
            disburseLoan = navController::navigateToLoanDisbursementScreen,
            onRepaymentClick = navController::navigateToLoanRepaymentScreen
        )

        loanDisbursementScreen {
            navController.popBackStack()
        }
        loanApprovalScreen {
            navController.popBackStack()
        }
        loanRepaymentSchedule {
            navController.popBackStack()
        }
        loanTransactionScreen {
            navController.popBackStack()
        }
        loanChargeScreen {
            navController.popBackStack()
        }
        loanRepaymentScreen{
            navController.popBackStack()
        }
    }
}

fun NavGraphBuilder.groupLoanScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = LoanScreens.GroupLoanScreen.route,
        arguments = listOf(
            navArgument(name = Constants.GROUP_ID, builder = { type = NavType.IntType })
        )
    ) {
        GroupLoanAccountScreen(
            onBackPressed = {},
        )
    }
}

fun NavGraphBuilder.addLoanAccountScreen(
    onBackPressed: () -> Unit,
    dataTable: (List<DataTable>, LoansPayload) -> Unit
) {
    composable(
        route = LoanScreens.LoanAccountScreen.route,
        arguments = listOf(
            navArgument(name = Constants.CLIENT_ID, builder = { type = NavType.IntType })
        )
    ) {
        LoanAccountScreen(
            onBackPressed = onBackPressed,
            dataTable = dataTable
        )
    }
}

fun NavGraphBuilder.loanAccountSummary(
    onBackPressed: () -> Unit,
    onMoreInfoClicked: (Int) -> Unit,
    onTransactionsClicked: (loadId: Int) -> Unit,
    onRepaymentScheduleClicked: (loanId: Int) -> Unit,
    onDocumentsClicked: (Int) -> Unit,
    onChargesClicked: (Int) -> Unit,
    approveLoan: (loadId: Int, loanWithAssociations: LoanWithAssociations) -> Unit,
    disburseLoan: (Int) -> Unit,
    onRepaymentClick: (LoanWithAssociations) -> Unit
) {
    composable(
        route = LoanScreens.LoanAccountSummaryScreen.route,
        arguments = listOf(
            navArgument(name = Constants.LOAN_ACCOUNT_NUMBER, builder = { type = NavType.IntType })
        )
    ) {
        LoanAccountSummaryScreen(
            navigateBack = onBackPressed,
            onMoreInfoClicked = onMoreInfoClicked,
            onTransactionsClicked = onTransactionsClicked,
            onRepaymentScheduleClicked = onRepaymentScheduleClicked,
            onDocumentsClicked = onDocumentsClicked,
            onChargesClicked = onChargesClicked,
            approveLoan = approveLoan,
            disburseLoan = disburseLoan,
            onRepaymentClick = onRepaymentClick
        )
    }
}

fun NavGraphBuilder.loanApprovalScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = LoanScreens.LoanApprovalScreen.route,
        arguments = listOf(
            navArgument(name = "arg", builder = { type = NavType.StringType })
        )
    ) {
        LoanAccountApprovalScreen(
            navigateBack = onBackPressed
        )
    }
}

fun NavGraphBuilder.loanChargeScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = LoanScreens.LoanChargeScreen.route,
        arguments = listOf(
            navArgument(name = Constants.LOAN_ACCOUNT_NUMBER, builder = { type = NavType.IntType })
        )
    ) {
        LoanChargeScreen(
            onBackPressed = onBackPressed
        )
    }
}

fun NavGraphBuilder.loanDisbursementScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = LoanScreens.LoanDisbursementScreen.route,
        arguments = listOf(
            navArgument(name = Constants.LOAN_ACCOUNT_NUMBER, builder = { type = NavType.IntType })
        )
    ) {
        LoanAccountDisbursementScreen(
            navigateBack = onBackPressed
        )
    }
}

fun NavGraphBuilder.loanRepaymentScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = LoanScreens.LoanRepaymentScreen.route,
        arguments = listOf(
            navArgument(name = Constants.LOAN_WITH_ASSOCIATIONS , builder = { type = NavType.StringType })
        )
    ) {
        LoanRepaymentScreen(
            navigateBack = onBackPressed
        )
    }
}

fun NavGraphBuilder.loanRepaymentSchedule(
    onBackPressed: () -> Unit
) {
    composable(
        route = LoanScreens.LoanRepaymentSchedule.route,
        arguments = listOf(
            navArgument(name = Constants.LOAN_ACCOUNT_NUMBER, builder = { type = NavType.IntType })
        )

    ) {
        LoanRepaymentScheduleScreen(
            navigateBack = onBackPressed
        )
    }
}

fun NavGraphBuilder.loanTransactionScreen(
    onBackPressed: () -> Unit
) {
    composable(
        route = LoanScreens.LoanTransactionScreen.route,
        arguments = listOf(
            navArgument(name = Constants.LOAN_ACCOUNT_NUMBER, builder = { type = NavType.IntType })
        )
    ) {
        LoanTransactionsScreen(
            navigateBack = onBackPressed
        )
    }
}

fun NavController.navigateToLoanAccountScreen(clientId: Int) {
    navigate(LoanScreens.LoanAccountScreen.argument(clientId))
}

fun NavController.navigateToLoanAccountSummaryScreen(loanAccountNumber: Int) {
    navigate(LoanScreens.LoanAccountSummaryScreen.argument(loanAccountNumber))
}

fun NavController.navigateToLoanDisbursementScreen(loanAccountNumber: Int) {
    navigate(LoanScreens.LoanDisbursementScreen.argument(loanAccountNumber))
}

fun NavController.navigateToLoanTransactionScreen(loanAccountNumber: Int) {
    navigate(LoanScreens.LoanTransactionScreen.argument(loanAccountNumber))
}

fun NavController.navigateToLoanRepaymentScheduleScreen(loanAccountNumber: Int) {
    navigate(LoanScreens.LoanRepaymentSchedule.argument(loanAccountNumber))
}

fun NavController.navigateToLoanChargesScreen(loanAccountNumber: Int) {
    navigate(LoanScreens.LoanChargeScreen.argument(loanAccountNumber))
}

fun NavController.navigateToLoanApprovalScreen(
    loanId: Int,
    loanWithAssociations: LoanWithAssociations
) {
    navigate(LoanScreens.LoanApprovalScreen.argument(loanId, loanWithAssociations))
}

fun NavController.navigateToLoanRepaymentScreen(loanWithAssociations: LoanWithAssociations) {
    navigate(LoanScreens.LoanRepaymentScreen.argument(loanWithAssociations))
}

fun NavController.navigateToGroupLoanScreen(groupId : Int){
    navigate(LoanScreens.GroupLoanScreen.argument(groupId))
}