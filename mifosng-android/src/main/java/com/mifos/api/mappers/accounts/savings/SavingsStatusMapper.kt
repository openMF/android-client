package com.mifos.api.mappers.accounts.savings

import com.mifos.objects.accounts.savings.Status
import org.apache.fineract.client.models.GetSavingsStatus
import org.mifos.core.data.AbstractMapper

object SavingsStatusMapper: AbstractMapper<GetSavingsStatus, Status>() {
    override fun mapFromEntity(entity: GetSavingsStatus): Status {
        return Status().apply {
            id = entity.id
            code = entity.code
            value = entity.value
            submittedAndPendingApproval = entity.submittedAndPendingApproval
            approved =  entity.approved
            rejected = entity.rejected
            withdrawnByApplicant = entity.withdrawnByApplicant
            active = entity.active
            closed =  entity.closed
        }
    }

    override fun mapToEntity(domainModel: Status): GetSavingsStatus {
        TODO("Not yet implemented")
    }
}