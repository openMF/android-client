package com.mifos.core.network.mappers.clients

import com.mifos.core.objects.noncore.DocumentType
import com.mifos.core.objects.noncore.IdentifierTemplate
import org.apache.fineract.client.models.GetClientsAllowedDocumentTypes
import org.apache.fineract.client.models.GetClientsClientIdIdentifiersTemplateResponse
import org.mifos.core.data.AbstractMapper

/**
 * Created by Aditya Gupta on 30/08/23.
 */

object GetIdentifiersTemplateMapper :
    AbstractMapper<GetClientsClientIdIdentifiersTemplateResponse, IdentifierTemplate>() {

    override fun mapFromEntity(entity: GetClientsClientIdIdentifiersTemplateResponse): IdentifierTemplate {
        return IdentifierTemplate().apply {
            allowedDocumentTypes = entity.allowedDocumentTypes?.map {
                DocumentType().apply {
                    id = it.id
                    name = it.name
                    position = it.position
                }
            }
        }
    }

    override fun mapToEntity(domainModel: IdentifierTemplate): GetClientsClientIdIdentifiersTemplateResponse {
        return GetClientsClientIdIdentifiersTemplateResponse().apply {
            allowedDocumentTypes = domainModel.allowedDocumentTypes?.map {
                GetClientsAllowedDocumentTypes().apply {
                    id = it.id
                    name = it.name
                    position = it.position
                }
            }
        }
    }
}