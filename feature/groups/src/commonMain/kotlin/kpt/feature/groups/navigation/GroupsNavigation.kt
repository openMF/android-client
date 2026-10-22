/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.groups.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import kpt.core.base.ui.nav.FeatureDestination
import kpt.core.base.ui.nav.composableWithPushTransitions
import kpt.core.base.ui.nav.composableWithStayTransitions
import kpt.core.base.ui.nav.popBackStackSafely
import kpt.core.common.utils.Constants
import kpt.feature.activate.navigation.navigateToActivateGroup
import kpt.feature.datatable.navigation.navigateToDataTableList
import kpt.feature.document.navigation.navigateToDocumentList
import kpt.feature.groups.detail.GroupsDetailScreen
import kpt.feature.groups.list.GroupsListScreen
import kpt.feature.loan.navigation.navigateToGroupLoanApplication

/** The tab's graph. Mirrors `ClientDestination` — the tab points at the graph, not at a screen. */
@Serializable
data object GroupsDestination

@Serializable
data object GroupsListRoute

/** Group detail. `groupId` is a type-safe route argument, not a SavedStateHandle default. */
@Serializable
data class GroupsDetailRoute(val groupId: Int)

fun NavController.navigateToGroupsList(navOptions: NavOptions? = null) =
    navigate(GroupsListRoute, navOptions)

fun NavController.navigateToGroups(navOptions: NavOptions? = null) =
    navigate(GroupsDestination, navOptions)

fun NavController.navigateToGroupsDetail(groupId: Int, navOptions: NavOptions? = null) =
    navigate(GroupsDetailRoute(groupId), navOptions)

/**
 * The group list — a TOP-LEVEL destination on the authenticated graph, collected into
 * `GeneratedFeatureDestinations` by `:cmp-navigation:generateFeatureDestinations`.
 *
 * This file MUST live in a `navigation` package: the generator calls `scan(roots, "navigation")`, so
 * an annotated destination anywhere else is silently skipped — a clean build and a screen that does
 * not exist in the app.
 *
 * ## What is NOT here
 *
 * `CreateNewGroup` and `SyncGroupDialog` from `6b66e8a43` are not in this slice.
 * `GroupCommandRepository.createGroup` exists, but create needs an office/staff/center picker whose
 * templates are not ported, and a create form that cannot populate its pickers is worse than none.
 * Tracked in CAPABILITY_GAPS. Activation — the group write that DOES have a complete path — is
 * ported, on the detail screen.
 */
@FeatureDestination
fun NavGraphBuilder.groupsListDestination(navController: NavController) {
    composableWithStayTransitions<GroupsListRoute> {
        GroupsListScreen(onGroupClick = navController::navigateToGroupsDetail)
    }

    // Nested drill-down, deliberately NOT annotated: only TOP-LEVEL entries carry
    // @FeatureDestination, and annotating this would register detail as its own root entry.
    composableWithPushTransitions<GroupsDetailRoute> {
        GroupsDetailScreen(
            onBackClick = navController::popBackStackSafely,
            onActivate = navController::navigateToActivateGroup,
            // The generic document list, scoped to this group — the original's `loadDocumentList`.
            onDocuments = { groupId ->
                navController.navigateToDocumentList(Constants.ENTITY_TYPE_GROUPS, groupId)
            },
            // The group's datatables. The APPLICATION table name is what selects them — the
            // original passed `Constants.DATA_TABLE_NAME_GROUP` here.
            onDataTables = { groupId ->
                navController.navigateToDataTableList(Constants.DATA_TABLE_NAME_GROUP, groupId)
            },
            // The group loan application form in `feature/loan` — the original's
            // `addGroupLoanAccount`. This edge is what makes `GroupLoanApplicationRoute`
            // reachable; it was registered and caller-less until S4k.
            onAddLoanAccount = navController::navigateToGroupLoanApplication,
        )
    }
}

/**
 * The group graph for the bottom-nav tab's INNER NavHost.
 *
 * Same two-NavHost split as `clientTabGraph` / `centerTabGraph`: the list is the tab's start
 * destination so the bottom bar persists and the tab keeps its own back stack, while detail pushes
 * on the OUTER controller and covers the bar.
 *
 * Not annotated — `@FeatureDestination` collects entries for the outer graph, and registering the
 * same routes twice is a duplicate-destination crash at NavHost build.
 */
fun NavGraphBuilder.groupsTabGraph(outerNav: NavController) {
    navigation<GroupsDestination>(startDestination = GroupsListRoute) {
        composableWithStayTransitions<GroupsListRoute> {
            GroupsListScreen(onGroupClick = outerNav::navigateToGroupsDetail)
        }
    }
}
