package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class InterestRecalculationData(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("productId")
    var productId: Int? = null,

    @SerializedName("interestRecalculationCompoundingType")
    var interestRecalculationCompoundingType: InterestRecalculationCompoundingType? = null,

    @SerializedName("rescheduleStrategyType")
    var rescheduleStrategyType: RescheduleStrategyType? = null,

    @SerializedName("recalculationRestFrequencyType")
    var recalculationRestFrequencyType: RecalculationRestFrequencyType? = null,

    @SerializedName("recalculationRestFrequencyInterval")
    var recalculationRestFrequencyInterval: Int? = null,

    @SerializedName("isArrearsBasedOnOriginalSchedule")
    var isArrearsBasedOnOriginalSchedule: Boolean? = null,

    @SerializedName("isCompoundingToBePostedAsTransaction")
    var isCompoundingToBePostedAsTransaction: Boolean? = null,

    @SerializedName("preClosureInterestCalculationStrategy")
    var preClosureInterestCalculationStrategy: PreClosureInterestCalculationStrategy? = null,

    @SerializedName("allowCompoundingOnEod")
    var allowCompoundingOnEod: Boolean? = null
) : Parcelable {
    override fun toString(): String {
        return "InterestRecalculationData(id=$id, productId=$productId, " +
                "interestRecalculationCompoundingType=$interestRecalculationCompoundingType, " +
                "rescheduleStrategyType=$rescheduleStrategyType, " +
                "recalculationRestFrequencyType=$recalculationRestFrequencyType, " +
                "recalculationRestFrequencyInterval=$recalculationRestFrequencyInterval, " +
                "isArrearsBasedOnOriginalSchedule=$isArrearsBasedOnOriginalSchedule, " +
                "isCompoundingToBePostedAsTransaction=$isCompoundingToBePostedAsTransaction, " +
                "preClosureInterestCalculationStrategy=$preClosureInterestCalculationStrategy, " +
                "allowCompoundingOnEod=$allowCompoundingOnEod)"
    }
}