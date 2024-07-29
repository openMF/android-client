/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import com.mifos.core.objects.templates.clients.ChargeTemplate

/**
 * Created by Aditya Gupta on 13/08/23.
 */
interface ChargeDialogRepository {

    suspend fun getAllChargesV2(clientId: Int): ChargeTemplate

    suspend fun createCharges(
        clientId: Int,
        payload: ChargesPayload,
    ): ChargeCreationResponse
}
