package com.mifos.objects.client;

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
public class Charges extends MifosBaseModel {

    @PrimaryKey
    Integer id;

    @Column
    Integer clientId;

    @Column
    Integer loanId;

    @Column
    Integer chargeId;

    @Column
    String name;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    ChargeTimeType chargeTimeType;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    ClientDate chargeDueDate;

    List<Integer> dueDate = new ArrayList<Integer>();

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    ChargeCalculationType chargeCalculationType;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Currency currency;

    @Column
    Double amount;

    @Column
    Double amountPaid;

    @Column
    Double amountWaived;

    @Column
    Double amountWrittenOff;

    @Column
    Double amountOutstanding;

    @Column
    Boolean penalty;

    @Column
    Boolean isActive;

    @Column
    Boolean isPaid;

    @Column
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

}

