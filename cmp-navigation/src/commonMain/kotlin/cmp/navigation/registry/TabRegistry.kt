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

import cmp.navigation.authenticatednavbar.AuthenticatedNavBarTabItem
import kpt.core.ui.NavigationItem

/**
 * TabRegistry — the FORK-OWNED white-label seam for the authenticated bottom-nav tabs.
 *
 * offline-first-template-migration 02-store-infra-screenstate T6: this seam never existed on this
 * fork — the pre-sync app hardcoded its own 4-tab (Search/Client/Center/Group) sealed class +
 * ViewModel + NavHost wiring directly, which the current template architecture replaced with this
 * generic, registry-driven tab strip. Authored fresh here with the template's Home + Profile
 * backbone tabs only (AC-23: renders + tappable) — field-officer's real domain tabs (Client, Center,
 * Groups, etc.) are added to [extraTabs] as each feature lands its own migration sub-plan
 * (10-feature-center, 15-feature-groups, 21-feature-client, ...), matching [FeatureRegistry]'s
 * same per-feature population pattern.
 *
 * The template navbar (`AuthenticatedNavbarNavigationScreenContent`) reads [tabs] and renders them
 * generically — adding/removing a tab is a ONE-file edit here.
 */
object TabRegistry {
    /** Fork tabs appended after the backbone Home/Profile tabs. Populated per-feature-sub-plan. */
    val extraTabs: List<NavigationItem> = emptyList()

    /** The full ordered tab list the navbar renders: backbone shell tabs + [extraTabs]. */
    val tabs: List<NavigationItem> = buildList {
        add(AuthenticatedNavBarTabItem.HomeTab)
        add(AuthenticatedNavBarTabItem.ProfileTab)
        addAll(extraTabs)
    }
}
