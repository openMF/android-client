package com.mifos.services.data;

/**
 * Created by nellyk on 2/10/2016.
 */
public class SavingsPayload {

    private Integer productId;
    private Integer clientId;
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

    public Integer getInterestCompoundingPeriodType() {
        return interestCompoundingPeriodType;
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


    public void setInterestCompoundingPeriodType(Integer interestCompoundingPeriodType) {
        this.interestCompoundingPeriodType = interestCompoundingPeriodType;
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
}
