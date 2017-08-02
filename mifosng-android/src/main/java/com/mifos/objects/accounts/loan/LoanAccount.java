/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;


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
public class LoanAccount extends MifosBaseModel implements Parcelable {


    @Column
    long clientId;

    @Column
    long groupId;

    @PrimaryKey
    Integer id;

    @Column
    String accountNo;

    @Column
    String externalId;

    @Column
    Integer productId;

    @Column
    String productName;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Status status;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    LoanType loanType;

    @Column
    Integer loanCycle;

    @Column
    Boolean inArrears;

    public LoanAccount() {
    }

    protected LoanAccount(Parcel in) {
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.externalId = in.readString();
        this.productId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.productName = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.loanType = in.readParcelable(LoanType.class.getClassLoader());
        this.loanCycle = (Integer) in.readValue(Integer.class.getClassLoader());
        this.inArrears = (Boolean) in.readValue(Boolean.class.getClassLoader());
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LoanAccount withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public LoanAccount withAccountNo(String accountNo) {
        this.accountNo = accountNo;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public LoanAccount withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public LoanAccount withProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LoanAccount withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LoanAccount withStatus(Status status) {
        this.status = status;
        return this;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public LoanAccount withLoanType(LoanType loanType) {
        this.loanType = loanType;
        return this;
    }

    public Integer getLoanCycle() {
        return loanCycle;
    }

    public void setLoanCycle(Integer loanCycle) {
        this.loanCycle = loanCycle;
    }

    public Boolean getInArrears() {
        return inArrears;
    }

    public void setInArrears(Boolean inArrears) {
        this.inArrears = inArrears;
    }

    @Override
    public String toString() {
        return "LoanAccount{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", externalId='" + externalId + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", status=" + status +
                ", loanType=" + loanType +
                ", loanCycle=" + loanCycle +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.clientId);
        dest.writeValue(this.id);
        dest.writeString(this.accountNo);
        dest.writeString(this.externalId);
        dest.writeValue(this.productId);
        dest.writeString(this.productName);
        dest.writeParcelable(this.status, flags);
        dest.writeParcelable(this.loanType, flags);
        dest.writeValue(this.loanCycle);
        dest.writeValue(this.inArrears);
    }

    public static final Parcelable.Creator<LoanAccount> CREATOR = new Parcelable
            .Creator<LoanAccount>() {
        @Override
        public LoanAccount createFromParcel(Parcel source) {
            return new LoanAccount(source);
        }

        @Override
        public LoanAccount[] newArray(int size) {
            return new LoanAccount[size];
        }
    };
}
