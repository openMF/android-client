package com.mifos.api.mappers.client.template

import com.mifos.objects.templates.clients.StaffOptions
import org.apache.fineract.client.models.GetClientsStaffOptions
import org.mifos.core.data.AbstractMapper

object StaffOptionMapper: AbstractMapper<GetClientsStaffOptions, StaffOptions>() {
    override fun mapFromEntity(entity: GetClientsStaffOptions): StaffOptions {
        return StaffOptions().apply {
            id = entity.id!!
            firstname = entity.firstname
            lastname = entity.lastname
            displayName = entity.displayName
            officeId = entity.officeId!!
            officeName = entity.officeName
            setIsLoanOfficer(entity.isLoanOfficer!!)
            setIsActive(entity.isActive!!)
        }
    }

    override fun mapToEntity(domainModel: StaffOptions): GetClientsStaffOptions {
        return GetClientsStaffOptions().apply {
            id = domainModel.id
            firstname = domainModel.firstname
            lastname = domainModel.lastname
            displayName = domainModel.displayName
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            isLoanOfficer = domainModel.isLoanOfficer
            isActive = domainModel.isActive
        }
    }
}