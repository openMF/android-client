/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.objects.common.InterestType;

/**
 * Created by ishankhanna on 14/02/14.
 */
public class SearchedEntity implements Parcelable {

    private int entityId;
    private String entityAccountNo;
    private String entityName;
    private String entityType;
    private int parentId;
    private String parentName;

    @SerializedName("entityStatus")
    private InterestType entityStatus;

    public InterestType getEntityStatus() {
        return this.entityStatus;
    }

    public void setEntityStatus(InterestType entityStatus) {
        this.entityStatus = entityStatus;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public SearchedEntity withEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getEntityAccountNo() {
        return entityAccountNo;
    }

    public void setEntityAccountNo(String entityAccountNo) {
        this.entityAccountNo = entityAccountNo;
    }

    public SearchedEntity withEntityAccountNo(String entityAccountNo) {
        this.entityAccountNo = entityAccountNo;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public SearchedEntity withEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public SearchedEntity withEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public SearchedEntity withParentId(int parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public SearchedEntity withParentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    public String getDescription() {
        return "#" + getEntityId() + " - " + getEntityName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.entityId);
        dest.writeString(this.entityAccountNo);
        dest.writeString(this.entityName);
        dest.writeString(this.entityType);
        dest.writeInt(this.parentId);
        dest.writeString(this.parentName);
        dest.writeParcelable(this.entityStatus, flags);
    }

    public SearchedEntity() {
    }

    protected SearchedEntity(Parcel in) {
        this.entityId = in.readInt();
        this.entityAccountNo = in.readString();
        this.entityName = in.readString();
        this.entityType = in.readString();
        this.parentId = in.readInt();
        this.parentName = in.readString();
        this.entityStatus = in.readParcelable(InterestType.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchedEntity> CREATOR = new Parcelable
            .Creator<SearchedEntity>() {
        @Override
        public SearchedEntity createFromParcel(Parcel source) {
            return new SearchedEntity(source);
        }

        @Override
        public SearchedEntity[] newArray(int size) {
            return new SearchedEntity[size];
        }
    };
}
