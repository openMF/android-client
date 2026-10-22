/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.loan.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable
import kpt.core.base.ui.nav.FeatureDestination
import kpt.core.base.ui.nav.composableWithPushTransitions
import kpt.core.base.ui.nav.popBackStackSafely
import kpt.core.common.utils.Constants
import kpt.feature.document.navigation.navigateToDocumentList
import kpt.feature.loan.approval.LoanApprovalScreen
import kpt.feature.loan.chargeform.LoanChargeFormScreen
import kpt.feature.loan.charges.LoanChargesScreen
import kpt.feature.loan.disbursement.LoanDisbursementScreen
import kpt.feature.loan.grouploan.GroupLoanApplicationScreen
import kpt.feature.loan.newloan.NewLoanApplicationScreen
import kpt.feature.loan.profile.LoanPrimaryAction
import kpt.feature.loan.profile.LoanProfileScreen
import kpt.feature.loan.profile.components.LoanProfileActionItem
import kpt.feature.loan.repayment.LoanRepaymentScreen
import kpt.feature.loan.rescheduleform.LoanRescheduleFormScreen
import kpt.feature.loan.reschedules.LoanReschedulesScreen
import kpt.feature.loan.schedule.LoanScheduleScreen
import kpt.feature.loan.summary.LoanSummaryAct
import kpt.feature.loan.summary.LoanSummaryMenuItem
import kpt.feature.loan.summary.LoanSummaryScreen
import kpt.feature.loan.transactions.LoanTransactionsScreen
import kpt.feature.loan.transfer.LoanTransferScreen

/**
 * A loan account's profile. `loanId` is required — the original's route defaulted it, so a missing
 * argument produced a screen that asked Fineract for loan `-1`.
 */
@Serializable
data class LoanProfileRoute(val loanId: Int)

/**
 * The repayment schedule and the transaction history.
 *
 * Both carry only `loanId`: they are projections of the SAME `loans/{id}?associations=all` read the
 * profile already cached, so there is nothing else to pass. The original's routes called these
 * `loanAccountNumber`, which they never were — the value was always the loan's primary key.
 */
@Serializable
data class LoanScheduleRoute(val loanId: Int)

@Serializable
data class LoanTransactionsRoute(val loanId: Int)

/**
 * Approve a pending loan.
 *
 * `loanId` only. The original route carried the whole `LoanWithAssociations` JSON-encoded through
 * the back stack — see [kpt.feature.loan.approval.LoanApprovalViewModel]'s header for the three
 * things that broke.
 */
@Serializable
data class LoanApprovalRoute(val loanId: Int)

/**
 * Record a repayment.
 *
 * `loanId` only, where the original's route carried five fields — client name, account number,
 * product name and arrears alongside the id — and branched in the ViewModel's `init` on whether
 * `loanAccountNumber` was empty to decide whether to fetch them. Two call sites existed, one
 * passing the whole loan and one passing just the id, so the screen had two header-population
 * paths. Reading them from the cached loan removes both.
 */
@Serializable
data class LoanRepaymentRoute(val loanId: Int)

/**
 * A loan account's SUMMARY — the screen a loan hit in SEARCH and a loan on GROUP detail open in the
 * original, as distinct from [LoanProfileRoute], which the client's loan LIST opens.
 *
 * Both exist and both are the original's; see [kpt.feature.loan.summary.LoanSummaryViewModel] for
 * the caller table. This is the one that makes `feature/loan` reachable at all, because the
 * client's loan list is not ported yet.
 */
@Serializable
data class LoanSummaryRoute(val loanId: Int)

/**
 * Disburse an approved loan.
 *
 * Reached only from the SUMMARY — `waitingForDisbursal` is one of the two states the loan profile
 * collapses into "Unknown", so the profile never offers this action.
 */
@Serializable
data class LoanDisbursementRoute(val loanId: Int)

/**
 * The charges levied on a loan.
 *
 * Reached from BOTH loan detail screens — the summary's overflow menu and the profile's "Charges"
 * row — which is why it is a top-level route here rather than nested under either.
 */
@Serializable
data class LoanChargesRoute(val loanId: Int)

/**
 * Transfer funds out of a loan.
 *
 * `loanId` only, where the original's route carried FIVE fields — client id, office id, account
 * number, currency and account id — each snapshotted by the caller from the loan it had just
 * rendered. The transfer screen reads the loan itself; see
 * [kpt.feature.loan.transfer.LoanTransferViewModel].
 */
@Serializable
data class LoanTransferRoute(val loanId: Int)

/**
 * Add a charge to a loan.
 *
 * Its own destination, where the original composed it as a `ModalBottomSheet` INSIDE the charges
 * list behind a boolean that list owned — see
 * [kpt.feature.loan.chargeform.LoanChargeFormViewModel].
 */
@Serializable
data class LoanChargeFormRoute(val loanId: Int)

/** Every reschedule proposed on a loan, with approve and reject. */
@Serializable
data class LoanReschedulesRoute(val loanId: Int)

/** Propose a reschedule. Reached from the reschedules list's "+". */
@Serializable
data class LoanRescheduleFormRoute(val loanId: Int)

/**
 * Apply for a new INDIVIDUAL loan. Reached from the client's loan-accounts list "add account"
 * affordance, whose whole chain was already ported and `null`-gated — the original's
 * `clientLoanAccountsDestination(createAccount = …)` edge.
 */
@Serializable
data class NewLoanApplicationRoute(val clientId: Int)

fun NavController.navigateToNewLoanApplication(clientId: Int, navOptions: NavOptions? = null) =
    navigate(NewLoanApplicationRoute(clientId), navOptions)

/**
 * Apply for a GROUP loan. Reached from group detail's overflow menu — the original's
 * `addGroupLoanAccount`.
 *
 * Keyed on `groupId`, not `loanId`: this is the one route in this file that CREATES a loan rather
 * than opening one. It lives in `feature/loan` because the form is loan-shaped (product template,
 * terms, amortization) and `feature/groups` would otherwise need every loan model and store.
 */
@Serializable
data class GroupLoanApplicationRoute(val groupId: Int)

fun NavController.navigateToGroupLoanApplication(groupId: Int, navOptions: NavOptions? = null) =
    navigate(GroupLoanApplicationRoute(groupId), navOptions)

fun NavController.navigateToLoanProfile(loanId: Int, navOptions: NavOptions? = null) =
    navigate(LoanProfileRoute(loanId), navOptions)

fun NavController.navigateToLoanSummary(loanId: Int, navOptions: NavOptions? = null) =
    navigate(LoanSummaryRoute(loanId), navOptions)

/**
 * Record a repayment, reached directly rather than through a loan detail screen.
 *
 * The client's loan-accounts list offers "Make Repayment" on an ACTIVE account's action menu, as
 * the original did (`clientLoanAccountsDestination(navigateToMakeRepayment = …)`), so the officer
 * can take a payment without opening the loan first.
 */
fun NavController.navigateToLoanRepayment(loanId: Int, navOptions: NavOptions? = null) =
    navigate(LoanRepaymentRoute(loanId), navOptions)

/**
 * `@FeatureDestination`, so the generator registers it ONCE — the same reasoning as the other
 * shared drill-downs. The client's loan list and group detail both open it.
 */
@FeatureDestination
fun NavGraphBuilder.loanDestinations(navController: NavController) {
    composableWithPushTransitions<LoanProfileRoute> {
        LoanProfileScreen(
            onBackPressed = navController::popBackStackSafely,
            onDetail = { item, loanId ->
                when (item) {
                    LoanProfileActionItem.RepaymentSchedule ->
                        navController.navigate(LoanScheduleRoute(loanId))
                    LoanProfileActionItem.Transactions ->
                        navController.navigate(LoanTransactionsRoute(loanId))
                    LoanProfileActionItem.Charges ->
                        navController.navigate(LoanChargesRoute(loanId))
                    LoanProfileActionItem.Reschedules ->
                        navController.navigate(LoanReschedulesRoute(loanId))
                    // The GENERIC document list, which is entity-agnostic and already ported —
                    // the original passed `Constants.ENTITY_TYPE_LOANS` to the same screen.
                    LoanProfileActionItem.Documents ->
                        navController.navigateToDocumentList(Constants.ENTITY_TYPE_LOANS, loanId)
                    // Unreachable: only the items in `PORTED_DETAILS` are drawn as rows.
                    else -> Unit
                }
            },
            // Only the rows whose destination exists are drawn. The other eleven arrive with the
            // rest of S4; until then they are ABSENT rather than tappable-and-dead.
            availableDetails = PORTED_DETAILS,
            onPrimaryAction = { action, loanId ->
                when (action) {
                    LoanPrimaryAction.Approve -> navController.navigate(LoanApprovalRoute(loanId))
                    LoanPrimaryAction.Repayment -> navController.navigate(LoanRepaymentRoute(loanId))
                    LoanPrimaryAction.Transfer -> navController.navigate(LoanTransferRoute(loanId))
                }
            },
            availableActions = PORTED_ACTIONS,
            // The account-details screen is the rest of S4; the card is not clickable until then.
            onAccountDetails = null,
        )
    }

    composableWithPushTransitions<LoanScheduleRoute> {
        LoanScheduleScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanTransactionsRoute> {
        LoanTransactionsScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanApprovalRoute> {
        LoanApprovalScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanRepaymentRoute> {
        LoanRepaymentScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanDisbursementRoute> {
        LoanDisbursementScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanTransferRoute> {
        LoanTransferScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanChargesRoute> {
        LoanChargesScreen(
            onBackPressed = navController::popBackStackSafely,
            onAddCharge = { loanId -> navController.navigate(LoanChargeFormRoute(loanId)) },
        )
    }

    composableWithPushTransitions<LoanChargeFormRoute> {
        LoanChargeFormScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanReschedulesRoute> {
        LoanReschedulesScreen(
            onBackPressed = navController::popBackStackSafely,
            onPropose = { loanId -> navController.navigate(LoanRescheduleFormRoute(loanId)) },
        )
    }

    composableWithPushTransitions<LoanRescheduleFormRoute> {
        LoanRescheduleFormScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<GroupLoanApplicationRoute> {
        GroupLoanApplicationScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<NewLoanApplicationRoute> {
        NewLoanApplicationScreen(onBackPressed = navController::popBackStackSafely)
    }

    composableWithPushTransitions<LoanSummaryRoute> {
        LoanSummaryScreen(
            onBackPressed = navController::popBackStackSafely,
            onMenuItem = { item, loanId ->
                when (item) {
                    LoanSummaryMenuItem.Transactions ->
                        navController.navigate(LoanTransactionsRoute(loanId))
                    LoanSummaryMenuItem.RepaymentSchedule ->
                        navController.navigate(LoanScheduleRoute(loanId))
                    LoanSummaryMenuItem.Charges ->
                        navController.navigate(LoanChargesRoute(loanId))
                    LoanSummaryMenuItem.Documents ->
                        navController.navigateToDocumentList(Constants.ENTITY_TYPE_LOANS, loanId)
                    // Unreachable: only `SUMMARY_MENU_ITEMS` entries are drawn. `MoreInfo` needs
                    // the loan data-table screen.
                    LoanSummaryMenuItem.MoreInfo -> Unit
                }
            },
            availableMenuItems = SUMMARY_MENU_ITEMS,
            onAction = { action, loanId ->
                when (action) {
                    LoanSummaryAct.Approve -> navController.navigate(LoanApprovalRoute(loanId))
                    LoanSummaryAct.MakeRepayment -> navController.navigate(LoanRepaymentRoute(loanId))
                    LoanSummaryAct.Disburse -> navController.navigate(LoanDisbursementRoute(loanId))
                    // Live here for the first time: in the original the SUMMARY's Transfer
                    // dispatched `NavigateToLoanTransfer`, whose handler was an empty `{ }` on
                    // both the ViewModel and the screen side — a drawn, enabled, dead button. The
                    // PROFILE's Transfer worked. Same action, two screens, one of them broken.
                    LoanSummaryAct.Transfer -> navController.navigate(LoanTransferRoute(loanId))
                }
            },
            availableActions = SUMMARY_ACTIONS,
        )
    }
}

/**
 * The detail rows that have somewhere to go, in the profile's declared order.
 *
 * This list IS the gate — the profile draws exactly these. Porting a detail screen means adding
 * its route above and its item here; nothing else in the profile changes.
 */
private val PORTED_DETAILS = listOf(
    LoanProfileActionItem.RepaymentSchedule,
    LoanProfileActionItem.Transactions,
    LoanProfileActionItem.Charges,
    LoanProfileActionItem.Reschedules,
    LoanProfileActionItem.Documents,
)

/**
 * The primary-button actions that have somewhere to go. Approve landed in S4b, Repayment in S4c.
 *
 * Repayment is the one that matters most: ACTIVE is the common loan status, so this is the button
 * a field officer presses on nearly every visit.
 */
private val PORTED_ACTIONS = setOf(
    LoanPrimaryAction.Approve,
    LoanPrimaryAction.Repayment,
    LoanPrimaryAction.Transfer,
)

/**
 * The SUMMARY's overflow entries that have somewhere to go, in the original's order.
 *
 * `MoreInfo` needs the loan data-table screen, `Documents` the loan document list and `Charges` the
 * loan charges list — none ported. An entry with no destination is not drawn, and when NONE are the
 * overflow icon itself is not drawn either: a menu button that opens an empty menu is the same dead
 * control as a dead row.
 */
private val SUMMARY_MENU_ITEMS = listOf(
    LoanSummaryMenuItem.Transactions,
    LoanSummaryMenuItem.RepaymentSchedule,
    LoanSummaryMenuItem.Charges,
    LoanSummaryMenuItem.Documents,
)

/**
 * The summary's primary actions that have somewhere to go.
 *
 * Disburse (`loanDisbursement`) and Transfer (`amountTransfer`) are the rest of S4. Note that
 * Transfer was a DEAD button in the original too — `NavigateToLoanTransfer` dispatched an event
 * whose handler was an empty `{ }` on both sides — so disabling it here is strictly better than
 * what shipped.
 */
private val SUMMARY_ACTIONS = setOf(
    LoanSummaryAct.Approve,
    LoanSummaryAct.MakeRepayment,
    LoanSummaryAct.Disburse,
    LoanSummaryAct.Transfer,
)
