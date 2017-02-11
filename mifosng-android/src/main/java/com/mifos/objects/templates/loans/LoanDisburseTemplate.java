package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mayankjindal on 14/12/16.
 */

public class LoanDisburseTemplate implements Parcelable {

    @SerializedName("type")
    Type type;

    @SerializedName("date")
    List<Integer> date;

    @SerializedName("amount")
    Integer amount;

    @SerializedName("manuallyReversed")
    Boolean manuallyReversed;

    @SerializedName("possibleNextRepaymentDate")
    List<Integer> possibleNextRepaymentDate;

    @SerializedName("paymentTypeOptions")
    List<PaymentTypeOptions> paymentTypeOptions;

    public List<PaymentTypeOptions> getPaymentTypeOptions() {
        return paymentTypeOptions;
    }

    public void setPaymentTypeOptions(List<PaymentTypeOptions> paymentTypeOptions) {
        this.paymentTypeOptions = paymentTypeOptions;
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.date);
        dest.writeValue(this.amount);
        dest.writeValue(this.manuallyReversed);
        dest.writeList(this.possibleNextRepaymentDate);
        dest.writeTypedList(this.paymentTypeOptions);
    }

    public LoanDisburseTemplate() {
    }

    protected LoanDisburseTemplate(Parcel in) {
        in.readList(this.date, Integer.class.getClassLoader());
        this.amount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.manuallyReversed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        in.readList(this.possibleNextRepaymentDate, Integer.class.getClassLoader());
        this.paymentTypeOptions = in.createTypedArrayList(PaymentTypeOptions.CREATOR);
    }

    public static final Parcelable.Creator<LoanDisburseTemplate> CREATOR =
            new Parcelable.Creator<LoanDisburseTemplate>() {
                @Override
                public LoanDisburseTemplate createFromParcel(Parcel source) {
                    return new LoanDisburseTemplate(source);
                }

                @Override
                public LoanDisburseTemplate[] newArray(int size) {
                    return new LoanDisburseTemplate[size];
                }
            };
}
