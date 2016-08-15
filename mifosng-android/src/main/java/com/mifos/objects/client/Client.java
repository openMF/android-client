/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.client;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.Timeline;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 08/02/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class Client extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    int id;

    @Column
    String accountNo;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Status status;

    @Column
    transient boolean sync;

    @Column
    boolean active;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    ClientDate clientDate;

    List<Integer> activationDate = new ArrayList<Integer>();

    List<Integer> dobDate = new ArrayList<Integer>();

    @Column
    String firstname;

    @Column
    String middlename;

    @Column
    String lastname;

    @Column
    String displayName;

    @Column
    int officeId;

    @Column
    String officeName;

    @Column
    int staffId;

    @Column
    String staffName;

    Timeline timeline;

    @Column
    String fullname;

    @Column
    int imageId;

    @Column
    boolean imagePresent;

    @Column
    private String externalId;

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public Client() {
    }

    public ClientDate getClientDate() {
        return clientDate;
    }

    public void setClientDate(ClientDate clientDate) {
        this.clientDate = clientDate;
    }

    protected Client(Parcel in) {
        this.id = in.readInt();
        this.accountNo = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.active = in.readByte() != 0;
        this.activationDate = new ArrayList<Integer>();
        in.readList(this.activationDate, Integer.class.getClassLoader());
        this.dobDate = new ArrayList<Integer>();
        in.readList(this.dobDate, Integer.class.getClassLoader());
        this.firstname = in.readString();
        this.middlename = in.readString();
        this.lastname = in.readString();
        this.displayName = in.readString();
        this.officeId = in.readInt();
        this.officeName = in.readString();
        this.staffId = in.readInt();
        this.staffName = in.readString();
        this.timeline = in.readParcelable(Timeline.class.getClassLoader());
        this.fullname = in.readString();
        this.imageId = in.readInt();
        this.imagePresent = in.readByte() != 0;
        this.externalId = in.readString();
    }

    public List<Integer> getDobDate() {
        return dobDate;
    }

    public void setDobDate(List<Integer> dobDate) {
        this.dobDate = dobDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
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

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isImagePresent() {
        return imagePresent;
    }

    public void setImagePresent(boolean imagePresent) {
        this.imagePresent = imagePresent;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", status=" + status +
                ", active=" + active +
                ", activationDate=" + activationDate +
                ", firstname='" + firstname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", lastname='" + lastname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                ", timeline=" + timeline +
                ", fullname='" + fullname + '\'' +
                ", imageId=" + imageId +
                ", imagePresent=" + imagePresent +
                ", externalId='" + externalId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.accountNo);
        dest.writeParcelable(this.status, flags);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeList(this.activationDate);
        dest.writeList(this.dobDate);
        dest.writeString(this.firstname);
        dest.writeString(this.middlename);
        dest.writeString(this.lastname);
        dest.writeString(this.displayName);
        dest.writeInt(this.officeId);
        dest.writeString(this.officeName);
        dest.writeInt(this.staffId);
        dest.writeString(this.staffName);
        dest.writeParcelable(this.timeline, flags);
        dest.writeString(this.fullname);
        dest.writeInt(this.imageId);
        dest.writeByte(this.imagePresent ? (byte) 1 : (byte) 0);
        dest.writeString(this.externalId);
    }

    public static final Parcelable.Creator<Client> CREATOR = new Parcelable.Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel source) {
            return new Client(source);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };
}
