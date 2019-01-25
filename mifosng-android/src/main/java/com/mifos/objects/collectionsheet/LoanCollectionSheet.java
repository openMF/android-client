package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.loan.Currency;

/**
 * Created by Tarun on 06-07-2017.
 */

public class LoanCollectionSheet implements Parcelable {

    private String accountId;

    private int accountStatusId;

    private Currency currency;

    private Double interestDue;

    private Double interestPaid;

    private Integer loanId;

    private Double principalDue;

    private Double productId;

    private Double totalDue;

    private Double chargesDue;

    private String productShortName;

    public Double getChargesDue() {
        return chargesDue;
    }

    public void setChargesDue(Double chargesDue) {
        this.chargesDue = chargesDue;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getAccountStatusId() {
        return accountStatusId;
    }

    public void setAccountStatusId(int accountStatusId) {
        this.accountStatusId = accountStatusId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getInterestDue() {
        return interestDue;
    }

    public void setInterestDue(Double interestDue) {
        this.interestDue = interestDue;
    }

    public Double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(Double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Double getPrincipalDue() {
        return principalDue;
    }

    public void setPrincipalDue(Double principalDue) {
        this.principalDue = principalDue;
    }

    public Double getProductId() {
        return productId;
    }

    public void setProductId(Double productId) {
        this.productId = productId;
    }

    public Double getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(Double totalDue) {
        this.totalDue = totalDue;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accountId);
        dest.writeInt(this.accountStatusId);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.interestDue);
        dest.writeValue(this.interestPaid);
        dest.writeValue(this.loanId);
        dest.writeValue(this.principalDue);
        dest.writeValue(this.productId);
        dest.writeValue(this.totalDue);
        dest.writeValue(this.chargesDue);
        dest.writeString(this.productShortName);
    }

    public LoanCollectionSheet() {
    }

    protected LoanCollectionSheet(Parcel in) {
        this.accountId = in.readString();
        this.accountStatusId = in.readInt();
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.interestDue = (Double) in.readValue(Double.class.getClassLoader());
        this.interestPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.loanId = (Integer) in.readValue(Double.class.getClassLoader());
        this.principalDue = (Double) in.readValue(Double.class.getClassLoader());
        this.productId = (Double) in.readValue(Double.class.getClassLoader());
        this.totalDue = (Double) in.readValue(Double.class.getClassLoader());
        this.chargesDue = (Double) in.readValue(Double.class.getClassLoader());
        this.productShortName = in.readString();
    }

    public static final Parcelable.Creator<LoanCollectionSheet> CREATOR = new
            Parcelable.Creator<LoanCollectionSheet>() {
        @Override
        public LoanCollectionSheet createFromParcel(Parcel source) {
            return new LoanCollectionSheet(source);
        }

        @Override
        public LoanCollectionSheet[] newArray(int size) {
            return new LoanCollectionSheet[size];
        }
    };
}
