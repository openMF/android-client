/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersDialog

import com.mifos.core.objects.noncore.IdentifierTemplate

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class ClientIdentifierDialogUiState {

    data object Loading : ClientIdentifierDialogUiState()

    data class Error(val message: Int) : ClientIdentifierDialogUiState()

    data class ClientIdentifierTemplate(val identifierTemplate: IdentifierTemplate) :
        ClientIdentifierDialogUiState()

    data object IdentifierCreatedSuccessfully : ClientIdentifierDialogUiState()
}
