package com.mifos.services.data;

/**
 * Created by nellyk on 2/10/2016.
 */
public class SavingsPayload {

    private Integer productId;
    private Integer clientId;
    private Integer groupId;
    private Integer fieldOfficerId;
    private String locale;
    private String dateFormat;
    private String submittedOnDate;
    private String externalId;
    private String nominalAnnualInterestRate;
    private Integer interestCompoundingPeriodType;
    private Integer interestCalculationType;
    private Integer interestCalculationDaysInYearType;
    private Integer interestPostingPeriodType;
    private Boolean allowOverdraft;
    private Boolean enforceMinRequiredBalance;
    private String minRequiredOpeningBalance;
    private String nominalAnnualInterestRateOverdraft;
    private String overdraftLimit;
    private String minOverdraftForInterestCalculation;

    public Integer getInterestCompoundingPeriodType() {
        return interestCompoundingPeriodType;
    }

    public void setInterestCompoundingPeriodType(Integer interestCompoundingPeriodType) {
        this.interestCompoundingPeriodType = interestCompoundingPeriodType;
    }

    public String getNominalAnnualInterestRate() {
        return nominalAnnualInterestRate;
    }

    public void setNominalAnnualInterestRate(String nominalAnnualInterestRate) {
        this.nominalAnnualInterestRate = nominalAnnualInterestRate;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getFieldOfficerId() {
        return fieldOfficerId;
    }

    public void setFieldOfficerId(Integer fieldOfficerId) {
        this.fieldOfficerId = fieldOfficerId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(String submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getInterestCalculationType() {
        return interestCalculationType;
    }

    public void setInterestCalculationType(Integer interestCalculationType) {
        this.interestCalculationType = interestCalculationType;
    }

    public Integer getInterestCalculationDaysInYearType() {
        return interestCalculationDaysInYearType;
    }

    public void setInterestCalculationDaysInYearType(Integer interestCalculationDaysInYearType) {
        this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
    }

    public Integer getInterestPostingPeriodType() {
        return interestPostingPeriodType;
    }

    public void setInterestPostingPeriodType(Integer interestPostingPeriodType) {
        this.interestPostingPeriodType = interestPostingPeriodType;
    }

    public Boolean getAllowOverdraft() {
        return allowOverdraft;
    }

    public void setAllowOverdraft(Boolean allowOverdraft) {
        this.allowOverdraft = allowOverdraft;
    }

    public Boolean getEnforceMinRequiredBalance() {
        return enforceMinRequiredBalance;
    }

    public void setEnforceMinRequiredBalance(Boolean enforceMinRequiredBalance) {
        this.enforceMinRequiredBalance = enforceMinRequiredBalance;
    }

    public String getMinRequiredOpeningBalance() {
        return minRequiredOpeningBalance;
    }

    public void setMinRequiredOpeningBalance(String minRequiredOpeningBalance) {
        this.minRequiredOpeningBalance = minRequiredOpeningBalance;
    }

    public String getNominalAnnualInterestRateOverdraft() {
        return nominalAnnualInterestRateOverdraft;
    }

    public void setNominalAnnualInterestRateOverdraft(String nominalAnnualInterestRateOverdraft) {
        this.nominalAnnualInterestRateOverdraft = nominalAnnualInterestRateOverdraft;
    }

    public String getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(String overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public String getMinOverdraftForInterestCalculation() {
        return minOverdraftForInterestCalculation;
    }

    public void setMinOverdraftForInterestCalculation(String minOverdraftForInterestCalculation) {
        this.minOverdraftForInterestCalculation = minOverdraftForInterestCalculation;
    }
}
