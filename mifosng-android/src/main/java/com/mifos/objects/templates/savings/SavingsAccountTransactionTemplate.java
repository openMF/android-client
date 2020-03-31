/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.templates.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.PaymentTypeOption;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 12/06/14.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class SavingsAccountTransactionTemplate extends MifosBaseModel implements Parcelable {

    @SerializedName("accountId")
    @PrimaryKey
    Integer accountId;

    @SerializedName("accountNo")
    @Column
    String accountNo;

    @SerializedName("date")
    List<Integer> date = new ArrayList<>();

    @SerializedName("reversed")
    @Column
    Boolean reversed;

    @SerializedName("paymentTypeOptions")
    List<PaymentTypeOption> paymentTypeOptions = new ArrayList<>();

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public List<Integer> getDate() {
        return date;
    }

    public void setDate(List<Integer> date) {
        this.date = date;
    }

    public Boolean getReversed() {
        return reversed;
    }

    public void setReversed(Boolean reversed) {
        this.reversed = reversed;
    }

    public List<PaymentTypeOption> getPaymentTypeOptions() {
        return paymentTypeOptions;
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        this.paymentTypeOptions = paymentTypeOptions;
    }

    @Override
    public String toString() {
        return "SavingsAccountTransactionTemplate{" +
                "accountId=" + accountId +
                ", accountNo='" + accountNo + '\'' +
                ", date=" + date +
                ", reversed=" + reversed +
                ", paymentTypeOptions=" + paymentTypeOptions +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.accountId);
        dest.writeString(this.accountNo);
        dest.writeList(this.date);
        dest.writeValue(this.reversed);
        dest.writeTypedList(this.paymentTypeOptions);
    }

    public SavingsAccountTransactionTemplate() {
    }

    protected SavingsAccountTransactionTemplate(Parcel in) {
        this.accountId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.date = new ArrayList<Integer>();
        in.readList(this.date, Integer.class.getClassLoader());
        this.reversed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.paymentTypeOptions = in.createTypedArrayList(PaymentTypeOption.CREATOR);
    }

    public static final Parcelable.Creator<SavingsAccountTransactionTemplate> CREATOR =
            new Parcelable.Creator<SavingsAccountTransactionTemplate>() {
        @Override
        public SavingsAccountTransactionTemplate createFromParcel(Parcel source) {
            return new SavingsAccountTransactionTemplate(source);
        }

        @Override
        public SavingsAccountTransactionTemplate[] newArray(int size) {
            return new SavingsAccountTransactionTemplate[size];
        }
    };
}
