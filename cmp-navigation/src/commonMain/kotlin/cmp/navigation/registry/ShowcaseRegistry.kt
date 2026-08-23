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

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kpt.feature.settings.DevMenuEntry

/**
 * ShowcaseRegistry — dev-only demo entry points (Transition Gallery, State Gallery in the
 * template's reference shape).
 *
 * offline-first-template-migration 02-store-infra-screenstate T6: this seam never existed on this
 * fork. Authored empty here — `feature/showcase` (the template's dev-tooling module the reference
 * `ShowcaseRegistry` wires) was never included in this fork's `settings.gradle.kts`, so there is
 * nothing to register. The shell still resolves this object; the dev menu simply stays hidden.
 */
object ShowcaseRegistry {
    /** Dev-menu entries surfaced in `SettingsScreen`'s Developer section. */
    fun devSettingsEntries(navController: NavController): List<DevMenuEntry> = emptyList()

    /** Dev-only nav destinations registered inside the authenticated graph. */
    val devDestinations: NavGraphBuilder.(NavController) -> Unit = { }
}
