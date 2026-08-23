/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.di

import cmp.navigation.AppViewModel
import cmp.navigation.authenticatednavbar.AuthenticatedNavbarNavigationViewModel
import cmp.navigation.registry.FeatureRegistry
import cmp.navigation.rootnav.RootNavViewModel
import com.mifos.feature.auth.di.AuthModule
import com.mifos.core.common.network.di.DispatchersModule
import com.mifos.core.data.di.RepositoryModule
import com.mifos.core.datastore.di.PreferencesModule
import com.mifos.core.domain.di.UseCaseModule
import com.mifos.core.network.di.DataManagerModule
import com.mifos.core.network.di.NetworkModule
import com.mifos.feature.passcode.di.MifosAuthenticatorModule
import com.mifos.room.di.DaoModule
import com.mifos.room.di.HelperModule
import kpt.core.base.common.di.CommonModule
import kpt.core.base.firebase.di.firebaseModule
import kpt.core.base.platform.di.platformModule
import kpt.core.base.security.di.SecurityModule
import kpt.core.data.di.DataModule
import kpt.core.database.di.DatabaseModule
import kpt.core.datastore.di.DatastoreModule
import kpt.core.store.di.appStoreModule
import kpt.feature.home.di.HomeModule
import kpt.feature.settings.SettingsModule
import kpt.sync.di.SyncModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object KoinModules {
    private val dataModule = module {
        includes(DataModule, appStoreModule)
    }

    private val dispatcherModule = module {
        includes(CommonModule)
    }

    private val AppModule = module {
        includes(platformModule)

        viewModelOf(::AppViewModel)
        viewModelOf(::AuthenticatedNavbarNavigationViewModel)
        viewModelOf(::RootNavViewModel)
    }

    private val featureModule = module {
        // Framework SHELL modules — always present.
        includes(HomeModule, SettingsModule)
        // Fork features — from the fork-owned FeatureRegistry seam (white-label: the template infra
        // NEVER edits this; a fork adds/removes features by editing cmp-navigation/registry/FeatureRegistry.kt).
        includes(FeatureRegistry.featureKoinModules)
    }

    /**
     * Fork-owned (`com.mifos.*`) DI backbone — the modules a template sync wired the template
     * (`kpt.*`) infra for but DROPPED, leaving `DataManager`/`BaseApiManager`/fork-repository/
     * use-case/Room-DAO bindings undefined. Every fork feature (loan is wired via FeatureRegistry)
     * and the eager `Store(loanTransaction)` singleton (appStoreModule) transitively depend on these.
     *
     * KMP-clean: all eight are authored in `commonMain`. The android-only `AndroidDataModule`
     * (`PlatformDependentDataModule.android.kt`) is NOT included from any android app class — it is
     * reached through the fork's existing commonMain `expect val platformModule` seam
     * (`com.mifos.core.data.di.PlatformDependentDataModule`, `actual` = AndroidDataModule on android),
     * which `RepositoryModule` already `includes(platformModule)`. So including RepositoryModule here
     * wires the platform data module on every target from commonMain, no per-platform edit.
     *
     * Ordering: this group is appended LAST in [allModules] on purpose. Both this group's
     * `RepositoryModule` (unqualified `single<CoroutineDispatcher> { get(named(IO)) }`, consumed by
     * the fork repositories' `ioDispatcher` ctor param) AND the template `kpt.core.base.platform.di.
     * platformModule` (`single<CoroutineDispatcher> { Dispatchers.Unconfined }`, consumed by the
     * GarbageCollectionManager) bind the SAME unqualified `CoroutineDispatcher` key. Koin's default
     * `allowOverride = true` makes the last-loaded definition win; loading the fork group last ensures
     * the fork repositories get `Dispatchers.IO` (correct for DB/network I/O — Unconfined would run
     * them on the caller/main thread). The GC manager tolerates IO. This is the only cross-graph
     * duplicate; every other fork↔template pair binds disjoint fully-qualified types / distinct
     * qualifiers (verified: fork MifosDatabase vs template AppDatabase; com.mifos.* repos vs kpt.*
     * repos; com.mifos UserPreferencesRepository + unqualified Settings vs kpt UserPreferencesRepository
     * + named plain/secure Settings; fork named-IO/Unconfined dispatchers vs CommonModule DispatcherManager).
     */
    private val forkModule = module {
        includes(
            NetworkModule,
            DataManagerModule,
            RepositoryModule,
            UseCaseModule,
            DaoModule,
            HelperModule,
            PreferencesModule,
            DispatchersModule,
            // Fork auth: binds org.mifos.authenticator PasscodeManager + Passcode/Biometric
            // adapters that RootNavViewModel's fork logout/auth sequence resolves.
            MifosAuthenticatorModule,
            // Login VM (feature/auth) — the RootNav AuthenticateUser state's login screen.
            AuthModule,
        )
    }

    val allModules = listOf(
        SecurityModule,
        dataModule,
        DatabaseModule,
        dispatcherModule,
        firebaseModule,
        DatastoreModule,
        featureModule,
        AppModule,
        SyncModule,
        // Fork DI backbone — appended LAST (see [forkModule] KDoc: CoroutineDispatcher override order).
        forkModule,
    )
}
