/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home.ui

import kpt.core.base.ui.viewmodel.BaseViewModel

/**
 * **Field-officer home ViewModel.**
 *
 * The home tab is the officer's launchpad: a scannable board of entry points to the real
 * field-operations features (clients, groups, centers, collection sheet, checker inbox,
 * path tracking, search). It carries no remote data of its own — every tile is a pure
 * navigation affordance — so this ViewModel is a thin MVI reducer:
 *
 *  - [HomeUiState] holds the ordered list of feature tiles to render (state is the single
 *    source of truth for what the screen shows).
 *  - [HomeAction.FeatureClicked] is dispatched when the officer taps a tile.
 *  - Each action is reduced into a one-shot [HomeEvent.NavigateTo] — the canonical
 *    "non-state-based navigation" event the [BaseViewModel] event channel exists for. The
 *    screen collects `eventFlow` and forwards the target to its `onNavigate` callback, which
 *    the navigation layer (`cmp-navigation`'s `BackboneRegistry.homeBody`) wires to the real
 *    destination.
 *
 * Keeping navigation on the event channel (rather than raw callbacks per tile) means the tap
 * → intent → navigate path is uniform, testable without Compose, and survives the addition of
 * new tiles without widening the ViewModel's constructor.
 */
class HomeViewModel : BaseViewModel<HomeUiState, HomeEvent, HomeAction>(
    initialState = HomeUiState(),
) {
    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.FeatureClicked -> sendEvent(HomeEvent.NavigateTo(action.target))
        }
    }
}

/**
 * State for the home board. [features] is the ordered set of tiles the screen renders; it is
 * seeded with the full field-officer set and driven from state so the screen never hardcodes
 * its own menu.
 */
data class HomeUiState(
    val features: List<HomeFeature> = HomeFeature.entries,
)

/** Actions the home screen can dispatch. */
sealed interface HomeAction {
    /** The officer tapped the [target] feature tile. */
    data class FeatureClicked(val target: HomeFeature) : HomeAction
}

/** One-shot events emitted by [HomeViewModel] — navigation only. */
sealed interface HomeEvent {
    /** Navigate to the [target] feature's entry screen. */
    data class NavigateTo(val target: HomeFeature) : HomeEvent
}

/**
 * The real field-officer features reachable from the home board. Each entry maps 1:1 to a
 * top-level destination the app already defines in `cmp-navigation` (e.g. the client list,
 * groups list, checker-inbox tasks). [section] groups the tiles into the two on-screen bands.
 */
enum class HomeFeature(val section: HomeSection) {
    /** Client list — view and manage client accounts. */
    CLIENTS(HomeSection.MANAGE),

    /** Group list — group-lending portfolios. */
    GROUPS(HomeSection.MANAGE),

    /** Center list — center meetings and their member groups. */
    CENTERS(HomeSection.MANAGE),

    /** Collection sheet — record repayments and savings for a group/center. */
    COLLECTION_SHEET(HomeSection.FIELD),

    /** Checker inbox — review and approve pending maker tasks. */
    CHECKER_INBOX(HomeSection.FIELD),

    /** Path tracking — the officer's field-visit route. */
    PATH_TRACKING(HomeSection.FIELD),

    /** Global search across clients, groups and loans. */
    SEARCH(HomeSection.FIELD),
}

/** The two visual bands the home board groups its tiles into. */
enum class HomeSection {
    /** Portfolio management entry points. */
    MANAGE,

    /** Day-to-day field-work entry points. */
    FIELD,
}
