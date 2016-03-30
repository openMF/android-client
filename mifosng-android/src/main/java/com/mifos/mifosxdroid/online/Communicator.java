/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.mifosxdroid.online;

import com.mifos.objects.survey.Scorecard;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public interface Communicator {
    void createScoreCard(Scorecard scorecard, int surveyid);
}

