/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.datasource

import com.mifos.core.common.utils.Constants
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
        val sanitized = query.replace("%", "\\%").replace("_", "\\_")
        val fuzzyQuery = "%$sanitized%"
        return clientDaoHelper.searchAddressesByQuery(fuzzyQuery)
            .map { addresses ->
                addresses.map { address ->
                    GenericSearchRecord(
                        id = address.addressId,
                        name = address.addressType ?: "",
                        description = buildAddressDescription(address),
                        type = RecordType.ADDRESS.name,
                        metadata = mapOf(
                            Constants.CLIENT_ID to (address.clientId?.toString() ?: ""),
                            Constants.CITY to (address.city ?: ""),
                            Constants.STATE to (address.stateName ?: ""),
                            Constants.COUNTRY to (address.countryName ?: ""),
                            Constants.POSTAL_CODE to (address.postalCode ?: ""),
                            Constants.ADDRESS_LINE_1 to (address.addressLine1 ?: ""),
                            Constants.ADDRESS_LINE_2 to (address.addressLine2 ?: ""),
                            Constants.ADDRESS_LINE_3 to (address.addressLine3 ?: ""),
                        ),
                    )
                }
            }
    }

    private fun searchIdentifiersLocal(query: String): Flow<List<GenericSearchRecord>> {
        val sanitized = query.replace("%", "\\%").replace("_", "\\_")
        val fuzzyQuery = "%$sanitized%"
        return clientDaoHelper.searchIdentifiersByQuery(fuzzyQuery)
            .map { identifiers ->
                identifiers.map { identifier ->
                    GenericSearchRecord(
                        id = identifier.id ?: 0,
                        name = identifier.documentTypeName ?: "",
                        description = identifier.description ?: "",
                        type = RecordType.IDENTIFIER.name,
                        metadata = mapOf(
                            Constants.CLIENT_ID to (identifier.clientId?.toString() ?: ""),
                            Constants.STATUS to (identifier.status ?: ""),
                            Constants.DOCUMENT_KEY to (identifier.documentKey ?: ""),
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
