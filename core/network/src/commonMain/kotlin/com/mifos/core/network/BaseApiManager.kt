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
import com.mifos.core.network.services.createCenterService
import com.mifos.core.network.services.createChargeService
import com.mifos.core.network.services.createCheckerInboxService
import com.mifos.core.network.services.createClientAccountsService
import com.mifos.core.network.services.createClientService
import com.mifos.core.network.services.createCollectionSheetService
import com.mifos.core.network.services.createDataTableService
import com.mifos.core.network.services.createDocumentService
import com.mifos.core.network.services.createGroupService
import com.mifos.core.network.services.createLoanService
import com.mifos.core.network.services.createNoteService
import com.mifos.core.network.services.createOfficeService
import com.mifos.core.network.services.createRunReportsService
import com.mifos.core.network.services.createSavingsAccountService
import com.mifos.core.network.services.createSearchService
import com.mifos.core.network.services.createStaffService
import com.mifos.core.network.services.createSurveyService
import de.jensklingenberg.ktorfit.Ktorfit

class BaseApiManager(
    private val prefManager: UserPreferencesRepository,
    private val ktorfit: Ktorfit,
) {

    val centerApi: CenterService = ktorfit.createCenterService()
    val accountsApi: ClientAccountsService = ktorfit.createClientAccountsService()
    val clientsApi: ClientService = ktorfit.createClientService()
    val dataTableApi: DataTableService = ktorfit.createDataTableService()
    val loanApi: LoanService = ktorfit.createLoanService()
    val savingsApi: SavingsAccountService = ktorfit.createSavingsAccountService()
    val searchApi: SearchService = ktorfit.createSearchService()
    val groupApi: GroupService = ktorfit.createGroupService()
    val documentApi: DocumentService = ktorfit.createDocumentService()
    val officeApi: OfficeService = ktorfit.createOfficeService()
    val staffApi: StaffService = ktorfit.createStaffService()
    val surveyApi: SurveyService = ktorfit.createSurveyService()
    val chargeApi: ChargeService = ktorfit.createChargeService()
    val checkerInboxApi: CheckerInboxService = ktorfit.createCheckerInboxService()
    val collectionSheetApi: CollectionSheetService = ktorfit.createCollectionSheetService()
    val noteApi: NoteService = ktorfit.createNoteService()
    val runReportsService: RunReportsService = ktorfit.createRunReportsService()

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
