/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createNewClient

import com.mifos.core.entity.templates.clients.ClientsTemplate

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CreateNewClientUiState {

    data object ShowProgressbar : CreateNewClientUiState()

    data class ShowProgress(val message: String) : CreateNewClientUiState()

    data class ShowError(val message: Int) : CreateNewClientUiState()

    data class ShowStringError(val message: String) : CreateNewClientUiState()

    data class OnImageUploadSuccess(val message: Int) : CreateNewClientUiState()

    data class ShowClientTemplate(val clientsTemplate: ClientsTemplate) : CreateNewClientUiState()

    data class ShowClientCreatedSuccessfully(val message: Int) : CreateNewClientUiState()

    data class SetClientId(val id: Int) : CreateNewClientUiState()

    data class ShowWaitingForCheckerApproval(val message: Int) : CreateNewClientUiState()
}
