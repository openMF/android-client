/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.surveyquestion

import com.mifos.core.objects.survey.Scorecard

/**
 * Created by Nasim Banu on 28,January,2016.
 */
interface Communicator {
    fun passScoreCardData(scorecard: Scorecard, surveyid: Int)
}