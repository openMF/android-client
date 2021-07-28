package com.mifos.api.mappers.accounts.loan

import com.mifos.objects.accounts.loan.LoanAccount
import org.apache.fineract.client.models.GetClientsLoanAccounts
import org.mifos.core.data.AbstractMapper

object LoanAccMapper: AbstractMapper<GetClientsLoanAccounts, LoanAccount>() {
    override fun mapFromEntity(entity: GetClientsLoanAccounts): LoanAccount {
        return LoanAccount().apply {
            id = entity.id
            accountNo = entity.accountNo
            externalId = entity.externalId.toString()
            productId = entity.productId
            productName = entity.productName
            status = LoanAccStatusMapper.mapFromEntity(entity.status!!)
            loanType = LoanAccTypeMapper.mapFromEntity(entity.loanType!!)
            loanCycle = entity.loanCycle
        }
    }

    override fun mapToEntity(domainModel: LoanAccount): GetClientsLoanAccounts {
        return GetClientsLoanAccounts().apply {
            id = domainModel.id
            accountNo = domainModel.accountNo
            externalId = domainModel.externalId.toInt()
            productId = domainModel.productId
            productName = domainModel.productName
            status = LoanAccStatusMapper.mapToEntity(domainModel.status!!)
            loanType = LoanAccTypeMapper.mapToEntity(domainModel.loanType!!)
            loanCycle = domainModel.loanCycle
        }
    }
}