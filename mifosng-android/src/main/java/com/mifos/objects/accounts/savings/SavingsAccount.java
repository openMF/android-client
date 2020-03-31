/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = MifosDatabase.class)
@ModelContainer
public class SavingsAccount extends MifosBaseModel implements Parcelable {


    @Column
    transient long clientId;

    @Column
    transient long groupId;

    @Column
    long centerId;

    @PrimaryKey
    Integer id;

    @Column
    String accountNo;

    @Column
    Integer productId;

    @Column
    String productName;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Status status;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Currency currency;

    @Column
    Double accountBalance;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    DepositType depositType;

    public SavingsAccount() {
    }

    protected SavingsAccount(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.productId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.productName = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.accountBalance = (Double) in.readValue(Double.class.getClassLoader());
        this.depositType = in.readParcelable(DepositType.class.getClassLoader());
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getClientId() {
        return this.clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getCenterId() {
        return this.centerId;
    }

    public void setCenterId(long centerId) {
        this.centerId = centerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SavingsAccount withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public SavingsAccount withAccountNo(String accountNo) {
        this.accountNo = accountNo;
        return this;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public SavingsAccount withProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public SavingsAccount withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SavingsAccount withStatus(Status status) {
        this.status = status;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public SavingsAccount withCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public SavingsAccount withAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
        return this;
    }

    public DepositType getDepositType() {
        return depositType;
    }

    public void setDepositType(DepositType depositType) {
        this.depositType = depositType;
    }

    public boolean isRecurring() {
        return this.getDepositType() != null && this.getDepositType().isRecurring();
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "accountBalance=" + accountBalance +
                ", id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", status=" + status +
                ", currency=" + currency +
                ", depositType=" + depositType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.accountNo);
        dest.writeValue(this.productId);
        dest.writeString(this.productName);
        dest.writeParcelable(this.status, flags);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.accountBalance);
        dest.writeParcelable(this.depositType, flags);
    }

    public static final Parcelable.Creator<SavingsAccount> CREATOR =
            new Parcelable.Creator<SavingsAccount>() {
        @Override
        public SavingsAccount createFromParcel(Parcel source) {
            return new SavingsAccount(source);
        }

        @Override
        public SavingsAccount[] newArray(int size) {
            return new SavingsAccount[size];
        }
    };
}
