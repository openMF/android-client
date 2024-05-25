package com.mifos.core.data

/**
 * Created by nellyk on 2/10/2016.
 */
class SavingsPayload {
    var productId: Int? = null
    var clientId: Int? = null
    var groupId: Int? = null
    var fieldOfficerId: Int? = null
    var locale: String? = null
    var dateFormat: String? = null
    var submittedOnDate: String? = null
    var externalId: String? = null
    var nominalAnnualInterestRate: String? = null
    var interestCompoundingPeriodType: Int? = null
    var interestCalculationType: Int? = null
    var interestCalculationDaysInYearType: Int? = null
    var interestPostingPeriodType: Int? = null
    var allowOverdraft: Boolean? = null
    var enforceMinRequiredBalance: Boolean? = null
    var minRequiredOpeningBalance: String? = null
    var nominalAnnualInterestRateOverdraft: String? = null
    var overdraftLimit: String? = null
    var minOverdraftForInterestCalculation: String? = null
}