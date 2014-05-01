package com.mifos.objects.db;

import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;


public class Client extends SugarRecord<Client>
{
    private int clientId;
    private String clientName;

    @Ignore
    private List<Loan> loans;

    private AttendanceType attendanceType;
    private MifosGroup mifosGroup;

    public MifosGroup getMifosGroup() {
        return mifosGroup;
    }

    public void setMifosGroup(MifosGroup mifosGroup) {
        this.mifosGroup = mifosGroup;
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public AttendanceType getAttendanceType()
    {
        return attendanceType;
    }

    public void setAttendanceType(AttendanceType attendanceType)
    {
        this.attendanceType = attendanceType;
    }

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    public int getClientId()
    {
        return clientId;
    }

    public void setClientId(int clientId)
    {
        this.clientId = clientId;
    }


}
