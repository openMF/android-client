/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package kpt.core.store.di

import com.mifos.core.store.provideCheckerTaskStore
import com.mifos.core.store.provideClientPageStore
import com.mifos.core.store.provideClientStore
import com.mifos.core.store.provideDocumentStore
import com.mifos.core.store.provideGroupStore
import com.mifos.core.store.provideLoanTransactionStore
import com.mifos.core.store.provideNoteStore
import com.mifos.core.store.provideReportCategoryStore
import com.mifos.core.store.provideSavingsAccountSummaryStore
import com.mifos.core.store.provideSavingsAccountTransactionStore
import kpt.core.base.store.infra.DraftInventory
import kpt.core.base.store.infra.StoreCacheManager
import kpt.core.base.store.infra.impl.DraftInventoryImpl
import kpt.core.base.store.infra.impl.StoreCacheManagerImpl
import kpt.core.store.AppStoreRegistry
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for app-level Store wiring — mirrors the template's `kpt.core.store.di.
 * StoreModule` shape (offline-first-template-migration 02-store-infra-screenstate T3),
 * kept at the template's own `kpt.core.store.di` package (infra/framework-level seam — kpt is the fixed base namespace), with its `// demo:begin`/`// demo:end` demo-store
 * bindings stripped (this fork never carried that demo content). Both bindings kept here are
 * framework infra, not demo:
 *  - [StoreCacheManager] — clears every registered store's cache on logout (D7).
 *  - [DraftInventory] — cross-form drafts inventory backing the Settings "Sync & Drafts" screen.
 *
 * field-officer's own `single(AppStoreRegistry.X) { provideXStore(...) }` blocks land here
 * starting with the Phase 4 loan-transaction ledger pilot, alongside a matching
 * `mgr.register(get(AppStoreRegistry.X))` entry in a `createdAtStart = true` block (see the
 * template's own StoreModule.kt for the exact registration pattern to replicate).
 *
 * Wire into the Koin start-up:
 * ```kotlin
 * startKoin {
 *     modules(appStoreModule, /* ...other modules */)
 * }
 * ```
 */
val appStoreModule: Module = module {
    // Store cache manager — clears all registered caches on logout (registration-based)
    single<StoreCacheManager> {
        StoreCacheManagerImpl(
            bookkeeperDao = get(),
            draftDao = get(),
        )
    }

    // Cross-form drafts inventory — the live feed + actions behind the Settings →
    // "Sync & Drafts" screen. Framework infra (not a demo store); survives sync.
    single<DraftInventory> { DraftInventoryImpl(draftDao = get()) }

    // Phase 4 loan-transaction ledger pilot — read-only offline-first Stores.
    // Internal: exposed only through repositories / the (follow-up) ViewModel cutover.
    single(AppStoreRegistry.LoanTransactions) {
        provideLoanTransactionStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.SavingsAccountTransactions) {
        provideSavingsAccountTransactionStore(api = get(), dao = get())
    }

    // Read-cache read-only offline-first Stores (Store5 adoption completion).
    single(AppStoreRegistry.Clients) {
        provideClientStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.ClientListPage) {
        provideClientPageStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.Groups) {
        provideGroupStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.CheckerTasks) {
        provideCheckerTaskStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.Documents) {
        provideDocumentStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.ReportCategories) {
        provideReportCategoryStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.Notes) {
        provideNoteStore(api = get(), dao = get())
    }
    single(AppStoreRegistry.SavingsAccountSummary) {
        provideSavingsAccountSummaryStore(api = get(), dao = get())
    }

    // Register the read-cache Stores for logout cache clearing (D7).
    single(createdAtStart = true) {
        val mgr = get<StoreCacheManager>() as StoreCacheManagerImpl
        mgr.register(get(AppStoreRegistry.LoanTransactions))
        mgr.register(get(AppStoreRegistry.SavingsAccountTransactions))
        mgr.register(get(AppStoreRegistry.Clients))
        mgr.register(get(AppStoreRegistry.ClientListPage))
        mgr.register(get(AppStoreRegistry.Groups))
        mgr.register(get(AppStoreRegistry.CheckerTasks))
        mgr.register(get(AppStoreRegistry.Documents))
        mgr.register(get(AppStoreRegistry.ReportCategories))
        mgr.register(get(AppStoreRegistry.Notes))
        mgr.register(get(AppStoreRegistry.SavingsAccountSummary))
    }
}
