/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.store.error

import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorCategoryTest {

    @Test
    fun categorize_genericRuntimeException_returnsGeneric() {
        assertEquals(
            ErrorCategory.Generic,
            categorize(RuntimeException("oops")),
        )
    }

    @Test
    fun categorize_ioExceptionByName_returnsNetwork() {
        // Use a custom Throwable whose simpleName contains 'IOException' — works on KMP without
        // depending on java.io.IOException (which isn't on iOS/Web targets).
        class IOException(message: String) : RuntimeException(message)
        assertEquals(
            ErrorCategory.Network,
            categorize(IOException("disconnect")),
        )
    }

    @Test
    fun categorize_connectExceptionByName_returnsNetwork() {
        class ConnectTimeoutException : RuntimeException()
        assertEquals(
            ErrorCategory.Network,
            categorize(ConnectTimeoutException()),
        )
    }

    @Test
    fun categorize_timeoutByName_returnsNetwork() {
        class SocketTimeoutException : RuntimeException()
        assertEquals(
            ErrorCategory.Network,
            categorize(SocketTimeoutException()),
        )
    }

    @Test
    fun categorize_authErrorMessage_returnsAuth() {
        // Match by message pattern when the throwable type isn't an HTTP exception.
        // ErrorCategory.Auth is detected by either message keyword or HTTP-style status patterns.
        assertEquals(
            ErrorCategory.Auth,
            categorize(RuntimeException("HTTP 401 Unauthorized")),
        )
        assertEquals(
            ErrorCategory.Auth,
            categorize(RuntimeException("403 Forbidden")),
        )
    }

    @Test
    fun categorize_serverError_returnsServer() {
        assertEquals(
            ErrorCategory.Server,
            categorize(RuntimeException("HTTP 500 Internal Server Error")),
        )
        assertEquals(
            ErrorCategory.Server,
            categorize(RuntimeException("503 Service Unavailable")),
        )
    }

    @Test
    fun categorize_causeChainInspected() {
        class IOException : RuntimeException("net")
        val wrapper = RuntimeException("wrapped", IOException())
        assertEquals(
            ErrorCategory.Network,
            categorize(wrapper),
            "categorize should inspect cause chain, not just the top throwable.",
        )
    }

    @Test
    fun categorize_isStable_acrossInvocations() {
        val err = RuntimeException("HTTP 500")
        assertEquals(categorize(err), categorize(err))
    }

    @Test
    fun categorize_offlineException_returnsNetwork() {
        // Contract that PagingScreenStream's offline guard relies on: throwing
        // an OfflineException must route the UI through the no-network treatment.
        // Class name contains "Offline" → matchesNetworkClassName() heuristic catches it.
        assertEquals(
            ErrorCategory.Network,
            categorize(OfflineException()),
            "OfflineException must categorize as Network so framework UI shows the offline treatment.",
        )
    }

    @Test
    fun categorize_http429InMessage_returnsRateLimit() {
        // Regression guard for the double-escape bug: RATE_LIMIT_HTTP_REGEX must match
        // "HTTP 429" as a word boundary, not as the literal string "\\b...429\\b".
        assertEquals(
            ErrorCategory.RateLimit,
            categorize(RuntimeException("HTTP 429 Too Many Requests")),
        )
    }

    @Test
    fun categorize_429Alone_returnsRateLimit() {
        assertEquals(
            ErrorCategory.RateLimit,
            categorize(RuntimeException("Status: 429")),
        )
    }
}
