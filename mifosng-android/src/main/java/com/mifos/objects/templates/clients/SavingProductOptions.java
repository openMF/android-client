package com.mifos.objects.templates.clients;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by rajan on 13/3/16.
 */
@Table(database = MifosDatabase.class, name = "ClientTemplateSavingProductsOptions")
@ModelContainer
public class SavingProductOptions extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    int id;

    @Column
    String name;

    @Column
    boolean withdrawalFeeForTransfers;

    @Column
    boolean allowOverdraft;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWithdrawalFeeForTransfers() {
        return withdrawalFeeForTransfers;
    }

    public void setWithdrawalFeeForTransfers(boolean withdrawalFeeForTransfers) {
        this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
    }

    public boolean isAllowOverdraft() {
        return allowOverdraft;
    }

    public void setAllowOverdraft(boolean allowOverdraft) {
        this.allowOverdraft = allowOverdraft;
    }

    @Override
    public String toString() {
        return "SavingProductOptions{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", withdrawalFeeForTransfers=" + withdrawalFeeForTransfers +
                ", allowOverdraft=" + allowOverdraft +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeByte(this.withdrawalFeeForTransfers ? (byte) 1 : (byte) 0);
        dest.writeByte(this.allowOverdraft ? (byte) 1 : (byte) 0);
    }

    public SavingProductOptions() {
    }

    protected SavingProductOptions(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.withdrawalFeeForTransfers = in.readByte() != 0;
        this.allowOverdraft = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SavingProductOptions> CREATOR = new Parcelable
            .Creator<SavingProductOptions>() {
        @Override
        public SavingProductOptions createFromParcel(Parcel source) {
            return new SavingProductOptions(source);
        }

        @Override
        public SavingProductOptions[] newArray(int size) {
            return new SavingProductOptions[size];
        }
    };
}
