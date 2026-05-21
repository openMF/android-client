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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CombineScreenStatesNArityTest {

    // ─── 3-source ────────────────────────────────────────────────────────────

    @Test
    fun `3-source all Content FRESH produces Content FRESH`() = runTest {
        combineScreenStates(
            content(1, DataFreshness.FRESH),
            content(2, DataFreshness.FRESH),
            content(3, DataFreshness.FRESH),
        ) { a, b, c -> Triple(a, b, c) }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Triple<Int, Int, Int>>>(item)
            assertEquals(Triple(1, 2, 3), item.data)
            assertEquals(DataFreshness.FRESH, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `3-source any STALE produces STALE`() = runTest {
        combineScreenStates(
            content(1, DataFreshness.FRESH),
            content(2, DataFreshness.STALE),
            content(3, DataFreshness.FRESH),
        ) { a, b, c -> a + b + c }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(DataFreshness.STALE, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `3-source any UPDATING (no STALE) produces UPDATING`() = runTest {
        combineScreenStates(
            content(1, DataFreshness.FRESH),
            content(2, DataFreshness.UPDATING),
            content(3, DataFreshness.FRESH),
        ) { a, b, c -> a + b + c }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(DataFreshness.UPDATING, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `3-source any NoNetwork produces NoNetwork`() = runTest {
        combineScreenStates(
            content(1),
            noNetwork<Int>(),
            content(3),
        ) { a, b, c -> a + b + c }.test {
            assertIs<ScreenState.NoNetwork>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `3-source any Loading produces Loading`() = runTest {
        combineScreenStates(
            content(1),
            loading<Int>(),
            content(3),
        ) { a, b, c -> a + b + c }.test {
            assertIs<ScreenState.Loading>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `3-source any Error produces Error`() = runTest {
        val err = RuntimeException("boom")
        combineScreenStates(
            content(1),
            error<Int>(err),
            content(3),
        ) { a, b, c -> a + b + c }.test {
            val item = awaitItem()
            assertIs<ScreenState.Error>(item)
            assertEquals("boom", item.error.message)
            awaitComplete()
        }
    }

    @Test
    fun `3-source any Empty produces Empty`() = runTest {
        combineScreenStates(
            content(1),
            empty<Int>(),
            content(3),
        ) { a, b, c -> a + b + c }.test {
            assertIs<ScreenState.Empty>(awaitItem())
            awaitComplete()
        }
    }

    // ─── 4-source ────────────────────────────────────────────────────────────

    @Test
    fun `4-source all Content FRESH produces Content FRESH`() = runTest {
        combineScreenStates(
            content(1, DataFreshness.FRESH),
            content(2, DataFreshness.FRESH),
            content(3, DataFreshness.FRESH),
            content(4, DataFreshness.FRESH),
        ) { a, b, c, d -> a + b + c + d }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(10, item.data)
            assertEquals(DataFreshness.FRESH, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `4-source STALE beats UPDATING`() = runTest {
        combineScreenStates(
            content(1, DataFreshness.FRESH),
            content(2, DataFreshness.UPDATING),
            content(3, DataFreshness.STALE),
            content(4, DataFreshness.FRESH),
        ) { a, b, c, d -> a + b + c + d }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(DataFreshness.STALE, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `4-source any NoNetwork produces NoNetwork`() = runTest {
        combineScreenStates(
            content(1),
            content(2),
            noNetwork<Int>(),
            content(4),
        ) { a, b, c, d -> a + b + c + d }.test {
            assertIs<ScreenState.NoNetwork>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `4-source NoNetwork beats Error`() = runTest {
        val err = RuntimeException("fail")
        combineScreenStates(
            error<Int>(err),
            noNetwork<Int>(),
            content(3),
            content(4),
        ) { a, b, c, d -> a + b + c + d }.test {
            assertIs<ScreenState.NoNetwork>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `4-source any Loading produces Loading`() = runTest {
        combineScreenStates(
            content(1),
            loading<Int>(),
            content(3),
            content(4),
        ) { a, b, c, d -> a + b + c + d }.test {
            assertIs<ScreenState.Loading>(awaitItem())
            awaitComplete()
        }
    }

    // ─── 5-source ────────────────────────────────────────────────────────────

    @Test
    fun `5-source all Content FRESH produces Content FRESH`() = runTest {
        combineScreenStates(
            content(1, DataFreshness.FRESH),
            content(2, DataFreshness.FRESH),
            content(3, DataFreshness.FRESH),
            content(4, DataFreshness.FRESH),
            content(5, DataFreshness.FRESH),
        ) { a, b, c, d, e -> a + b + c + d + e }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(15, item.data)
            assertEquals(DataFreshness.FRESH, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `5-source any STALE produces STALE`() = runTest {
        combineScreenStates(
            content(1, DataFreshness.FRESH),
            content(2, DataFreshness.FRESH),
            content(3, DataFreshness.STALE),
            content(4, DataFreshness.UPDATING),
            content(5, DataFreshness.FRESH),
        ) { a, b, c, d, e -> a + b + c + d + e }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(DataFreshness.STALE, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `5-source any NoNetwork produces NoNetwork`() = runTest {
        combineScreenStates(
            content(1),
            content(2),
            content(3),
            noNetwork<Int>(),
            content(5),
        ) { a, b, c, d, e -> a + b + c + d + e }.test {
            assertIs<ScreenState.NoNetwork>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `5-source any Error produces first Error`() = runTest {
        val err1 = RuntimeException("first")
        val err2 = RuntimeException("second")
        combineScreenStates(
            content(1),
            error<Int>(err1),
            error<Int>(err2),
            content(4),
            content(5),
        ) { a, b, c, d, e -> a + b + c + d + e }.test {
            val item = awaitItem()
            assertIs<ScreenState.Error>(item)
            assertEquals("first", item.error.message)
            awaitComplete()
        }
    }

    @Test
    fun `5-source any Empty produces Empty`() = runTest {
        combineScreenStates(
            content(1),
            content(2),
            empty<Int>(),
            content(4),
            content(5),
        ) { a, b, c, d, e -> a + b + c + d + e }.test {
            assertIs<ScreenState.Empty>(awaitItem())
            awaitComplete()
        }
    }

    // ─── 2-source refactor parity ────────────────────────────────────────────

    @Test
    fun `2-source refactored still produces Content FRESH when both fresh`() = runTest {
        combineScreenStates(
            content(10, DataFreshness.FRESH),
            content(20, DataFreshness.FRESH),
        ) { a, b -> a + b }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(30, item.data)
            assertEquals(DataFreshness.FRESH, item.freshness)
            awaitComplete()
        }
    }

    @Test
    fun `2-source refactored STALE wins over UPDATING`() = runTest {
        combineScreenStates(
            content(10, DataFreshness.STALE),
            content(20, DataFreshness.UPDATING),
        ) { a, b -> a + b }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(DataFreshness.STALE, item.freshness)
            awaitComplete()
        }
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private fun <T> content(value: T, freshness: DataFreshness = DataFreshness.FRESH) =
        flowOf(ScreenState.Content(value, freshness))

    private fun <T> noNetwork() =
        flowOf<ScreenState<T>>(ScreenState.NoNetwork())

    private fun <T> loading() =
        flowOf<ScreenState<T>>(ScreenState.Loading)

    private fun <T> error(err: Throwable) =
        flowOf<ScreenState<T>>(ScreenState.Error(err))

    private fun <T> empty() =
        flowOf<ScreenState<T>>(ScreenState.Empty)
}
