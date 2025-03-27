/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.cmp.navigation

import com.mifos.core.common.network.di.DispatchersModule
import com.mifos.core.data.di.RepositoryModule
import com.mifos.core.domain.di.UseCaseModule
import com.mifos.core.network.di.DataManagerModule
import com.mifos.core.network.di.NetworkModule
import com.mifos.core.testing.di.TestDispatcherModule
import com.mifos.core.testing.di.TestDispatchersModule
import com.mifos.feature.about.di.AboutModule
import com.mifos.feature.activate.di.ActivateModule
import com.mifos.feature.auth.di.AuthModule
import com.mifos.feature.center.di.CenterModule
import com.mifos.feature.checkerInboxTask.di.CheckerInboxTaskModule
import com.mifos.feature.client.di.ClientModule
import com.mifos.feature.dataTable.di.DataTableModule
import com.mifos.feature.document.ui.DocumentModule
import com.mifos.feature.groups.di.GroupsModule
import com.mifos.feature.individualCollectionSheet.di.CollectionSheetModule
import com.mifos.feature.loan.di.LoanModule
import com.mifos.feature.note.di.NoteModule
import com.mifos.feature.offline.di.OfflineModule
import com.mifos.feature.pathTracking.di.PathTrackingModule
import com.mifos.feature.report.di.ReportModule
import com.mifos.feature.savings.di.SavingsModule
import com.mifos.feature.search.di.SearchModule
import com.mifos.feature.settings.di.SettingsModule
import com.mifos.feature.splash.di.SplashModule
import com.mifos.room.di.DaoModule
import com.mifos.room.di.DatabaseModule
import com.mifos.room.di.HelperModule
import org.koin.dsl.module

object KoinModules {

    private val commonModules = module { includes(DispatchersModule) }
    private val domainModule = module { includes(UseCaseModule) }
    private val dataModules = module { includes(RepositoryModule) }

    private val databaseModules = module {
        includes(
            DaoModule,
            DatabaseModule,
            HelperModule,
        )
    }

    private val networkModules = module {
        includes(
            DataManagerModule,
            NetworkModule,
        )
    }

    private val testingModules = module {
        includes(
            TestDispatcherModule,
            TestDispatchersModule,
        )
    }

    private val featureModules = module {
        includes(
            AboutModule,
            ActivateModule,
            AuthModule,
            CenterModule,
            CheckerInboxTaskModule,
            ClientModule,
            CollectionSheetModule,
            DataTableModule,
            DocumentModule,
            GroupsModule,
            LoanModule,
            NoteModule,
            OfflineModule,
            PathTrackingModule,
            ReportModule,
            SavingsModule,
            SearchModule,
            SettingsModule,
            SplashModule,
        )
    }

    val allModules = listOf(
        testingModules,
        commonModules,
        domainModule,
        dataModules,
        databaseModules,
        featureModules,
        testingModules,
        networkModules,
    )
}
