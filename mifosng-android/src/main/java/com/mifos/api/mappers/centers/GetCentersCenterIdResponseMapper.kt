package com.mifos.api.mappers.centers

import com.mifos.api.mappers.accounts.savings.SavingsAccMapper
import com.mifos.objects.accounts.CenterAccounts
import org.apache.fineract.client.models.GetCentersCenterIdAccountsResponse
import org.mifos.core.data.AbstractMapper

object GetCentersCenterIdResponseMapper: AbstractMapper<GetCentersCenterIdAccountsResponse, CenterAccounts>() {
    override fun mapFromEntity(entity: GetCentersCenterIdAccountsResponse): CenterAccounts {
        return CenterAccounts().apply {
            savingsAccounts = entity.savingsAccounts?.map { SavingsAccMapper.mapFromEntity(it) } ?:
            listOf()
            loanAccounts = listOf()
            memberLoanAccounts = listOf()
        }
    }

    override fun mapToEntity(domainModel: CenterAccounts): GetCentersCenterIdAccountsResponse {
        return GetCentersCenterIdAccountsResponse().apply {
            savingsAccounts = domainModel.savingsAccounts.map { SavingsAccMapper.mapToGetCentersSavingsAccountsEntity(it) }

        }
    }
}