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

import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Status
import org.apache.fineract.client.models.GetClientStatus
import org.apache.fineract.client.models.GetClientsPageItemsResponse
import org.mifos.core.data.AbstractMapper

object ClientMapper : AbstractMapper<GetClientsPageItemsResponse, Client>() {

    override fun mapFromEntity(entity: GetClientsPageItemsResponse): Client {
        return Client().apply {
            id = entity.id!!
            accountNo = entity.accountNo
            fullname = entity.fullname
            firstname = entity.displayName!!.split(" ")[0]
            lastname =
                if (entity.displayName!!.split(" ").size >= 2) entity.displayName!!.split(" ")[1] else ""
            displayName = entity.displayName
            officeId = entity.officeId!!
            officeName = entity.officeName
            active = entity.active!!
            status = Status().apply {
                id = entity.status?.id!!
                code = entity.status?.code
                value = entity.status?.description
            }
        }
    }

    override fun mapToEntity(domainModel: Client): GetClientsPageItemsResponse {
        return GetClientsPageItemsResponse().apply {
            id = domainModel.id
            accountNo = domainModel.accountNo
            fullname = domainModel.fullname
            displayName = domainModel.displayName
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            active = domainModel.active
            status = GetClientStatus().apply {
                id = domainModel.status?.id
                code = domainModel.status?.code
                description = domainModel.status?.value
            }
        }
    }
}
