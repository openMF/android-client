/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDetails.ui

import okhttp3.ResponseBody

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class ClientDetailsUiState {

    data class ShowUploadImageSuccessfully(val response: ResponseBody?, val imagePath: String?) :
        ClientDetailsUiState()

    data object ShowClientImageDeletedSuccessfully : ClientDetailsUiState()

    data object Empty : ClientDetailsUiState()

    data class ShowError(val message: String) : ClientDetailsUiState()
}
