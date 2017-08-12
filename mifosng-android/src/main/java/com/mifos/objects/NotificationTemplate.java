package com.mifos.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayankjindal on 12/08/17.
 */

public class NotificationTemplate implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("object_type")
    String objectType;

    @SerializedName("object_identifier")
    String objectIdentifier;

    @SerializedName("action")
    String action;

    @SerializedName("actor")
    String actor;

    @SerializedName("is_system_generated")
    Boolean isSystemGenerated;

    @SerializedName("notification_content")
    NotificationContent notificationContent;

    @SerializedName("created_at")
    String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    public void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Boolean getSystemGenerated() {
        return isSystemGenerated;
    }

    public void setSystemGenerated(Boolean systemGenerated) {
        isSystemGenerated = systemGenerated;
    }

    public NotificationContent getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(NotificationContent notificationContent) {
        this.notificationContent = notificationContent;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.objectType);
        dest.writeString(this.objectIdentifier);
        dest.writeString(this.action);
        dest.writeString(this.actor);
        dest.writeValue(this.isSystemGenerated);
        dest.writeParcelable(this.notificationContent, flags);
        dest.writeString(this.createdAt);
    }

    public NotificationTemplate() {
    }

    protected NotificationTemplate(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.objectType = in.readString();
        this.objectIdentifier = in.readString();
        this.action = in.readString();
        this.actor = in.readString();
        this.isSystemGenerated = (Boolean) in.readValue(Boolean.class
                .getClassLoader());
        this.notificationContent = in.readParcelable(NotificationContent.class
                .getClassLoader());
        this.createdAt = in.readString();
    }

    public static final Parcelable.Creator<NotificationTemplate> CREATOR =
            new Parcelable.Creator<NotificationTemplate>() {
                @Override
                public NotificationTemplate createFromParcel(Parcel source) {
                    return new NotificationTemplate(source);
                }

                @Override
                public NotificationTemplate[] newArray(int size) {
                    return new NotificationTemplate[size];
                }
            };
}