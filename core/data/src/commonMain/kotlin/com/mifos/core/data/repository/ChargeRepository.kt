/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import androidx.paging.PagingData
import com.mifos.core.common.utils.DataState
import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ChargeRepository {

    fun getListOfPagingCharges(
        resourceType: String,
        resourceId: Int,
    ): Flow<PagingData<ChargesEntity>>

    suspend fun getChargeTemplate(resourceType: String, resourceId: Int): ChargeTemplate

    suspend fun createCharges(
        resourceType: String,
        resourceId: Int,
        payload: ChargesPayload,
    ): ChargeCreationResponse

    suspend fun deleteCharge(
        resourceId: Int,
        resourceType: String,
        chargeId: Int,
    ): Flow<DataState<Unit>>

    suspend fun updateCharge(
        resourceId: Int,
        resourceType: String,
        chargeId: Int,
        payload: ChargesPayload,
    ): Flow<DataState<Unit>>

    fun getListOfClientCharges(
        resourceType: String,
        resourceId: Int,
    ): Flow<DataState<Page<ChargesEntity>>>

    fun getListOfOtherAccountCharge(
        resourceType: String,
        resourceId: Int,
    ): Flow<DataState<List<ChargesEntity>>>

    fun getCharge(
        resourceType: String,
        resourceId: Int,
        chargeId: Int,
    ): Flow<DataState<ChargesEntity>>
}
