/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.store.screen

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Unified UI state produced by [ScreenDataStream].
 * Replaces per-ViewModel ScreenUiState + isFromCache + isRefreshing + networkStatus.
 */
sealed interface ScreenState<out T> {

    /** Initial load — no data available yet. */
    data object Loading : ScreenState<Nothing>

    /** Data fetched successfully but content is empty (e.g., empty list). */
    data object Empty : ScreenState<Nothing>

    /**
     * No network connectivity and no cached data available.
     * @param isCaptivePortal true when behind a captive portal (hotel WiFi login).
     */
    data class NoNetwork(
        val isCaptivePortal: Boolean = false,
    ) : ScreenState<Nothing>

    /**
     * Authentication required — HTTP 401/403 or equivalent token expiry.
     * Screens should redirect the user to the login flow.
     */
    data object Unauthenticated : ScreenState<Nothing>

    /** Error occurred with no usable cached data. */
    data class Error(
        val error: Throwable,
        val isNetworkError: Boolean = false,
    ) : ScreenState<Nothing>

    /**
     * Data available with freshness metadata.
     * @param data The domain data payload.
     * @param freshness Whether data is fresh, stale, or updating.
     * @param fetchedAt Wall-clock instant of last successful fetch, for UI display
     *   (e.g., "Last updated: 5 min ago"). Null if data has never been fetched from network.
     */
    @OptIn(ExperimentalTime::class)
    data class Content<T>(
        val data: T,
        val freshness: DataFreshness,
        val fetchedAt: Instant? = null,
    ) : ScreenState<T>
}

/**
 * Indicates how fresh the data in [ScreenState.Content] is.
 */
enum class DataFreshness {
    /** Online, data is the latest from server. */
    FRESH,
    /** Showing cached data — offline or refresh failed. */
    STALE,
    /** Has cached data, network refresh currently in flight. */
    UPDATING,
}
