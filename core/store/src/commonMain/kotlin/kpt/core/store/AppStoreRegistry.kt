/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package kpt.core.store

import kpt.core.base.store.infra.StoreRegistry

/**
 * Application-level [StoreRegistry] — the single named-qualifier registry for every
 * `org.mobilenativefoundation.store.store5.Store` the app exposes.
 *
 * Mirrors the template's `kpt.core.store.AppStoreRegistry` shape (offline-first-template-
 * migration 02-store-infra-screenstate T3) with its `// demo:begin`/`// demo:end` example
 * stores (ExchangeRates, RateHistory, CoinMarkets, CoinDetail, ...) already stripped — this
 * fork never carried that demo content (never synced in, per 01-template-adoption). Add
 * field-officer's own stores here starting with the Phase 4 loan-transaction ledger pilot:
 *
 * ```kotlin
 * object AppStoreRegistry : StoreRegistry() {
 *     val LoanTransactions = store("loanTransactions")
 *     val Clients = store("clients")
 * }
 * ```
 *
 * Then reference the qualifier from Koin DI in `appStoreModule`:
 *
 * ```kotlin
 * single<Store<ClientId, ClientAccount>>(qualifier = AppStoreRegistry.Clients) { ... }
 * ```
 *
 * Centralizing here gives a one-place audit of every Store the app owns and prevents
 * qualifier-name collisions across feature modules.
 */
object AppStoreRegistry : StoreRegistry() {
    /** Read-only offline-first ledger of a loan's transactions (Phase 4 pilot). */
    val LoanTransactions = store("loanTransaction")

    /** Read-only offline-first ledger of a savings account's transactions (Phase 4 pilot). */
    val SavingsAccountTransactions = store("savingsAccountTransaction")

    /** Read-only offline-first single-client details (client-details read-cache). */
    val Clients = store("clients")

    /** Offline-first paged client list (Store5 native paging — `Store<PageKey, List<ClientEntity>>`). */
    val ClientListPage = store("clientListPage")

    /** Read-only offline-first single-group details (getGroup read-cache). */
    val Groups = store("groups")

    /** Offline-first paged group list (Store5 native paging — `Store<PageKey, List<GroupEntity>>`). */
    val GroupListPage = store("groupListPage")

    /** Read-only offline-first checker-inbox task list (single global list, keyed by Unit). */
    val CheckerTasks = store("checkerTasks")

    /** Read-only offline-first document-metadata list, keyed by (entityType, entityId). */
    val Documents = store("documents")

    /** Read-only offline-first run-report category list, keyed by the request scope tuple. */
    val ReportCategories = store("reportCategories")

    /** Read-only offline-first note list for a parent, keyed by (entityType, entityId). */
    val Notes = store("notes")

    /** Read-only offline-first single savings-account summary (full-entity read-cache). */
    val SavingsAccountSummary = store("savingsAccountSummary")
}
