package com.mifos.api.mappers.groups

import com.mifos.api.mappers.accounts.loan.LoanAccMapper
import com.mifos.api.mappers.accounts.savings.SavingsAccMapper
import com.mifos.objects.accounts.GroupAccounts
import org.apache.fineract.client.models.GetGroupsGroupIdAccountsResponse
import org.mifos.core.data.AbstractMapper

object GetGroupsGroupIdAccountsMapper: AbstractMapper<GetGroupsGroupIdAccountsResponse, GroupAccounts>() {
    override fun mapFromEntity(entity: GetGroupsGroupIdAccountsResponse): GroupAccounts {
        return GroupAccounts().apply {
            loanAccounts = entity.loanAccounts?.let { LoanAccMapper.mapFromEntityGroupsList(it) } ?: listOf()
            savingsAccounts = entity.savingsAccounts?.let {
                SavingsAccMapper.mapFromEntityGroupsList(it)
            } ?: listOf()
        }
    }

    @Deprecated("This mapper doesn't support mapping to entity")
    override fun mapToEntity(domainModel: GroupAccounts): GetGroupsGroupIdAccountsResponse {
        TODO("Not yet implemented")
    }
}