package com.mifos.objects.collectionsheet

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by Tarun on 25-07-2017.
 */

class GroupCollectionSheet() : Parcelable {
    var clients: MutableList<ClientCollectionSheet> = ArrayList()
    var groupId: Int = 0
    var groupName: String? = null
    var levelId: Int = 0
    var levelName: String? = null
    var staffId: Int = 0
    var staffName: String? = null

    constructor(parcel: Parcel) : this() {
        parcel.readTypedList(clients, ClientCollectionSheet.CREATOR)
        groupId = parcel.readInt()
        groupName = parcel.readString()
        levelId = parcel.readInt()
        levelName = parcel.readString()
        staffId = parcel.readInt()
        staffName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(clients)
        parcel.writeInt(groupId)
        parcel.writeString(groupName)
        parcel.writeInt(levelId)
        parcel.writeString(levelName)
        parcel.writeInt(staffId)
        parcel.writeString(staffName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupCollectionSheet> {
        override fun createFromParcel(parcel: Parcel): GroupCollectionSheet {
            return GroupCollectionSheet(parcel)
        }

        override fun newArray(size: Int): Array<GroupCollectionSheet?> {
            return arrayOfNulls(size)
        }
    }
}