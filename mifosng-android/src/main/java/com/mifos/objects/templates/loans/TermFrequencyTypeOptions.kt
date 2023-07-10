package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class TermFrequencyTypeOptions(
    var id: Int,
    var code: String,
    var value: String
) : Parcelable {

    override fun toString(): String {
        return "TermFrequencyTypeOptions{" +
                "id=$id, " +
                "code='$code', " +
                "value='$value'" +
                '}'
    }
}