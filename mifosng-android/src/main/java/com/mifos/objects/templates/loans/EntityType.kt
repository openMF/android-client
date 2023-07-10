package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class EntityType(
    var id: Int,
    var code: String,
    var value: String
) : Parcelable {

    override fun toString(): String {
        return "EntityType{" +
                "id=$id, code='$code', value='$value'}"
    }
}