/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.store.infra

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import template.core.base.store.error.ErrorCategory
import template.core.base.store.error.categorize
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import template.core.base.store.screen.StoreData

/**
 * Pure function combining StoreData metadata + NetworkStatus into ScreenState.
 * No side effects, no coroutines — exhaustively unit testable.
 *
 * Uses full [NetworkStatus] (not just Boolean) to detect captive portals.
 * Error classification delegates to [categorize] — single source of truth.
 */
object DecisionEngine {

    /**
     * Maps [storeData] + [networkStatus] to the correct [ScreenState].
     *
     * @param storeData snapshot from the Store pipeline (data, error, freshness flags)
     * @param networkStatus current connectivity; [NetworkStatus.CaptivePortal] is treated
     *   as offline for content decisions but surfaces a distinct UI flag
     * @return the [ScreenState] variant the screen should render
     */
    fun <T> decide(
        storeData: StoreData<T>,
        networkStatus: NetworkStatus,
    ): ScreenState<T> {
        val noData = storeData.isEmpty
        val error = storeData.error
        val isOnline = networkStatus is NetworkStatus.Available
        val isCaptivePortal = networkStatus is NetworkStatus.CaptivePortal

        // === No data branch ===
        if (noData) {
            return when {
                isCaptivePortal -> ScreenState.NoNetwork(isCaptivePortal = true)
                !isOnline -> ScreenState.NoNetwork()
                error != null -> when (categorize(error)) {
                    ErrorCategory.Network -> ScreenState.NoNetwork()
                    ErrorCategory.Auth -> ScreenState.Unauthenticated
                    ErrorCategory.RateLimit,
                    ErrorCategory.Server,
                    ErrorCategory.Generic -> ScreenState.Error(error, isNetworkError = false)
                }
                else -> ScreenState.Loading
            }
        }

        // === Has data branch ===
        val fetchedAt = storeData.fetchedAtInstant
        return when {
            storeData.isRefreshing -> ScreenState.Content(storeData.data, DataFreshness.UPDATING, fetchedAt)
            !isOnline || isCaptivePortal -> ScreenState.Content(storeData.data, DataFreshness.STALE, fetchedAt)
            error != null -> ScreenState.Content(storeData.data, DataFreshness.STALE, fetchedAt)
            else -> ScreenState.Content(storeData.data, DataFreshness.FRESH, fetchedAt)
        }
    }
}
