package com.mifos.feature.loan.loanUndoApproval

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class LoanUndoApprovalRoute(val loanId : Int)

fun NavGraphBuilder.loanUndoApprovalDestination(
    navController: NavController
){
    composable<LoanUndoApprovalRoute>{
        LoanUndoApprovalScreen(
            navController = navController,
            navigateBack = navController::popBackStack
        )
    }
}

fun NavController.navigateToLoanUndoApprovalScreen(
    loanId: Int
){
    this.navigate(
        LoanUndoApprovalRoute(loanId = loanId)
    )
}