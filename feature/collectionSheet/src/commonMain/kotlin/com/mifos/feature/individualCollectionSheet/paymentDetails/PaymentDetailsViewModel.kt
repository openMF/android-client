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
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.feature.individualCollectionSheet.navigation.PaymentDetailsArgs
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class PaymentDetailsViewModel(
    private val prefManager: UserPreferencesRepository,
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

    suspend fun getClientImageUrl(): String {
        val serverConfig = prefManager.serverConfig.first()
        return (
            serverConfig.getInstanceUrl() +
                "clients/" +
                clientId +
                "/images?maxHeight=120&maxWidth=120"
            )
    }
}
