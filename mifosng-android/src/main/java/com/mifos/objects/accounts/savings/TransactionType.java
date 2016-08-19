/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = MifosDatabase.class)
@ModelContainer
public class TransactionType extends MifosBaseModel implements Parcelable {

    @SerializedName("id")
    @PrimaryKey
    Integer id;

    @SerializedName("code")
    String code;

    @SerializedName("value")
    String value;

    @SerializedName("deposit")
    @Column
    Boolean deposit;

    @SerializedName("withdrawal")
    @Column
    Boolean withdrawal;

    @SerializedName("interestPosting")
    Boolean interestPosting;

    @SerializedName("feeDeduction")
    Boolean feeDeduction;

    @SerializedName("initiateTransfer")
    Boolean initiateTransfer;

    @SerializedName("approveTransfer")
    Boolean approveTransfer;

    @SerializedName("withdrawTransfer")
    Boolean withdrawTransfer;

    @SerializedName("rejectTransfer")
    Boolean rejectTransfer;

    @SerializedName("overdraftInterest")
    Boolean overdraftInterest;

    @SerializedName("writtenoff")
    Boolean writtenoff;

    @SerializedName("overdraftFee")
    Boolean overdraftFee;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getDeposit() {
        return deposit;
    }

    public void setDeposit(Boolean deposit) {
        this.deposit = deposit;
    }

    public Boolean getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(Boolean withdrawal) {
        this.withdrawal = withdrawal;
    }

    public Boolean getInterestPosting() {
        return interestPosting;
    }

    public void setInterestPosting(Boolean interestPosting) {
        this.interestPosting = interestPosting;
    }

    public Boolean getFeeDeduction() {
        return feeDeduction;
    }

    public void setFeeDeduction(Boolean feeDeduction) {
        this.feeDeduction = feeDeduction;
    }

    public Boolean getInitiateTransfer() {
        return initiateTransfer;
    }

    public void setInitiateTransfer(Boolean initiateTransfer) {
        this.initiateTransfer = initiateTransfer;
    }

    public Boolean getApproveTransfer() {
        return approveTransfer;
    }

    public void setApproveTransfer(Boolean approveTransfer) {
        this.approveTransfer = approveTransfer;
    }

    public Boolean getWithdrawTransfer() {
        return withdrawTransfer;
    }

    public void setWithdrawTransfer(Boolean withdrawTransfer) {
        this.withdrawTransfer = withdrawTransfer;
    }

    public Boolean getRejectTransfer() {
        return rejectTransfer;
    }

    public void setRejectTransfer(Boolean rejectTransfer) {
        this.rejectTransfer = rejectTransfer;
    }

    public Boolean getOverdraftInterest() {
        return overdraftInterest;
    }

    public void setOverdraftInterest(Boolean overdraftInterest) {
        this.overdraftInterest = overdraftInterest;
    }

    public Boolean getWrittenoff() {
        return writtenoff;
    }

    public void setWrittenoff(Boolean writtenoff) {
        this.writtenoff = writtenoff;
    }

    public Boolean getOverdraftFee() {
        return overdraftFee;
    }

    public void setOverdraftFee(Boolean overdraftFee) {
        this.overdraftFee = overdraftFee;
    }

    @Override
    public String toString() {
        return "TransactionType{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", deposit=" + deposit +
                ", withdrawal=" + withdrawal +
                ", interestPosting=" + interestPosting +
                ", feeDeduction=" + feeDeduction +
                ", initiateTransfer=" + initiateTransfer +
                ", approveTransfer=" + approveTransfer +
                ", withdrawTransfer=" + withdrawTransfer +
                ", rejectTransfer=" + rejectTransfer +
                ", overdraftInterest=" + overdraftInterest +
                ", writtenoff=" + writtenoff +
                ", overdraftFee=" + overdraftFee +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
        dest.writeValue(this.deposit);
        dest.writeValue(this.withdrawal);
        dest.writeValue(this.interestPosting);
        dest.writeValue(this.feeDeduction);
        dest.writeValue(this.initiateTransfer);
        dest.writeValue(this.approveTransfer);
        dest.writeValue(this.withdrawTransfer);
        dest.writeValue(this.rejectTransfer);
        dest.writeValue(this.overdraftInterest);
        dest.writeValue(this.writtenoff);
        dest.writeValue(this.overdraftFee);
    }

    public TransactionType() {
    }

    protected TransactionType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
        this.deposit = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.withdrawal = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.interestPosting = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.feeDeduction = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.initiateTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.approveTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.withdrawTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.rejectTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.overdraftInterest = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.writtenoff = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.overdraftFee = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<TransactionType> CREATOR =
            new Parcelable.Creator<TransactionType>() {
        @Override
        public TransactionType createFromParcel(Parcel source) {
            return new TransactionType(source);
        }

        @Override
        public TransactionType[] newArray(int size) {
            return new TransactionType[size];
        }
    };
}
