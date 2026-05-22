/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package com.mifos.core.store

import template.core.base.store.infra.StoreRegistry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit.DAYS
import kotlin.time.toDuration

/**
 * Application-level [StoreRegistry] — the single named-qualifier registry for every
 * `org.mobilenativefoundation.store.store5.Store` the app exposes.
 *
 * Add domain stores here as `val`s, e.g.:
 *
 * ```kotlin
 * object AppStoreRegistry : StoreRegistry() {
 *     val ClientList = store("clientList")
 *     val LoanAccounts = store("loanAccounts")
 * }
 * ```
 *
 * Then reference the qualifier from Koin DI:
 *
 * ```kotlin
 * single<Store<Long, ClientList>>(qualifier = AppStoreRegistry.ClientList) { ... }
 * ```
 *
 * Centralizing here gives a one-place audit of every Store the app owns and prevents
 * qualifier-name collisions across feature modules.
 */
object AppStoreRegistry : StoreRegistry()

/**
 * Standard freshness windows for Store5 cache policies — compare `fetchedAt`
 * column against `now - Ttl.X` to decide whether to refetch from the network.
 *
 * Pick by data volatility, not by feature:
 *  - [Short]  — list views, search results, dashboards (high churn).
 *  - [Medium] — account/loan/savings details, profile lookups.
 *  - [Long]   — catalog/config: products, currencies, payment types.
 *  - [Day]    — slow-moving metadata: countries, address options.
 */
object Ttl {
    val Short: Duration = 5.minutes
    val Medium: Duration = 30.minutes
    val Long: Duration = 1.hours
    val Day: Duration = 1.toDuration(DAYS)
}
