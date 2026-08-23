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
import com.mifos.feature.client.di.ClientModule
import com.mifos.feature.loan.di.LoanModule
import com.mifos.feature.center.di.CenterModule
import com.mifos.feature.center.navigation.centerListScreenRoute
import com.mifos.feature.client.navigation.clientListScreenRoute
import com.mifos.feature.groups.di.GroupsModule
import com.mifos.feature.groups.navigation.groupListScreenRoute
import com.mifos.feature.loan.navigation.loanDestination
import org.koin.core.module.Module

/**
 * FeatureRegistry — the FORK-OWNED white-label seam for feature contributions.
 *
 * offline-first-template-migration 02-store-infra-screenstate T6: this seam never existed on this
 * fork (pre-sync, all 20 feature modules' Koin DI + nav destinations were wired inline in
 * `AuthenticatedNavigation.kt` directly — see that file's `dev` branch history). The template's
 * current architecture replaced that inline pattern with this registry, which the merge-owned
 * `AuthenticatedNavigation.kt` + `KoinModules.kt` now read from.
 *
 * Deliberately EMPTY here — re-wiring all 20 features (client, loan, savings, groups, center,
 * checker-inbox-task, data-table, collection-sheet, offline, document, note, report, path-tracking,
 * activate, recurring-deposit, search, auth, about, settings) into this seam is exactly the
 * per-feature migration work `offline-first-template-migration` sub-plans 05-21 own (D19: one
 * sub-plan per feature; D5: bottom-to-top sequencing) — each sub-plan lands its feature's real
 * `xModule` (Koin) + `xGraph(navController)` (nav) entries here as it migrates that feature onto
 * the new Store5/ScreenState architecture, rather than eagerly porting 20 features' worth of
 * pre-ScreenState-cutover wiring now.
 *
 * The template infra modules READ from this registry; a fork extends the app by editing THIS ONE
 * file (+ its build.gradle deps + settings.gradle include), never the template infra files:
 *   - `cmp-navigation/di/KoinModules.kt` includes [featureKoinModules] into the app DI graph.
 *   - `cmp-navigation/.../AuthenticatedNavigation.kt` invokes [featureDestinations] to register routes.
 *
 * Ownership: `owner: fork` in customization-surface.yaml — `sync-dirs`/`white-label-doctor` NEVER
 * overwrite it, so a template sync full-copies the infra modules while features survive.
 */
object FeatureRegistry {
    /**
     * Feature Koin modules the app installs. The framework SHELL modules (Home, Settings) live in
     * [cmp.navigation.di.KoinModules] and are always present; this is the fork's own features.
     * Populated per-feature-sub-plan (05-21) — empty until the first feature migrates.
     */
    val featureKoinModules: List<Module> = listOf(
        // loan feature — first feature wired (pilot: offline-first-template-migration 04/04b).
        // Its LoanTransaction vertical (Store5 ledger) is device-proven here; the remaining
        // features land per-sub-plan (05-21).
        LoanModule,
        // client feature — its ViewModels (incl. ClientListViewModel for the offline-first
        // Store5 paged list) must be in the DI graph for the home Clients tile to render.
        ClientModule,
        // group + center features — their ViewModels for the offline-first paged lists.
        GroupsModule,
        CenterModule,
    )

    /**
     * Feature nav destinations — registered into the authenticated graph. The shell destinations
     * (settings, notification) stay in [BackboneRegistry]; this is the fork's routes. Populated
     * per-feature-sub-plan (05-21) — the loan feature is wired first for the pilot.
     */
    val featureDestinations: NavGraphBuilder.(NavController) -> Unit = { navController ->
        // Cross-feature callbacks (documents/notes/more-info/loan-created) route to features not
        // yet migrated (document/note/client — sub-plans 12/11/21); no-op until they land, then
        // re-point to their real navigateTo* here.
        loanDestination(
            navController = navController,
            onDocumentsClicked = { _, _ -> },
            onNotesClicked = { _, _ -> },
            onMoreInfoClicked = { _, _ -> },
            onLoanCreated = { },
        )
        // Client list — reachable from the home Clients tile. The offline-first Store5 paged list
        // (PagingScreenContent). Per-client drill-down (clientDetailRoute) is part of the broader
        // client-graph wiring; the list itself renders + paginates offline here.
        clientListScreenRoute(
            onClientSelect = { },
            createNewClient = { },
        )
        // Group + center lists — reachable from their home tiles. Offline-first Store5 paged
        // lists (PagingScreenContent). Per-item drill-down is part of the broader graph wiring.
        groupListScreenRoute(
            onAddGroupClick = { },
            onGroupClick = { },
        )
        centerListScreenRoute(
            createNewCenter = { },
            onCenterSelect = { },
        )
    }
}
