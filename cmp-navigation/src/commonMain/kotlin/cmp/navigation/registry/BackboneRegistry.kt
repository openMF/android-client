/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.registry

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import cmp.navigation.authenticatednavbar.PendingFeatureScreen
import cmp.navigation.generated.resources.Res
import cmp.navigation.generated.resources.home_dashboard_feature
import kpt.core.base.ui.nav.popBackStackSafely
import kpt.feature.profile.identity.ProfileBody
import kpt.feature.settings.SettingsBody
import kpt.feature.settings.navigateToNotification
import kpt.feature.about.navigation.navigateToAbout
import kpt.feature.collectionsheet.navigation.navigateToIndividualCollectionSheet
import kpt.feature.report.navigation.navigateToReports
import kpt.feature.checkerinbox.navigation.navigateToCheckerInbox
import kpt.feature.settings.navigateToSettings
import kpt.feature.settings.navigateToSyncAndDrafts
import kpt.feature.settings.notificationDestination
import kpt.feature.settings.settingsDestination
import kpt.feature.settings.syncAndDraftsDestination
import org.jetbrains.compose.resources.stringResource

/**
 * BackboneRegistry — the FORK-OWNED white-label seam for the app **backbone** (the home-tab body and,
 * as forks grow, backbone module overrides).
 *
 * It is the home-body twin of [FeatureRegistry] (which owns feature routes + DI). The template shell —
 * `feature/home`'s `HomeScreen` (top bar + settings) and the `cmp-navigation` navbar graph — carries ZERO
 * demo imports and simply renders whatever body this seam provides. A fork swaps its dashboard by editing
 * THIS ONE file, never the template shell. Ownership: `owner: fork` in customization-surface.yaml, so a
 * template sync full-copies the shell while this file (and its body) survives.
 *
 * S5 heal (epic pure-white-label-store5-network, T7): the 12 hardcoded `navigateToX` demo lambdas used to
 * thread through FIVE template layers (AuthenticatedNavigation → navbarGraph → navbarScreen → homeGraph →
 * HomeScreen) just to reach the fork-owned [HomeDashboard]. They now live here, in one fork-owned place —
 * the shell only forwards an opaque `homeBody`.
 */
object BackboneRegistry {
    /**
     * The home tab's body. `HomeScreen` renders this inside its framework-owned Scaffold. The template
     * default is the demo Money-Toolkit dashboard wired to its feature destinations; `customizer --clean`
     * strips the fenced block below, leaving `{ }` — an empty backbone body for the fork to fill.
     */
    val homeBody: @Composable (NavController) -> Unit = { _ ->
        // Search used to render HERE — it was the original app's first tab and so its de-facto
        // dashboard. It now has its own destination behind the top bar's magnifier (SearchNavigation),
        // which leaves this seam needing content of its own.
        //
        // There is no home dashboard to put in it: the original has none (its four tabs are Search ·
        // Clients · Centers · Groups), and a Home-first bar is a NEW product decision, so nothing has
        // been designed or ported for this surface. Rather than improvise a plausible-looking
        // dashboard — fabricated summary cards being precisely what RULE-DESIGN-CONFORMANCE-001
        // forbids — the tab states what it is waiting for, and the gap is tracked in
        // CAPABILITY_GAPS.yaml like every other unported destination.
        PendingFeatureScreen(featureName = stringResource(Res.string.home_dashboard_feature))
    }

    /**
     * The settings tab's INNER content. The template-owned `SettingsScreen` shell (via
     * `settingsDestination`) forwards this opaque seam; the fork owns this body. It wires the
     * back / sync-and-drafts / dev-menu callbacks into the demo [SettingsDemoBody], so the
     * merge-owned shell carries no `kpt.feature.settings.*` content wiring. `customizer --clean`
     * strips the fenced block, leaving `{ }` for the fork to fill (mirrors `homeBody`). WS01 / AC7.
     */
    val settingsBody: @Composable (NavController) -> Unit = { navController ->
        // Settings content (S7). This seam was EMPTY — the same class as `profileBody` below, and
        // with a worse symptom: `HomeDestinationsScreen.SettingsScreen` declares `isMigrated = true`,
        // so the drawer routed to a destination that rendered NOTHING. No crash, no log, a blank
        // screen. Found on device 2026-10-22; the desktop suite missed it because every settings
        // test drives `SettingsScreenContent` directly and never passes through this seam.
        //
        // `SettingsBody` is the fork's stand-in for the stripped `demo.SettingsDemoBody`: it owns the
        // theme/language dialog state that the stateless, `internal` content composable cannot.
        SettingsBody(
            onBackClick = { navController.popBackStackSafely() },
            onSyncAndDraftsClick = { navController.navigateToSyncAndDrafts() },
        )
    }

    /**
     * The profile tab's INNER content. `ProfileScreen` (the template shell) forwards this opaque
     * seam so the shell carries no `kpt.feature.profile.*` content imports; the fork owns this body.
     * `customizer --clean` strips the fenced block, leaving `{ }` for the fork to fill (mirrors
     * `homeBody`). WS01 / AC7.
     */
    val profileBody: @Composable (NavController) -> Unit = { _ ->
        // Signed-in officer identity + sign-out (S2). Another seam that was EMPTY.
        //
        // Sign-out needs no navigation callback: clearing the session flips
        // `UserData.isAuthenticated` to false, RootNavViewModel emits `RootNavState.Auth`, and the
        // root effect routes to sign-in. Navigating here as well would race the state machine.
        ProfileBody(onSignedOut = {})
    }

    /**
     * Route the authenticated navbar to the Settings backbone. Kept here so the
     * merge-owned `AuthenticatedNavigation.kt` shell carries no `kpt.feature.*`
     * imports (mirrors the `homeBody` seam pattern).
     */
    val navigateToSettings: (NavController) -> Unit = { it.navigateToSettings() }

    /** The drawer's About entry → `feature/about`. Same seam shape as [navigateToSettings]. */
    val navigateToAbout: (NavController) -> Unit = { it.navigateToAbout() }

    /** The drawer's Collection Sheet entry → `feature/collectionsheet`. */
    val navigateToCollectionSheet: (NavController) -> Unit = { it.navigateToIndividualCollectionSheet() }

    /** The drawer's Run Reports entry → `feature/report`. */
    val navigateToReports: (NavController) -> Unit = { it.navigateToReports() }

    /** The drawer's Checker Inbox & Tasks entry → `feature/checkerinbox`. */
    val navigateToCheckerInbox: (NavController) -> Unit = { it.navigateToCheckerInbox() }

    /**
     * Route the dashboard top bar's bell to the Notification backbone. Same seam shape as
     * [navigateToSettings] and for the same reason: it keeps the `kpt.feature.settings` import out
     * of the merge-owned shell, which `customization-surface.yaml` has as `owner: merge`.
     */
    val navigateToNotification: (NavController) -> Unit = { it.navigateToNotification() }

    /**
     * Backbone (framework) nav destinations — settings, notification, sync-and-drafts.
     * The showcase dev entries hang off the generic `devMenuEntries` slot from
     * [ShowcaseRegistry.devSettingsEntries]; a `--clean` fork drops to `emptyList()`
     * and the Settings dev menu hides.
     */
    val backboneDestinations: NavGraphBuilder.(NavController) -> Unit = { navController ->
        notificationDestination(onBackClick = { navController.popBackStackSafely() })
        syncAndDraftsDestination(onBackClick = { navController.popBackStackSafely() })
        // Settings inner content flows through the fork-owned `settingsBody` seam (WS01 / AC7); the
        // template `settingsDestination` shell forwards this opaque body, carrying no demo wiring.
        settingsDestination(settingsBody = { BackboneRegistry.settingsBody(navController) })
    }
}
