package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class Group(
    var id: Int,
    var accountNo: Int,
    var name: String,
    var externalId: Int,
    var status: Status,
    var active: Boolean,
    var activationDate: List<Int>,
    var officeId: Int,
    var officeName: String,
    var hierarchy: String,
    var groupLevel: Int,
    var timeline: GroupTimeline
) : Parcelable