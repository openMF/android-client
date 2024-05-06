/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import javax.annotation.processing.Generated

@Parcelize
@Generated("org.jsonschema2pojo")
data class Charge (
    var id: Int? = null,

    var chargeId: Int? = null,

    var accountId: Int? = null,

    var name: String? = null,

    var chargeTimeType: ChargeTimeType? = null,

    var chargeCalculationType: ChargeCalculationType? = null,

    var percentage: Int? = null,

    var amountPercentageAppliedTo: Int? = null,

    var currency: Currency? = null,

    var amount: Double? = null,

    var amountPaid: Double? = null,

    var amountWaived: Double? = null,

    var amountWrittenOff: Double? = null,

    var amountOutstanding: Double? = null,

    var amountOrPercentage: Double? = null,

    var penalty: Boolean? = null,

    val additionalProperties: MutableMap<String, String> = HashMap()
) : Parcelable {

    fun setAdditionalProperty(name: String, value: String) {
        additionalProperties[name] = value
    }
}