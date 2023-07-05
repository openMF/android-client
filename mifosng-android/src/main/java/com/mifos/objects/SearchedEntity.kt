/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.objects.common.InterestType

/**
 * Created by ishankhanna on 14/02/14.
 */
class SearchedEntity() : Parcelable {

    var entityId: Int = 0
    var entityAccountNo: String? = null
    var entityName: String? = null
    var entityType: String? = null
    var parentId: Int = 0
    var parentName: String? = null

    @SerializedName("entityStatus")
    var mEntityStatus: InterestType? = null

    fun getEntityStatus(): InterestType? {
        return mEntityStatus
    }

    fun setEntityStatus(entityStatus: InterestType?) {
        this.mEntityStatus = entityStatus
    }

    fun withEntityId(entityId: Int): SearchedEntity {
        this.entityId = entityId
        return this
    }

    fun withEntityAccountNo(entityAccountNo: String?): SearchedEntity {
        this.entityAccountNo = entityAccountNo
        return this
    }

    fun withEntityName(entityName: String?): SearchedEntity {
        this.entityName = entityName
        return this
    }

    fun withEntityType(entityType: String?): SearchedEntity {
        this.entityType = entityType
        return this
    }

    fun withParentId(parentId: Int): SearchedEntity {
        this.parentId = parentId
        return this
    }

    fun withParentName(parentName: String?): SearchedEntity {
        this.parentName = parentName
        return this
    }

    val description : String
        get() = "#$entityId - $entityName"


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(entityId)
        dest.writeString(entityAccountNo)
        dest.writeString(entityName)
        dest.writeString(entityType)
        dest.writeInt(parentId)
        dest.writeString(parentName)
        dest.writeParcelable(mEntityStatus, flags)
    }

    constructor(parcel: Parcel) : this() {
        entityId = parcel.readInt()
        entityAccountNo = parcel.readString()
        entityName = parcel.readString()
        entityType = parcel.readString()
        parentId = parcel.readInt()
        parentName = parcel.readString()
        mEntityStatus = parcel.readParcelable(InterestType::class.java.classLoader)
    }

    companion object CREATOR : Parcelable.Creator<SearchedEntity> {
        override fun createFromParcel(parcel: Parcel): SearchedEntity {
            return SearchedEntity(parcel)
        }

        override fun newArray(size: Int): Array<SearchedEntity?> {
            return arrayOfNulls(size)
        }
    }

}