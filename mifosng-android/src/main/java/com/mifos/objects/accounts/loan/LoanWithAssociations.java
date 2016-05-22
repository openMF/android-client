/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import com.mifos.objects.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class LoanWithAssociations {
    //TODO Fix OBJECT Typed Lists
    private Integer id;
    private String accountNo;
    private Status status;
    private Integer clientId;
    private String clientName;
    private Integer clientOfficeId;
    private Integer loanProductId;
    private String loanProductName;
    private String loanProductDescription;
    private Integer fundId;
    private String fundName;
    private Integer loanPurposeId;
    private String loanPurposeName;
    private Integer loanOfficerId;
    private String loanOfficerName;
    private LoanType loanType;
    private Currency currency;
    private Double principal;
    private Double approvedPrincipal;
    private Integer termFrequency;
    private TermPeriodFrequencyType termPeriodFrequencyType;
    private Integer numberOfRepayments;
    private Integer repaymentEvery;
    private RepaymentFrequencyType repaymentFrequencyType;
    private Double interestRatePerPeriod;
    private InterestRateFrequencyType interestRateFrequencyType;
    private Double annualInterestRate;
    private AmortizationType amortizationType;
    private InterestType interestType;
    private InterestCalculationPeriodType interestCalculationPeriodType;
    private Integer transactionProcessingStrategyId;
    private String transactionProcessingStrategyName;
    private Boolean syncDisbursementWithMeeting;
    private Timeline timeline;
    private Summary summary;
    private RepaymentSchedule repaymentSchedule;
    private List<Transaction> transactions = new ArrayList<Transaction>();
    private List<Object> disbursementDetails;
    private Double feeChargesAtDisbursementCharged;
    private Double totalOverpaid;
    private Integer loanCounter;
    private Integer loanProductCounter;
    private Boolean multiDisburseLoan;
    private Boolean canDisburse;
    private List<Object> emiAmountVariations;
    private Boolean inArrears;
    private Boolean isNPA;
    private List<Object> overdueCharges;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getClientOfficeId() {
        return clientOfficeId;
    }

    public void setClientOfficeId(Integer clientOfficeId) {
        this.clientOfficeId = clientOfficeId;
    }

    public Integer getLoanProductId() {
        return loanProductId;
    }

    public void setLoanProductId(Integer loanProductId) {
        this.loanProductId = loanProductId;
    }

    public String getLoanProductName() {
        return loanProductName;
    }

    public void setLoanProductName(String loanProductName) {
        this.loanProductName = loanProductName;
    }

    public String getLoanProductDescription() {
        return loanProductDescription;
    }

    public void setLoanProductDescription(String loanProductDescription) {
        this.loanProductDescription = loanProductDescription;
    }

    public Integer getFundId() {
        return fundId;
    }

    public void setFundId(Integer fundId) {
        this.fundId = fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public Integer getLoanPurposeId() {
        return loanPurposeId;
    }

    public void setLoanPurposeId(Integer loanPurposeId) {
        this.loanPurposeId = loanPurposeId;
    }

    public String getLoanPurposeName() {
        return loanPurposeName;
    }

    public void setLoanPurposeName(String loanPurposeName) {
        this.loanPurposeName = loanPurposeName;
    }

    public Integer getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(Integer loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getLoanOfficerName() {
        return loanOfficerName;
    }

    public void setLoanOfficerName(String loanOfficerName) {
        this.loanOfficerName = loanOfficerName;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getPrincipal() {
        return principal;
    }

    public void setPrincipal(Double principal) {
        this.principal = principal;
    }

    public Double getApprovedPrincipal() {
        return approvedPrincipal;
    }

    public void setApprovedPrincipal(Double approvedPrincipal) {
        this.approvedPrincipal = approvedPrincipal;
    }

    public Integer getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(Integer termFrequency) {
        this.termFrequency = termFrequency;
    }

    public TermPeriodFrequencyType getTermPeriodFrequencyType() {
        return termPeriodFrequencyType;
    }

    public void setTermPeriodFrequencyType(TermPeriodFrequencyType termPeriodFrequencyType) {
        this.termPeriodFrequencyType = termPeriodFrequencyType;
    }

    public Integer getNumberOfRepayments() {
        return numberOfRepayments;
    }

    public void setNumberOfRepayments(Integer numberOfRepayments) {
        this.numberOfRepayments = numberOfRepayments;
    }

    public Integer getRepaymentEvery() {
        return repaymentEvery;
    }

    public void setRepaymentEvery(Integer repaymentEvery) {
        this.repaymentEvery = repaymentEvery;
    }

    public RepaymentFrequencyType getRepaymentFrequencyType() {
        return repaymentFrequencyType;
    }

    public void setRepaymentFrequencyType(RepaymentFrequencyType repaymentFrequencyType) {
        this.repaymentFrequencyType = repaymentFrequencyType;
    }

    public Double getInterestRatePerPeriod() {
        return interestRatePerPeriod;
    }

    public void setInterestRatePerPeriod(Double interestRatePerPeriod) {
        this.interestRatePerPeriod = interestRatePerPeriod;
    }

    public InterestRateFrequencyType getInterestRateFrequencyType() {
        return interestRateFrequencyType;
    }

    public void setInterestRateFrequencyType(InterestRateFrequencyType interestRateFrequencyType) {
        this.interestRateFrequencyType = interestRateFrequencyType;
    }

    public Double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(Double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public AmortizationType getAmortizationType() {
        return amortizationType;
    }

    public void setAmortizationType(AmortizationType amortizationType) {
        this.amortizationType = amortizationType;
    }

    public InterestType getInterestType() {
        return interestType;
    }

    public void setInterestType(InterestType interestType) {
        this.interestType = interestType;
    }

    public InterestCalculationPeriodType getInterestCalculationPeriodType() {
        return interestCalculationPeriodType;
    }

    public void setInterestCalculationPeriodType(InterestCalculationPeriodType interestCalculationPeriodType) {
        this.interestCalculationPeriodType = interestCalculationPeriodType;
    }

    public Integer getTransactionProcessingStrategyId() {
        return transactionProcessingStrategyId;
    }

    public void setTransactionProcessingStrategyId(Integer transactionProcessingStrategyId) {
        this.transactionProcessingStrategyId = transactionProcessingStrategyId;
    }

    public String getTransactionProcessingStrategyName() {
        return transactionProcessingStrategyName;
    }

    public void setTransactionProcessingStrategyName(String transactionProcessingStrategyName) {
        this.transactionProcessingStrategyName = transactionProcessingStrategyName;
    }

    public Boolean getSyncDisbursementWithMeeting() {
        return syncDisbursementWithMeeting;
    }

    public void setSyncDisbursementWithMeeting(Boolean syncDisbursementWithMeeting) {
        this.syncDisbursementWithMeeting = syncDisbursementWithMeeting;
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public RepaymentSchedule getRepaymentSchedule() {
        return repaymentSchedule;
    }

    public void setRepaymentSchedule(RepaymentSchedule repaymentSchedule) {
        this.repaymentSchedule = repaymentSchedule;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Object> getDisbursementDetails() {
        return disbursementDetails;
    }

    public void setDisbursementDetails(List<Object> disbursementDetails) {
        this.disbursementDetails = disbursementDetails;
    }

    public Double getFeeChargesAtDisbursementCharged() {
        return feeChargesAtDisbursementCharged;
    }

    public void setFeeChargesAtDisbursementCharged(Double feeChargesAtDisbursementCharged) {
        this.feeChargesAtDisbursementCharged = feeChargesAtDisbursementCharged;
    }

    public Double getTotalOverpaid() {
        return totalOverpaid;
    }

    public void setTotalOverpaid(Double totalOverpaid) {
        this.totalOverpaid = totalOverpaid;
    }

    public Integer getLoanCounter() {
        return loanCounter;
    }

    public void setLoanCounter(Integer loanCounter) {
        this.loanCounter = loanCounter;
    }

    public Integer getLoanProductCounter() {
        return loanProductCounter;
    }

    public void setLoanProductCounter(Integer loanProductCounter) {
        this.loanProductCounter = loanProductCounter;
    }

    public Boolean getMultiDisburseLoan() {
        return multiDisburseLoan;
    }

    public void setMultiDisburseLoan(Boolean multiDisburseLoan) {
        this.multiDisburseLoan = multiDisburseLoan;
    }

    public Boolean getCanDisburse() {
        return canDisburse;
    }

    public void setCanDisburse(Boolean canDisburse) {
        this.canDisburse = canDisburse;
    }

    public List<Object> getEmiAmountVariations() {
        return emiAmountVariations;
    }

    public void setEmiAmountVariations(List<Object> emiAmountVariations) {
        this.emiAmountVariations = emiAmountVariations;
    }

    public Boolean getInArrears() {
        return inArrears;
    }

    public void setInArrears(Boolean inArrears) {
        this.inArrears = inArrears;
    }

    public Boolean getIsNPA() {
        return isNPA;
    }

    public void setIsNPA(Boolean isNPA) {
        this.isNPA = isNPA;
    }

    public List<Object> getOverdueCharges() {
        return overdueCharges;
    }

    public void setOverdueCharges(List<Object> overdueCharges) {
        this.overdueCharges = overdueCharges;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
