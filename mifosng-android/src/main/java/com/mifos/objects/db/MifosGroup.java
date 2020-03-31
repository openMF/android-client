/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;


import com.google.gson.Gson;

import java.util.List;

public class MifosGroup {
    public int staffId;
    public String staffName;
    public int levelId;
    public String levelName;
    private long groupId;
    private String groupName;
    private long centerId;

    private List<Client> clients;


    public List<Client> getClients() {
        return clients;
    }


    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getCenterId() {
        return centerId;
    }

    public void setCenterId(long centerId) {
        this.centerId = centerId;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getLevelName() {
        return levelName;
    }
}
