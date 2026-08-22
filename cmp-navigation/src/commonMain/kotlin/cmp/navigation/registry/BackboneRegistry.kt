/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package cmp.navigation.registry

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kpt.core.base.ui.nav.popBackStackSafely
import kpt.feature.profile.demo.ProfileDemoBody
import kpt.feature.settings.navigateToSettings
import kpt.feature.settings.notificationDestination
import kpt.feature.settings.settingsDestination
import kpt.feature.settings.syncAndDraftsDestination

/**
 * BackboneRegistry — the FORK-OWNED white-label seam for the app **backbone** (home-tab body,
 * settings-tab inner content, profile-tab inner content).
 *
 * offline-first-template-migration 02-store-infra-screenstate T6: this seam never existed on this
 * fork. Authored fresh here:
 *  - [homeBody] — EMPTY for now. The template's reference `HomeDashboard` demo composable wires 12
 *    `onNavigateToX` callbacks into `:feature:loans`/`:feature:bills`/`:feature:rates`/etc. — none
 *    of those demo financial-toolkit modules are included in this fork's `settings.gradle.kts`, and
 *    `HomeDashboard`'s default `HomeViewModel` resolves demo Store5 stores this fork never
 *    populated (T3 left `AppStoreRegistry` intentionally empty). Wiring field-officer's own real
 *    home-dashboard content is future work, tracked alongside the per-feature migration sub-plans
 *    (05-21) — this stays an honest empty seam rather than a demo placeholder that would crash.
 *  - [settingsBody] — EMPTY. The template's `SettingsDemoBody` reference composable was never
 *    synced/authored on this fork; `settingsDestination`'s `settingsBody` param already defaults to
 *    `{}`, so this seam simply forwards that default until field-officer's real settings-tab
 *    content (matching the fork's pre-sync `serverConfigGraph`/passcode-management flows) lands.
 *  - [profileBody] — wired to the template's self-contained `ProfileDemoBody` (zero external feature
 *    deps, renders a generic placeholder) since it's genuinely safe/non-crashing as a stand-in.
 *  - [backboneDestinations] / [navigateToSettings] — mirror the template's reference shape exactly;
 *    these are framework-infra routes (notification, sync-and-drafts, settings shell) with no
 *    fork-specific content dependency.
 *
 * Ownership: `owner: fork` in customization-surface.yaml — a template sync full-copies the shell
 * (`feature/home`'s `HomeScreen`, the navbar graph) while this file survives.
 */
object BackboneRegistry {
    /** The home tab's body. See class doc — empty until field-officer's real dashboard lands. */
    val homeBody: @Composable (NavController) -> Unit = { }

    /** The settings tab's INNER content. See class doc — empty until field-officer's real settings body lands. */
    val settingsBody: @Composable (NavController) -> Unit = { }

    /** The profile tab's INNER content. Template's self-contained demo body (no external deps). */
    val profileBody: @Composable (NavController) -> Unit = { _ -> ProfileDemoBody() }

    /**
     * Route the authenticated navbar to the Settings backbone. Kept here so the merge-owned
     * `AuthenticatedNavigation.kt` shell carries no `kpt.feature.*` imports.
     */
    val navigateToSettings: (NavController) -> Unit = { it.navigateToSettings() }

    /** Backbone (framework) nav destinations — settings, notification, sync-and-drafts. */
    val backboneDestinations: NavGraphBuilder.(NavController) -> Unit = { navController ->
        notificationDestination(onBackClick = { navController.popBackStackSafely() })
        syncAndDraftsDestination(onBackClick = { navController.popBackStackSafely() })
        settingsDestination(settingsBody = { BackboneRegistry.settingsBody(navController) })
    }
}
