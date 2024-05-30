package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class GroupTimeline(
    var submittedOnDate: List<Int>? = null,

    var submittedByUsername: String? = null,

    var submittedByFirstname: String? = null,

    var submittedByLastname: String? = null,

    var activatedOnDate: List<Int>? = null,

    var activatedByUsername: String? = null,

    var activatedByFirstname: String? = null,

    var activatedByLastname: String? = null
) : Parcelable