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

/**
 * Controls whether a screen stream reads from cache, hits the network, or both.
 *
 * Pass to [ScreenDataStream.asScreenStream], [LoadOnceStream.asLoadOnceStream], or
 * [PagingScreenStream] to override the default cache-then-network behaviour.
 *
 * **Choosing a policy:**
 * | Scenario | Policy |
 * |---|---|
 * | Normal screen — show cached data immediately, refresh in background | [CACHE_THEN_NETWORK] (default) |
 * | Always-fresh data required (e.g. payment confirmation) | [NETWORK_ONLY] |
 * | Offline-only view or explicit "load from cache" | [CACHE_ONLY] |
 */
enum class FetchPolicy {

    /**
     * Emit cached data immediately (if present), then trigger a background network fetch
     * and emit refreshed data when it arrives.
     *
     * This is the default and works well for most screens. The user sees content quickly
     * while the data is silently refreshed in the background. The staleness banner from
     * [DataFreshnessIndicator] is shown when the data is older than the configured TTL.
     */
    CACHE_THEN_NETWORK,

    /**
     * Skip the local cache entirely and always fetch from the network.
     *
     * Use for screens where stale data would be misleading or harmful (e.g. payment status,
     * balance after a transaction). If the network fails the stream emits
     * [ScreenState.NoNetwork] or [ScreenState.Error] instead of showing stale data.
     */
    NETWORK_ONLY,

    /**
     * Read only from the local cache; never perform a network request.
     *
     * Use for explicitly offline screens or when the calling code knows connectivity is
     * unavailable and wants to avoid error flicker. If the cache is empty the stream emits
     * [ScreenState.Empty].
     *
     * **DataFreshnessIndicator behaviour:** the staleness banner will still show the
     * `lastFetchedAt` timestamp from cache — even if it is very old — because no background
     * refresh is triggered to update it. The staleness threshold is applied as normal; if the
     * cached data is older than the TTL, the stale banner renders. To suppress the banner on
     * explicitly offline screens, pass `showFreshnessIndicator = false` to [ScreenContent].
     */
    CACHE_ONLY,
}
