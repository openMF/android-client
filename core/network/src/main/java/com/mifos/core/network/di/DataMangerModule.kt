package com.mifos.core.network.di

import com.mifos.core.network.datamanager.DataManagerAuth
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.network.datamanager.DataManagerDataTable
import com.mifos.core.network.datamanager.DataManagerDocument
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerLoan
import com.mifos.core.network.datamanager.DataManagerNote
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerRunReport
import com.mifos.core.network.datamanager.DataManagerSavings
import com.mifos.core.network.datamanager.DataManagerSearch
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.core.network.datamanager.DataManagerSurveys
import org.koin.dsl.module

val DataManagerModule = module {
    single { DataManagerAuth(get()) }
    single { DataManagerCenter(get(), get(), get(), get()) }
    single { DataManagerCharge(get(), get(), get()) }
    single { DataManagerCheckerInbox(get()) }
    single { DataManagerClient(get(), get(), get(), get()) }
    single { DataManagerCollectionSheet(get()) }
    single { DataManagerDataTable(get(), get()) }
    single { DataManagerDocument(get()) }
    single { DataManagerGroups(get(), get(), get(), get(), get()) }
    single { DataManagerLoan(get(), get(), get()) }
    single { DataManagerNote(get()) }
    single { DataManagerOffices(get(), get(), get(), get()) }
    single { DataManagerRunReport(get()) }
    single { DataManagerSavings(get(), get(), get()) }
    single { DataManagerSearch(get()) }
    single { DataManagerStaff(get(), get(), get(), get()) }
    single { DataManagerSurveys(get(), get(), get()) }
}