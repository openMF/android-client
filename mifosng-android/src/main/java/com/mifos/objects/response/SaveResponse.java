/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.response;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mifos.api.model.Changes;

public class SaveResponse {

    @SerializedName("groupId")
    Integer groupId;

    @SerializedName("resourceId")
    Integer resourceId;

    @SerializedName("officeId")
    Integer officeId;

    Changes changes;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public Changes getChanges() {
        return changes;
    }

    public void setChanges(Changes changes) {
        this.changes = changes;
    }
}
