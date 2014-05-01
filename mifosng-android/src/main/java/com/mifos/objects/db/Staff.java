package com.mifos.objects.db;


import com.orm.SugarRecord;

public class Staff extends SugarRecord<Staff>
{
    private int staffId;
    private String staffName;
    private int groupId;

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public void setStaffId(int staffId)
    {
        this.staffId = staffId;
    }

    public void setStaffName(String staffName)
    {
        this.staffName = staffName;
    }

    public int getStaffId()
    {
        return staffId;
    }

    public String getStaffName()
    {
        return staffName;
    }

    @Override
    public String toString()
    {
        return "Staff{" +
                "staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
