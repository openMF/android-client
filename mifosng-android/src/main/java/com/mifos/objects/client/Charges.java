package com.mifos.objects.client;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nellyk on 2/15/2016.
 */

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class Charges extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    @SerializedName("id")
    Integer id;

    @Column
    @SerializedName("clientId")
    Integer clientId;

    @Column
    @SerializedName("loanId")
    Integer loanId;

    @Column
    @SerializedName("chargeId")
    Integer chargeId;

    @Column
    @SerializedName("name")
    String name;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("chargeTimeType")
    ChargeTimeType chargeTimeType;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("chargeDueDate")
    ClientDate chargeDueDate;

    List<Integer> dueDate = new ArrayList<Integer>();

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getWaived() {
        return isWaived;
    }

    public void setWaived(Boolean waived) {
        isWaived = waived;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("chargeCalculationType")
    ChargeCalculationType chargeCalculationType;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("currency")
    Currency currency;

    @Column
    @SerializedName("amount")
    Double amount;

    @Column
    @SerializedName("amountPaid")
    Double amountPaid;

    @Column
    @SerializedName("amountWaived")
    Double amountWaived;

    @Column
    @SerializedName("amountWrittenOff")
    Double amountWrittenOff;

    @Column
    @SerializedName("amountOutstanding")
    Double amountOutstanding;

    @Column
    @SerializedName("penalty")
    Boolean penalty;

    @Column
    @SerializedName("isActive")
    Boolean isActive;

    @Column
    @SerializedName("isPaid")
    Boolean isPaid;

    @Column
    @SerializedName("isWaived")
    Boolean isWaived;

    public ClientDate getChargeDueDate() {
        return chargeDueDate;
    }

    public void setChargeDueDate(ClientDate chargeDueDate) {
        this.chargeDueDate = chargeDueDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getChargeId() {
        return chargeId;
    }

    public void setChargeId(Integer chargeId) {
        this.chargeId = chargeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getDueDate() {
        return dueDate;
    }

    public void setDueDate(List<Integer> dueDate) {
        this.dueDate = dueDate;
    }

    public ChargeTimeType getChargeTimeType() {
        return chargeTimeType;
    }

    public void setChargeTimeType(ChargeTimeType chargeTimeType) {
        this.chargeTimeType = chargeTimeType;
    }

    public ChargeCalculationType getChargeCalculationType() {
        return chargeCalculationType;
    }

    public void setChargeCalculationType(ChargeCalculationType chargeCalculationType) {
        this.chargeCalculationType = chargeCalculationType;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
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

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Double getAmountWaived() {
        return amountWaived;
    }

    public void setAmountWaived(Double amountWaived) {
        this.amountWaived = amountWaived;
    }

    public Double getAmountWrittenOff() {
        return amountWrittenOff;
    }

    public void setAmountWrittenOff(Double amountWrittenOff) {
        this.amountWrittenOff = amountWrittenOff;
    }

    public Double getAmountOutstanding() {
        return amountOutstanding;
    }

    public void setAmountOutstanding(Double amountOutstanding) {
        this.amountOutstanding = amountOutstanding;
    }

    public Boolean getPenalty() {
        return penalty;
    }

    public void setPenalty(Boolean penalty) {
        this.penalty = penalty;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Boolean getIsWaived() {
        return isWaived;
    }

    public void setIsWaived(Boolean isWaived) {
        this.isWaived = isWaived;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.clientId);
        dest.writeValue(this.loanId);
        dest.writeValue(this.chargeId);
        dest.writeString(this.name);
        dest.writeParcelable(this.chargeTimeType, flags);
        dest.writeParcelable(this.chargeDueDate, flags);
        dest.writeParcelable(this.chargeCalculationType, flags);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.amount);
        dest.writeValue(this.amountPaid);
        dest.writeValue(this.amountWaived);
        dest.writeValue(this.amountWrittenOff);
        dest.writeValue(this.amountOutstanding);
        dest.writeValue(this.penalty);
        dest.writeValue(this.isActive);
        dest.writeValue(this.isPaid);
        dest.writeValue(this.isWaived);
    }

    public Charges() {
    }

    protected Charges(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.loanId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.chargeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.chargeTimeType = in.readParcelable(ChargeTimeType.class.getClassLoader());
        this.chargeDueDate = in.readParcelable(ClientDate.class.getClassLoader());
        this.chargeCalculationType = in.readParcelable(ChargeTimeType.class.getClassLoader());
        this.currency = in.readParcelable(ChargeTimeType.class.getClassLoader());
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.amountPaid = (Double) in.readValue(Double.class.getClassLoader());
        this.amountWaived = (Double) in.readValue(Double.class.getClassLoader());
        this.amountWrittenOff = (Double) in.readValue(Double.class.getClassLoader());
        this.amountOutstanding = (Double) in.readValue(Double.class.getClassLoader());
        this.penalty = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isPaid = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isWaived = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Charges> CREATOR =
            new Parcelable.Creator<Charges>() {
                @Override
                public Charges createFromParcel(Parcel source) {
                    return new Charges(source);
                }

                @Override
                public Charges[] newArray(int size) {
                    return new Charges[size];
                }
            };
}

