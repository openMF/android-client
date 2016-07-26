/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.loan;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.accounts.savings.Currency;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;

@Table(database = MifosDatabase.class)
@ModelContainer
public class LoanWithAssociations extends MifosBaseModel implements Parcelable {
    //TODO Fix OBJECT Typed Lists

    @PrimaryKey
    Integer id;

    @Column
    String accountNo;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Status status;

    Integer clientId;

    @Column
    String clientName;

    Integer clientOfficeId;

    Integer loanProductId;

    @Column
    String loanProductName;

    String loanProductDescription;

    Integer fundId;

    String fundName;

    Integer loanPurposeId;

    String loanPurposeName;

    Integer loanOfficerId;

    @Column
    String loanOfficerName;

    LoanType loanType;

    Currency currency;

    Double principal;

    Double approvedPrincipal;

    Integer termFrequency;

    TermPeriodFrequencyType termPeriodFrequencyType;

    Integer numberOfRepayments;

    Integer repaymentEvery;

    RepaymentFrequencyType repaymentFrequencyType;

    Double interestRatePerPeriod;

    InterestRateFrequencyType interestRateFrequencyType;

    Double annualInterestRate;

    AmortizationType amortizationType;

    InterestType interestType;

    InterestCalculationPeriodType interestCalculationPeriodType;

    Integer transactionProcessingStrategyId;

    String transactionProcessingStrategyName;

    Boolean syncDisbursementWithMeeting;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Timeline timeline;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Summary summary;

    RepaymentSchedule repaymentSchedule;

    List<Transaction> transactions = new ArrayList<Transaction>();

    List<Object> disbursementDetails;

    Double feeChargesAtDisbursementCharged;

    Double totalOverpaid;

    Integer loanCounter;

    Integer loanProductCounter;

    Boolean multiDisburseLoan;

    Boolean canDisburse;

    List<Object> emiAmountVariations;

    Boolean inArrears;

    Boolean isNPA;

    List<Object> overdueCharges;

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

    public void setInterestCalculationPeriodType(InterestCalculationPeriodType
                                                         interestCalculationPeriodType) {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.accountNo);
        dest.writeParcelable(this.status, flags);
        dest.writeValue(this.clientId);
        dest.writeString(this.clientName);
        dest.writeValue(this.clientOfficeId);
        dest.writeValue(this.loanProductId);
        dest.writeString(this.loanProductName);
        dest.writeString(this.loanProductDescription);
        dest.writeValue(this.fundId);
        dest.writeString(this.fundName);
        dest.writeValue(this.loanPurposeId);
        dest.writeString(this.loanPurposeName);
        dest.writeValue(this.loanOfficerId);
        dest.writeString(this.loanOfficerName);
        dest.writeParcelable(this.loanType, flags);
        dest.writeParcelable(this.currency, flags);
        dest.writeValue(this.principal);
        dest.writeValue(this.approvedPrincipal);
        dest.writeValue(this.termFrequency);
        dest.writeParcelable(this.termPeriodFrequencyType, flags);
        dest.writeValue(this.numberOfRepayments);
        dest.writeValue(this.repaymentEvery);
        dest.writeParcelable(this.repaymentFrequencyType, flags);
        dest.writeValue(this.interestRatePerPeriod);
        dest.writeParcelable(this.interestRateFrequencyType, flags);
        dest.writeValue(this.annualInterestRate);
        dest.writeParcelable(this.amortizationType, flags);
        dest.writeParcelable(this.interestType, flags);
        dest.writeParcelable(this.interestCalculationPeriodType, flags);
        dest.writeValue(this.transactionProcessingStrategyId);
        dest.writeString(this.transactionProcessingStrategyName);
        dest.writeValue(this.syncDisbursementWithMeeting);
        dest.writeParcelable(this.timeline, flags);
        dest.writeParcelable(this.summary, flags);
        dest.writeParcelable(this.repaymentSchedule, flags);
        dest.writeList(this.transactions);
        dest.writeList(this.disbursementDetails);
        dest.writeValue(this.feeChargesAtDisbursementCharged);
        dest.writeValue(this.totalOverpaid);
        dest.writeValue(this.loanCounter);
        dest.writeValue(this.loanProductCounter);
        dest.writeValue(this.multiDisburseLoan);
        dest.writeValue(this.canDisburse);
        dest.writeList(this.emiAmountVariations);
        dest.writeValue(this.inArrears);
        dest.writeValue(this.isNPA);
        dest.writeList(this.overdueCharges);
    }

    public LoanWithAssociations() {
    }

    protected LoanWithAssociations(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.accountNo = in.readString();
        this.status = in.readParcelable(Status.class.getClassLoader());
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.clientName = in.readString();
        this.clientOfficeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.loanProductId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.loanProductName = in.readString();
        this.loanProductDescription = in.readString();
        this.fundId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.fundName = in.readString();
        this.loanPurposeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.loanPurposeName = in.readString();
        this.loanOfficerId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.loanOfficerName = in.readString();
        this.loanType = in.readParcelable(LoanType.class.getClassLoader());
        this.currency = in.readParcelable(Currency.class.getClassLoader());
        this.principal = (Double) in.readValue(Double.class.getClassLoader());
        this.approvedPrincipal = (Double) in.readValue(Double.class.getClassLoader());
        this.termFrequency = (Integer) in.readValue(Integer.class.getClassLoader());
        this.termPeriodFrequencyType = in.readParcelable(TermPeriodFrequencyType.class
                .getClassLoader());
        this.numberOfRepayments = (Integer) in.readValue(Integer.class.getClassLoader());
        this.repaymentEvery = (Integer) in.readValue(Integer.class.getClassLoader());
        this.repaymentFrequencyType = in.readParcelable(RepaymentFrequencyType.class
                .getClassLoader());
        this.interestRatePerPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.interestRateFrequencyType = in.readParcelable(InterestRateFrequencyType.class
                .getClassLoader());
        this.annualInterestRate = (Double) in.readValue(Double.class.getClassLoader());
        this.amortizationType = in.readParcelable(AmortizationType.class.getClassLoader());
        this.interestType = in.readParcelable(InterestType.class.getClassLoader());
        this.interestCalculationPeriodType = in.readParcelable(InterestCalculationPeriodType
                .class.getClassLoader());
        this.transactionProcessingStrategyId = (Integer) in.readValue(Integer.class
                .getClassLoader());
        this.transactionProcessingStrategyName = in.readString();
        this.syncDisbursementWithMeeting = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.timeline = in.readParcelable(Timeline.class.getClassLoader());
        this.summary = in.readParcelable(Summary.class.getClassLoader());
        this.repaymentSchedule = in.readParcelable(RepaymentSchedule.class.getClassLoader());
        this.transactions = new ArrayList<Transaction>();
        in.readList(this.transactions, Transaction.class.getClassLoader());
        this.disbursementDetails = new ArrayList<Object>();
        in.readList(this.disbursementDetails, Object.class.getClassLoader());
        this.feeChargesAtDisbursementCharged = (Double) in.readValue(Double.class.getClassLoader());
        this.totalOverpaid = (Double) in.readValue(Double.class.getClassLoader());
        this.loanCounter = (Integer) in.readValue(Integer.class.getClassLoader());
        this.loanProductCounter = (Integer) in.readValue(Integer.class.getClassLoader());
        this.multiDisburseLoan = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.canDisburse = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.emiAmountVariations = new ArrayList<Object>();
        in.readList(this.emiAmountVariations, Object.class.getClassLoader());
        this.inArrears = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isNPA = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.overdueCharges = new ArrayList<Object>();
        in.readList(this.overdueCharges, Object.class.getClassLoader());
    }

    public static final Parcelable.Creator<LoanWithAssociations> CREATOR =
            new Parcelable.Creator<LoanWithAssociations>() {
        @Override
        public LoanWithAssociations createFromParcel(Parcel source) {
            return new LoanWithAssociations(source);
        }

        @Override
        public LoanWithAssociations[] newArray(int size) {
            return new LoanWithAssociations[size];
        }
    };
}
