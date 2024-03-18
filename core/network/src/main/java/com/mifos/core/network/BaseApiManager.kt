/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network

import com.google.gson.GsonBuilder
import com.mifos.core.datastore.PrefManager
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
import org.mifos.core.utils.JsonDateSerializer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Date
import javax.inject.Inject

/**
 * @author fomenkoo
 */
class BaseApiManager @Inject constructor(private val prefManager: PrefManager) {


    init {
        createService(prefManager)
    }

    val centerApi: CenterService
        get() = Companion.centerApi
    val accountsApi: ClientAccountsService
        get() = Companion.accountsApi
    val clientsApi: ClientService
        get() = Companion.clientsApi
    val dataTableApi: DataTableService
        get() = Companion.dataTableApi
    val loanApi: LoanService
        get() = Companion.loanApi
    val savingsApi: SavingsAccountService
        get() = Companion.savingsApi
    val searchApi: SearchService
        get() = Companion.searchApi
    val groupApi: GroupService
        get() = Companion.groupApi
    val documentApi: DocumentService
        get() = Companion.documentApi
    val officeApi: OfficeService
        get() = Companion.officeApi
    val staffApi: StaffService
        get() = Companion.staffApi
    val surveyApi: SurveyService
        get() = Companion.surveyApi
    val chargeApi: ChargeService
        get() = Companion.chargeApi
    val checkerInboxApi: CheckerInboxService
        get() = Companion.checkerInboxApi
    val collectionSheetApi: CollectionSheetService
        get() = Companion.collectionSheetApi
    val noteApi: NoteService
        get() = Companion.noteApi
    val runReportsService: RunReportsService
        get() = Companion.runReportsService

    companion object {
        private var mRetrofit: Retrofit? = null
        private lateinit var centerApi: CenterService
        private lateinit var accountsApi: ClientAccountsService
        private lateinit var clientsApi: ClientService
        private lateinit var dataTableApi: DataTableService
        private lateinit var loanApi: LoanService
        private lateinit var savingsApi: SavingsAccountService
        private lateinit var chargeApi: ChargeService
        private lateinit var searchApi: SearchService
        private lateinit var groupApi: GroupService
        private lateinit var documentApi: DocumentService
        private lateinit var officeApi: OfficeService
        private lateinit var staffApi: StaffService
        private lateinit var surveyApi: SurveyService
        private lateinit var runReportsService: RunReportsService
        private lateinit var noteApi: NoteService
        private lateinit var collectionSheetApi: CollectionSheetService
        private lateinit var checkerInboxApi: CheckerInboxService

        fun init() {
            centerApi = createApi(
                CenterService::class.java
            )
            accountsApi = createApi(
                ClientAccountsService::class.java
            )
            clientsApi = createApi(
                ClientService::class.java
            )
            dataTableApi = createApi(
                DataTableService::class.java
            )
            loanApi = createApi(
                LoanService::class.java
            )
            savingsApi = createApi(
                SavingsAccountService::class.java
            )
            searchApi = createApi(
                SearchService::class.java
            )
            groupApi = createApi(
                GroupService::class.java
            )
            documentApi = createApi(
                DocumentService::class.java
            )
            officeApi = createApi(
                OfficeService::class.java
            )
            staffApi = createApi(
                StaffService::class.java
            )
            surveyApi = createApi(
                SurveyService::class.java
            )
            chargeApi = createApi(
                ChargeService::class.java
            )
            runReportsService = createApi(
                RunReportsService::class.java
            )
            noteApi = createApi(
                NoteService::class.java
            )
            collectionSheetApi = createApi(
                CollectionSheetService::class.java
            )
            checkerInboxApi = createApi(
                CheckerInboxService::class.java
            )
        }

        private fun <T> createApi(clazz: Class<T>): T {
            return mRetrofit!!.create(clazz)
        }

        fun createService(prefManager: PrefManager) {
            val gson = GsonBuilder()
                .registerTypeAdapter(Date::class.java, JsonDateSerializer()).create()
            mRetrofit = Retrofit.Builder()
                .baseUrl(prefManager.getInstanceUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(MifosOkHttpClient(prefManager).mifosOkHttpClient)
                .build()
            init()
        }
    }
}