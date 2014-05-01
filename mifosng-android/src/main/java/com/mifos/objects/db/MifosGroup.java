package com.mifos.objects.db;


import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class MifosGroup extends SugarRecord<MifosGroup>
{
        private int groupId;
        private String groupName;
    public int staffId;
    public String staffName;

    public int levelId;
    public String levelName;

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
    public String toString() {
        return new Gson().toJson(this);
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


    public String getStaffName() {
        return staffName;
    }

    public String getLevelName() {
        return levelName;
    }
}
