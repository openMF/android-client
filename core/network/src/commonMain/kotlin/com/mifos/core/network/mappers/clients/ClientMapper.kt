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

import com.mifos.core.network.data.AbstractMapper
import com.mifos.core.network.model.GetClientStatus
import com.mifos.core.network.model.GetClientsPageItemsResponse
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientStatusEntity

object ClientMapper : AbstractMapper<GetClientsPageItemsResponse, ClientEntity>() {

    override fun mapFromEntity(entity: GetClientsPageItemsResponse): ClientEntity {
        return ClientEntity(
            id = entity.id!!.toInt(),
            accountNo = entity.accountNo,
            fullname = entity.fullName,
            firstname = entity.displayName!!.split(" ")[0],
            lastname =
            if (entity.displayName.split(" ").size >= 2) entity.displayName.split(" ")[1] else "",
            displayName = entity.displayName,
            officeId = entity.officeId!!.toInt(),
            officeName = entity.officeName,
            active = entity.active!!,
            status = ClientStatusEntity(
                id = entity.status?.id!!.toInt(),
                code = entity.status.code,
                value = entity.status.description,
            ),
        )
    }

    override fun mapToEntity(domainModel: ClientEntity): GetClientsPageItemsResponse {
        return GetClientsPageItemsResponse(
            id = domainModel.id.toLong(),
            accountNo = domainModel.accountNo,
            fullName = domainModel.fullname,
            displayName = domainModel.displayName,
            officeId = domainModel.officeId.toLong(),
            officeName = domainModel.officeName,
            active = domainModel.active,
            status = GetClientStatus(
                id = domainModel.status?.id?.toLong(),
                code = domainModel.status?.code,
                description = domainModel.status?.value,
            ),
        )
    }
}
