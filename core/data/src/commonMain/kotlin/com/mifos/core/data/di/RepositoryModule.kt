/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.di

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.data.activate.ActivateRepository
import com.mifos.core.data.activate.impl.ActivateRepositoryImpl
import com.mifos.core.data.auth.LoginRepository
import com.mifos.core.data.auth.impl.LoginRepositoryImpl
import com.mifos.core.data.infra.NetworkMonitor
import com.mifos.core.data.infra.impl.NetworkMonitorImpl
import com.mifos.core.data.infra.impl.RoomFetchedAtRepository
import com.mifos.core.data.note.NoteRepository
import com.mifos.core.data.note.impl.NoteRepositoryImpl
import com.mifos.core.data.note.impl.provideNoteListStore
import com.mifos.core.data.pathtracking.PathTrackingRepository
import com.mifos.core.data.pathtracking.impl.PathTrackingRepositoryImpl
import com.mifos.core.data.pathtracking.impl.providePathTrackingListStore
import com.mifos.core.data.repository.AppLockRepository
import com.mifos.core.data.repository.UserVerificationRepository
import com.mifos.core.data.repositoryImp.AppLockRepositoryImpl
import com.mifos.core.data.repositoryImp.BiometricStorageAdapterImpl
import com.mifos.core.data.repositoryImp.PasscodeStorageAdapterImpl
import com.mifos.core.data.repositoryImp.UserVerificationRepositoryImpl
import com.mifos.core.data.search.SearchRepository
import com.mifos.core.data.search.impl.SearchRepositoryImpl
import com.mifos.core.data.searchrecord.SearchRecordRepository
import com.mifos.core.data.searchrecord.impl.SearchRecordRepositoryImpl
import com.mifos.core.data.searchrecord.local.SearchRecordLocalDataSource
import com.mifos.core.data.searchrecord.local.SearchRecordLocalDataSourceImpl
import com.mifos.core.store.infra.StoreCacheManager
import com.mifos.core.store.infra.impl.StoreCacheManagerImpl
import com.mifos.room.MifosDatabase
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.mifos.authenticator.biometrics.BiometricStorageAdapter
import org.mifos.authenticator.passcode.PasscodeStorageAdapter
import template.core.base.store.infra.FetchedAtRepository

/**
 * Active Koin module — binds only the repositories whose Impls are NOT quarantined.
 *
 * **Quarantined bindings live in `_legacy/`** outside the Kotlin source root. Each feature
 * wave (W4 client, W6 document, W9 loan, W10 savings, W13 center, W14 groups, etc.) un-
 * quarantines its specific repositories + reintroduces their bindings here, refactored to
 * the Store5 pattern (`single<XRepository> { XRepositoryImpl(xStore = get(AppStoreRegistry.X), …) }`).
 *
 * Current active surface (post 2026-05-22 structural checkpoint):
 *
 * - Auth (W1 ✅), Search-record (W4 ✅), Note (W7 partial ✅), Path-tracking (W11 partial ✅)
 * - Activate (active), Search (active), Document interface only (impl quarantined)
 * - App-lock / Passcode storage / Biometrics storage / User verification (W2 prep)
 * - Framework infra: NetworkMonitor, FetchedAtRepository, StoreCacheManager + framework DAOs
 *
 * Per-wave migration recipe in `plan-layer/.../feature-vertical-migration/PLAN.md`.
 */
val RepositoryModule = module {
    single<CoroutineDispatcher> { get(named(MifosDispatchers.IO.name)) }

    // Authentication + search (W1 ✅, search-record W4 ✅)
    singleOf(::LoginRepositoryImpl) bind LoginRepository::class
    singleOf(::SearchRepositoryImpl) bind SearchRepository::class

    // Activate (currently active — small feature, will graduate to Store5 at W11)
    singleOf(::ActivateRepositoryImpl) bind ActivateRepository::class

    // Note feature — Store5 list cache + offline-first repository (W7 partial ✅)
    single { provideNoteListStore(get(), get(), get()) }
    single<NoteRepository> {
        NoteRepositoryImpl(
            noteApi = get(),
            noteListStore = get(),
            networkMonitor = get(),
            fetchedAtRepository = get(),
        )
    }

    // Path-tracking feature — Store5 list cache + offline-first repository (W11 partial ✅)
    single { providePathTrackingListStore(get(), get(), get()) }
    single<PathTrackingRepository> {
        PathTrackingRepositoryImpl(
            pathTrackingApi = get(),
            pathTrackingListStore = get(),
            networkMonitor = get(),
            fetchedAtRepository = get(),
        )
    }

    // Search-record feature — local cached search history (W4 ✅)
    singleOf(::SearchRecordRepositoryImpl) bind SearchRecordRepository::class
    singleOf(::SearchRecordLocalDataSourceImpl) bind SearchRecordLocalDataSource::class

    // App lock, passcode, biometrics, user-verification (W2 prep)
    singleOf(::AppLockRepositoryImpl) bind(AppLockRepository::class)
    singleOf(::PasscodeStorageAdapterImpl) bind(PasscodeStorageAdapter::class)
    singleOf(::BiometricStorageAdapterImpl) bind(BiometricStorageAdapter::class)
    singleOf(::UserVerificationRepositoryImpl) bind(UserVerificationRepository::class)

    includes(platformModule)

    // Framework infra (Phase B2 — `core/data/.../infra/` + `core/store/.../infra/`)
    single<NetworkMonitor> { NetworkMonitorImpl() }
    single<FetchedAtRepository> { RoomFetchedAtRepository(get<MifosDatabase>().fetchedAtDao) }
    single<StoreCacheManager> {
        StoreCacheManagerImpl(
            bookkeeperDao = get<MifosDatabase>().bookkeeperDao,
            draftDao = get<MifosDatabase>().draftDao,
        )
    }

    // Framework Room DAOs (direct injection for SubmitOutbox / Bookkeeper consumers)
    single { get<MifosDatabase>().bookkeeperDao }
    single { get<MifosDatabase>().draftDao }
    single { get<MifosDatabase>().fetchedAtDao }
}
