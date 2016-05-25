/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Type {

    private Integer id;
    private String code;
    private String value;
    private Boolean disbursement;
    private Boolean repaymentAtDisbursement;
    private Boolean repayment;
    private Boolean contra;
    private Boolean waiveInterest;
    private Boolean waiveCharges;
    private Boolean accrual;
    private Boolean writeOff;
    private Boolean recoveryRepayment;
    private Boolean initiateTransfer;
    private Boolean approveTransfer;
    private Boolean withdrawTransfer;
    private Boolean rejectTransfer;
    private Boolean chargePayment;
    private Boolean refund;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
