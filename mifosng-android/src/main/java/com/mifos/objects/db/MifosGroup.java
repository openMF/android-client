/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;


import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public class MifosGroup extends SugarRecord<MifosGroup> {
    public int staffId;
    public String staffName;
    public int levelId;
    public String levelName;
    private long groupId;
    private String groupName;
    private long centerId;
    @Ignore
    private List<Client> clients;

    @Ignore
    public List<Client> getClients() {
        return clients;
    }

    @Ignore
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean isNew() {
        long count = Select.from(MifosGroup.class).where(Condition.prop("group_id").eq(groupId)).count();
        return count == 0;
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
