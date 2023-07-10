package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class GroupTimeline(
    var submittedOnDate: List<Int>,
    var submittedByUsername: String,
    var submittedByFirstname: String,
    var submittedByLastname: String,
    var activatedOnDate: List<Int>,
    var activatedByUsername: String,
    var activatedByFirstname: String,
    var activatedByLastname: String
) : Parcelable