package com.mifos.api.mappers.client.identifiers

import com.mifos.objects.noncore.IdentifierTemplate
import org.apache.fineract.client.models.GetClientsClientIdIdentifiersTemplateResponse
import org.mifos.core.data.AbstractMapper

object GetIdentifiersTemplateMapper :
    AbstractMapper<GetClientsClientIdIdentifiersTemplateResponse, IdentifierTemplate>() {
    override fun mapFromEntity(entity: GetClientsClientIdIdentifiersTemplateResponse): IdentifierTemplate {
        return IdentifierTemplate().apply {
            allowedDocumentTypes = entity.allowedDocumentTypes?.let {
                AllowedDocumentTypeMapper.mapFromEntityList(
                    it
                )
            }
        }
    }

    override fun mapToEntity(domainModel: IdentifierTemplate): GetClientsClientIdIdentifiersTemplateResponse {
        return GetClientsClientIdIdentifiersTemplateResponse().apply {
            allowedDocumentTypes = AllowedDocumentTypeMapper.mapToEntityList(domainModel.allowedDocumentTypes)
        }
    }
}