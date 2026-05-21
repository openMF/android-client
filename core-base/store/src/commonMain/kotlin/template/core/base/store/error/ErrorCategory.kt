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

/**
 * High-level classification of a [Throwable] for state-aware UI rendering.
 *
 * Pure logic — no platform calls, no side effects. Lives in `core-base/store` (state layer)
 * so both UI and non-UI consumers can branch on the same categorization.
 *
 * The classification is heuristic: it inspects the throwable's class name, message, and
 * cause chain. It is intentionally conservative — anything that doesn't match a specific
 * pattern falls through to [Generic].
 */
enum class ErrorCategory {
    /** No connectivity, timeout, or transport-level failure. Suggest retry + network UX. */
    Network,

    /** HTTP 401/403 or auth-equivalent. Suggest session refresh / login UX. */
    Auth,

    /** HTTP 429 — server is rate-limiting this client. Suggest backing off and retrying. */
    RateLimit,

    /** HTTP 5xx or upstream service error. Suggest retry-with-backoff + status UX. */
    Server,

    /** Anything not specifically classified. UI shows the generic error treatment. */
    Generic,
}

/**
 * Classifies [error] into an [ErrorCategory] by inspecting its class name, message, and
 * cause chain.
 *
 * Matching rules (first match wins):
 * - Network: class or cause name contains `IOException`, `Connect`, `Timeout`, `UnknownHost`
 * - Auth: message matches HTTP 401 / 403 patterns, or contains `Unauthorized` / `Forbidden`
 * - RateLimit: message matches HTTP 429 or contains `Too Many Requests` / `Rate Limit`
 * - Server: message matches HTTP 5xx pattern (500–599)
 * - Generic: fallback
 *
 * Stable across invocations for the same throwable instance.
 */
fun categorize(error: Throwable): ErrorCategory {
    if (error.classNameChain().any { it.matchesNetworkClassName() }) {
        return ErrorCategory.Network
    }
    return error.messageChain()
        .firstNotNullOfOrNull { msg ->
            when {
                msg.matchesAuthPattern() -> ErrorCategory.Auth
                msg.matchesRateLimitPattern() -> ErrorCategory.RateLimit
                msg.matchesServerPattern() -> ErrorCategory.Server
                else -> null
            }
        }
        ?: ErrorCategory.Generic
}

private fun Throwable.classNameChain(): Sequence<String> = sequence {
    var t: Throwable? = this@classNameChain
    while (t != null) {
        t::class.simpleName?.let { yield(it) }
        t = t.cause
    }
}

private fun Throwable.messageChain(): Sequence<String> = sequence {
    var t: Throwable? = this@messageChain
    while (t != null) {
        t.message?.let { yield(it) }
        t = t.cause
    }
}

private fun String.matchesNetworkClassName(): Boolean =
    contains("IOException", ignoreCase = true) ||
        contains("Connect", ignoreCase = true) ||
        contains("Timeout", ignoreCase = true) ||
        contains("UnknownHost", ignoreCase = true) ||
        contains("SSLException", ignoreCase = true) ||
        contains("Offline", ignoreCase = true)

private val AUTH_HTTP_REGEX = Regex("""\b(?:HTTP\s+)?(?:401|403)\b""")

private fun String.matchesAuthPattern(): Boolean =
    AUTH_HTTP_REGEX.containsMatchIn(this) ||
        contains("Unauthorized", ignoreCase = true) ||
        contains("Forbidden", ignoreCase = true)

private val RATE_LIMIT_HTTP_REGEX = Regex("""\b(?:HTTP\s+)?429\b""")

private fun String.matchesRateLimitPattern(): Boolean =
    RATE_LIMIT_HTTP_REGEX.containsMatchIn(this) ||
        contains("Too Many Requests", ignoreCase = true) ||
        contains("Rate Limit", ignoreCase = true) ||
        contains("RateLimit", ignoreCase = true)

private val SERVER_HTTP_REGEX = Regex("""\b(?:HTTP\s+)?5\d{2}\b""")

private fun String.matchesServerPattern(): Boolean =
    SERVER_HTTP_REGEX.containsMatchIn(this)
