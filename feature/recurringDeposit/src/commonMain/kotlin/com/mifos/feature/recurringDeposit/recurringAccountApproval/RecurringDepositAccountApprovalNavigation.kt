package com.mifos.feature.recurringDeposit.recurringAccountApproval

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class RecurringDepositAccountApprovalRoute(
    val accountId: String,
)

fun NavGraphBuilder.recurringDepositAccountApprovalDestination(
    navigateBack: () -> Unit,
) {
    composable<RecurringDepositAccountApprovalRoute> {
        RecurringDepositAccountApprovalScreen(
            navigateBack = navigateBack,
        )
    }
}

fun NavController.navigateToRecurringDepositAccountApproval(
    accountId: String,
) {
    this.navigate(RecurringDepositAccountApprovalRoute(accountId = accountId))
}