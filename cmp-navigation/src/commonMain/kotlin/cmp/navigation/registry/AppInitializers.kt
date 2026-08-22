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

/**
 * AppInitializers — the FORK-OWNED white-label seam for app-startup hooks.
 *
 * offline-first-template-migration 02-store-infra-screenstate T6: this seam never existed on this
 * fork (pre-dates the registry pattern) — authored fresh from the template's reference shape.
 *
 * Each platform app entry point (Android `Application.onCreate`, iOS/desktop `main`) calls [runAll]
 * ONCE, right after Koin is initialized, so a fork can register startup work (analytics init, crash
 * reporting, remote-config prefetch, feature-flag warmup) WITHOUT editing the template-owned entry
 * points. Ownership: `owner: fork` in customization-surface.yaml. Field-officer has no startup hooks
 * yet — template default (empty) applies.
 */
object AppInitializers {
    /**
     * Startup hooks, run in order after Koin init at every platform entry point. Koin is already
     * started when these run, so a hook may resolve dependencies via `KoinComponent`/`getKoin()`.
     */
    val onAppStart: List<() -> Unit> = emptyList()

    /** Invoked once by each platform app entry point after Koin init. Safe when the list is empty. */
    fun runAll() {
        onAppStart.forEach { it() }
    }
}
