package com.mifos.services.data;


import com.google.gson.Gson;

public class ClientsAttendance
{
    public int attendanceType;
    public int clientId;
    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}
