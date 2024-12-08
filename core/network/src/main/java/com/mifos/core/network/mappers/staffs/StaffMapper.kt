package com.mifos.core.network.mappers.staffs

import com.mifos.core.objects.organisation.Staff
import org.mifos.core.data.AbstractMapper
import org.openapitools.client.models.RetrieveOneResponse

object StaffMapper : AbstractMapper<RetrieveOneResponse, Staff>() {
    override fun mapFromEntity(entity: RetrieveOneResponse): Staff {
        return Staff().apply {
            id = entity.id!!.toInt()
            firstname = entity.firstname
            lastname = entity.lastname
            displayName = entity.displayName
            officeId = entity.officeId!!.toInt()
            officeName = entity.officeName
            isLoanOfficer = entity.isLoanOfficer
            isActive = entity.isActive
        }
    }

    override fun mapToEntity(domainModel: Staff): RetrieveOneResponse {
        return RetrieveOneResponse(
            id = domainModel.id?.toLong(),
            firstname = domainModel.firstname,
            lastname = domainModel.lastname,
            displayName = domainModel.displayName,
            officeId = domainModel.officeId?.toLong(),
            officeName = domainModel.officeName,
            isLoanOfficer = domainModel.isLoanOfficer,
            isActive = domainModel.isActive
        )
    }
}