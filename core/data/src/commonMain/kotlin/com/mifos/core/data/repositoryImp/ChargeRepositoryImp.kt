/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.pagingSource.ClientChargesPagingSource
import com.mifos.core.data.repository.ChargeRepository
import com.mifos.core.model.objects.clients.ChargeCreationResponse
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChargeRepositoryImp(
    private val dataManagerCharge: DataManagerCharge,
) : ChargeRepository {

    override fun getListOfPagingCharges(
        resourceType: String,
        resourceId: Int,
    ): Flow<PagingData<ChargesEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                ClientChargesPagingSource(resourceType, resourceId, dataManagerCharge)
            },
        ).flow
    }

    override suspend fun getChargeTemplate(resourceType: String, resourceId: Int): ChargeTemplate {
        return dataManagerCharge.getChargeTemplate(resourceType, resourceId)
    }

    override suspend fun createCharges(
        resourceType: String,
        resourceId: Int,
        payload: ChargesPayload,
    ): ChargeCreationResponse {
        return dataManagerCharge.createCharges(
            resourceId = resourceId,
            resourceType = resourceType,
            payload = payload,
        )
    }

    override suspend fun deleteCharge(
        resourceId: Int,
        resourceType: String,
        chargeId: Int,
    ): Flow<DataState<Unit>> {
        return flow {
            emit(
                dataManagerCharge.deleteCharge(
                    resourceId = resourceId,
                    resourceType = resourceType,
                    chargeId = chargeId,
                ),
            )
        }.asDataStateFlow()
    }

    override suspend fun updateCharge(
        resourceId: Int,
        resourceType: String,
        chargeId: Int,
        payload: ChargesPayload,
    ): Flow<DataState<Unit>> {
        return flow {
            emit(
                dataManagerCharge.updateCharge(
                    resourceId = resourceId,
                    resourceType = resourceType,
                    chargeId = chargeId,
                    payload = payload,
                ),
            )
        }.asDataStateFlow()
    }

    override fun getListOfClientCharges(
        resourceType: String,
        resourceId: Int,
    ): Flow<DataState<Page<ChargesEntity>>> {
        return dataManagerCharge.getListOfClientCharges(resourceType, resourceId).asDataStateFlow()
    }

    override fun getListOfOtherAccountCharge(
        resourceType: String,
        resourceId: Int,
    ): Flow<DataState<List<ChargesEntity>>> {
        return dataManagerCharge.getListOfOtherAccountCharge(resourceType, resourceId)
            .asDataStateFlow()
    }

    override fun getCharge(
        resourceType: String,
        resourceId: Int,
        chargeId: Int,
    ): Flow<DataState<ChargesEntity>> {
        return dataManagerCharge.getCharge(
            resourceId = resourceId,
            resourceType = resourceType,
            chargeId = chargeId,
        ).asDataStateFlow()
    }
}
