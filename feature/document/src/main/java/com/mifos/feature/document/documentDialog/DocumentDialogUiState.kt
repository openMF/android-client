/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.documentDialog

import com.mifos.core.network.GenericResponse

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class DocumentDialogUiState {

    object Initial : DocumentDialogUiState()
    object ShowProgressbar : DocumentDialogUiState()

    data class ShowUploadError(val message: String) : DocumentDialogUiState()

    data class ShowDocumentedCreatedSuccessfully(val genericResponse: GenericResponse) :
        DocumentDialogUiState()

    data class ShowError(val message: String) : DocumentDialogUiState()

    data class ShowDocumentUpdatedSuccessfully(val genericResponse: GenericResponse) :
        DocumentDialogUiState()
}
