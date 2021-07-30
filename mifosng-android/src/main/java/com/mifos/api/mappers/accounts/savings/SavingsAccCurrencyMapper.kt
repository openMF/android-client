package com.mifos.api.mappers.accounts.savings

import com.mifos.objects.accounts.savings.Currency
import org.apache.fineract.client.models.GetClientsSavingsAccountsCurrency
import org.apache.fineract.client.models.GetGroupsGroupIdAccountsSavingCurrency
import org.mifos.core.data.AbstractMapper

object SavingsAccCurrencyMapper: AbstractMapper<GetClientsSavingsAccountsCurrency, Currency>() {
    override fun mapFromEntity(entity: GetClientsSavingsAccountsCurrency): Currency {
        return Currency().apply {
            code = entity.code
            name = entity.name
            nameCode = entity.nameCode
            decimalPlaces = entity.decimalPlaces
            displaySymbol = entity.displaySymbol
            displayLabel = entity.displayLabel
        }
    }

    override fun mapToEntity(domainModel: Currency): GetClientsSavingsAccountsCurrency {
        return GetClientsSavingsAccountsCurrency().apply {
            code = domainModel.code
            name = domainModel.name
            nameCode = domainModel.nameCode
            decimalPlaces = domainModel.decimalPlaces
            displaySymbol = domainModel.displaySymbol
            displayLabel = domainModel.displayLabel
        }
    }

    fun mapFromEntity(entity: GetGroupsGroupIdAccountsSavingCurrency): Currency {
        return Currency().apply {
            code = entity.code
            name = entity.name
            nameCode = entity.nameCode
            decimalPlaces = entity.decimalPlaces
            displaySymbol = entity.displaySymbol
            displayLabel = entity.displayLabel
        }
    }
}