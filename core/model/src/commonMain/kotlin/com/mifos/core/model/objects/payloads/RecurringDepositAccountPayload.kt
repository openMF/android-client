package com.mifos.core.model.objects.payloads

import kotlinx.serialization.Serializable

@Serializable
data class RecurringDepositAccountPayload(
    val adjustAdvanceTowardsFuturePayments: Boolean? = null,
    val allowWithdrawal: Boolean? = null,
//    val charges: List<Any>? = null,
    val clientId: Int? = null,
    val dateFormat: String? = null,
    val depositPeriod: Int? = null,
    val depositPeriodFrequencyId: Int? = null,
    val expectedFirstDepositOnDate: String? = null,
    val externalId: String? = null,
    val fieldOfficerId: Int? = null,
    val interestCalculationDaysInYearType: Int? = null,
    val interestCalculationType: Int? = null,
    val interestCompoundingPeriodType: Int? = null,
    val interestPostingPeriodType: Int? = null,
    val isCalendarInherited: Boolean? = null,
    val isMandatoryDeposit: Boolean? = null,
    val locale: String? = null,
    val lockinPeriodFrequency: Int? = null,
    val lockinPeriodFrequencyType: Int? = null,
    val mandatoryRecommendedDepositAmount: Int? = null,
    val monthDayFormat: String? = null,
    val productId: Int? = null,
    val recurringFrequency: Int? = null,
    val recurringFrequencyType: Int? = null,
    val submittedOnDate: String? = null,
)