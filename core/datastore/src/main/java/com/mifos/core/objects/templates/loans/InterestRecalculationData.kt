package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class InterestRecalculationData(
    var id: Int? = null,

    var productId: Int? = null,

    var interestRecalculationCompoundingType: InterestRecalculationCompoundingType? = null,

    var rescheduleStrategyType: RescheduleStrategyType? = null,

    var recalculationRestFrequencyType: RecalculationRestFrequencyType? = null,

    var recalculationRestFrequencyInterval: Int? = null,

    var isArrearsBasedOnOriginalSchedule: Boolean? = null,

    var isCompoundingToBePostedAsTransaction: Boolean? = null,

    var preClosureInterestCalculationStrategy: PreClosureInterestCalculationStrategy? = null,

    var allowCompoundingOnEod: Boolean? = null
) : Parcelable