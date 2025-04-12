/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network

import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.network.services.CenterService
import com.mifos.core.network.services.ChargeService
import com.mifos.core.network.services.CheckerInboxService
import com.mifos.core.network.services.ClientAccountsService
import com.mifos.core.network.services.ClientService
import com.mifos.core.network.services.CollectionSheetService
import com.mifos.core.network.services.DataTableService
import com.mifos.core.network.services.DocumentService
import com.mifos.core.network.services.GroupService
import com.mifos.core.network.services.LoanService
import com.mifos.core.network.services.NoteService
import com.mifos.core.network.services.OfficeService
import com.mifos.core.network.services.RunReportsService
import com.mifos.core.network.services.SavingsAccountService
import com.mifos.core.network.services.SearchService
import com.mifos.core.network.services.StaffService
import com.mifos.core.network.services.SurveyService


///**
// * @author fomenkoo
// */
//class BaseApiManager(private val prefManager: UserPreferencesRepository) {
//    init {
//        createService(prefManager)
//    }
//
//    val centerApi: CenterService
//        get() = Companion.centerApi
//    val accountsApi: ClientAccountsService
//        get() = Companion.accountsApi
//    val clientsApi: ClientService
//        get() = Companion.clientsApi
//    val dataTableApi: DataTableService
//        get() = Companion.dataTableApi
//    val loanApi: LoanService
//        get() = Companion.loanApi
//    val savingsApi: SavingsAccountService
//        get() = Companion.savingsApi
//    val searchApi: SearchService
//        get() = Companion.searchApi
//    val groupApi: GroupService
//        get() = Companion.groupApi
//    val documentApi: DocumentService
//        get() = Companion.documentApi
//    val officeApi: OfficeService
//        get() = Companion.officeApi
//    val staffApi: StaffService
//        get() = Companion.staffApi
//    val surveyApi: SurveyService
//        get() = Companion.surveyApi
//    val chargeApi: ChargeService
//        get() = Companion.chargeApi
//    val checkerInboxApi: CheckerInboxService
//        get() = Companion.checkerInboxApi
//    val collectionSheetApi: CollectionSheetService
//        get() = Companion.collectionSheetApi
//    val noteApi: NoteService
//        get() = Companion.noteApi
//    val runReportsService: RunReportsService
//        get() = Companion.runReportsService
//
//    companion object {
//        private var mRetrofit: Retrofit? = null
//        private lateinit var centerApi: CenterService
//        private lateinit var accountsApi: ClientAccountsService
//        private lateinit var clientsApi: ClientService
//        private lateinit var dataTableApi: DataTableService
//        private lateinit var loanApi: LoanService
//        private lateinit var savingsApi: SavingsAccountService
//        private lateinit var chargeApi: ChargeService
//        private lateinit var searchApi: SearchService
//        private lateinit var groupApi: GroupService
//        private lateinit var documentApi: DocumentService
//        private lateinit var officeApi: OfficeService
//        private lateinit var staffApi: StaffService
//        private lateinit var surveyApi: SurveyService
//        private lateinit var runReportsService: RunReportsService
//        private lateinit var noteApi: NoteService
//        private lateinit var collectionSheetApi: CollectionSheetService
//        private lateinit var checkerInboxApi: CheckerInboxService
//
//        fun init() {
//            centerApi = createApi(
//                CenterService::class.java,
//            )
//            accountsApi = createApi(
//                ClientAccountsService::class.java,
//            )
//            clientsApi = createApi(
//                ClientService::class.java,
//            )
//            dataTableApi = createApi(
//                DataTableService::class.java,
//            )
//            loanApi = createApi(
//                LoanService::class.java,
//            )
//            savingsApi = createApi(
//                SavingsAccountService::class.java,
//            )
//            searchApi = createApi(
//                SearchService::class.java,
//            )
//            groupApi = createApi(
//                GroupService::class.java,
//            )
//            documentApi = createApi(
//                DocumentService::class.java,
//            )
//            officeApi = createApi(
//                OfficeService::class.java,
//            )
//            staffApi = createApi(
//                StaffService::class.java,
//            )
//            surveyApi = createApi(
//                SurveyService::class.java,
//            )
//            chargeApi = createApi(
//                ChargeService::class.java,
//            )
//            runReportsService = createApi(
//                RunReportsService::class.java,
//            )
//            noteApi = createApi(
//                NoteService::class.java,
//            )
//            collectionSheetApi = createApi(
//                CollectionSheetService::class.java,
//            )
//            checkerInboxApi = createApi(
//                CheckerInboxService::class.java,
//            )
//        }
//
//        private fun <T> createApi(clazz: Class<T>): T {
//            return mRetrofit!!.create(clazz)
//        }
//
//        fun createService(prefManager: UserPreferencesRepository) {
//            /**
//             *  JsonDateSerializer is imported from com.mifos.core.network.utils.JsonDateSerializer
//             *  but it required to import from org.mifos.core.utils.JsonDateSerializer in
//             *  fineract-android-sdk library after convert from Gson to kotlinx.json
//             */
//
//            val json = Json {
//                serializersModule = SerializersModule {
//                    contextual(Date::class, JsonDateSerializer)
//                }
//                ignoreUnknownKeys = true
//                isLenient = true
//                prettyPrint = true
//            }
//
//            val instanceUrl = prefManager.getServerConfig.value.getInstanceUrl()
//            mRetrofit = Retrofit.Builder()
//                .baseUrl(instanceUrl)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
////                .addCallAdapterFactory(FlowCallAdapterFactory.create())
//                .client(MifosOkHttpClient(prefManager).okHttpClient)
//                .build()
//            init()
//        }
//    }
//}


/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import de.jensklingenberg.ktorfit.Ktorfit

class BaseApiManager(
    private val prefManager: UserPreferencesRepository,
    private val ktorfit: Ktorfit
) {

    val centerApi: CenterService = ktorfit.create()
    val accountsApi: ClientAccountsService = ktorfit.create()
    val clientsApi: ClientService = ktorfit.create()
    val dataTableApi: DataTableService = ktorfit.create()
    val loanApi: LoanService = ktorfit.create()
    val savingsApi: SavingsAccountService = ktorfit.create()
    val searchApi: SearchService = ktorfit.create()
    val groupApi: GroupService = ktorfit.create()
    val documentApi: DocumentService = ktorfit.create()
    val officeApi: OfficeService = ktorfit.create()
    val staffApi: StaffService = ktorfit.create()
    val surveyApi: SurveyService = ktorfit.create()
    val chargeApi: ChargeService = ktorfit.create()
    val checkerInboxApi: CheckerInboxService = ktorfit.create()
    val collectionSheetApi: CollectionSheetService = ktorfit.create()
    val noteApi: NoteService = ktorfit.create()
    val runReportsService: RunReportsService = ktorfit.create()

    companion object {
        fun build(prefManager: UserPreferencesRepository): BaseApiManager {
            val ktorfitClient  = KtorfitClient.builder()
                .httpClient(KtorHttpClient)
                .baseURL(prefManager.getServerConfig.value.getInstanceUrl())
                .build()

            return BaseApiManager(prefManager, ktorfitClient.ktorfit)
        }
    }
}

