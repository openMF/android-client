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

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkInfo
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import template.core.base.store.screen.DataOrigin
import template.core.base.store.screen.StoreData
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DecisionEngineTest {

    private val available = NetworkStatus.Available(
        NetworkInfo(type = NetworkType.WiFi, isMetered = false),
    )
    private val unavailable = NetworkStatus.Unavailable
    private val captivePortal = NetworkStatus.CaptivePortal(
        NetworkInfo(type = NetworkType.WiFi, isMetered = false),
    )

    // === No data branch ===

    @Test
    fun `no data + Available + no error = Loading`() {
        val result = DecisionEngine.decide(emptyStoreData(), available)
        assertIs<ScreenState.Loading>(result)
    }

    @Test
    fun `no data + Unavailable + no error = NoNetwork`() {
        val result = DecisionEngine.decide(emptyStoreData(), unavailable)
        assertIs<ScreenState.NoNetwork>(result)
        assertEquals(false, result.isCaptivePortal)
    }

    @Test
    fun `starvation-fix integration - empty sentinel while refreshing + Unavailable = NoNetwork`() {
        // Integration contract for the StoreDataMapper starvation fix:
        // mapToStoreDataNoFallback now emits StoreData(EMPTY_SENTINEL, isEmpty=true,
        // isRefreshing=true) on Initial/Loading-without-cache. DecisionEngine must route
        // this to NoNetwork when offline, preventing the "spinner stuck ~30s waiting for
        // HTTP timeout" UX bug on cold-start offline launches (Coin Detail offline).
        val starvationFixEmission = StoreData(
            data = "<empty>",
            origin = DataOrigin.NETWORK,
            isEmpty = true,
            isRefreshing = true, // The new flag combination this test guards.
            error = null,
        )
        val result = DecisionEngine.decide(starvationFixEmission, unavailable)
        assertIs<ScreenState.NoNetwork>(result)
        assertEquals(false, result.isCaptivePortal)
    }

    @Test
    fun `no data + CaptivePortal + no error = NoNetwork captive=true`() {
        val result = DecisionEngine.decide(emptyStoreData(), captivePortal)
        assertIs<ScreenState.NoNetwork>(result)
        assertTrue(result.isCaptivePortal)
    }

    @Test
    fun `no data + Available + IOException = NoNetwork`() {
        val data = emptyStoreData(error = FakeIOException("timeout"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `no data + Unavailable + IOException = NoNetwork`() {
        val data = emptyStoreData(error = FakeIOException("no route"))
        val result = DecisionEngine.decide(data, unavailable)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `no data + Available + other error = Error`() {
        val error = IllegalStateException("parse error")
        val data = emptyStoreData(error = error)
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
        assertEquals(error, result.error)
        assertEquals(false, result.isNetworkError)
    }

    // === Has data branch ===

    @Test
    fun `has data + Available + no error + not refreshing = Content FRESH`() {
        val data = dataStoreData("hello")
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Content<String>>(result)
        assertEquals("hello", result.data)
        assertEquals(DataFreshness.FRESH, result.freshness)
    }

    @Test
    fun `has data + Unavailable = Content STALE`() {
        val data = dataStoreData("cached")
        val result = DecisionEngine.decide(data, unavailable)
        assertIs<ScreenState.Content<String>>(result)
        assertEquals(DataFreshness.STALE, result.freshness)
    }

    @Test
    fun `has data + CaptivePortal = Content STALE`() {
        val data = dataStoreData("cached")
        val result = DecisionEngine.decide(data, captivePortal)
        assertIs<ScreenState.Content<String>>(result)
        assertEquals(DataFreshness.STALE, result.freshness)
    }

    @Test
    fun `has data + refreshing = Content UPDATING`() {
        val data = dataStoreData("old", isRefreshing = true)
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Content<String>>(result)
        assertEquals(DataFreshness.UPDATING, result.freshness)
    }

    @Test
    fun `has data + error refresh failed = Content STALE`() {
        val data = dataStoreData("stale", error = FakeIOException("refresh failed"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Content<String>>(result)
        assertEquals(DataFreshness.STALE, result.freshness)
    }

    @Test
    fun `has data + refreshing + Unavailable = Content UPDATING refreshing wins`() {
        val data = dataStoreData("old", isRefreshing = true)
        val result = DecisionEngine.decide(data, unavailable)
        assertIs<ScreenState.Content<String>>(result)
        assertEquals(DataFreshness.UPDATING, result.freshness)
    }

    // === error categorization (delegates to categorize()) ===

    @Test
    fun `network error - IOException direct = NoNetwork`() {
        val data = emptyStoreData(error = FakeIOException("connection reset"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `network error - IOException as cause = NoNetwork`() {
        val data = emptyStoreData(error = RuntimeException("wrapped", FakeIOException("cause")))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.NoNetwork>(result)
    }

    @Test
    fun `generic error - non-network exception = Error`() {
        val data = emptyStoreData(error = IllegalArgumentException("bad input"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
    }


    @Test
    fun `auth error - 401 message = Unauthenticated`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 401 Unauthorized"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Unauthenticated>(result)
    }

    @Test
    fun `auth error - 403 message = Unauthenticated`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 403 Forbidden"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Unauthenticated>(result)
    }

    @Test
    fun `rate limit error - 429 message = Error`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 429 Too Many Requests"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
    }

    @Test
    fun `server error - 500 message = Error`() {
        val data = emptyStoreData(error = RuntimeException("HTTP 500 Internal Server Error"))
        val result = DecisionEngine.decide(data, available)
        assertIs<ScreenState.Error>(result)
    }

    // === Helpers ===

    private fun emptyStoreData(error: Throwable? = null): StoreData<String> = StoreData(
        data = "", // Sentinel — isEmpty=true means data is never used by DecisionEngine
        origin = DataOrigin.NETWORK,
        isEmpty = true,
        error = error,
    )

    private fun dataStoreData(
        value: String,
        isRefreshing: Boolean = false,
        error: Throwable? = null,
    ): StoreData<String> = StoreData(
        data = value,
        origin = DataOrigin.CACHE,
        isEmpty = false,
        isRefreshing = isRefreshing,
        error = error,
    )
}

/** Fake IOException for multiplatform tests (class name contains "IOException"). */
private class FakeIOException(message: String) : Exception(message)
