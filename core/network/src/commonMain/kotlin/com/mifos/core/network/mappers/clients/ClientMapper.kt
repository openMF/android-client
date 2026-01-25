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
import com.mifos.core.network.model.GetClientClassificationOptions
import com.mifos.core.network.model.GetClientStatus
import com.mifos.core.network.model.GetClientTypeOptions
import com.mifos.core.network.model.GetClientsPageItemsResponse
import com.mifos.core.network.model.GetGenderOptions
import com.mifos.room.entities.client.ClientClassificationEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientGenderEntity
import com.mifos.room.entities.client.ClientStatusEntity
import com.mifos.room.entities.client.ClientTypeEntity

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
            groupId = entity.groupId,
            groupName = entity.groupName,
            active = entity.active!!,
            status = ClientStatusEntity(
                id = entity.status?.id!!.toInt(),
                code = entity.status.code,
                value = entity.status.value,
            ),
            externalId = entity.externalId,
            emailAddress = entity.emailAddress,
            legalForm = ClientStatusEntity(
                id = entity.legalForm?.id?.toInt() ?: -1,
                code = entity.legalForm?.code,
                value = entity.legalForm?.value,
            ),
            dateOfBirth = entity.dateOfBirth ?: emptyList(),
            gender = entity.gender?.let {
                ClientGenderEntity(
                    id = it.id?.toInt() ?: -1,
                    name = it.name,
                )
            },
            clientType = entity.clientType?.let {
                ClientTypeEntity(
                    id = it.id?.toInt() ?: -1,
                    name = it.name,
                )
            },
            clientClassification = entity.clientClassification?.let {
                ClientClassificationEntity(
                    id = it.id?.toInt() ?: -1,
                    name = it.name,
                )
            },
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
            groupId = domainModel.groupId,
            groupName = domainModel.groupName,
            active = domainModel.active,
            status = GetClientStatus(
                id = domainModel.status?.id?.toLong(),
                code = domainModel.status?.code,
                value = domainModel.status?.value,
            ),
            externalId = domainModel.externalId,
            emailAddress = domainModel.emailAddress,
            legalForm = GetClientStatus(
                id = domainModel.legalForm?.id?.toLong(),
                code = domainModel.legalForm?.code,
                value = domainModel.legalForm?.value,
            ),
            dateOfBirth = domainModel.dateOfBirth,
            gender = domainModel.gender?.let {
                GetGenderOptions(
                    id = it.id?.toLong(),
                    name = it.name,
                )
            },
            clientType = domainModel.clientType?.let {
                GetClientTypeOptions(
                    id = it.id?.toLong(),
                    name = it.name,
                )
            },
            clientClassification = domainModel.clientClassification?.let {
                GetClientClassificationOptions(
                    id = it.id?.toLong(),
                    name = it.name,
                )
            },
        )
    }
}
