/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.center.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import kpt.core.base.ui.nav.FeatureDestination
import kpt.core.base.ui.nav.composableWithPushTransitions
import kpt.core.base.ui.nav.composableWithStayTransitions
import kpt.core.base.ui.nav.popBackStackSafely
import kpt.feature.activate.navigation.navigateToActivateCenter
import kpt.feature.center.detail.CenterDetailScreen
import kpt.feature.center.list.CenterListScreen

/** The tab's graph. Mirrors `ClientDestination` — the tab points at the graph, not at a screen. */
@Serializable
data object CenterDestination

@Serializable
data object CenterListRoute

/** Center detail. `centerId` is a type-safe route argument, not a SavedStateHandle default. */
@Serializable
data class CenterDetailRoute(val centerId: Int)

fun NavController.navigateToCenterList(navOptions: NavOptions? = null) =
    navigate(CenterListRoute, navOptions)

fun NavController.navigateToCenters(navOptions: NavOptions? = null) =
    navigate(CenterDestination, navOptions)

fun NavController.navigateToCenterDetail(centerId: Int, navOptions: NavOptions? = null) =
    navigate(CenterDetailRoute(centerId), navOptions)

/**
 * The center list — a TOP-LEVEL destination on the authenticated graph, collected into
 * `GeneratedFeatureDestinations` by `:cmp-navigation:generateFeatureDestinations`.
 *
 * This file MUST live in a `navigation` package: the generator calls `scan(roots, "navigation")`, so
 * an annotated destination anywhere else is silently skipped — a clean build and a screen that does
 * not exist in the app.
 *
 * ## What is NOT here
 *
 * `CreateNewCenter` and `SyncCentersDialog` from `6b66e8a43` are not ported in this slice. Both are
 * WRITE paths and neither has a command repository behind it yet — `CenterCommandRepository` exists
 * but is not wired to a create endpoint. They are passed as `null` rather than as empty lambdas, so
 * the FAB and the sync action are ABSENT rather than drawn inert
 * (RULE-IMPL-DEAD-CLICKABLE-001), and they are tracked in CAPABILITY_GAPS.
 */
@FeatureDestination
fun NavGraphBuilder.centerListDestination(navController: NavController) {
    composableWithStayTransitions<CenterListRoute> {
        CenterListScreen(onCenterClick = navController::navigateToCenterDetail)
    }

    // Nested drill-down, deliberately NOT annotated: only TOP-LEVEL entries carry
    // @FeatureDestination, and annotating this would register detail as its own root entry.
    composableWithPushTransitions<CenterDetailRoute> {
        CenterDetailScreen(
            onBackClick = navController::popBackStackSafely,
            // Group detail belongs to feature/groups and lands with it. Null until then, so a group
            // row in a center renders without a click rather than with one that goes nowhere.
            onGroupClick = null,
            onActivate = navController::navigateToActivateCenter,
        )
    }
}

/**
 * The center graph for the bottom-nav tab's INNER NavHost.
 *
 * Same two-NavHost split as `clientTabGraph`: the list is the tab's start destination so the bottom
 * bar persists and the tab keeps its own back stack, while detail pushes on the OUTER controller and
 * covers the bar.
 *
 * Not annotated — `@FeatureDestination` collects entries for the outer graph, and registering the
 * same routes twice is a duplicate-destination crash at NavHost build.
 */
fun NavGraphBuilder.centerTabGraph(outerNav: NavController) {
    navigation<CenterDestination>(startDestination = CenterListRoute) {
        composableWithStayTransitions<CenterListRoute> {
            CenterListScreen(onCenterClick = outerNav::navigateToCenterDetail)
        }
    }
}
