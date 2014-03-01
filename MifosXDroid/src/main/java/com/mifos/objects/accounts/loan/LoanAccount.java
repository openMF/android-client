
package com.mifos.objects.accounts.loan;



import java.util.HashMap;
import java.util.Map;

public class LoanAccount {

    private Integer id;
    private String accountNo;
    private String externalId;
    private Integer productId;
    private String productName;
    private Status status;
    private LoanType loanType;
    private Integer loanCycle;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LoanAccount withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public LoanAccount withAccountNo(String accountNo) {
        this.accountNo = accountNo;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public LoanAccount withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public LoanAccount withProductId(Integer productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LoanAccount withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LoanAccount withStatus(Status status) {
        this.status = status;
        return this;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public LoanAccount withLoanType(LoanType loanType) {
        this.loanType = loanType;
        return this;
    }

    public Integer getLoanCycle() {
        return loanCycle;
    }

    public void setLoanCycle(Integer loanCycle) {
        this.loanCycle = loanCycle;
    }

    public LoanAccount withLoanCycle(Integer loanCycle) {
        this.loanCycle = loanCycle;
        return this;
    }

    @Override
    public String toString() {
        return "LoanAccount{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", externalId='" + externalId + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", status=" + status +
                ", loanType=" + loanType +
                ", loanCycle=" + loanCycle +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
