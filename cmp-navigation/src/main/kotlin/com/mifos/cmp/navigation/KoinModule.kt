package com.mifos.cmp.navigation

import com.mifos.core.common.network.di.DispatchersModule
import com.mifos.core.data.di.RepositoryModule
import com.mifos.core.domain.di.UseCaseModule
import com.mifos.core.testing.di.TestDispatcherModule
import com.mifos.core.testing.di.TestDispatchersModule
import com.mifos.feature.auth.di.AuthModule
import com.mifos.feature.about.di.AboutModule
import com.mifos.feature.activate.di.ActivateModule
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

import org.koin.dsl.module

object KoinModules {

    private val commonModules = module { DispatchersModule }
    private val domainModule = module { includes(UseCaseModule) }
    private val dataModules = module { includes(RepositoryModule) }

    private val databaseModules = module {
        includes(
            DaoModule,
            DatabaseModule,
        )
    }

    private val testingModules = module {
        includes(
            TestDispatcherModule,
            TestDispatchersModule
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

    private val libraryModule = module {
        includes(

        )
    }
    val allModules = listOf(
        testingModules,
        commonModules,
        domainModule,
        dataModules,
        databaseModules,
        featureModules,
        libraryModule
    )
}