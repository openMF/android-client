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

import com.mifos.feature.client.clientCharges.ClientChargesViewModel
import com.mifos.feature.client.clientDetails.ClientDetailsViewModel
import com.mifos.feature.client.clientIdentifiers.ClientIdentifiersViewModel
import com.mifos.feature.client.clientPinpoint.PinPointClientViewModel
import com.mifos.feature.client.clientSignature.SignatureViewModel
import com.mifos.feature.client.clientSurveyList.SurveyListViewModel
import com.mifos.feature.client.clientSurveySubmit.SurveySubmitViewModel
import com.mifos.feature.client.clientsList.ClientListViewModel
import com.mifos.feature.client.createNewClient.CreateNewClientViewModel
import com.mifos.feature.client.syncClientDialog.SyncClientsDialogViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ClientModule = module {
    viewModelOf(::ClientChargesViewModel)
    viewModelOf(::ClientDetailsViewModel)
    viewModelOf(::ClientIdentifiersViewModel)
    viewModelOf(::ClientListViewModel)
    viewModelOf(::PinPointClientViewModel)
    viewModelOf(::SignatureViewModel)
    viewModelOf(::SurveyListViewModel)
    viewModelOf(::SurveySubmitViewModel)
    viewModelOf(::CreateNewClientViewModel)
    viewModelOf(::SyncClientsDialogViewModel)
}
