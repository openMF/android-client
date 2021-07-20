package com.mifos.api.mappers.accounts.loan

import com.mifos.objects.accounts.loan.LoanType
import org.apache.fineract.client.models.GetClientsLoanAccountsType
import org.mifos.core.data.AbstractMapper

object LoanAccTypeMapper: AbstractMapper<GetClientsLoanAccountsType, LoanType>() {
    override fun mapFromEntity(entity: GetClientsLoanAccountsType): LoanType {
        return LoanType().apply {
            id = entity.id
            code = entity.code
            value = entity.description
        }
    }

    override fun mapToEntity(domainModel: LoanType): GetClientsLoanAccountsType {
        return GetClientsLoanAccountsType().apply {
            id = domainModel.id
            code = domainModel.code
            description = domainModel.value
        }
    }
}