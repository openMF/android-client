package com.mifos.objects.db;

import com.orm.SugarRecord;

public class AttendanceType extends SugarRecord<AttendanceType>
{
    private int attendanceTypeId;
    private String code;
    private String value;
    private int clientId;
    public int getClientId()
    {
        return clientId;
    }

    public void setClientId(int clientId)
    {
        this.clientId = clientId;
    }


    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public int getAttendanceTypeId()
    {
       return attendanceTypeId;
    }

    public void setAttendanceTypeId(int attendanceTypeId)
    {
        this.attendanceTypeId = attendanceTypeId;
    }

    @Override
    public String toString()
    {
        return "AttendanceType{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
