/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation.di

import com.mifos.core.common.network.di.DispatchersModule
import com.mifos.core.data.di.RepositoryModule
import com.mifos.core.datastore.di.PreferencesModule
import com.mifos.core.domain.di.UseCaseModule
import com.mifos.core.network.di.DataManagerModule
import com.mifos.core.network.di.NetworkModule
import com.mifos.feature.auth.di.AuthModule
import com.mifos.feature.note.di.NoteModule
import com.mifos.room.di.DaoModule
import com.mifos.room.di.HelperModule
import com.mifos.room.di.PlatformSpecificDatabaseModule
import org.koin.dsl.module

object KoinModules {

    private val commonModules = module { includes(DispatchersModule) }
    private val domainModule = module { includes(UseCaseModule) }
    private val dataModules = module { includes(RepositoryModule) }
    private val coreDataStoreModules = module { includes(PreferencesModule) }
    private val databaseModules = module {
        includes(
            DaoModule,
            HelperModule,
            PlatformSpecificDatabaseModule,
        )
    }

    private val networkModules = module {
        includes(
            DataManagerModule,
            NetworkModule,
        )
    }

    private val featureModules = module {
        includes(
//            AboutModule,
//            ActivateModule,
            AuthModule,
//            CenterModule,
//            CheckerInboxTaskModule,
//            ClientModule,
//            CollectionSheetModule,
//            DataTableModule,
//            DocumentModule,
//            GroupsModule,
//            LoanModule,
            NoteModule,
//            OfflineModule,
//            PathTrackingModule,
//            ReportModule,
//            SavingsModule,
//            SearchModule,
//            SettingsModule,
//            SplashModule,
        )
    }

    val allModules = listOf(
        commonModules,
        domainModule,
        dataModules,
        databaseModules,
        featureModules,
        networkModules,
        coreDataStoreModules,
    )
}
