package com.mifos.feature.client.di

import org.koin.dsl.module
import com.mifos.feature.client.clientChargeDialog.ChargeDialogViewModel
import com.mifos.feature.client.clientCharges.ClientChargesViewModel
import com.mifos.feature.client.clientDetails.ui.ClientDetailsViewModel
import com.mifos.feature.client.clientIdentifiers.ClientIdentifiersViewModel
import com.mifos.feature.client.clientIdentifiersDialog.ClientIdentifiersDialogViewModel
import com.mifos.feature.client.clientList.presentation.ClientListViewModel
import com.mifos.feature.client.clientPinpoint.PinPointClientViewModel
import com.mifos.feature.client.clientSignature.SignatureViewModel
import com.mifos.feature.client.clientSurveyList.SurveyListViewModel
import com.mifos.feature.client.clientSurveySubmit.SurveySubmitViewModel
import com.mifos.feature.client.createNewClient.CreateNewClientViewModel
import com.mifos.feature.client.syncClientDialog.SyncClientsDialogViewModel
import org.koin.core.module.dsl.viewModelOf

val ClientModule = module {
    viewModelOf(::ChargeDialogViewModel)
    viewModelOf(::ClientChargesViewModel)
    viewModelOf(::ClientDetailsViewModel)
    viewModelOf(::ClientIdentifiersViewModel)
    viewModelOf(::ClientIdentifiersDialogViewModel)
    viewModelOf(::ClientListViewModel)
    viewModelOf(::PinPointClientViewModel)
    viewModelOf(::SignatureViewModel)
    viewModelOf(::SurveyListViewModel)
    viewModelOf(::SurveySubmitViewModel)
    viewModelOf(::CreateNewClientViewModel)
    viewModelOf(::SyncClientsDialogViewModel)
}