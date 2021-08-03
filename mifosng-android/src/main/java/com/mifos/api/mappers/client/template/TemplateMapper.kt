package com.mifos.api.mappers.client.template

import com.mifos.api.utils.toDate
import com.mifos.api.utils.toIntArray
import com.mifos.objects.templates.clients.ClientsTemplate
import org.apache.fineract.client.models.GetClientsTemplateResponse
import org.mifos.core.data.AbstractMapper

object TemplateMapper: AbstractMapper<GetClientsTemplateResponse, ClientsTemplate>() {
    override fun mapFromEntity(entity: GetClientsTemplateResponse): ClientsTemplate {
        return ClientsTemplate().apply {
            activationDate = entity.activationDate!!.toIntArray()
            officeId = entity.officeId!!
            officeOptions = entity.officeOptions?.let { OfficeOptionMapper.mapFromEntityList(it) } ?:  listOf()
            staffOptions = entity.staffOptions?.let { StaffOptionMapper.mapFromEntityList(it) } ?:  listOf()
            savingProductOptions = entity.savingProductOptions?.let {
                SavingProductOptionMapper.mapFromEntityList(
                    it
                )
            } ?:  listOf()
            genderOptions = listOf()
            clientTypeOptions = listOf()
            clientClassificationOptions = listOf()
            clientLegalFormOptions = listOf()
            dataTables = entity.datatables?.let { ClientDataTableMapper.mapFromEntityList(it) } ?:  listOf()
        }
    }

    override fun mapToEntity(domainModel: ClientsTemplate): GetClientsTemplateResponse {
        return GetClientsTemplateResponse().apply {
            activationDate = domainModel.activationDate!!.toDate()
            officeId = domainModel.officeId!!
            officeOptions = OfficeOptionMapper.mapToEntityList(domainModel.officeOptions!!)
        }
    }
}