package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tarun on 25-07-2017.
 */

public class GroupCollectionSheet implements Parcelable {

    private List<ClientCollectionSheet> clients = new ArrayList<>();

    private int groupId;

    private String groupName;

    private int levelId;

    private String levelName;

    private int staffId;

    private String staffName;

    public List<ClientCollectionSheet> getClients() {
        return clients;
    }

    public void setClients(List<ClientCollectionSheet> clients) {
        this.clients = clients;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.clients);
        dest.writeInt(this.groupId);
        dest.writeString(this.groupName);
        dest.writeInt(this.levelId);
        dest.writeString(this.levelName);
        dest.writeInt(this.staffId);
        dest.writeString(this.staffName);
    }

    public GroupCollectionSheet() {
    }

    protected GroupCollectionSheet(Parcel in) {
        this.clients = in.createTypedArrayList(ClientCollectionSheet.CREATOR);
        this.groupId = in.readInt();
        this.groupName = in.readString();
        this.levelId = in.readInt();
        this.levelName = in.readString();
        this.staffId = in.readInt();
        this.staffName = in.readString();
    }

    public static final Creator<GroupCollectionSheet> CREATOR = new
            Creator<GroupCollectionSheet>() {
        @Override
        public GroupCollectionSheet createFromParcel(Parcel source) {
            return new GroupCollectionSheet(source);
        }

        @Override
        public GroupCollectionSheet[] newArray(int size) {
            return new GroupCollectionSheet[size];
        }
    };
}
