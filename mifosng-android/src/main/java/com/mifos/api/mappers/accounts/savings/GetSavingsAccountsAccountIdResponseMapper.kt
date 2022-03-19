package com.mifos.api.mappers.accounts.savings

import com.mifos.api.mappers.GenericMapper.convert
import com.mifos.api.utils.toArray
import com.mifos.api.utils.toIntArray
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.accounts.savings.Timeline
import org.apache.fineract.client.models.GetSavingsAccountsAccountIdResponse
import org.mifos.core.data.AbstractMapper

object GetSavingsAccountsAccountIdResponseMapper: AbstractMapper<GetSavingsAccountsAccountIdResponse, SavingsAccountWithAssociations>() {
    override fun mapFromEntity(entity: GetSavingsAccountsAccountIdResponse): SavingsAccountWithAssociations {
        return SavingsAccountWithAssociations().apply {
            id = entity.id
            accountNo = entity.accountNo
            clientId = entity.clientId
            clientName = entity.clientName
            savingsProductId = entity.savingsProductId
            savingsProductName = entity.savingsProductName
            fieldOfficerId = entity.fieldOfficerId
            status = convert(entity.status)
            currency = convert(entity.currency)
            timeline = Timeline().apply {
                submittedOnDate = entity.timeline!!.submittedOnDate!!.toArray()
                // todo: add remaining fields once GetSavingsTimeline is fixed
            }
            nominalAnnualInterestRate = entity.nominalAnnualInterestRate
            interestCompoundingPeriodType = convert(entity.interestCompoundingPeriodType)
            interestPostingPeriodType = convert(entity.interestPostingPeriodType)
            interestCalculationType = convert(entity.interestCalculationType)
            interestCalculationDaysInYearType = convert(entity.interestCalculationDaysInYearType)
            summary = convert(entity.summary)
        }
    }

    override fun mapToEntity(domainModel: SavingsAccountWithAssociations): GetSavingsAccountsAccountIdResponse {
        TODO("Not yet implemented")
    }
}