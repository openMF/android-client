/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.templates.savings;

import com.mifos.objects.PaymentTypeOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ishankhanna on 12/06/14.
 */
public class SavingsAccountTransactionTemplate {

    private Integer accountId;
    private String accountNo;
    private List<Integer> date = new ArrayList<Integer>();
    private Boolean reversed;
    private List<PaymentTypeOption> paymentTypeOptions = new ArrayList<PaymentTypeOption>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "SavingsAccountTransactionTemplate{" +
                "accountId=" + accountId +
                ", accountNo='" + accountNo + '\'' +
                ", date=" + date +
                ", reversed=" + reversed +
                ", paymentTypeOptions=" + paymentTypeOptions +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
