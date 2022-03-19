package com.mifos.api.mappers.accounts.savings

import com.mifos.objects.accounts.savings.Status
import org.apache.fineract.client.models.GetCentersCenterIdStatus
import org.apache.fineract.client.models.GetClientsSavingsAccountsStatus
import org.apache.fineract.client.models.GetGroupsGroupIdAccountsSavingStatus
import org.mifos.core.data.AbstractMapper

object SavingsAccStatusMapper: AbstractMapper<GetClientsSavingsAccountsStatus, Status>() {
    override fun mapFromEntity(entity: GetClientsSavingsAccountsStatus): Status {
        return Status().apply {
            id = entity.id
            code = entity.code
            value = entity.value
            submittedAndPendingApproval = entity.submittedAndPendingApproval
            approved = entity.approved
            rejected = entity.rejected
            withdrawnByApplicant = entity.withdrawnByApplicant
            active = entity.active
            closed = entity.closed
        }
    }

    override fun mapToEntity(domainModel: Status): GetClientsSavingsAccountsStatus {
        return GetClientsSavingsAccountsStatus().apply {
            id = domainModel.id
            code = domainModel.code
            value = domainModel.value
            submittedAndPendingApproval = domainModel.submittedAndPendingApproval
            approved = domainModel.approved
            rejected = domainModel.rejected
            withdrawnByApplicant = domainModel.withdrawnByApplicant
            active = domainModel.active
            closed = domainModel.closed
        }
    }

    fun mapFromEntity(entity: GetGroupsGroupIdAccountsSavingStatus): Status {
        return Status().apply {
            id = entity.id
            code = entity.code
            value = entity.description
            submittedAndPendingApproval = entity.submittedAndPendingApproval
            approved = entity.approved
            rejected = entity.rejected
            withdrawnByApplicant = entity.withdrawnByApplicant
            active = entity.active
            closed = entity.closed
        }
    }

    fun mapFromEntity(entity: GetCentersCenterIdStatus): Status {
        return Status().apply {
            id = entity.id
            code = entity.code
            value = entity.description
            submittedAndPendingApproval = entity.submittedAndPendingApproval
            approved = entity.approved
            rejected = entity.rejected
            withdrawnByApplicant = entity.withdrawnByApplicant
            active = entity.active
            closed = entity.closed
        }
    }

    fun mapToGetCentersCenterIdStatusEntity(domainModel: Status): GetCentersCenterIdStatus {
        return GetCentersCenterIdStatus().apply {
            id = domainModel.id
            code = domainModel.code
            description = domainModel.value
            submittedAndPendingApproval = domainModel.submittedAndPendingApproval
            approved = domainModel.approved
            rejected = domainModel.rejected
            withdrawnByApplicant = domainModel.withdrawnByApplicant
            active = domainModel.active
            closed = domainModel.closed
        }
    }
}