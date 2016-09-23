package com.mifos.objects.zipmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.group.Group;

/**
 * Created by Rajan Maurya on 11/09/16.
 */
public class GroupAndGroupAccounts implements Parcelable {

    Group group;

    GroupAccounts groupAccounts;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupAccounts getGroupAccounts() {
        return groupAccounts;
    }

    public void setGroupAccounts(GroupAccounts groupAccounts) {
        this.groupAccounts = groupAccounts;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.group, flags);
        dest.writeParcelable(this.groupAccounts, flags);
    }

    public GroupAndGroupAccounts() {
    }

    public GroupAndGroupAccounts(Group group, GroupAccounts groupAccounts) {
        this.group = group;
        this.groupAccounts = groupAccounts;
    }

    protected GroupAndGroupAccounts(Parcel in) {
        this.group = in.readParcelable(Group.class.getClassLoader());
        this.groupAccounts = in.readParcelable(GroupAccounts.class.getClassLoader());
    }

    public static final Parcelable.Creator<GroupAndGroupAccounts> CREATOR =
            new Parcelable.Creator<GroupAndGroupAccounts>() {
        @Override
        public GroupAndGroupAccounts createFromParcel(Parcel source) {
            return new GroupAndGroupAccounts(source);
        }

        @Override
        public GroupAndGroupAccounts[] newArray(int size) {
            return new GroupAndGroupAccounts[size];
        }
    };
}
