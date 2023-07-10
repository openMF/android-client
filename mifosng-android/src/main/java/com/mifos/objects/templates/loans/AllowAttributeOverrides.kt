package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class AllowAttributeOverrides(
    @SerializedName("amortizationType")
    var amortizationType: Boolean = false,

    @SerializedName("interestType")
    var interestType: Boolean = false,

    @SerializedName("transactionProcessingStrategyId")
    var transactionProcessingStrategyId: Boolean = false,

    @SerializedName("interestCalculationPeriodType")
    var interestCalculationPeriodType: Boolean = false,

    @SerializedName("inArrearsTolerance")
    var inArrearsTolerance: Boolean = false,

    @SerializedName("repaymentEvery")
    var repaymentEvery: Boolean = false,

    @SerializedName("graceOnPrincipalAndInterestPayment")
    var graceOnPrincipalAndInterestPayment: Boolean = false,

    @SerializedName("graceOnArrearsAgeing")
    var graceOnArrearsAgeing: Boolean = false
) : Parcelable {

    override fun toString(): String {
        return "AllowAttributeOverrides(amortizationType=$amortizationType, " +
                "interestType=$interestType, " +
                "transactionProcessingStrategyId=$transactionProcessingStrategyId, " +
                "interestCalculationPeriodType=$interestCalculationPeriodType, " +
                "inArrearsTolerance=$inArrearsTolerance, " +
                "repaymentEvery=$repaymentEvery, " +
                "graceOnPrincipalAndInterestPayment=$graceOnPrincipalAndInterestPayment, " +
                "graceOnArrearsAgeing=$graceOnArrearsAgeing)"
    }
}