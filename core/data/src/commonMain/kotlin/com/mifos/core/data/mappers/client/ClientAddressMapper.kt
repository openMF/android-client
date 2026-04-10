/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.client

import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.network.data.AbstractMapper
import com.mifos.room.entities.client.ClientAddressEntity as RoomAddressEntity

object ClientAddressMapper : AbstractMapper<ClientAddressEntity, RoomAddressEntity>() {

    override fun mapFromEntity(entity: ClientAddressEntity): RoomAddressEntity {
        return RoomAddressEntity(
            addressId = if (entity.addressId != -1) entity.addressId else 0,
            clientId = entity.clientID,
            addressType = entity.addressType,
            addressLine1 = entity.addressLine1,
            addressLine2 = entity.addressLine2,
            addressLine3 = entity.addressLine3,
            city = entity.city,
            stateProvinceId = entity.stateProvinceId,
            countryName = entity.countryName,
            stateName = entity.stateName,
            countryId = entity.countryId,
            postalCode = entity.postalCode,
            isActive = entity.isActive,
            addressTypeId = entity.addressTypeId,
        )
    }

    override fun mapToEntity(roomEntity: RoomAddressEntity): ClientAddressEntity {
        return ClientAddressEntity(
            clientID = roomEntity.clientId,
            addressId = roomEntity.addressId,
            addressType = roomEntity.addressType,
            addressLine1 = roomEntity.addressLine1,
            addressLine2 = roomEntity.addressLine2,
            addressLine3 = roomEntity.addressLine3,
            city = roomEntity.city,
            stateProvinceId = roomEntity.stateProvinceId,
            countryName = roomEntity.countryName,
            stateName = roomEntity.stateName,
            countryId = roomEntity.countryId,
            postalCode = roomEntity.postalCode,
            isActive = roomEntity.isActive,
            addressTypeId = roomEntity.addressTypeId,
        )
    }
}
