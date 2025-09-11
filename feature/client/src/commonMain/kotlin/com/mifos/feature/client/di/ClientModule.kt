/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.di

import com.mifos.feature.client.clientAddress.ClientAddressViewModel
import com.mifos.feature.client.clientApplyNewApplications.ClientApplyNewApplicationsViewModel
import com.mifos.feature.client.clientCharges.ClientChargesViewModel
import com.mifos.feature.client.clientClosure.ClientClosureViewModel
import com.mifos.feature.client.clientCollateral.ClientCollateralViewModel
import com.mifos.feature.client.clientDetails.ClientDetailsViewModel
import com.mifos.feature.client.clientDetailsProfile.ClientProfileDetailsViewModel
import com.mifos.feature.client.clientEditDetails.ClientEditDetailsViewModel
import com.mifos.feature.client.clientEditProfile.ClientProfileEditViewModel
import com.mifos.feature.client.clientGeneral.ClientProfileGeneralViewmodel
import com.mifos.feature.client.clientIdentifiers.ClientIdentifiersViewModel
import com.mifos.feature.client.clientIdentitiesList.ClientIdentitiesListViewModel
import com.mifos.feature.client.clientLoanAccounts.ClientLoanAccountsViewModel
import com.mifos.feature.client.clientPinpoint.PinPointClientViewModel
import com.mifos.feature.client.clientProfile.ClientProfileViewModel
import com.mifos.feature.client.clientSignature.ClientSignatureViewModel
import com.mifos.feature.client.clientStaff.ClientStaffViewModel
import com.mifos.feature.client.clientSurveyList.SurveyListViewModel
import com.mifos.feature.client.clientSurveySubmit.SurveySubmitViewModel
import com.mifos.feature.client.clientTransfer.ClientTransferViewModel
import com.mifos.feature.client.clientUpcomingCharges.ClientUpcomingChargesViewmodel
import com.mifos.feature.client.clientUpdateDefaultAccount.UpdateDefaultAccountViewModel
import com.mifos.feature.client.clientsList.ClientListViewModel
import com.mifos.feature.client.createNewClient.CreateNewClientViewModel
import com.mifos.feature.client.fixedDepositAccount.FixedDepositAccountViewModel
import com.mifos.feature.client.recurringDepositAccount.RecurringDepositAccountViewModel
import com.mifos.feature.client.savingsAccounts.SavingsAccountsViewModel
import com.mifos.feature.client.shareAccounts.ShareAccountsViewModel
import com.mifos.feature.client.syncClientDialog.SyncClientsDialogViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ClientModule = module {
    viewModelOf(::ClientChargesViewModel)
    viewModelOf(::ClientDetailsViewModel)
    viewModelOf(::ClientIdentifiersViewModel)
    viewModelOf(::ClientAddressViewModel)
    viewModelOf(::ClientListViewModel)
    viewModelOf(::PinPointClientViewModel)
    viewModelOf(::SurveyListViewModel)
    viewModelOf(::SurveySubmitViewModel)
    viewModelOf(::CreateNewClientViewModel)
    viewModelOf(::ClientEditDetailsViewModel)
    viewModelOf(::SyncClientsDialogViewModel)
    viewModelOf(::ClientProfileViewModel)
    viewModelOf(::ClientProfileGeneralViewmodel)
    viewModelOf(::ClientProfileDetailsViewModel)
    viewModelOf(::ClientProfileEditViewModel)
    viewModelOf(::ClientStaffViewModel)
    viewModelOf(::ClientTransferViewModel)
    viewModelOf(::UpdateDefaultAccountViewModel)
    viewModelOf(::ClientClosureViewModel)
    viewModelOf(::SavingsAccountsViewModel)
    viewModelOf(::RecurringDepositAccountViewModel)
    viewModelOf(::FixedDepositAccountViewModel)
    viewModelOf(::ClientCollateralViewModel)
    viewModelOf(::ClientLoanAccountsViewModel)
    viewModelOf(::ClientIdentitiesListViewModel)
    viewModelOf(::ClientApplyNewApplicationsViewModel)
    viewModelOf(::ClientSignatureViewModel)
    viewModelOf(::ClientUpcomingChargesViewmodel)
    viewModelOf(::ShareAccountsViewModel)
}
