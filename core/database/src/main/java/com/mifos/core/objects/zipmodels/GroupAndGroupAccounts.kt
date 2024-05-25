package com.mifos.core.objects.zipmodels

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.group.Group

/**
 * Created by Rajan Maurya on 11/09/16.
 */
class GroupAndGroupAccounts : Parcelable {
    var group: Group? = null
    var groupAccounts: GroupAccounts? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(group, flags)
        dest.writeParcelable(groupAccounts, flags)
    }

    constructor()
    constructor(group: Group?, groupAccounts: GroupAccounts?) {
        this.group = group
        this.groupAccounts = groupAccounts
    }

    protected constructor(`in`: Parcel) {
        group = `in`.readParcelable(Group::class.java.classLoader)
        groupAccounts = `in`.readParcelable(GroupAccounts::class.java.classLoader)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<GroupAndGroupAccounts> = object : Creator<GroupAndGroupAccounts> {
            override fun createFromParcel(source: Parcel): GroupAndGroupAccounts {
                return GroupAndGroupAccounts(source)
            }

            override fun newArray(size: Int): Array<GroupAndGroupAccounts?> {
                return arrayOfNulls(size)
            }
        }
    }
}