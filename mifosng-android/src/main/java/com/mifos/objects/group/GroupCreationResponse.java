package com.mifos.objects.group;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class GroupCreationResponse {

    private int officeId;
    private int groupId;
    private int resourceId;

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "GroupCreationResponse{" +
                "officeId=" + officeId +
                ", groupId=" + groupId +
                ", resourceId=" + resourceId +
                '}';
    }
}
