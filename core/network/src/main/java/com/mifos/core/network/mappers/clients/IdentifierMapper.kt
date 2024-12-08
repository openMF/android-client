package com.mifos.core.network.mappers.clients

import com.mifos.core.objects.noncore.DocumentType
import com.mifos.core.objects.noncore.Identifier
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetClientsClientIdIdentifiersResponse
import org.openapitools.client.models.GetClientsDocumentType

/**
 * Created by Aditya Gupta on 30/08/23.
 */
object IdentifierMapper : AbstractMapper<GetClientsClientIdIdentifiersResponse, Identifier>() {
    override fun mapFromEntity(entity: GetClientsClientIdIdentifiersResponse): Identifier {
        return Identifier().apply {
            id = entity.id?.toInt()
            clientId = entity.clientId?.toInt()
            documentKey = entity.documentKey
            description = entity.description
            documentType = entity.documentType?.let {
                DocumentType().apply {
                    id = it.id?.toInt()
                    name = it.name
                }
            }
        }
    }

    override fun mapToEntity(domainModel: Identifier): GetClientsClientIdIdentifiersResponse {
        return GetClientsClientIdIdentifiersResponse(
            id = domainModel.id?.toLong(),
            clientId = domainModel.clientId?.toLong(),
            documentKey = domainModel.documentKey,
            description = domainModel.description,
            documentType = domainModel.documentType?.let {
                GetClientsDocumentType(
                    id = it.id?.toLong(),
                    name = it.name
                )
            }
        )
    }
}