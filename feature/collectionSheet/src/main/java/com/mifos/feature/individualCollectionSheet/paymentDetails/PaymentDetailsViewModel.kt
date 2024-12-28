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
import coil.request.ImageResult
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.`object`.account.loan.PaymentTypeOptions
import com.mifos.core.`object`.collectionsheets.LoanAndClientName
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.network.utils.ImageLoaderUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentDetailsViewModel @Inject constructor(
    private val imageLoaderUtils: ImageLoaderUtils,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val payloadArg = savedStateHandle.getStateFlow(key = Constants.PAYLOAD, initialValue = "")
    private val paymentListArg = savedStateHandle.getStateFlow(key = Constants.PAYMENT_LIST, initialValue = "")
    private val loanAndClientNameArg = savedStateHandle.getStateFlow(key = Constants.LOAN_AND_CLIENT, initialValue = "")
    private val paymentOptionsArg = savedStateHandle.getStateFlow(key = Constants.PAYMENT_OPTIONS, initialValue = "")

    private val decodedPosition = savedStateHandle.get<String>(Constants.ADAPTER_POSITION)?.toIntOrNull() ?: 0
    private val decodedClientId = savedStateHandle.get<String>(Constants.CLIENT_ID)?.toIntOrNull() ?: 0

    val clientId = decodedClientId
    val position = decodedPosition
    val individualCollectionSheetPayload: IndividualCollectionSheetPayload =
        Gson().fromJson(payloadArg.value, IndividualCollectionSheetPayload::class.java)
    val paymentTypeOptionsName = Gson().fromJson(paymentListArg.value, Array<String>::class.java).toList()
    val loanAndClientName: LoanAndClientName = Gson().fromJson(loanAndClientNameArg.value, LoanAndClientName::class.java)
    val paymentTypeOptions = Gson().fromJson(paymentOptionsArg.value, Array<PaymentTypeOptions>::class.java).toList()

    fun getClientImageUrl(clientId: Int): ImageResult? {
        var image: ImageResult? = null
        viewModelScope.launch(Dispatchers.IO) {
            image = imageLoaderUtils.loadImage(clientId)
        }
        return image
    }
}
