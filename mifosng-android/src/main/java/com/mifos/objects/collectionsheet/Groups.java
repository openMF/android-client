/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.collectionsheet;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class Groups {

    private int groupId;
    private String groupName;
    private int staffId;
    private String staffName;
    private int levelId;
    private String levelName;
    private List<ClientCollection> clients;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public List<ClientCollection> getClients() {
        return clients;
    }

    public void setClients(List<ClientCollection> clients) {
        this.clients = clients;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", levelId=" + levelId +
                ", levelName='" + levelName + '\'' +
                ", clients=" + clients +
                '}';
    }
}
