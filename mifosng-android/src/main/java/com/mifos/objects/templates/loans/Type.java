/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.templates.loans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Type implements Parcelable {

    @Expose
    private Integer id;
    @Expose
    private String code;
    @Expose
    private String value;
    @Expose
    private Boolean disbursement;
    @Expose
    private Boolean repaymentAtDisbursement;
    @Expose
    private Boolean repayment;
    @Expose
    private Boolean contra;
    @Expose
    private Boolean waiveInterest;
    @Expose
    private Boolean waiveCharges;
    @Expose
    private Boolean accrual;
    @Expose
    private Boolean writeOff;
    @Expose
    private Boolean recoveryRepayment;
    @Expose
    private Boolean initiateTransfer;
    @Expose
    private Boolean approveTransfer;
    @Expose
    private Boolean withdrawTransfer;
    @Expose
    private Boolean rejectTransfer;
    @Expose
    private Boolean chargePayment;
    @Expose
    private Boolean refund;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getDisbursement() {
        return disbursement;
    }

    public void setDisbursement(Boolean disbursement) {
        this.disbursement = disbursement;
    }

    public Boolean getRepaymentAtDisbursement() {
        return repaymentAtDisbursement;
    }

    public void setRepaymentAtDisbursement(Boolean repaymentAtDisbursement) {
        this.repaymentAtDisbursement = repaymentAtDisbursement;
    }

    public Boolean getRepayment() {
        return repayment;
    }

    public void setRepayment(Boolean repayment) {
        this.repayment = repayment;
    }

    public Boolean getContra() {
        return contra;
    }

    public void setContra(Boolean contra) {
        this.contra = contra;
    }

    public Boolean getWaiveInterest() {
        return waiveInterest;
    }

    public void setWaiveInterest(Boolean waiveInterest) {
        this.waiveInterest = waiveInterest;
    }

    public Boolean getWaiveCharges() {
        return waiveCharges;
    }

    public void setWaiveCharges(Boolean waiveCharges) {
        this.waiveCharges = waiveCharges;
    }

    public Boolean getAccrual() {
        return accrual;
    }

    public void setAccrual(Boolean accrual) {
        this.accrual = accrual;
    }

    public Boolean getWriteOff() {
        return writeOff;
    }

    public void setWriteOff(Boolean writeOff) {
        this.writeOff = writeOff;
    }

    public Boolean getRecoveryRepayment() {
        return recoveryRepayment;
    }

    public void setRecoveryRepayment(Boolean recoveryRepayment) {
        this.recoveryRepayment = recoveryRepayment;
    }

    public Boolean getInitiateTransfer() {
        return initiateTransfer;
    }

    public void setInitiateTransfer(Boolean initiateTransfer) {
        this.initiateTransfer = initiateTransfer;
    }

    public Boolean getApproveTransfer() {
        return approveTransfer;
    }

    public void setApproveTransfer(Boolean approveTransfer) {
        this.approveTransfer = approveTransfer;
    }

    public Boolean getWithdrawTransfer() {
        return withdrawTransfer;
    }

    public void setWithdrawTransfer(Boolean withdrawTransfer) {
        this.withdrawTransfer = withdrawTransfer;
    }

    public Boolean getRejectTransfer() {
        return rejectTransfer;
    }

    public void setRejectTransfer(Boolean rejectTransfer) {
        this.rejectTransfer = rejectTransfer;
    }

    public Boolean getChargePayment() {
        return chargePayment;
    }

    public void setChargePayment(Boolean chargePayment) {
        this.chargePayment = chargePayment;
    }

    public Boolean getRefund() {
        return refund;
    }

    public void setRefund(Boolean refund) {
        this.refund = refund;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
        dest.writeValue(this.disbursement);
        dest.writeValue(this.repaymentAtDisbursement);
        dest.writeValue(this.repayment);
        dest.writeValue(this.contra);
        dest.writeValue(this.waiveInterest);
        dest.writeValue(this.waiveCharges);
        dest.writeValue(this.accrual);
        dest.writeValue(this.writeOff);
        dest.writeValue(this.recoveryRepayment);
        dest.writeValue(this.initiateTransfer);
        dest.writeValue(this.approveTransfer);
        dest.writeValue(this.withdrawTransfer);
        dest.writeValue(this.rejectTransfer);
        dest.writeValue(this.chargePayment);
        dest.writeValue(this.refund);
    }

    public Type() {
    }

    protected Type(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
        this.disbursement = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.repaymentAtDisbursement = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.repayment = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.contra = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.waiveInterest = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.waiveCharges = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.accrual = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.writeOff = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.recoveryRepayment = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.initiateTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.approveTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.withdrawTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.rejectTransfer = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.chargePayment = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.refund = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Type> CREATOR = new Parcelable.Creator<Type>() {
        @Override
        public Type createFromParcel(Parcel source) {
            return new Type(source);
        }

        @Override
        public Type[] newArray(int size) {
            return new Type[size];
        }
    };
}
