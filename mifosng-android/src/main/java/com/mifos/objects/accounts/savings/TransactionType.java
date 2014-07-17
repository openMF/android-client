
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class TransactionType {

    private Integer id;
    private String code;
    private String value;
    private Boolean deposit;
    private Boolean withdrawal;
    private Boolean interestPosting;
    private Boolean feeDeduction;
    private Boolean initiateTransfer;
    private Boolean approveTransfer;
    private Boolean withdrawTransfer;
    private Boolean rejectTransfer;
    private Boolean overdraftInterest;
    private Boolean writtenoff;
    private Boolean overdraftFee;
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

    public Boolean getDeposit() {
        return deposit;
    }

    public void setDeposit(Boolean deposit) {
        this.deposit = deposit;
    }

    public Boolean getWithdrawal() {
        return withdrawal;
    }

    public void setWithdrawal(Boolean withdrawal) {
        this.withdrawal = withdrawal;
    }

    public Boolean getInterestPosting() {
        return interestPosting;
    }

    public void setInterestPosting(Boolean interestPosting) {
        this.interestPosting = interestPosting;
    }

    public Boolean getFeeDeduction() {
        return feeDeduction;
    }

    public void setFeeDeduction(Boolean feeDeduction) {
        this.feeDeduction = feeDeduction;
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

    public Boolean getOverdraftInterest() {
        return overdraftInterest;
    }

    public void setOverdraftInterest(Boolean overdraftInterest) {
        this.overdraftInterest = overdraftInterest;
    }

    public Boolean getWrittenoff() {
        return writtenoff;
    }

    public void setWrittenoff(Boolean writtenoff) {
        this.writtenoff = writtenoff;
    }

    public Boolean getOverdraftFee() {
        return overdraftFee;
    }

    public void setOverdraftFee(Boolean overdraftFee) {
        this.overdraftFee = overdraftFee;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
