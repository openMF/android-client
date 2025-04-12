/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.mappers.clients

import com.mifos.core.common.utils.Page
import com.mifos.core.network.data.AbstractMapper
import com.mifos.core.network.model.GetClientsResponse
import com.mifos.room.entities.client.ClientEntity

object GetClientResponseMapper : AbstractMapper<GetClientsResponse, Page<ClientEntity>>() {

    override fun mapFromEntity(entity: GetClientsResponse): Page<ClientEntity> {
        return Page<ClientEntity>().apply {
            totalFilteredRecords = entity.totalFilteredRecords!!
            pageItems = ClientMapper.mapFromEntityList(entity.pageItems!!.toList())
        }
    }

    override fun mapToEntity(domainModel: Page<ClientEntity>): GetClientsResponse {
        return GetClientsResponse(
            totalFilteredRecords = domainModel.totalFilteredRecords,
            pageItems = ClientMapper.mapToEntityList(domainModel.pageItems).toSet(),
        )
    }
}
