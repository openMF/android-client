package com.mifos.api.mappers.client.template

import com.mifos.objects.templates.clients.SavingProductOptions
import org.apache.fineract.client.models.GetClientsSavingProductOptions
import org.mifos.core.data.AbstractMapper

object SavingProductOptionMapper: AbstractMapper<GetClientsSavingProductOptions, SavingProductOptions>() {
    override fun mapFromEntity(entity: GetClientsSavingProductOptions): SavingProductOptions {
        return SavingProductOptions().apply {
            id = entity.id!!
            name = entity.name
            isWithdrawalFeeForTransfers = entity.withdrawalFeeForTransfers!!
            isAllowOverdraft = entity.allowOverdraft!!
        }
    }

    override fun mapToEntity(domainModel: SavingProductOptions): GetClientsSavingProductOptions {
        return GetClientsSavingProductOptions().apply {
            id = domainModel.id!!
            name = domainModel.name
            withdrawalFeeForTransfers = domainModel.isWithdrawalFeeForTransfers
            allowOverdraft = domainModel.isAllowOverdraft
        }
    }
}