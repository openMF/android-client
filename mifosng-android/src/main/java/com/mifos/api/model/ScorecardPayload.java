/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.model;

import com.mifos.objects.survey.ScorecardValues;

import java.util.Date;
import java.util.List;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class ScorecardPayload {
    private int userId;
    private int clientId;
    private Date createdOn;
    private List<ScorecardValues> scorecardValues;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public List<ScorecardValues> getScorecardValues() {
        return scorecardValues;
    }

    public void setScorecardValues(List<ScorecardValues> scorecardValues) {
        this.scorecardValues = scorecardValues;
    }
}

