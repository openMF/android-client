/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.services.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.noncore.DataTablePayload;

import java.util.ArrayList;

/**
 * Created by nellyk on 2/20/2016.
 */
public class LoansPayload implements Parcelable {

    boolean allowPartialPeriodInterestCalcualtion;
    int amortizationType;
    int clientId;
    String dateFormat;
    String expectedDisbursementDate;
    int interestCalculationPeriodType;
    Double interestRatePerPeriod;
    int interestType;
    int loanTermFrequency;
    int loanTermFrequencyType;
    String loanType;
    String locale;
    String numberOfRepayments;
    String principal;
    int productId;
    String repaymentEvery;
    int repaymentFrequencyType;
    Integer repaymentFrequencyDayOfWeekType;
    Integer repaymentFrequencyNthDayType;
    String submittedOnDate;
    int transactionProcessingStrategyId;
    int loanPurposeId;
    int loanOfficerId;
    int fundId;
    Integer linkAccountId;
    ArrayList<DataTablePayload> datatables;

    public ArrayList<DataTablePayload> getDataTables() {
        return datatables;
    }

    public void setDataTables(ArrayList<DataTablePayload> dataTablePayloads) {
        this.datatables = dataTablePayloads;
    }

    public Integer getLinkAccountId() {
        return linkAccountId;
    }

    public void setLinkAccountId(Integer linkAccountId) {
        this.linkAccountId = linkAccountId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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

    public int getFundId() {
        return fundId;
    }

    public void setFundId(int fundId) {
        this.fundId = fundId;
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

    public int getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(int loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
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


    public LoansPayload() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.allowPartialPeriodInterestCalcualtion ? (byte) 1 : (byte) 0);
        dest.writeInt(this.amortizationType);
        dest.writeInt(this.clientId);
        dest.writeString(this.dateFormat);
        dest.writeString(this.expectedDisbursementDate);
        dest.writeInt(this.interestCalculationPeriodType);
        dest.writeValue(this.interestRatePerPeriod);
        dest.writeInt(this.interestType);
        dest.writeInt(this.loanTermFrequency);
        dest.writeInt(this.loanTermFrequencyType);
        dest.writeString(this.loanType);
        dest.writeString(this.locale);
        dest.writeString(this.numberOfRepayments);
        dest.writeString(this.principal);
        dest.writeInt(this.productId);
        dest.writeString(this.repaymentEvery);
        dest.writeInt(this.repaymentFrequencyType);
        dest.writeValue(this.repaymentFrequencyDayOfWeekType);
        dest.writeValue(this.repaymentFrequencyNthDayType);
        dest.writeString(this.submittedOnDate);
        dest.writeInt(this.transactionProcessingStrategyId);
        dest.writeInt(this.loanPurposeId);
        dest.writeInt(this.loanOfficerId);
        dest.writeInt(this.fundId);
        dest.writeValue(this.linkAccountId);
        dest.writeTypedList(this.datatables);
    }

    protected LoansPayload(Parcel in) {
        this.allowPartialPeriodInterestCalcualtion = in.readByte() != 0;
        this.amortizationType = in.readInt();
        this.clientId = in.readInt();
        this.dateFormat = in.readString();
        this.expectedDisbursementDate = in.readString();
        this.interestCalculationPeriodType = in.readInt();
        this.interestRatePerPeriod = (Double) in.readValue(Double.class.getClassLoader());
        this.interestType = in.readInt();
        this.loanTermFrequency = in.readInt();
        this.loanTermFrequencyType = in.readInt();
        this.loanType = in.readString();
        this.locale = in.readString();
        this.numberOfRepayments = in.readString();
        this.principal = in.readString();
        this.productId = in.readInt();
        this.repaymentEvery = in.readString();
        this.repaymentFrequencyType = in.readInt();
        this.repaymentFrequencyDayOfWeekType = (Integer) in.readValue(
                Integer.class.getClassLoader());
        this.repaymentFrequencyNthDayType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.submittedOnDate = in.readString();
        this.transactionProcessingStrategyId = in.readInt();
        this.loanPurposeId = in.readInt();
        this.loanOfficerId = in.readInt();
        this.fundId = in.readInt();
        this.linkAccountId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.datatables = in.createTypedArrayList(DataTablePayload.CREATOR);
    }

    public static final Parcelable.Creator<LoansPayload> CREATOR =
            new Parcelable.Creator<LoansPayload>() {
        @Override
        public LoansPayload createFromParcel(Parcel source) {
            return new LoansPayload(source);
        }

        @Override
        public LoansPayload[] newArray(int size) {
            return new LoansPayload[size];
        }
    };
}
