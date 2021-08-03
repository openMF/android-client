package com.mifos.api.mappers.note

import com.mifos.objects.noncore.Note
import org.apache.fineract.client.models.GetResourceTypeResourceIdNotesResponse
import org.mifos.core.data.AbstractMapper
import java.util.*

object NoteMapper: AbstractMapper<GetResourceTypeResourceIdNotesResponse, Note>() {
    override fun mapFromEntity(entity: GetResourceTypeResourceIdNotesResponse): Note {
        return Note().apply {
            id = entity.id!!
            clientId = entity.clientId!!
            noteContent = entity.note
            createdById = entity.createdById!!
            createdByUsername = entity.createdByUsername
            createdOn = entity.createdOn!!.time
            updatedById = entity.updatedById!!
            updatedByUsername = entity.updatedByUsername
            updatedOn = entity.updatedOn!!.time
        }
    }

    override fun mapToEntity(domainModel: Note): GetResourceTypeResourceIdNotesResponse {
        return GetResourceTypeResourceIdNotesResponse().apply {
            id = domainModel.id
            clientId = domainModel.clientId
            note = domainModel.noteContent
            createdById = domainModel.createdById
            createdByUsername = domainModel.createdByUsername
            createdOn = Date(domainModel.createdOn)
            updatedById = domainModel.updatedById
            updatedByUsername = domainModel.updatedByUsername
            updatedOn = Date(domainModel.updatedOn)
        }
    }
}