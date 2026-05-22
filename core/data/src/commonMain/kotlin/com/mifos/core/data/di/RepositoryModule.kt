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
import com.mifos.core.data.document.DocumentRepository
import com.mifos.core.data.document.impl.DocumentRepositoryImpl
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
import com.mifos.core.data.repository.GroupListRepository
import com.mifos.core.data.repository.GroupLoanAccountRepository
import com.mifos.core.data.repository.LoanAccountApprovalRepository
import com.mifos.core.data.repository.LoanChargeRepository
import com.mifos.core.data.repository.LoanRepaymentScheduleRepository
import com.mifos.core.data.repository.LoanTransactionsRepository
import com.mifos.core.data.repository.UserVerificationRepository
import com.mifos.core.data.repositoryImp.AppLockRepositoryImpl
import com.mifos.core.data.repositoryImp.BiometricStorageAdapterImpl
import com.mifos.core.data.repositoryImp.GroupListRepositoryImp
import com.mifos.core.data.repositoryImp.GroupLoanAccountRepositoryImp
import com.mifos.core.data.repositoryImp.LoanAccountApprovalRepositoryImp
import com.mifos.core.data.repositoryImp.LoanChargeRepositoryImp
import com.mifos.core.data.repositoryImp.LoanRepaymentScheduleRepositoryImp
import com.mifos.core.data.repositoryImp.LoanTransactionsRepositoryImp
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

val RepositoryModule = module {
    single<CoroutineDispatcher> { get(named(MifosDispatchers.IO.name)) }

    // Authentication + search
    singleOf(::LoginRepositoryImpl) bind LoginRepository::class
    singleOf(::SearchRepositoryImpl) bind SearchRepository::class

    // Live legacy repositories — Impls in `core/data/repositoryImp/` that did NOT
    // depend on deleted `DataManager*` types. Bindings stay until per-feature
    // waves replace them with Store5 builders.
    singleOf(::GroupListRepositoryImp) bind GroupListRepository::class
    singleOf(::GroupLoanAccountRepositoryImp) bind GroupLoanAccountRepository::class
    singleOf(::LoanAccountApprovalRepositoryImp) bind LoanAccountApprovalRepository::class
    singleOf(::LoanChargeRepositoryImp) bind LoanChargeRepository::class
    singleOf(::LoanRepaymentScheduleRepositoryImp) bind LoanRepaymentScheduleRepository::class
    singleOf(::LoanTransactionsRepositoryImp) bind LoanTransactionsRepository::class

    // Other live repositories
    singleOf(::ActivateRepositoryImpl) bind ActivateRepository::class
    singleOf(::DocumentRepositoryImpl) bind DocumentRepository::class

    // Note feature — Store5 list cache + offline-first repository (Phase C Wave 7)
    single { provideNoteListStore(get(), get(), get()) }
    single<NoteRepository> {
        NoteRepositoryImpl(
            noteApi = get(),
            noteListStore = get(),
            networkMonitor = get(),
            fetchedAtRepository = get(),
        )
    }

    // Path-tracking feature — Store5 list cache + offline-first repository (Phase C Wave 11)
    single { providePathTrackingListStore(get(), get(), get()) }
    single<PathTrackingRepository> {
        PathTrackingRepositoryImpl(
            pathTrackingApi = get(),
            pathTrackingListStore = get(),
            networkMonitor = get(),
            fetchedAtRepository = get(),
        )
    }

    // Search-record feature — local cached search history (Phase C Wave 4)
    singleOf(::SearchRecordRepositoryImpl) bind SearchRecordRepository::class
    singleOf(::SearchRecordLocalDataSourceImpl) bind SearchRecordLocalDataSource::class

    // App lock, passcode, biometrics, user-verification
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
