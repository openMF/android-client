/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.zipmodels

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.group.Group

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

    private constructor(parcel: Parcel) {
        group = parcel.readParcelable(Group::class.java.classLoader)
        groupAccounts = parcel.readParcelable(GroupAccounts::class.java.classLoader)
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
