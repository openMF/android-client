package com.mifos.objects.db;

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
    private int groupId;

    @Override
    public String toString()
    {
        return "Client{" +
                "clientId=" + clientId +
                ", clientName='" + clientName + '\'' +
                ", loans=" + loans +
                ", attendanceType=" + attendanceType +
                ", groupId=" + groupId +
                '}';
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public AttendanceType getAttendanceType()
    {
        return attendanceType;
    }

    public void setAttendanceType(AttendanceType attendanceType)
    {
        this.attendanceType = attendanceType;
    }
    @Ignore
    public  List<Loan> getLoans()
    {
        return loans;
    }
    @Ignore
    public void setLoans( List<Loan>  loans)
    {
        this.loans = loans;
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
