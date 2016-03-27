/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.survey;
import java.util.Date;
import java.util.List;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class Scorecard {

    private int userId;
    private int clientId;
    private Date createdOn;
    private List<ScorecardValues> scorecardValues;

    public Scorecard() {
        super();
    }

    public Scorecard( final int userId, final int clientId, final Date createdOn, final List<ScorecardValues> scorecardValues) {

        super();
        this.userId = userId;
        this.clientId = clientId;
        this.createdOn = createdOn;
        this.scorecardValues= scorecardValues;

    }


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

    public void setScorecardValues(List<ScorecardValues> scorecardValues ) {
        this.scorecardValues = scorecardValues;
    }

    @Override
    public String toString() {
        return "Scorecard{" +
                "userId=" + userId +
                ", clientId=" + clientId +
                ", createdOn=" + createdOn +
                ", scorecardValues=" + scorecardValues +
                '}';
    }
}



