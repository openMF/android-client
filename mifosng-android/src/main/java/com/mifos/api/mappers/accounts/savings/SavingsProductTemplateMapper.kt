package com.mifos.api.mappers.accounts.savings

import com.mifos.api.mappers.GenericMapper.convert
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.savings.Currency
import com.mifos.objects.common.InterestType
import com.mifos.objects.templates.savings.SavingProductsTemplate
import org.apache.fineract.client.models.*
import org.mifos.core.data.AbstractMapper

object SavingsProductTemplateMapper: AbstractMapper<GetSavingsProductsTemplateResponse, SavingProductsTemplate>() {

    override fun mapFromEntity(entity: GetSavingsProductsTemplateResponse): SavingProductsTemplate {
        return SavingProductsTemplate().apply {
            currency = convert(entity.currency)
            interestCompoundingPeriodType = convert(entity.interestCompoundingPeriodType)
            interestPostingPeriodType = convert(entity.interestPostingPeriodType)
            interestCalculationType = convert(entity.interestCalculationType)
            interestCalculationDaysInYearType = convert(entity.interestCalculationDaysInYearType)
            accountingRule = convert(entity.accountingRule)
            currencyOptions = entity.currencyOptions?.let { it ->
                it.map { convert<GetSavingsCurrency, Currency>(it) }
            } ?: listOf()
            interestCompoundingPeriodTypeOptions = entity.interestCompoundingPeriodTypeOptions?.let {
                convert<GetSavingsProductsInterestCompoundingPeriodType, InterestType>(it)
            } ?: listOf()
            interestPostingPeriodTypeOptions = entity.interestPostingPeriodTypeOptions?.let {
                convert<GetSavingsProductsInterestPostingPeriodType, InterestType>(it)
            } ?: listOf()
            interestCalculationTypeOptions = entity.interestCalculationTypeOptions?.let {
                convert<GetSavingsProductsInterestCalculationType, InterestType>(it)
            } ?: listOf()
            interestCalculationDaysInYearTypeOptions = entity.interestCalculationDaysInYearTypeOptions?.let {
                convert<GetSavingsProductsInterestCalculationDaysInYearType, InterestType>(it)
            } ?: listOf()
            lockinPeriodFrequencyTypeOptions = entity.lockinPeriodFrequencyTypeOptions?.let {
                convert<GetSavingsProductsLockinPeriodFrequencyTypeOptions, InterestType>(it)
            } ?: listOf()
            withdrawalFeeTypeOptions = entity.withdrawalFeeTypeOptions?.let {
                convert<GetSavingsProductsWithdrawalFeeTypeOptions, InterestType>(it)
            } ?: listOf()
            paymentTypeOptions = entity.paymentTypeOptions?.let {
                convert<GetSavingsProductsPaymentTypeOptions, PaymentTypeOption>(it)
            } ?: listOf()
            accountingRuleOptions = entity.accountingRuleOptions?.let {
                convert<GetSavingsProductsTemplateAccountingRule, InterestType>(it)
            } ?: listOf()
            assetAccountOptions = listOf()
            expenseAccountOptions = listOf()
            incomeAccountOptions = listOf()
            fieldOfficerOptions = listOf()
        }
    }

    override fun mapToEntity(domainModel: SavingProductsTemplate): GetSavingsProductsTemplateResponse {
        return convert(domainModel)
    }
}