/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.client.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import kpt.core.base.ui.nav.FeatureDestination
import kpt.core.base.ui.nav.composableWithPushTransitions
import kpt.core.base.ui.nav.composableWithStayTransitions
import kpt.core.base.ui.nav.popBackStackSafely
import kpt.feature.client.address.ClientAddressScreen
import kpt.feature.client.charges.ClientChargesScreen
import kpt.feature.client.closure.ClientClosureScreen
import kpt.feature.client.collateral.ClientCollateralScreen
import kpt.feature.client.collateraldetails.ClientCollateralDetailsScreen
import kpt.feature.client.details.ClientDetailsScreen
import kpt.feature.client.documents.ClientDocumentsScreen
import kpt.feature.client.general.ClientGeneralScreen
import kpt.feature.client.identifiers.ClientIdentifiersScreen
import kpt.feature.client.list.ClientListScreen
import kpt.feature.client.loanaccounts.ClientLoanAccountsScreen
import kpt.feature.client.profile.ClientProfileScreen
import kpt.feature.client.signature.ClientSignatureScreen
import kpt.feature.client.staff.ClientStaffScreen
import kpt.feature.client.transfer.ClientTransferScreen
import kpt.feature.loan.navigation.navigateToLoanProfile
import kpt.feature.loan.navigation.navigateToLoanRepayment
import kpt.feature.loan.navigation.navigateToNewLoanApplication
import kpt.feature.note.navigation.navigateToClientNotes
import kpt.feature.note.navigation.noteDestinations

/** The tab's graph. Mirrors `HomeDestination` — the tab points at the graph, not at a screen. */
@Serializable
data object ClientDestination

@Serializable
data object ClientListRoute

/**
 * The client PROFILE — what tapping a client in the list opens.
 *
 * Named for the original's `ClientProfileRoute`, which is what `clientListScreenRoute(onClientSelect
 * = navController::navigateToClientProfileRoute)` pointed at. An earlier pass of this port called it
 * `ClientDetailRoute` and pointed it at a rebuilt screen; the original's `clientDetails` (884 lines)
 * is a SEPARATE, legacy destination the list never opened.
 *
 * `clientId` is a type-safe route argument, not a SavedStateHandle default.
 */
@Serializable
data class ClientProfileRoute(val clientId: Int)

/**
 * Charges levied on a client.
 *
 * Typed `clientId`, NOT the original's `resourceId: Int` + `resourceType: String`. Every caller of
 * that route passed "clients", so the type parameter was a hole rather than flexibility; loan and
 * savings charges get their own routes when those slices land.
 */
@Serializable
data class ClientChargesRoute(val clientId: Int)

/**
 * Assign a loan officer to a client.
 *
 * `clientId`, not the original's bare `id: Int = -1`. The default existed because the original read
 * the route through `savedStateHandle.toRoute<ClientStaffRoute>()` and needed a value when the key
 * was absent — a screen reachable with id `-1` that would then ask Fineract for client -1. A
 * type-safe route with no default makes the caller supply it.
 */
@Serializable
data class ClientStaffRoute(val clientId: Int)

/** Close a client. Same `clientId`-without-a-default reasoning as [ClientStaffRoute]. */
@Serializable
data class ClientClosureRoute(val clientId: Int)

/** Propose transferring a client to another office. */
@Serializable
data class ClientTransferRoute(val clientId: Int)

/** Pledge collateral against a client. */
@Serializable
data class ClientCollateralRoute(val clientId: Int)

/**
 * The client's GENERAL view — performance history + linked-account actions. Reached from the
 * profile's "General" row, and where the client's accounts actually live.
 */
@Serializable
data class ClientGeneralRoute(val clientId: Int)

/** The client's identity documents. Reached from the profile's "Identifiers" row. */
@Serializable
data class ClientIdentifiersRoute(val clientId: Int)

/**
 * The client's DETAIL fields + its eight actions — what the profile's ProfileCard taps through to.
 *
 * Named for the original's `clientDetailsProfile`. Not to be confused with the original's separate
 * legacy `clientDetails` screen, which nothing navigates to (see [ClientProfileRoute]).
 */
@Serializable
data class ClientDetailsRoute(val clientId: Int)

/** The client's postal addresses. Reached from the profile's "Address" row. */
@Serializable
data class ClientAddressRoute(val clientId: Int)

/**
 * The collateral a client has already PLEDGED — read-only. Reached from client-general's
 * "Collateral Data" row.
 *
 * Distinct from [ClientCollateralRoute], which is the ADD form reached from client details'
 * "Create Collateral". An earlier pass pointed General's row at the form; the original points it
 * here.
 */
@Serializable
data class ClientCollateralDetailsRoute(val clientId: Int)

/**
 * The client's LOAN ACCOUNTS list.
 *
 * In the original this is the only caller of the loan account PROFILE
 * (`clientLoanAccountsDestination(navigateToViewAccount = navigateToLoanAccountProfileScreen)`),
 * which is why it is ported here rather than later: `feature/loan`'s profile had no caller at all
 * until S4d wired search to the loan SUMMARY, and this is its own path in. See the spec's
 * "Verify the PATH into the screen" rule.
 */
@Serializable
data class ClientLoanAccountsRoute(val clientId: Int)

/**
 * The client's signature. Reached from client details' "Update Signature".
 *
 * Carries `clientName` + `accountNo` because the screen shows them in its header and has no
 * client read of its own — it reads the DOCUMENT list. The original threaded the same two values
 * through its route for the same reason.
 */
@Serializable
data class ClientSignatureRoute(
    val clientId: Int,
    val clientName: String,
    val accountNo: String,
)

/** The client's attached documents. Reached from the profile's "Documents" row. */
@Serializable
data class ClientDocumentsRoute(val clientId: Int)

fun NavController.navigateToClientList(navOptions: NavOptions? = null) =
    navigate(ClientListRoute, navOptions)

fun NavController.navigateToClients(navOptions: NavOptions? = null) =
    navigate(ClientDestination, navOptions)

fun NavController.navigateToClientProfile(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientProfileRoute(clientId), navOptions)

fun NavController.navigateToClientCharges(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientChargesRoute(clientId), navOptions)

fun NavController.navigateToClientStaff(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientStaffRoute(clientId), navOptions)

fun NavController.navigateToClientClosure(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientClosureRoute(clientId), navOptions)

fun NavController.navigateToClientTransfer(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientTransferRoute(clientId), navOptions)

fun NavController.navigateToClientCollateral(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientCollateralRoute(clientId), navOptions)

fun NavController.navigateToClientGeneral(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientGeneralRoute(clientId), navOptions)

fun NavController.navigateToClientIdentifiers(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientIdentifiersRoute(clientId), navOptions)

fun NavController.navigateToClientDetails(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientDetailsRoute(clientId), navOptions)

fun NavController.navigateToClientAddress(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientAddressRoute(clientId), navOptions)

fun NavController.navigateToClientCollateralDetails(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientCollateralDetailsRoute(clientId), navOptions)

fun NavController.navigateToClientLoanAccounts(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientLoanAccountsRoute(clientId), navOptions)

fun NavController.navigateToClientSignature(
    clientId: Int,
    clientName: String,
    accountNo: String,
    navOptions: NavOptions? = null,
) = navigate(ClientSignatureRoute(clientId, clientName, accountNo), navOptions)

fun NavController.navigateToClientDocuments(clientId: Int, navOptions: NavOptions? = null) =
    navigate(ClientDocumentsRoute(clientId), navOptions)

/**
 * The client list — a TOP-LEVEL destination on the authenticated graph, so it is annotated and
 * collected into `GeneratedFeatureDestinations` by `:cmp-navigation:generateFeatureDestinations`.
 *
 * This file MUST live in a `navigation` package: the generator calls `scan(roots, "navigation")`, so
 * an annotated destination anywhere else is silently skipped and the screen simply does not exist in
 * the app — with a clean build. (A wrong SIGNATURE throws loudly; a wrong PACKAGE does not. Found the
 * hard way in S3a, when `ClientModule` appeared in the Koin aggregate and the destination did not.)
 *
 * The original registered ~38 client destinations through one `clientNavGraph`. Those arrive with
 * their screens across S3a–S3d; nested drill-downs stay nested and are NOT annotated — only
 * top-level entries are (spec §4).
 *
 * Create-client does not exist yet, so it is passed as `null` rather than as an empty lambda — the
 * screen then omits the FAB entirely instead of drawing one that does nothing. S3c supplies it.
 */
@FeatureDestination
fun NavGraphBuilder.clientListDestination(navController: NavController) {
    composableWithStayTransitions<ClientListRoute> {
        ClientListScreen(
            // Detail exists as of S3b, so rows are clickable. Create-client is still null — S3c —
            // and null (not an empty lambda) means the FAB is not drawn rather than drawn inert.
            onClientClick = navController::navigateToClientProfile,
            onCreateClient = null,
        )
    }

    // Nested drill-down: registered inside the feature graph and deliberately NOT annotated. Only
    // TOP-LEVEL entries carry @FeatureDestination (spec §4) — annotating this one would register the
    // profile screen as its own root entry.
    composableWithPushTransitions<ClientProfileRoute> {
        ClientProfileScreen(
            onNavigateBack = navController::popBackStackSafely,
            onNavigateToClientDetails = navController::navigateToClientDetails,
            // Live as of S3b/S5. The rest are null, so their ROWS are not drawn at all rather than
            // drawn inert (RULE-IMPL-DEAD-CLICKABLE-001).
            onNotes = navController::navigateToClientNotes,
            onIdentifiers = navController::navigateToClientIdentifiers,
            onDocuments = navController::navigateToClientDocuments,
            onViewAddress = navController::navigateToClientAddress,
            onViewAssociatedAccounts = navController::navigateToClientGeneral,
            // Blocked on Google Maps — see `client-pinpoint-blocked-on-maps-key`.
            onPinpointLocation = null,
            onNavigateToGroupDetails = null,
        )
    }

    // Charges list. Nested drill-down — NOT annotated: only TOP-LEVEL entries carry
    // @FeatureDestination (spec §4), and annotating this would register it as its own root entry.
    composableWithPushTransitions<ClientChargesRoute> {
        ClientChargesScreen(onBackClick = navController::popBackStackSafely)
    }

    // Assign staff. Nested drill-down, NOT annotated — same reason as charges.
    composableWithPushTransitions<ClientStaffRoute> {
        ClientStaffScreen(
            onNavigateBack = navController::popBackStackSafely,
            // The original navigated FORWARD to client detail on success, which re-pushed detail on
            // top of the detail the officer came FROM. Popping returns to the live detail screen,
            // which re-reads the client and shows the new officer.
            onNavigateNext = { navController.popBackStackSafely() },
        )
    }

    // Close a client. Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientClosureRoute> {
        ClientClosureScreen(
            onNavigateBack = navController::popBackStackSafely,
            // On success the client is CLOSED, so returning to its detail screen is right: the
            // detail re-reads and shows the closed status. The original navigated to the client
            // list instead, which discarded the back stack the officer came through.
            onNavigateNext = { navController.popBackStackSafely() },
        )
    }

    // Propose transfer. Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientTransferRoute> {
        ClientTransferScreen(
            onNavigateBack = navController::popBackStackSafely,
            onNavigateNext = { navController.popBackStackSafely() },
        )
    }

    // Add collateral. Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientCollateralRoute> {
        ClientCollateralScreen(
            onNavigateBack = navController::popBackStackSafely,
            onNavigateNext = { navController.popBackStackSafely() },
        )
    }

    // The client's general view. Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientGeneralRoute> {
        ClientGeneralScreen(
            onNavigateBack = navController::popBackStackSafely,
            // The original's "Collateral Data" row opens the read-only PLEDGED list, not the add
            // form — `clientProfileGeneralDestination(collateralData =
            // navigateToClientCollateralDetailRoute)`. An earlier pass of this port had it on the
            // form; corrected. The add form is reached from client details' "Create Collateral".
            onCollateralData = navController::navigateToClientCollateralDetails,
            onUpcomingCharges = null,
            // The client's loan accounts — and the loan PROFILE's only path in, which is why this
            // row is no longer null-gated. See `ClientLoanAccountsRoute`.
            onLoanAccounts = navController::navigateToClientLoanAccounts,
            onSavingAccounts = null,
            onFixedDepositAccounts = null,
            onRecurringDepositAccounts = null,
            onSharesAccounts = null,
        )
    }

    // The client's identity documents. Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientIdentifiersRoute> {
        ClientIdentifiersScreen(
            onBackPress = navController::popBackStackSafely,
            // The add/update identifier FORM is `clientIdentifiersAddUpdate` (657-line VM,
            // 413-line screen) and is not ported — so the "+" is not drawn at all rather than
            // drawn inert. It arrives with the rest of S3d.
            onAddNewIdentifier = null,
        )
    }

    // The client's detail fields + actions. This is what makes the four S3c action forms
    // reachable: Closure, Transfer, Assign Staff and Create Collateral all route from here.
    // Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientDetailsRoute> {
        ClientDetailsScreen(
            onNavigateBack = navController::popBackStackSafely,
            onClosureApplication = navController::navigateToClientClosure,
            onTransferClient = navController::navigateToClientTransfer,
            onAssignStaff = navController::navigateToClientStaff,
            onCreateCollateral = navController::navigateToClientCollateral,
            onAddCharge = navController::navigateToClientCharges,
            // `clientApplyNewApplications`, `clientUpdateDefaultAccount`, standing instructions,
            // `clientEditDetails` and the photo capture are unported — null so their row / button
            // is absent rather than inert. Signature is live as of this slice.
            onApplyNewApplication = null,
            onUpdateDefaultAccount = null,
            onCreateStandingInstructions = null,
            onUpdateDetails = null,
            onUpdatePhoto = null,
            onUpdateSignature = navController::navigateToClientSignature,
        )
    }

    // The client's postal addresses + attached documents. Nested drill-downs, NOT annotated.
    composableWithPushTransitions<ClientAddressRoute> {
        ClientAddressScreen(
            onNavigateBack = navController::popBackStackSafely,
            // The add-address form is a 610-line screen (S3d) — null, so the "+" is absent.
            onAddAddress = null,
        )
    }

    composableWithPushTransitions<ClientDocumentsRoute> {
        ClientDocumentsScreen(
            onNavigateBack = navController::popBackStackSafely,
            // `clientAddDocuments` is S3d — null, so the "+" is absent. The in-place search
            // filter needs no destination and IS wired.
            onAddDocument = null,
        )
    }

    // The client's pledged collateral, read-only. Nested drill-down, NOT annotated.
    // The client's loan accounts. Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientLoanAccountsRoute> {
        ClientLoanAccountsScreen(
            onNavigateBack = navController::popBackStackSafely,
            // The loan PROFILE, not the summary — this list is what the original points at the
            // profile, and the two are different screens over the same loan.
            onViewAccount = navController::navigateToLoanProfile,
            onMakeRepayment = navController::navigateToLoanRepayment,
            // `newLoanAccount` is a ~2,900-line five-page wizard and is not ported, so the "+"
            // and the empty state's create button are not drawn at all.
            // The new-application wizard in `feature/loan` — the original's
            // `clientLoanAccountsDestination(createAccount = …)`. This was `null` (so the "add
            // account" affordance was ABSENT rather than drawn dead) until S4l landed the screen.
            onAddAccount = navController::navigateToNewLoanApplication,
        )
    }

    composableWithPushTransitions<ClientCollateralDetailsRoute> {
        ClientCollateralDetailsScreen()
    }

    // The client's signature. Nested drill-down, NOT annotated.
    composableWithPushTransitions<ClientSignatureRoute> { entry ->
        val route = entry.toRoute<ClientSignatureRoute>()
        ClientSignatureScreen(
            clientName = route.clientName,
            accountNo = route.accountNo,
            onNavigateBack = navController::popBackStackSafely,
        )
    }

    // Notes list + add/edit, owned by `feature/note` and registered here.
    //
    // The ORIGINAL did exactly this: `noteDestination(navController, onBackPressed)` called from
    // the client graph, with `feature/client` depending on `projects.feature.note`. Keeping the
    // dependency direction is what makes the remaining drill-down slices mechanical — client is
    // the hub, and each feature owns its own routes and screens.
    //
    // Nested, so NOT annotated: only TOP-LEVEL entries carry @FeatureDestination (spec §4).
    noteDestinations(navController)
}

/**
 * The client graph for the bottom-nav tab's INNER NavHost.
 *
 * Two NavHosts are in play and they want different things. [clientListDestination] registers the list
 * on the OUTER authenticated graph, where another feature can push it full-screen. This graph is what
 * the Clients TAB renders: the list is the tab's start destination, so the bottom bar stays visible and
 * the tab keeps its own back stack, while the detail drill-down pushes on the OUTER controller and
 * covers the bar — the standard affordance, and the shape `TabRegistry.extraInlineTabDestinations`
 * documents.
 *
 * Not annotated: `@FeatureDestination` collects entries for the outer graph, and registering the same
 * routes there twice is a duplicate-destination crash at NavHost build.
 */
fun NavGraphBuilder.clientTabGraph(outerNav: NavController) {
    navigation<ClientDestination>(startDestination = ClientListRoute) {
        composableWithStayTransitions<ClientListRoute> {
            ClientListScreen(
                onClientClick = outerNav::navigateToClientProfile,
                onCreateClient = null,
            )
        }
    }
}
