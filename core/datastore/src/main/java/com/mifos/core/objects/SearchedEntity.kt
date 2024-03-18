/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects

import android.os.Parcelable
import com.mifos.core.objects.common.InterestType
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 14/02/14.
 */
@Parcelize
data class SearchedEntity(

    var entityId: Int = 0,

    var entityAccountNo: String? = null,

    var entityName: String? = null,

    var entityType: String? = null,

    var parentId: Int = 0,

    var parentName: String? = null,

    var entityStatus: InterestType? = null

) : Parcelable {
    val description: String
        get() = "#$entityId - $entityName"
}