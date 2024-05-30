/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.survey

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@Parcelize
data class Scorecard(
    var userId: Int = 0,

    var clientId: Int = 0,

    var createdOn: Date? = null,

    var scorecardValues: List<ScorecardValues>? = null
) : Parcelable