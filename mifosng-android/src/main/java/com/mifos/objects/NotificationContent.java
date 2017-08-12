package com.mifos.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mayankjindal on 12/08/17.
 */

public class NotificationContent implements Parcelable {

    @SerializedName("Subject")
    String subject;

    @SerializedName("Object")
    String object;

    @SerializedName("Action")
    String action;

    @SerializedName("Actor")
    String actor;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subject);
        dest.writeString(this.object);
        dest.writeString(this.action);
        dest.writeString(this.actor);
    }

    public NotificationContent() {
    }

    protected NotificationContent(Parcel in) {
        this.subject = in.readString();
        this.object = in.readString();
        this.action = in.readString();
        this.actor = in.readString();
    }

    public static final Parcelable.Creator<NotificationContent> CREATOR =
            new Parcelable.Creator<NotificationContent>() {
                @Override
                public NotificationContent createFromParcel(Parcel source) {
                    return new NotificationContent(source);
                }

                @Override
                public NotificationContent[] newArray(int size) {
                    return new NotificationContent[size];
                }
            };
}
