package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 31-07-17.
 */

public class SavingsProduct implements Parcelable {

    private boolean allowOverdraft;

    private String depositAccountType;

    private boolean enforceMinRequiredBalance;

    private  int id;

    private String name;

    private boolean withHoldTax;

    private boolean withdrawalFeeForTransfers;

    public boolean isAllowOverdraft() {
        return allowOverdraft;
    }

    public void setAllowOverdraft(boolean allowOverdraft) {
        this.allowOverdraft = allowOverdraft;
    }

    public String getDepositAccountType() {
        return depositAccountType;
    }

    public void setDepositAccountType(String depositAccountType) {
        this.depositAccountType = depositAccountType;
    }

    public boolean isEnforceMinRequiredBalance() {
        return enforceMinRequiredBalance;
    }

    public void setEnforceMinRequiredBalance(boolean enforceMinRequiredBalance) {
        this.enforceMinRequiredBalance = enforceMinRequiredBalance;
    }

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

    public boolean isWithHoldTax() {
        return withHoldTax;
    }

    public void setWithHoldTax(boolean withHoldTax) {
        this.withHoldTax = withHoldTax;
    }

    public boolean isWithdrawalFeeForTransfers() {
        return withdrawalFeeForTransfers;
    }

    public void setWithdrawalFeeForTransfers(boolean withdrawalFeeForTransfers) {
        this.withdrawalFeeForTransfers = withdrawalFeeForTransfers;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.allowOverdraft ? (byte) 1 : (byte) 0);
        dest.writeString(this.depositAccountType);
        dest.writeByte(this.enforceMinRequiredBalance ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeByte(this.withHoldTax ? (byte) 1 : (byte) 0);
        dest.writeByte(this.withdrawalFeeForTransfers ? (byte) 1 : (byte) 0);
    }

    public SavingsProduct() {
    }

    protected SavingsProduct(Parcel in) {
        this.allowOverdraft = in.readByte() != 0;
        this.depositAccountType = in.readString();
        this.enforceMinRequiredBalance = in.readByte() != 0;
        this.id = in.readInt();
        this.name = in.readString();
        this.withHoldTax = in.readByte() != 0;
        this.withdrawalFeeForTransfers = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SavingsProduct> CREATOR = new
            Parcelable.Creator<SavingsProduct>() {
        @Override
        public SavingsProduct createFromParcel(Parcel source) {
            return new SavingsProduct(source);
        }

        @Override
        public SavingsProduct[] newArray(int size) {
            return new SavingsProduct[size];
        }
    };
}
