
package com.mifos.objects.templates.loans;

import com.google.gson.annotations.Expose;

public class Type {

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

}
