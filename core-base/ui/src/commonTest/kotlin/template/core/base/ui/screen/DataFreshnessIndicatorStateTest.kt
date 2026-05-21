/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.ui.screen

import template.core.base.store.screen.DataFreshness
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the [deriveDisplayState] contract — the pure mapping from
 * (DataFreshness × showReconnected) to [DisplayState] used by
 * [DataFreshnessIndicator].
 *
 * Compose-runtime behavior (LaunchedEffect timing of the just-reconnected
 * banner, AnimatedVisibility transitions) is verified manually on device —
 * Compose UI test infra is not yet wired into core-base/ui's commonTest.
 */
class DataFreshnessIndicatorStateTest {

    // ── showReconnected = false: pure freshness mapping ───────────────────

    @Test
    fun freshFalseReconnectedIsHidden() {
        assertEquals(DisplayState.Hidden, deriveDisplayState(DataFreshness.FRESH, showReconnected = false))
    }

    @Test
    fun staleFalseReconnectedIsStale() {
        assertEquals(DisplayState.Stale, deriveDisplayState(DataFreshness.STALE, showReconnected = false))
    }

    @Test
    fun updatingFalseReconnectedIsUpdating() {
        assertEquals(DisplayState.Updating, deriveDisplayState(DataFreshness.UPDATING, showReconnected = false))
    }

    // ── showReconnected = true: wins over freshness ───────────────────────

    @Test
    fun freshTrueReconnectedIsReconnected() {
        assertEquals(DisplayState.Reconnected, deriveDisplayState(DataFreshness.FRESH, showReconnected = true))
    }

    @Test
    fun staleTrueReconnectedIsReconnected() {
        // Edge case: shouldn't happen via the state machine (we only set
        // showReconnected after STALE → non-STALE), but the helper must be
        // correct in isolation.
        assertEquals(DisplayState.Reconnected, deriveDisplayState(DataFreshness.STALE, showReconnected = true))
    }

    @Test
    fun updatingTrueReconnectedIsReconnected() {
        assertEquals(DisplayState.Reconnected, deriveDisplayState(DataFreshness.UPDATING, showReconnected = true))
    }

    // ── Reconnected text ──────────────────────────────────────────────────

    @Test
    fun reconnectedTextIsStandardCopy() {
        assertEquals("Connected · Refreshing data", RECONNECTED_TEXT)
    }
}
