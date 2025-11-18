package com.mifos.core.network.model

import com.mifos.core.model.objects.account.saving.FieldOfficerOptions
import com.mifos.core.model.objects.template.recurring.Currency
import com.mifos.core.model.objects.template.recurring.interest.InterestCalculationDaysInYearTypeOption
import com.mifos.core.model.objects.template.recurring.interest.InterestCalculationTypeOption
import com.mifos.core.model.objects.template.recurring.interest.InterestCompoundingPeriodTypeOption
import com.mifos.core.model.objects.template.recurring.interest.InterestPostingPeriodTypeOption
import com.mifos.core.model.objects.template.recurring.period.PeriodFrequencyTypeOption
import com.mifos.core.model.utils.IgnoredOnParcel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class FixedDepositTemplate(

    @SerialName("clientId")
    val clientId: Int? = null,

    @SerialName(value = "clientName")
    val clientName: String? = null,

    @SerialName(value = "currency")
    val currency: Currency? = null,

    @SerialName("productOptions")
    val productOptions: List<FixedDepositProductOption>? = null,

    @SerialName("fieldOfficerOptions")
    val fieldOfficerOptions: List<FieldOfficerOptions>? = null,

    @SerialName("periodFrequencyTypeOptions")
    val periodFrequencyTypeOptions: List<PeriodFrequencyTypeOption>? = null,

    @SerialName("interestCompoundingPeriodTypeOptions")
    val interestCompoundingPeriodTypeOptions: List<InterestCompoundingPeriodTypeOption>? = null,

    @SerialName("interestPostingPeriodTypeOptions")
    val interestPostingPeriodTypeOptions: List<InterestPostingPeriodTypeOption>? = null,

    @SerialName("interestCalculationDaysInYearTypeOptions")
    val interestCalculationDaysInYearTypeOptions: List<InterestCalculationDaysInYearTypeOption>? = null,

    @SerialName("interestCalculationTypeOptions")
    val interestCalculationTypeOptions: List<InterestCalculationTypeOption>? = null,

    )