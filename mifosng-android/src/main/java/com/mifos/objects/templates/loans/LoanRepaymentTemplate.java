/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.savings.Currency;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(database = MifosDatabase.class)
@ModelContainer
public class LoanRepaymentTemplate extends MifosBaseModel implements Parcelable {

    // Loan id is not present in Response, It's only for primary key use case to save
    // LoanRepaymentTemplate in Database
    @PrimaryKey
    Integer loanId;

    Type type;

    List<Integer> date = new ArrayList<Integer>();

    Currency currency;

    @Column
    Double amount;

    @Column
    Double principalPortion;

    @Column
    Double interestPortion;

    @Column
    Double feeChargesPortion;

    @Column
    Double penaltyChargesPortion;

    List<PaymentTypeOption> paymentTypeOptions = new ArrayList<PaymentTypeOption>();

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
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

    public List<PaymentTypeOption> getPaymentTypeOptions() {
        return paymentTypeOptions;
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        this.paymentTypeOptions = paymentTypeOptions;
    }

    @Override
    public String toString() {
        return "LoanRepaymentTemplate{" +
                "loanId=" + loanId +
                ", type=" + type +
                ", date=" + date +
                ", currency=" + currency +
                ", amount=" + amount +
                ", principalPortion=" + principalPortion +
                ", interestPortion=" + interestPortion +
                ", feeChargesPortion=" + feeChargesPortion +
                ", penaltyChargesPortion=" + penaltyChargesPortion +
                ", paymentTypeOptions=" + paymentTypeOptions +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.loanId);
        dest.writeList(this.date);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.amount);
        dest.writeValue(this.principalPortion);
        dest.writeValue(this.interestPortion);
        dest.writeValue(this.feeChargesPortion);
        dest.writeValue(this.penaltyChargesPortion);
        dest.writeTypedList(this.paymentTypeOptions);
    }

    public LoanRepaymentTemplate() {
    }

    protected LoanRepaymentTemplate(Parcel in) {
        this.loanId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.date = new ArrayList<Integer>();
        in.readList(this.date, Integer.class.getClassLoader());
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.principalPortion = (Double) in.readValue(Double.class.getClassLoader());
        this.interestPortion = (Double) in.readValue(Double.class.getClassLoader());
        this.feeChargesPortion = (Double) in.readValue(Integer.class.getClassLoader());
        this.penaltyChargesPortion = (Double) in.readValue(Integer.class.getClassLoader());
        this.paymentTypeOptions = in.createTypedArrayList(PaymentTypeOption.CREATOR);
    }

    public static final Parcelable.Creator<LoanRepaymentTemplate> CREATOR =
            new Parcelable.Creator<LoanRepaymentTemplate>() {
        @Override
        public LoanRepaymentTemplate createFromParcel(Parcel source) {
            return new LoanRepaymentTemplate(source);
        }

        @Override
        public LoanRepaymentTemplate[] newArray(int size) {
            return new LoanRepaymentTemplate[size];
        }
    };
}
