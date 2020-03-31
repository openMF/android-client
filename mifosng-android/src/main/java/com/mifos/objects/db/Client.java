/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;

import com.google.gson.Gson;

import java.util.List;


public class Client {
    private int clientId;
    private String clientName;
    private AttendanceType attendanceType;
    private MifosGroup mifosGroup;

    private List<Loan> loans;

    public MifosGroup getMifosGroup() {
        return mifosGroup;
    }

    public void setMifosGroup(MifosGroup mifosGroup) {
        this.mifosGroup = mifosGroup;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public AttendanceType getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(AttendanceType attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }


}
