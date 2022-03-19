package com.mifos.api.mappers.client.identifiers

import com.mifos.objects.noncore.DocumentType
import org.apache.fineract.client.models.GetClientsAllowedDocumentTypes
import org.apache.fineract.client.models.GetClientsDocumentType
import org.mifos.core.data.AbstractMapper

object AllowedDocumentTypeMapper: AbstractMapper<GetClientsAllowedDocumentTypes, DocumentType>() {
    override fun mapFromEntity(entity: GetClientsAllowedDocumentTypes): DocumentType {
        return DocumentType().apply {
            id = entity.id
            name = entity.name
            position = entity.position
        }
    }

    override fun mapToEntity(domainModel: DocumentType): GetClientsAllowedDocumentTypes {
        return GetClientsAllowedDocumentTypes().apply {
            id = domainModel.id
            name = domainModel.name
            position = domainModel.position
        }
    }
}