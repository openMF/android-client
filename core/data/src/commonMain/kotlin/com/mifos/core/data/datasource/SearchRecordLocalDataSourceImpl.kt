/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.datasource

import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import com.mifos.core.model.objects.searchrecord.RecordType
import com.mifos.room.entities.client.ClientAddressEntity
import com.mifos.room.helper.ClientDaoHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SearchRecordLocalDataSourceImpl(
    private val clientDaoHelper: ClientDaoHelper,
) : SearchRecordLocalDataSource {

    override fun searchRecords(
        recordType: RecordType,
        query: String,
    ): Flow<List<GenericSearchRecord>> {
        return when (recordType) {
            RecordType.ADDRESS -> searchAddressesLocal(query)
            RecordType.IDENTIFIER -> searchIdentifiersLocal(query)
            else -> flowOf(emptyList())
        }
    }

    private fun searchAddressesLocal(query: String): Flow<List<GenericSearchRecord>> {
        val fuzzyQuery = "%$query%"
        return clientDaoHelper.searchAddressesByQuery(fuzzyQuery)
            .map { addresses ->
                addresses.map { address ->
                    GenericSearchRecord(
                        id = address.addressId,
                        name = address.addressType,
                        description = buildAddressDescription(address),
                        type = RecordType.ADDRESS.displayName,
                        metadata = mapOf(
                            "clientId" to address.clientID.toString(),
                            "city" to address.city,
                            "state" to address.stateName,
                            "country" to address.countryName,
                            "postalCode" to address.postalCode,
                            "addressLine1" to address.addressLine1,
                        ),
                    )
                }
            }
    }

    private fun searchIdentifiersLocal(query: String): Flow<List<GenericSearchRecord>> {
        val fuzzyQuery = "%$query%"
        return clientDaoHelper.searchIdentifiersByQuery(fuzzyQuery)
            .map { identifiers ->
                identifiers.map { identifier ->
                    GenericSearchRecord(
                        id = identifier.id,
                        name = identifier.documentTypeName,
                        description = identifier.description,
                        type = RecordType.IDENTIFIER.displayName,
                        metadata = mapOf(
                            "clientId" to identifier.clientId.toString(),
                            "status" to identifier.status,
                            "documentKey" to identifier.documentKey,
                        ),
                    )
                }
            }
    }

    private fun buildAddressDescription(address: ClientAddressEntity): String {
        return listOfNotNull(
            address.addressLine1,
            address.city,
            address.stateName,
            address.countryName,
        ).filter { it.isNotBlank() }.joinToString(", ")
    }
}
