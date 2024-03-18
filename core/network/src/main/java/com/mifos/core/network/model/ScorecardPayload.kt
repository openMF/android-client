/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import com.mifos.core.objects.survey.ScorecardValues
import java.util.Date

/**
 * Created by Nasim Banu on 28,January,2016.
 */
data class ScorecardPayload(
    var userId: Int = 0,
    var clientId: Int = 0,
    var createdOn: Date? = null,
    var scorecardValues: List<ScorecardValues>? = null
)