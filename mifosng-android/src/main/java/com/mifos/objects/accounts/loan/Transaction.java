/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.savings.Currency;

import java.util.ArrayList;
import java.util.List;


public class Transaction implements Parcelable {

    private Integer id;
    private Integer officeId;
    private String officeName;
    private Type type;
    private List<Integer> date = new ArrayList<Integer>();
    private Currency currency;
    private PaymentDetailData paymentDetailData;
    private Double amount;
    private Double principalPortion;
    private Double interestPortion;
    private Double feeChargesPortion;
    private Double penaltyChargesPortion;
    private Double overpaymentPortion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public PaymentDetailData getPaymentDetailData() {
        return paymentDetailData;
    }

    public void setPaymentDetailData(PaymentDetailData paymentDetailData) {
        this.paymentDetailData = paymentDetailData;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getPrincipalPortion() {
        return principalPortion;
    }

    public void setPrincipalPortion(Double principalPortion) {
        this.principalPortion = principalPortion;
    }

    public Double getInterestPortion() {
        return interestPortion;
    }

    public void setInterestPortion(Double interestPortion) {
        this.interestPortion = interestPortion;
    }

    public Double getFeeChargesPortion() {
        return feeChargesPortion;
    }

    public void setFeeChargesPortion(Double feeChargesPortion) {
        this.feeChargesPortion = feeChargesPortion;
    }

    public Double getPenaltyChargesPortion() {
        return penaltyChargesPortion;
    }

    public void setPenaltyChargesPortion(Double penaltyChargesPortion) {
        this.penaltyChargesPortion = penaltyChargesPortion;
    }

    public Double getOverpaymentPortion() {
        return overpaymentPortion;
    }

    public void setOverpaymentPortion(Double overpaymentPortion) {
        this.overpaymentPortion = overpaymentPortion;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", currency=" + currency +
                ", paymentDetailData=" + paymentDetailData +
                ", amount=" + amount +
                ", principalPortion=" + principalPortion +
                ", interestPortion=" + interestPortion +
                ", feeChargesPortion=" + feeChargesPortion +
                ", penaltyChargesPortion=" + penaltyChargesPortion +
                ", overpaymentPortion=" + overpaymentPortion +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.officeId);
        dest.writeString(this.officeName);
        dest.writeParcelable(this.type, flags);
        dest.writeList(this.date);
        dest.writeParcelable(this.currency, flags);
        dest.writeParcelable(this.paymentDetailData, flags);
        dest.writeValue(this.amount);
        dest.writeValue(this.principalPortion);
        dest.writeValue(this.interestPortion);
        dest.writeValue(this.feeChargesPortion);
        dest.writeValue(this.penaltyChargesPortion);
        dest.writeValue(this.overpaymentPortion);
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.officeName = in.readString();
        this.type = in.readParcelable(Type.class.getClassLoader());
        this.date = new ArrayList<Integer>();
        in.readList(this.date, Integer.class.getClassLoader());
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.paymentDetailData = in.readParcelable(PaymentDetailData.class.getClassLoader());
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.principalPortion = (Double) in.readValue(Double.class.getClassLoader());
        this.interestPortion = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesPortion = (Double) in.readValue(Double.class.getClassLoader());
        this.penaltyChargesPortion = (Double) in.readValue(Double.class.getClassLoader());
        this.overpaymentPortion = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<Transaction> CREATOR =
            new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
