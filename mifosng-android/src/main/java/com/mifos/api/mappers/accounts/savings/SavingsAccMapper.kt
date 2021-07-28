package com.mifos.api.mappers.accounts.savings

import com.mifos.objects.accounts.savings.SavingsAccount
import org.apache.fineract.client.models.GetClientsSavingsAccounts
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
}