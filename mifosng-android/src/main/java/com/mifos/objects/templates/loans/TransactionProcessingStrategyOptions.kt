package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class TransactionProcessingStrategyOptions(
    var id: Int,
    var code: String,
    var name: String
) : Parcelable {
    override fun toString(): String {
        return "TransactionProcessingStrategyOptions{" +
                "id=$id, " +
                "code='$code', " +
                "name='$name'" +
                '}'
    }
}