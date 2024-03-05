/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.survey

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@Parcelize
data class ScorecardValues(
    var questionId: Int? = null,

    var responseId: Int? = null,

    var value: Int? = null
) : Parcelable