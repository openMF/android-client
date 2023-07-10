package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class LoanPurposeOptions(
    var id: Int,
    var name: String,
    var position: Int,
    var description: String,
    var isActive: Boolean
) : Parcelable {
    override fun toString(): String {
        return "LoanPurposeOptions(id=$id, name=$name, position=$position, description=$description, isActive=$isActive)"
    }
}