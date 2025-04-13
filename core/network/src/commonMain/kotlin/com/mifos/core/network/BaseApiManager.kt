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
import de.jensklingenberg.ktorfit.Ktorfit

class BaseApiManager(
    private val prefManager: UserPreferencesRepository,
    private val ktorfit: Ktorfit,
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
            val ktorfitClient = KtorfitClient.builder()
                .httpClient(KtorHttpClient)
                .baseURL(prefManager.getServerConfig.value.getInstanceUrl())
                .build()

            return BaseApiManager(prefManager, ktorfitClient.ktorfit)
        }
    }
}
