/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers

import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.room.entities.client.ClientIdentifierEntity
import com.mifos.room.entities.client.ClientAddressEntity as RoomAddressEntity

fun ClientAddressEntity.toRoomEntity(fallbackClientId: Int): RoomAddressEntity {
    return RoomAddressEntity(
        clientId = this.clientID ?: fallbackClientId,
        addressId = this.addressId ?: 0,
        addressType = this.addressType ?: "",
        addressLine1 = this.addressLine1 ?: "",
        addressLine2 = this.addressLine2 ?: "",
        addressLine3 = this.addressLine3 ?: "",
        city = this.city ?: "",
        stateProvinceId = this.stateProvinceId ?: -1,
        countryName = this.countryName ?: "",
        stateName = this.stateName ?: "",
        countryId = this.countryId ?: -1,
        postalCode = this.postalCode ?: "",
        isActive = this.isActive ?: false,
    )
}

fun Identifier.toRoomEntity(fallbackClientId: Int): ClientIdentifierEntity {
    return ClientIdentifierEntity(
        id = this.id ?: -1,
        clientId = this.clientId ?: fallbackClientId,
        documentKey = this.documentKey ?: "",
        documentTypeName = this.documentType?.name ?: "",
        documentTypeId = this.documentType?.id ?: -1,
        description = this.description ?: "",
        status = this.status ?: "",
    )
}
