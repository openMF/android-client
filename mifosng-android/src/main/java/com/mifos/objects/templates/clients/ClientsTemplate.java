package com.mifos.objects.templates.clients;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.noncore.DataTable;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

/**
 * Created by rajan on 13/3/16.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class ClientsTemplate extends MifosBaseModel implements Parcelable {

    private int[] activationDate;

    @PrimaryKey
    int officeId;

    private List<OfficeOptions> officeOptions;
    private List<StaffOptions> staffOptions;
    private List<SavingProductOptions> savingProductOptions;
    private List<Options> genderOptions;
    private List<Options> clientTypeOptions;
    private List<Options> clientClassificationOptions;
    private List<InterestType> clientLegalFormOptions;
    private List<DataTable> datatables = new ArrayList<>();

    public List<DataTable> getDataTables() {
        return datatables;
    }

    public void setDataTables(List<DataTable> dataTables) {
        this.datatables = dataTables;
    }

    public int[] getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(int[] activationDate) {
        this.activationDate = activationDate;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public List<OfficeOptions> getOfficeOptions() {
        return officeOptions;
    }

    public void setOfficeOptions(List<OfficeOptions> officeOptions) {
        this.officeOptions = officeOptions;
    }

    public List<StaffOptions> getStaffOptions() {
        return staffOptions;
    }

    public void setStaffOptions(List<StaffOptions> staffOptions) {
        this.staffOptions = staffOptions;
    }

    public List<SavingProductOptions> getSavingProductOptions() {
        return savingProductOptions;
    }

    public void setSavingProductOptions(List<SavingProductOptions> savingProductOptions) {
        this.savingProductOptions = savingProductOptions;
    }

    public List<Options> getGenderOptions() {
        return genderOptions;
    }

    public void setGenderOptions(List<Options> genderOptions) {
        this.genderOptions = genderOptions;
    }

    public List<Options> getClientTypeOptions() {
        return clientTypeOptions;
    }

    public void setClientTypeOptions(List<Options> clientTypeOptions) {
        this.clientTypeOptions = clientTypeOptions;
    }

    public List<InterestType> getClientLegalFormOptions() {
        return clientLegalFormOptions;
    }

    public void setClientLegalFormOptions(List<InterestType> clientLegalFormOptions) {
        this.clientLegalFormOptions = clientLegalFormOptions;
    }

    public List<Options> getClientClassificationOptions() {
        return clientClassificationOptions;
    }

    public void setClientClassificationOptions(List<Options> clientClassificationOptions) {
        this.clientClassificationOptions = clientClassificationOptions;
    }

    @Override
    public String toString() {
        return "ClientsTemplate{" +
                "activationDate=" + Arrays.toString(activationDate) +
                ", officeId=" + officeId +
                ", officeOptions=" + officeOptions +
                ", staffOptions=" + staffOptions +
                ", savingProductOptions=" + savingProductOptions +
                ", genderOptions=" + genderOptions +
                ", clientTypeOptions=" + clientTypeOptions +
                ", clientClassificationOptions=" + clientClassificationOptions +
                ", clientLegalFormOptions=" + clientLegalFormOptions +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.activationDate);
        dest.writeInt(this.officeId);
        dest.writeTypedList(this.officeOptions);
        dest.writeTypedList(this.staffOptions);
        dest.writeTypedList(this.savingProductOptions);
        dest.writeTypedList(this.genderOptions);
        dest.writeTypedList(this.clientTypeOptions);
        dest.writeTypedList(this.clientClassificationOptions);
        dest.writeTypedList(this.clientLegalFormOptions);
    }

    public ClientsTemplate() {
    }

    protected ClientsTemplate(Parcel in) {
        this.activationDate = in.createIntArray();
        this.officeId = in.readInt();
        this.officeOptions = in.createTypedArrayList(OfficeOptions.CREATOR);
        this.staffOptions = in.createTypedArrayList(StaffOptions.CREATOR);
        this.savingProductOptions = in.createTypedArrayList(SavingProductOptions.CREATOR);
        this.genderOptions = in.createTypedArrayList(Options.CREATOR);
        this.clientTypeOptions = in.createTypedArrayList(Options.CREATOR);
        this.clientClassificationOptions = in.createTypedArrayList(Options.CREATOR);
        this.clientLegalFormOptions = in.createTypedArrayList(InterestType.CREATOR);
    }

    public static final Parcelable.Creator<ClientsTemplate> CREATOR = new Parcelable
            .Creator<ClientsTemplate>() {
        @Override
        public ClientsTemplate createFromParcel(Parcel source) {
            return new ClientsTemplate(source);
        }

        @Override
        public ClientsTemplate[] newArray(int size) {
            return new ClientsTemplate[size];
        }
    };
}
