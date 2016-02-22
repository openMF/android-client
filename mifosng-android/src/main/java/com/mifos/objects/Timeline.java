/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class Timeline {

    private List<Integer> submittedOnDate = new ArrayList<Integer>();
    private List<Integer> activatedOnDate = new ArrayList<Integer>();
    private List<Integer> closedOnDate = new ArrayList<Integer>();
    private String closedByUsername;
    private String closedByFirstname;
    private String closedByLastname;

    public List<Integer> getClosedOnDate() {
        return closedOnDate;
    }

    public void setClosedOnDate(List<Integer> closedOnDate) {
        this.closedOnDate = closedOnDate;
    }

    public String getClosedByUsername() {
        return closedByUsername;
    }

    public void setClosedByUsername(String closedByUsername) {
        this.closedByUsername = closedByUsername;
    }

    public String getClosedByFirstname() {
        return closedByFirstname;
    }

    public void setClosedByFirstname(String closedByFirstname) {
        this.closedByFirstname = closedByFirstname;
    }

    public String getClosedByLastname() {
        return closedByLastname;
    }

    public void setClosedByLastname(String closedByLastname) {
        this.closedByLastname = closedByLastname;
    }

    public List<Integer> getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(List<Integer> submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public List<Integer> getActivatedOnDate() {
        return activatedOnDate;
    }

    public void setActivatedOnDate(List<Integer> activatedOnDate) {
        this.activatedOnDate = activatedOnDate;
    }
}
