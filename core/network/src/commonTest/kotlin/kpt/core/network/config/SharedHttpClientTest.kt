/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.network.config

import kpt.core.base.network.AccessPoint
import kpt.core.base.network.AccessPointKind
import kpt.core.base.network.AccessPointRegistry
import kpt.core.base.network.ktorfitFor
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * ONE `HttpClient` per access point, however many APIs sit on it.
 *
 * ## Why this test exists
 *
 * `restApi(...)` registers one Koin `single` per API TYPE, and `ktorfitFor` used to build a fresh
 * client on every call. For a monolith backend that multiplies: this app declares 22
 * `@ApiBinding("mifos")` interfaces over ONE Fineract server, so it was opening 23 HttpClients at
 * the same host — 23 connection pools, 23 TLS session caches, and no reuse between them. Every API
 * paid a full handshake on first use.
 *
 * Measured on device 2026-10-20 before the fix: an authenticated note write took 45–85s and the
 * paired read never settled, on both a newly migrated screen and an already-migrated one.
 *
 * `mobile-wallet` — the older fork, hand-wired before `@ApiBinding` existed — already had the right
 * shape: three `single<KtorfitClient>` keyed by ROLE, each exposing ~24 services from one Ktorfit.
 *
 * ## What it pins
 *
 * The sharing is INVISIBLE at every other layer: the app compiles, every gate passes, and each API
 * works in isolation. Only latency betrays it, and latency is exactly what a test suite does not
 * measure. So the invariant is asserted on object identity instead.
 */
class SharedHttpClientTest {

    private fun point(id: String, host: String) = AccessPoint(
        id = id,
        kind = AccessPointKind.REST,
        baseUrl = "https://$host/",
        loggableHost = host,
    )

    private fun registry() = AccessPointRegistry(
        listOf(point("mifos", "apis.mifos.community"), point("other", "other.example")),
    )

    @Test
    fun every_api_on_one_access_point_shares_one_client() {
        val registry = registry()

        // Two independent resolutions — exactly what two `restApi("mifos") { … }` registrations do
        // for two different API interfaces.
        val first = registry.ktorfitFor("mifos")
        val second = registry.ktorfitFor("mifos")

        // The Ktorfit WRAPPERS are allowed to differ; they are cheap and hold no connections.
        // The CLIENT must not.
        assertSame(
            first.httpClient,
            second.httpClient,
            "Each API on an access point built its own HttpClient. That is 22 connection pools " +
                "against one Fineract host, and it is invisible to every gate — only latency shows it.",
        )
    }

    @Test
    fun distinct_access_points_keep_distinct_clients() {
        val registry = registry()

        // The cache must key on the access point, not collapse to one client for the whole app:
        // a credential belongs to ONE endpoint, and `defaultHeaders` are per access point. Sharing
        // across hosts would leak it to servers that never needed it.
        assertNotSame(
            registry.ktorfitFor("mifos").httpClient,
            registry.ktorfitFor("other").httpClient,
            "Two hosts shared one client — a per-endpoint credential would reach the wrong server.",
        )
    }

    @Test
    fun the_shared_client_survives_across_registry_lookups() {
        val registry = registry()
        val clients = List(5) { registry.ktorfitFor("mifos").httpClient }

        // Five resolutions, one client — the property that actually matters once 22 APIs resolve.
        assertSame(clients.first(), clients.last())
        clients.forEach { assertSame(clients.first(), it) }
    }
}
