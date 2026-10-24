/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import cmp.navigation.generated.resources.Res
import cmp.navigation.generated.resources.drawer_about
import cmp.navigation.generated.resources.drawer_checker_inbox_tasks
import cmp.navigation.generated.resources.drawer_collection_sheet
import cmp.navigation.generated.resources.drawer_offline_sync
import cmp.navigation.generated.resources.drawer_path_tracker
import cmp.navigation.generated.resources.drawer_run_reports
import cmp.navigation.generated.resources.drawer_settings
import kpt.core.designsystem.icon.AppIcons
import org.jetbrains.compose.resources.StringResource

/**
 * The navigation-drawer entries — ported from
 * `6b66e8a43:cmp-navigation/src/commonMain/kotlin/cmp/navigation/navigation/HomeDestinationsScreen.kt`.
 *
 * Order is the original's and is load-bearing twice over: the drawer renders it top to bottom, and
 * [AuthenticatedNavbarNavigationScreen] draws a divider at `index == size - 2`, which is what places
 * the rule above Offline Sync. Reordering this list silently moves that divider.
 *
 * ## migrated vs pending
 *
 * Each entry declares whether its feature exists yet. Settings, About and Collection Sheet do. The other four render as
 * real rows and open [PendingFeatureRoute], which names the missing feature rather than imitating
 * it — see that file for why the shell ships complete instead of partially drawn.
 *
 * Flipping one to migrated is a one-line change here plus its route: nothing else in the shell
 * knows the difference.
 */
sealed class HomeDestinationsScreen(
    val title: StringResource,
    val icon: ImageVector,
    /**
     * The feature's own name, used by the pending host. NOT a label — [title] is the label; this is
     * what gets printed in "X has not been ported yet", so it reads as a feature rather than a
     * menu entry.
     */
    val featureName: String,
    /** `false` until the feature module lands. Drives routing, never visibility. */
    val isMigrated: Boolean = false,
) {
    /** Migrated: `feature/checkerinbox` — maker-checker approvals + pending reschedules (2026-10-24). */
    data object CheckerInboxAndTasksScreen : HomeDestinationsScreen(
        title = Res.string.drawer_checker_inbox_tasks,
        icon = AppIcons.Checkbox,
        featureName = "Checker Inbox & Tasks",
        isMigrated = true,
    )

    /** Migrated: `feature/collectionsheet` — the individual collection sheet (2026-10-24). */
    data object CollectionSheetScreen : HomeDestinationsScreen(
        title = Res.string.drawer_collection_sheet,
        icon = AppIcons.Assignment,
        featureName = "Collection Sheet",
        isMigrated = true,
    )

    /** Migrated: `feature/report` — report catalogue, parameter form, result + CSV (2026-10-24). */
    data object RunReportsScreen : HomeDestinationsScreen(
        title = Res.string.drawer_run_reports,
        icon = AppIcons.Task,
        featureName = "Run Reports",
        isMigrated = true,
    )

    data object PathTrackerScreen : HomeDestinationsScreen(
        title = Res.string.drawer_path_tracker,
        icon = AppIcons.PersonPinCircle,
        featureName = "Path Tracker",
    )

    /**
     * Migrated. `feature/settings` is in fact RICHER than the original's, which had
     * only Settings + UpdateServerConfig: the port adds SyncAndDrafts and Notification.
     */
    data object SettingsScreen : HomeDestinationsScreen(
        title = Res.string.drawer_settings,
        icon = AppIcons.Settings,
        featureName = "Settings",
        isMigrated = true,
    )

    /** Migrated: `feature/about` (2026-10-24). */
    data object AboutScreen : HomeDestinationsScreen(
        title = Res.string.drawer_about,
        icon = AppIcons.Info,
        featureName = "About",
        isMigrated = true,
    )

    /** Last by design — the divider above it is computed from this list's size. */
    data object OfflineSyncScreen : HomeDestinationsScreen(
        title = Res.string.drawer_offline_sync,
        icon = AppIcons.CloudOff,
        featureName = "Offline Sync",
    )

    companion object {
        /**
         * Drawer order, exactly as the original declared it.
         *
         * `get()`, NOT a stored `val`. A stored list is built by the COMPANION's initializer, and
         * the companion is initialized by the first touch of `HomeDestinationsScreen` — including a
         * touch that IS `entries` itself. Each `data object` below needs its parent class
         * initialized to construct, that initialization is already in progress, and the JVM hands
         * back `null` rather than recursing: `entries` comes out as seven nulls and the drawer dies
         * with `Cannot invoke getIcon() because "$entry" is null`.
         *
         * Caught by the device-free render tier, which rendered the drawer before any device run
         * had opened it. Computing on access is correct and costs one list of seven singletons.
         */
        val entries: List<HomeDestinationsScreen> get() = listOf(
            CheckerInboxAndTasksScreen,
            CollectionSheetScreen,
            RunReportsScreen,
            PathTrackerScreen,
            SettingsScreen,
            AboutScreen,
            OfflineSyncScreen,
        )
    }
}
