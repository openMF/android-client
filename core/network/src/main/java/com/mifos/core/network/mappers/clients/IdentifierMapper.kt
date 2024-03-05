package com.mifos.core.network.mappers.clients

import com.mifos.core.objects.noncore.DocumentType
import com.mifos.core.objects.noncore.Identifier
import org.apache.fineract.client.models.GetClientsClientIdIdentifiersResponse
import org.apache.fineract.client.models.GetClientsDocumentType
import org.mifos.core.data.AbstractMapper

/**
 * Created by Aditya Gupta on 30/08/23.
 */
object IdentifierMapper : AbstractMapper<GetClientsClientIdIdentifiersResponse, Identifier>() {
    override fun mapFromEntity(entity: GetClientsClientIdIdentifiersResponse): Identifier {
        return Identifier().apply {
            id = entity.id
            clientId = entity.clientId
            documentKey = entity.documentKey
            description = entity.description
            documentType = entity.documentType?.let {
                DocumentType().apply {
                    id = it.id
                    name = it.name
                }
            }
        }
    }

    override fun mapToEntity(domainModel: Identifier): GetClientsClientIdIdentifiersResponse {
        return GetClientsClientIdIdentifiersResponse().apply {
            id = domainModel.id
            clientId = domainModel.clientId
            documentKey = domainModel.documentKey
            description = domainModel.description
            documentType = GetClientsDocumentType().apply {
                id = this.id
                name = this.name
            }
        }
    }
}