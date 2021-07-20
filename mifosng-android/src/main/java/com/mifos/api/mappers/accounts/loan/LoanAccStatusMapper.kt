package com.mifos.api.mappers.accounts.loan

import com.mifos.objects.accounts.loan.Status
import org.apache.fineract.client.models.GetClientsLoanAccountsStatus
import org.mifos.core.data.AbstractMapper

object LoanAccStatusMapper: AbstractMapper<GetClientsLoanAccountsStatus, Status>() {
    override fun mapFromEntity(entity: GetClientsLoanAccountsStatus): Status {
        return Status().apply {
            id = entity.id
            code = entity.code
            value = entity.description
            pendingApproval = entity.pendingApproval
            waitingForDisbursal = entity.waitingForDisbursal
            active = entity.active
            closedObligationsMet = entity.closedObligationsMet
            closedWrittenOff = entity.closedWrittenOff
            closedRescheduled = entity.closedRescheduled
            closed = entity.closed
            overpaid = entity.overpaid
        }
    }

    override fun mapToEntity(domainModel: Status): GetClientsLoanAccountsStatus {
        return GetClientsLoanAccountsStatus().apply {
            id = domainModel.id
            code = domainModel.code
            description = domainModel.value
            pendingApproval = domainModel.pendingApproval
            waitingForDisbursal = domainModel.waitingForDisbursal
            active = domainModel.active
            closedObligationsMet = domainModel.closedObligationsMet
            closedWrittenOff = domainModel.closedWrittenOff
            closedRescheduled = domainModel.closedRescheduled
            closed = domainModel.closed
            overpaid = domainModel.overpaid
        }
    }
}