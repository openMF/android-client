package com.mifos.core.network.mappers.clients

import com.mifos.core.objects.noncore.DocumentType
import com.mifos.core.objects.noncore.IdentifierTemplate
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.GetClientsAllowedDocumentTypes
import org.openapitools.client.models.GetClientsClientIdIdentifiersTemplateResponse

/**
 * Created by Aditya Gupta on 30/08/23.
 */

object GetIdentifiersTemplateMapper :
    AbstractMapper<GetClientsClientIdIdentifiersTemplateResponse, IdentifierTemplate>() {

    override fun mapFromEntity(entity: GetClientsClientIdIdentifiersTemplateResponse): IdentifierTemplate {
        return IdentifierTemplate().apply {
            allowedDocumentTypes = entity.allowedDocumentTypes?.map {
                DocumentType().apply {
                    id = it.id?.toInt()
                    name = it.name
                    position = it.position
                }
            }
        }
    }

    override fun mapToEntity(domainModel: IdentifierTemplate): GetClientsClientIdIdentifiersTemplateResponse {
        return GetClientsClientIdIdentifiersTemplateResponse(
            allowedDocumentTypes = domainModel.allowedDocumentTypes?.map {
                GetClientsAllowedDocumentTypes(
                    id = it.id?.toLong(),
                    name = it.name,
                    position = it.position
                )
            }?.toSet()
        )
    }
}