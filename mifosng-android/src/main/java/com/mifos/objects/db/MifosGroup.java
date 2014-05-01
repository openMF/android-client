package com.mifos.objects.db;


import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class MifosGroup extends SugarRecord<MifosGroup>
{
        private int groupId;
        private String groupName;
        private Staff staff;
        private Level level;
        @Ignore
        private List<Client> clients;

    @Ignore
    public List<Client> getClients()
    {
        return clients;
    }
    @Ignore
    public void setClients(List<Client> clients)
    {
        this.clients = clients;
    }

    @Override
    public String toString()
    {
        return "MifosGroup{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", staff=" + staff +
                ", level=" + level+
                ", clients=" + clients +
                '}';
    }

    public Level getLevel()
    {
        return level;
    }

    public void setLevel(Level level)
    {
        this.level = level;
    }

    public Staff getStaff()
    {
        return staff;
    }

    public void setStaff(Staff staff)
    {
        this.staff = staff;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }


}
