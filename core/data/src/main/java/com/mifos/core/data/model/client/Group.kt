/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.model.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * This is the Groups Model Table
 * Created by ishankhanna on 28/06/14.
 */
@Parcelize
data class Group(
    var id: Int? = null,

    var accountNo: String? = null,

    var sync: Boolean = false,

    var name: String? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    var groupDate: GroupDate? = null,

    var activationDate: List<Int> = ArrayList(),

    var officeId: Int? = null,

    var officeName: String? = null,

    var centerId: Int? = 0,

    var centerName: String? = null,

    var staffId: Int? = null,

    var staffName: String? = null,

    var hierarchy: String? = null,

    var groupLevel: Int = 0,

    var timeline: Timeline? = null,

    var externalId: String? = null
) : Parcelable