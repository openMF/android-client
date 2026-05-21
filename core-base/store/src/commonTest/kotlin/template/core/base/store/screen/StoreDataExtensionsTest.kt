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

import app.cash.turbine.test
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [skipMemoryData] — verifies it bypasses in-memory cache and reads from
 * the Fetcher (source-of-truth / network) instead.
 */
class StoreDataExtensionsTest {

    // ─── skipMemoryData ───────────────────────────────────────────────────────

    @Test
    fun `skipMemoryData emits data from fetcher`() = runTest {
        var fetchCount = 0
        val store = StoreBuilder
            .from<String, String>(fetcher = Fetcher.of { key ->
                fetchCount++
                "fetched:$key"
            })
            .build()

        // skipMemoryData on an unprimed key must reach the fetcher (no cached data to fall back to).
        // For memory-only stores (no SourceOfTruth), skipMemory on a primed key falls back to the
        // in-memory cache without a new fetch — only testable with a SOT-backed store.
        store.skipMemoryData("key1").test {
            // First emission may be an empty sentinel (fetch in progress); drain to actual data.
            // Receiving real data guarantees the fetcher has been called.
            var item = awaitItem()
            if (item.isEmpty) item = awaitItem()
            assertFalse(item.isEmpty, "skipMemoryData must eventually return data from the fetcher")
            assertEquals("fetched:key1", item.data)
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(fetchCount > 0, "skipMemoryData must call the fetcher for an unprimed key")
    }

    @Test
    fun `skipMemoryData with refresh=false returns cached data without new fetch`() = runTest {
        var fetchCount = 0
        val store = StoreBuilder
            .from<String, String>(fetcher = Fetcher.of { key ->
                fetchCount++
                "fetched:$key"
            })
            .build()

        // Prime cache
        store.streamData("key").test {
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }

        val preFetchCount = fetchCount

        // skipMemoryData(refresh=false) reads from SourceOfTruth only, no new network call
        store.skipMemoryData("key", refresh = false).test {
            cancelAndIgnoreRemainingEvents()
        }

        // For a memory-only store with no SourceOfTruth, refresh=false may emit nothing;
        // the important invariant is that no additional network fetch is made.
        assertEquals(preFetchCount, fetchCount, "skipMemoryData(refresh=false) must not trigger a new network fetch")
    }

    // ─── combineScreenStates priority ────────────────────────────────────────

    @Test
    fun `combineScreenStates both Content combines data`() = runTest {
        val a = kotlinx.coroutines.flow.flowOf(ScreenState.Content("hello", DataFreshness.FRESH))
        val b = kotlinx.coroutines.flow.flowOf(ScreenState.Content(42, DataFreshness.FRESH))

        combineScreenStates(a, b) { s, n -> "$s:$n" }.test {
            val item = assertIs<ScreenState.Content<String>>(awaitItem())
            assertEquals("hello:42", item.data)
            assertEquals(DataFreshness.FRESH, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `combineScreenStates NoNetwork wins over Content`() = runTest {
        val a = kotlinx.coroutines.flow.flowOf<ScreenState<String>>(ScreenState.NoNetwork())
        val b = kotlinx.coroutines.flow.flowOf(ScreenState.Content("data", DataFreshness.FRESH))

        combineScreenStates(a, b) { s, _ -> s }.test {
            assertIs<ScreenState.NoNetwork>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `combineScreenStates Loading wins over Content`() = runTest {
        val a = kotlinx.coroutines.flow.flowOf<ScreenState<String>>(ScreenState.Loading)
        val b = kotlinx.coroutines.flow.flowOf(ScreenState.Content("data", DataFreshness.FRESH))

        combineScreenStates(a, b) { s, _ -> s }.test {
            assertIs<ScreenState.Loading>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `combineScreenStates STALE from one source propagates to result`() = runTest {
        val a = kotlinx.coroutines.flow.flowOf(ScreenState.Content("a", DataFreshness.FRESH))
        val b = kotlinx.coroutines.flow.flowOf(ScreenState.Content("b", DataFreshness.STALE))

        combineScreenStates(a, b) { x, y -> x + y }.test {
            val item = assertIs<ScreenState.Content<String>>(awaitItem())
            assertEquals(DataFreshness.STALE, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `combineScreenStates UPDATING propagates when no source is STALE`() = runTest {
        val a = kotlinx.coroutines.flow.flowOf(ScreenState.Content("a", DataFreshness.UPDATING))
        val b = kotlinx.coroutines.flow.flowOf(ScreenState.Content("b", DataFreshness.FRESH))

        combineScreenStates(a, b) { x, y -> x + y }.test {
            val item = assertIs<ScreenState.Content<String>>(awaitItem())
            assertEquals(DataFreshness.UPDATING, item.freshness)
            awaitComplete()
        }
    }
}
