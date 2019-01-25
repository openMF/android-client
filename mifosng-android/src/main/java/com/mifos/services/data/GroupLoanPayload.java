/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.services.data;

/**
 * Created by nellyk on 2/20/2016.
 */
public class GroupLoanPayload {

    boolean allowPartialPeriodInterestCalcualtion;
    int amortizationType;
    int groupId;

    String dateFormat;
    String expectedDisbursementDate;
    int interestCalculationPeriodType;
    Double interestRatePerPeriod;
    int interestType;
    int loanTermFrequency;
    int loanTermFrequencyType;
    Integer repaymentFrequencyDayOfWeekType;
    Integer repaymentFrequencyNthDayType;
    String loanType;
    String locale;
    String numberOfRepayments;
    String principal;
    int productId;
    String repaymentEvery;
    int repaymentFrequencyType;
    String submittedOnDate;
    int transactionProcessingStrategyId;
    int loanPurposeId;
    Integer linkAccountId;

    public Integer getLinkAccountId() {
        return linkAccountId;
    }

    public void setLinkAccountId(Integer linkAccountId) {
        this.linkAccountId = linkAccountId;
    }

    public Integer getRepaymentFrequencyDayOfWeekType() {
        return repaymentFrequencyDayOfWeekType;
    }

    public void setRepaymentFrequencyDayOfWeekType(Integer repaymentFrequencyDayOfWeekType) {
        this.repaymentFrequencyDayOfWeekType = repaymentFrequencyDayOfWeekType;
    }

    public Integer getRepaymentFrequencyNthDayType() {
        return repaymentFrequencyNthDayType;
    }

    public void setRepaymentFrequencyNthDayType(Integer repaymentFrequencyNthDayType) {
        this.repaymentFrequencyNthDayType = repaymentFrequencyNthDayType;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isAllowPartialPeriodInterestCalcualtion() {
        return allowPartialPeriodInterestCalcualtion;
    }

    public void setAllowPartialPeriodInterestCalcualtion(
            boolean allowPartialPeriodInterestCalcualtion) {
        this.allowPartialPeriodInterestCalcualtion = allowPartialPeriodInterestCalcualtion;
    }

    public int getAmortizationType() {
        return amortizationType;
    }

    public void setAmortizationType(int amortizationType) {
        this.amortizationType = amortizationType;
    }


    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

   /* public String getDisbursementData() {
        return disbursementData;
    }

    public void setDisbursementData(String disbursementData) {
        this.disbursementData = disbursementData;
    }*/

    public String getExpectedDisbursementDate() {
        return expectedDisbursementDate;
    }

    public void setExpectedDisbursementDate(String expectedDisbursementDate) {
        this.expectedDisbursementDate = expectedDisbursementDate;
    }

    public int getInterestCalculationPeriodType() {
        return interestCalculationPeriodType;
    }

    public void setInterestCalculationPeriodType(int interestCalculationPeriodType) {
        this.interestCalculationPeriodType = interestCalculationPeriodType;
    }

    public Double getInterestRatePerPeriod() {
        return interestRatePerPeriod;
    }

    public void setInterestRatePerPeriod(Double interestRatePerPeriod) {
        this.interestRatePerPeriod = interestRatePerPeriod;
    }

    public int getInterestType() {
        return interestType;
    }

    public void setInterestType(int interestType) {
        this.interestType = interestType;
    }

    public int getLoanPurposeId() {
        return loanPurposeId;
    }

    public void setLoanPurposeId(int loanPurposeId) {
        this.loanPurposeId = loanPurposeId;
    }

    public int getLoanTermFrequency() {
        return loanTermFrequency;
    }

    public void setLoanTermFrequency(int loanTermFrequency) {
        this.loanTermFrequency = loanTermFrequency;
    }

    public int getLoanTermFrequencyType() {
        return loanTermFrequencyType;
    }

    public void setLoanTermFrequencyType(int loanTermFrequencyType) {
        this.loanTermFrequencyType = loanTermFrequencyType;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getNumberOfRepayments() {
        return numberOfRepayments;
    }

    public void setNumberOfRepayments(String numberOfRepayments) {
        this.numberOfRepayments = numberOfRepayments;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getRepaymentEvery() {
        return repaymentEvery;
    }

    public void setRepaymentEvery(String repaymentEvery) {
        this.repaymentEvery = repaymentEvery;
    }

    public int getRepaymentFrequencyType() {
        return repaymentFrequencyType;
    }

    public void setRepaymentFrequencyType(int repaymentFrequencyType) {
        this.repaymentFrequencyType = repaymentFrequencyType;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public int getTransactionProcessingStrategyId() {
        return transactionProcessingStrategyId;
    }

    public void setTransactionProcessingStrategyId(int transactionProcessingStrategyId) {
        this.transactionProcessingStrategyId = transactionProcessingStrategyId;
    }
}
