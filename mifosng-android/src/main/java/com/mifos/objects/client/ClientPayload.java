package com.mifos.objects.client;
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.noncore.DataTablePayload;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 16-Jun-15.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class ClientPayload extends MifosBaseModel implements Parcelable {

    @PrimaryKey(autoincrement = true)
    transient Integer id;

    @Column
    transient Long clientCreationTime;

    @Column
    transient String errorMessage;

    @SerializedName("firstname")
    @Column
    String firstname;

    @SerializedName("lastname")
    @Column
    String lastname;

    @SerializedName("middlename")
    @Column
    String middlename;

    @SerializedName("officeId")
    @Column
    Integer officeId;

    @SerializedName("staffId")
    @Column
    Integer staffId;

    @SerializedName("genderId")
    @Column
    Integer genderId;

    @SerializedName("active")
    @Column
    Boolean active;

    @SerializedName("activationDate")
    @Column
    String activationDate;

    @SerializedName("submittedOnDate")
    @Column
    String submittedOnDate;

    @SerializedName("dateOfBirth")
    @Column
    String dateOfBirth;

    @SerializedName("mobileNo")
    @Column
    String mobileNo;

    @SerializedName("externalId")
    @Column
    String externalId;

    @SerializedName("clientTypeId")
    @Column
    Integer clientTypeId;

    @SerializedName("clientClassificationId")
    @Column
    Integer clientClassificationId;

    @SerializedName("address")
    List<Address> address = new ArrayList<>();

    @SerializedName("dateFormat")
    @Column
    String dateFormat = "dd MMMM YYYY";

    @SerializedName("locale")
    @Column
    String locale = "en";

    @SerializedName("datatables")
    List<DataTablePayload> datatables = new ArrayList<>();

    public List<DataTablePayload> getDatatables() {
        return datatables;
    }

    public void setDatatables(List<DataTablePayload> datatables) {
        this.datatables = datatables;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getClientCreationTime() {
        return clientCreationTime;
    }

    public void setClientCreationTime(Long clientCreationTime) {
        this.clientCreationTime = clientCreationTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getClientTypeId() {
        return clientTypeId;
    }

    public void setClientTypeId(Integer clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public Integer getClientClassificationId() {
        return clientClassificationId;
    }

    public void setClientClassificationId(Integer clientClassificationId) {
        this.clientClassificationId = clientClassificationId;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "ClientPayload{" +
                "id=" + id +
                ", errorMessage='" + errorMessage + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", officeId=" + officeId +
                ", staffId=" + staffId +
                ", genderId=" + genderId +
                ", active=" + active +
                ", activationDate='" + activationDate + '\'' +
                ", submittedOnDate='" + submittedOnDate + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", externalId='" + externalId + '\'' +
                ", clientTypeId=" + clientTypeId +
                ", clientClassificationId=" + clientClassificationId +
                ", address=" + address +
                ", dateFormat='" + dateFormat + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.middlename);
        dest.writeValue(this.officeId);
        dest.writeValue(this.staffId);
        dest.writeValue(this.genderId);
        dest.writeValue(this.active);
        dest.writeString(this.activationDate);
        dest.writeString(this.submittedOnDate);
        dest.writeString(this.dateOfBirth);
        dest.writeString(this.mobileNo);
        dest.writeString(this.externalId);
        dest.writeValue(this.clientTypeId);
        dest.writeValue(this.clientClassificationId);
        dest.writeTypedList(this.address);
        dest.writeString(this.dateFormat);
        dest.writeString(this.locale);
    }

    public ClientPayload() {
    }

    protected ClientPayload(Parcel in) {
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.middlename = in.readString();
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.staffId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.genderId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.activationDate = in.readString();
        this.submittedOnDate = in.readString();
        this.dateOfBirth = in.readString();
        this.mobileNo = in.readString();
        this.externalId = in.readString();
        this.clientTypeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientClassificationId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.address = in.createTypedArrayList(Address.CREATOR);
        this.dateFormat = in.readString();
        this.locale = in.readString();
    }

    public static final Parcelable.Creator<ClientPayload> CREATOR =
            new Parcelable.Creator<ClientPayload>() {
        @Override
        public ClientPayload createFromParcel(Parcel source) {
            return new ClientPayload(source);
        }

        @Override
        public ClientPayload[] newArray(int size) {
            return new ClientPayload[size];
        }
    };
}

