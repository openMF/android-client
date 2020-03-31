package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.objects.PaymentTypeOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajan Maurya on 14/02/17.
 */

public class LoanTransactionTemplate implements Parcelable {

    @SerializedName("type")
    Type type;

    @SerializedName("date")
    List<Integer> date = new ArrayList<>();

    @SerializedName("amount")
    Double amount;

    @SerializedName("manuallyReversed")
    Boolean manuallyReversed;

    @SerializedName("possibleNextRepaymentDate")
    List<Integer> possibleNextRepaymentDate = new ArrayList<>();

    @SerializedName("paymentTypeOptions")
    List<PaymentTypeOption> paymentTypeOptions = new ArrayList<>();

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getManuallyReversed() {
        return manuallyReversed;
    }

    public void setManuallyReversed(Boolean manuallyReversed) {
        this.manuallyReversed = manuallyReversed;
    }

    public List<Integer> getPossibleNextRepaymentDate() {
        return possibleNextRepaymentDate;
    }

    public void setPossibleNextRepaymentDate(List<Integer> possibleNextRepaymentDate) {
        this.possibleNextRepaymentDate = possibleNextRepaymentDate;
    }

    public List<PaymentTypeOption> getPaymentTypeOptions() {
        return paymentTypeOptions;
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        this.paymentTypeOptions = paymentTypeOptions;
    }


    public LoanTransactionTemplate() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.type, flags);
        dest.writeList(this.date);
        dest.writeValue(this.amount);
        dest.writeValue(this.manuallyReversed);
        dest.writeList(this.possibleNextRepaymentDate);
        dest.writeTypedList(this.paymentTypeOptions);
    }

    protected LoanTransactionTemplate(Parcel in) {
        this.type = in.readParcelable(Type.class.getClassLoader());
        this.date = new ArrayList<Integer>();
        in.readList(this.date, Integer.class.getClassLoader());
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.manuallyReversed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.possibleNextRepaymentDate = new ArrayList<Integer>();
        in.readList(this.possibleNextRepaymentDate, Integer.class.getClassLoader());
        this.paymentTypeOptions = in.createTypedArrayList(PaymentTypeOption.CREATOR);
    }

    public static final Creator<LoanTransactionTemplate> CREATOR = new
            Creator<LoanTransactionTemplate>() {
        @Override
        public LoanTransactionTemplate createFromParcel(Parcel source) {
            return new LoanTransactionTemplate(source);
        }

        @Override
        public LoanTransactionTemplate[] newArray(int size) {
            return new LoanTransactionTemplate[size];
        }
    };
}
