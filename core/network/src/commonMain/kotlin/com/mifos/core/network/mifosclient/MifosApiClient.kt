/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.mifosclient

import com.mifos.core.network.auth.api.AuthApi
import com.mifos.core.network.auth.api.createAuthApi
import com.mifos.core.network.center.api.CenterApi
import com.mifos.core.network.center.api.createCenterApi
import com.mifos.core.network.charge.api.ChargeApi
import com.mifos.core.network.charge.api.createChargeApi
import com.mifos.core.network.checkerinbox.api.CheckerInboxApi
import com.mifos.core.network.checkerinbox.api.createCheckerInboxApi
import com.mifos.core.network.client.api.ClientApi
import com.mifos.core.network.client.api.createClientApi
import com.mifos.core.network.collectionsheet.api.CollectionSheetApi
import com.mifos.core.network.collectionsheet.api.createCollectionSheetApi
import com.mifos.core.network.datatable.api.DataTableApi
import com.mifos.core.network.datatable.api.createDataTableApi
import com.mifos.core.network.document.api.DocumentApi
import com.mifos.core.network.document.api.createDocumentApi
import com.mifos.core.network.fixeddeposit.api.FixedDepositApi
import com.mifos.core.network.fixeddeposit.api.createFixedDepositApi
import com.mifos.core.network.group.api.GroupApi
import com.mifos.core.network.group.api.createGroupApi
import com.mifos.core.network.loan.api.LoanApi
import com.mifos.core.network.loan.api.createLoanApi
import com.mifos.core.network.note.api.NoteApi
import com.mifos.core.network.note.api.createNoteApi
import com.mifos.core.network.office.api.OfficeApi
import com.mifos.core.network.office.api.createOfficeApi
import com.mifos.core.network.pathtracking.api.PathTrackingApi
import com.mifos.core.network.pathtracking.api.createPathTrackingApi
import com.mifos.core.network.recurringdeposit.api.RecurringDepositApi
import com.mifos.core.network.recurringdeposit.api.createRecurringDepositApi
import com.mifos.core.network.report.api.ReportApi
import com.mifos.core.network.report.api.createReportApi
import com.mifos.core.network.savings.api.SavingsApi
import com.mifos.core.network.savings.api.createSavingsApi
import com.mifos.core.network.search.api.SearchApi
import com.mifos.core.network.search.api.createSearchApi
import com.mifos.core.network.share.api.ShareApi
import com.mifos.core.network.share.api.createShareApi
import com.mifos.core.network.staff.api.StaffApi
import com.mifos.core.network.staff.api.createStaffApi
import com.mifos.core.network.survey.api.SurveyApi
import com.mifos.core.network.survey.api.createSurveyApi
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * Holds the Ktorfit-built instance of every per-domain Api in `core/network/<domain>/api/`.
 *
 * Each property is lazy so individual Apis are only instantiated when first accessed. Koin
 * exposes a single [MifosApiClient] and per-Api accessors via `single<XxxApi> { get<MifosApiClient>().xxxApi }`.
 */
class MifosApiClient(
    private val ktorfit: Ktorfit,
) {
    val authApi: AuthApi by lazy { ktorfit.createAuthApi() }
    val centerApi: CenterApi by lazy { ktorfit.createCenterApi() }
    val chargeApi: ChargeApi by lazy { ktorfit.createChargeApi() }
    val checkerInboxApi: CheckerInboxApi by lazy { ktorfit.createCheckerInboxApi() }
    val clientApi: ClientApi by lazy { ktorfit.createClientApi() }
    val collectionSheetApi: CollectionSheetApi by lazy { ktorfit.createCollectionSheetApi() }
    val dataTableApi: DataTableApi by lazy { ktorfit.createDataTableApi() }
    val documentApi: DocumentApi by lazy { ktorfit.createDocumentApi() }
    val fixedDepositApi: FixedDepositApi by lazy { ktorfit.createFixedDepositApi() }
    val groupApi: GroupApi by lazy { ktorfit.createGroupApi() }
    val loanApi: LoanApi by lazy { ktorfit.createLoanApi() }
    val noteApi: NoteApi by lazy { ktorfit.createNoteApi() }
    val officeApi: OfficeApi by lazy { ktorfit.createOfficeApi() }
    val pathTrackingApi: PathTrackingApi by lazy { ktorfit.createPathTrackingApi() }
    val recurringDepositApi: RecurringDepositApi by lazy { ktorfit.createRecurringDepositApi() }
    val reportApi: ReportApi by lazy { ktorfit.createReportApi() }
    val savingsApi: SavingsApi by lazy { ktorfit.createSavingsApi() }
    val searchApi: SearchApi by lazy { ktorfit.createSearchApi() }
    val shareApi: ShareApi by lazy { ktorfit.createShareApi() }
    val staffApi: StaffApi by lazy { ktorfit.createStaffApi() }
    val surveyApi: SurveyApi by lazy { ktorfit.createSurveyApi() }
}
