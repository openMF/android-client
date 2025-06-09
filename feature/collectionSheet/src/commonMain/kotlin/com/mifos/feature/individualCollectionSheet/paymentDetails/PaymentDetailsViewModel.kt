/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.individualCollectionSheet.paymentDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.request.ImageResult
import com.mifos.core.common.utils.Constants
import com.mifos.core.network.utils.ImageLoaderUtils
import com.mifos.feature.individualCollectionSheet.navigation.PaymentDetailsArgs
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class PaymentDetailsViewModel(
    private val imageLoaderUtils: ImageLoaderUtils,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val argsJson = savedStateHandle.get<String>(Constants.PAYMENT_DETAILS_ARGS).orEmpty()
    private val args = Json.decodeFromString<PaymentDetailsArgs>(argsJson)

    val clientId = args.clientId
    val position = args.position
    val individualCollectionSheetPayload = args.individualCollectionSheetPayload
    val paymentTypeOptionsName = args.paymentTypeOptionsName
    val loanAndClientName = args.loanAndClientName
    val paymentTypeOptions = args.paymentTypeOptions

    fun getClientImageUrl(clientId: Int): ImageResult? {
        var image: ImageResult? = null
        viewModelScope.launch {
            image = imageLoaderUtils.loadImage(clientId)
        }
        return image
    }
}
