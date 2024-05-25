package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class Group(
    var id: Int? = null,

    var accountNo: Int? = null,

    var name: String? = null,

    var externalId: Int? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    var activationDate: List<Int>? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var hierarchy: String? = null,

    var groupLevel: Int? = null,

    var timeline: GroupTimeline? = null
) : Parcelable