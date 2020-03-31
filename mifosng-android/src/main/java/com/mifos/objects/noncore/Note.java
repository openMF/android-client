package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;

/**
 * Created by rahul on 4/3/17.
 */
public class Note extends MifosBaseModel implements Parcelable {

    @SerializedName("id")
    Integer id;

    @SerializedName("clientId")
    Integer clientId;

    @SerializedName("note")
    String noteContent;

    @SerializedName("createdById")
    Integer createdById;

    @SerializedName("createdByUsername")
    String createdByUsername;

    @SerializedName("createdOn")
    long createdOn;

    @SerializedName("updatedById")
    Integer updatedById;

    @SerializedName("updatedByUsername")
    String updatedByUsername;

    @SerializedName("updatedOn")
    long updatedOn;

    protected Note(Parcel in) {
        id = in.readInt();
        clientId = in.readInt();
        noteContent = in.readString();
        createdById = in.readInt();
        createdByUsername = in.readString();
        createdOn = in.readLong();
        updatedById = in.readInt();
        updatedByUsername = in.readString();
        updatedOn = in.readLong();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public long getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(long updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedByUsername() {
        return updatedByUsername;
    }

    public void setUpdatedByUsername(String updatedByUsername) {
        this.updatedByUsername = updatedByUsername;
    }

    public int getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(int updatedById) {
        this.updatedById = updatedById;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public int getCreatedById() {
        return createdById;
    }

    public void setCreatedById(int createdById) {
        this.createdById = createdById;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Note() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(clientId);
        parcel.writeString(noteContent);
        parcel.writeInt(createdById);
        parcel.writeString(createdByUsername);
        parcel.writeLong(createdOn);
        parcel.writeInt(updatedById);
        parcel.writeString(updatedByUsername);
        parcel.writeLong(updatedOn);
    }
}