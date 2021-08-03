package com.mifos.api.mappers.accounts.savings

import com.mifos.api.mappers.accounts.loan.LoanAccMapper
import com.mifos.objects.accounts.loan.LoanAccount
import com.mifos.objects.accounts.savings.SavingsAccount
import org.apache.fineract.client.models.GetCentersSavingsAccounts
import org.apache.fineract.client.models.GetClientsSavingsAccounts
import org.apache.fineract.client.models.GetGroupsGroupIdAccountsLoanAccounts
import org.apache.fineract.client.models.GetGroupsGroupIdAccountsSavingAccounts
import org.mifos.core.data.AbstractMapper

object SavingsAccMapper: AbstractMapper<GetClientsSavingsAccounts, SavingsAccount>() {
    override fun mapFromEntity(entity: GetClientsSavingsAccounts): SavingsAccount {
        return SavingsAccount().apply {
            id = entity.id
            accountNo = entity.accountNo
            productId = entity.productId
            productName = entity.productName
            status = SavingsAccStatusMapper.mapFromEntity(entity.status!!)
            currency = SavingsAccCurrencyMapper.mapFromEntity(entity.currency!!)
        }
    }

    override fun mapToEntity(domainModel: SavingsAccount): GetClientsSavingsAccounts {
        return GetClientsSavingsAccounts().apply {
            id = domainModel.id
            accountNo = domainModel.accountNo
            productId = domainModel.productId
            productName = domainModel.productName
            status = SavingsAccStatusMapper.mapToEntity(domainModel.status!!)
            currency = SavingsAccCurrencyMapper.mapToEntity(domainModel.currency!!)
        }
    }

    fun mapFromEntity(entity: GetGroupsGroupIdAccountsSavingAccounts): SavingsAccount {
        return SavingsAccount().apply {
            id = entity.id
            accountNo = entity.accountNo.toString()
            productId = entity.productId
            productName = entity.productName
            status = SavingsAccStatusMapper.mapFromEntity(entity.status!!)
            currency = SavingsAccCurrencyMapper.mapFromEntity(entity.currency!!)
        }
    }

    fun mapFromEntityGroupsList(entities: List<GetGroupsGroupIdAccountsSavingAccounts>):
            List<SavingsAccount> {
        return entities.map { mapFromEntity(it) }
    }

    fun mapFromEntity(entity: GetCentersSavingsAccounts): SavingsAccount {

        return SavingsAccount().apply {
            id = entity.id
            accountNo = entity.accountNo.toString()
            productId = entity.productId
            productName = entity.productName
            status = SavingsAccStatusMapper.mapFromEntity(entity.status!!)
            currency = SavingsAccCurrencyMapper.mapFromEntity(entity.currency!!)
        }
    }

    fun mapToGetCentersSavingsAccountsEntity(domainModel: SavingsAccount):
            GetCentersSavingsAccounts {
        return GetCentersSavingsAccounts().apply {
            id = domainModel.id
            accountNo = domainModel.accountNo.toLong()
            productId = domainModel.productId
            productName = domainModel.productName
            status = SavingsAccStatusMapper
                .mapToGetCentersCenterIdStatusEntity(domainModel.status!!)
            currency = SavingsAccCurrencyMapper
                .mapToGetCentersCenterIdCurrencyEntity(domainModel.currency!!)
        }
    }
}