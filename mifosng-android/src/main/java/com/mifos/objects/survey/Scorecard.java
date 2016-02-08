/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.survey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class Scorecard {
    private Long userId;
    private Long clientId;
    private Date createdOn;
    private List<ScorecardValues> scorecardValues;
    // private Integer value;

    public Scorecard() {
        super();
    }

    public Scorecard( final Long userId, final Long clientId,
                      final Date createdOn, final List<ScorecardValues> scorecardValues) {
        super();

        this.userId = userId;
        this.clientId = clientId;
        this.createdOn = createdOn;
        this.scorecardValues= scorecardValues;

    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
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
}



