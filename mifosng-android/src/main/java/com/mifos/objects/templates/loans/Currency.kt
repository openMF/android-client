package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class Currency(
    var code: String = "",
    var name: String = "",
    var decimalPlaces: Double = 0.0,
    var inMultiplesOf: Int = 0,
    var displaySymbol: String = "",
    var nameCode: String = "",
    var displayLabel: String = ""
) : Parcelable {

    override fun toString(): String {
        return "Currency{" +
                "code='$code', " +
                "name='$name', " +
                "decimalPlaces=$decimalPlaces, " +
                "inMultiplesOf=$inMultiplesOf, " +
                "displaySymbol='$displaySymbol', " +
                "nameCode='$nameCode', " +
                "displayLabel='$displayLabel'" +
                '}'
    }
}