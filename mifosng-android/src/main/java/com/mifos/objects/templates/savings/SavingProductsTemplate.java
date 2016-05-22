package com.mifos.objects.templates.savings;

import com.mifos.objects.Currency;
import com.mifos.objects.InterestType;
import com.mifos.objects.PaymentTypeOption;

import java.util.List;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

/**
 * Created by rajan on 13/3/16.
 */
public class SavingProductsTemplate {

    private Currency currency;
    private InterestType interestCompoundingPeriodType;
    private InterestType interestPostingPeriodType;
    private InterestType interestCalculationType;
    private InterestType interestCalculationDaysInYearType;
    private InterestType accountingRule;
    private List<Currency> currencyOptions;
    private List<InterestType> interestCompoundingPeriodTypeOptions;
    private List<InterestType> interestPostingPeriodTypeOptions;
    private List<InterestType> interestCalculationTypeOptions;
    private List<InterestType> interestCalculationDaysInYearTypeOptions;
    private List<InterestType> lockinPeriodFrequencyTypeOptions;
    private List<InterestType> withdrawalFeeTypeOptions;
    private List<PaymentTypeOption> paymentTypeOptions;
    private List<InterestType> accountingRuleOptions;
    private AccountOptions liabilityAccountOptions;
    private List<AccountOptions> assetAccountOptions;
    private List<AccountOptions> expenseAccountOptions;
    private List<AccountOptions> incomeAccountOptions;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public InterestType getInterestCompoundingPeriodType() {
        return interestCompoundingPeriodType;
    }

    public void setInterestCompoundingPeriodType(InterestType interestCompoundingPeriodType) {
        this.interestCompoundingPeriodType = interestCompoundingPeriodType;
    }

    public InterestType getInterestPostingPeriodType() {
        return interestPostingPeriodType;
    }

    public void setInterestPostingPeriodType(InterestType interestPostingPeriodType) {
        this.interestPostingPeriodType = interestPostingPeriodType;
    }

    public InterestType getInterestCalculationType() {
        return interestCalculationType;
    }

    public void setInterestCalculationType(InterestType interestCalculationType) {
        this.interestCalculationType = interestCalculationType;
    }

    public InterestType getInterestCalculationDaysInYearType() {
        return interestCalculationDaysInYearType;
    }

    public void setInterestCalculationDaysInYearType(InterestType interestCalculationDaysInYearType) {
        this.interestCalculationDaysInYearType = interestCalculationDaysInYearType;
    }

    public InterestType getAccountingRule() {
        return accountingRule;
    }

    public void setAccountingRule(InterestType accountingRule) {
        this.accountingRule = accountingRule;
    }

    public List<Currency> getCurrencyOptions() {
        return currencyOptions;
    }

    public void setCurrencyOptions(List<Currency> currencyOptions) {
        this.currencyOptions = currencyOptions;
    }

    public List<InterestType> getInterestCompoundingPeriodTypeOptions() {
        return interestCompoundingPeriodTypeOptions;
    }

    public void setInterestCompoundingPeriodTypeOptions(List<InterestType> interestCompoundingPeriodTypeOptions) {
        this.interestCompoundingPeriodTypeOptions = interestCompoundingPeriodTypeOptions;
    }

    public List<InterestType> getInterestPostingPeriodTypeOptions() {
        return interestPostingPeriodTypeOptions;
    }

    public void setInterestPostingPeriodTypeOptions(List<InterestType> interestPostingPeriodTypeOptions) {
        this.interestPostingPeriodTypeOptions = interestPostingPeriodTypeOptions;
    }

    public List<InterestType> getInterestCalculationTypeOptions() {
        return interestCalculationTypeOptions;
    }

    public void setInterestCalculationTypeOptions(List<InterestType> interestCalculationTypeOptions) {
        this.interestCalculationTypeOptions = interestCalculationTypeOptions;
    }

    public List<InterestType> getInterestCalculationDaysInYearTypeOptions() {
        return interestCalculationDaysInYearTypeOptions;
    }

    public void setInterestCalculationDaysInYearTypeOptions(List<InterestType> interestCalculationDaysInYearTypeOptions) {
        this.interestCalculationDaysInYearTypeOptions = interestCalculationDaysInYearTypeOptions;
    }

    public List<InterestType> getLockinPeriodFrequencyTypeOptions() {
        return lockinPeriodFrequencyTypeOptions;
    }

    public void setLockinPeriodFrequencyTypeOptions(List<InterestType> lockinPeriodFrequencyTypeOptions) {
        this.lockinPeriodFrequencyTypeOptions = lockinPeriodFrequencyTypeOptions;
    }

    public List<InterestType> getAccountingRuleOptions() {
        return accountingRuleOptions;
    }

    public void setAccountingRuleOptions(List<InterestType> accountingRuleOptions) {
        this.accountingRuleOptions = accountingRuleOptions;
    }

    public List<PaymentTypeOption> getPaymentTypeOptions() {
        return paymentTypeOptions;
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        this.paymentTypeOptions = paymentTypeOptions;
    }

    public List<InterestType> getWithdrawalFeeTypeOptions() {
        return withdrawalFeeTypeOptions;
    }

    public void setWithdrawalFeeTypeOptions(List<InterestType> withdrawalFeeTypeOptions) {
        this.withdrawalFeeTypeOptions = withdrawalFeeTypeOptions;
    }

    public AccountOptions getLiabilityAccountOptions() {
        return liabilityAccountOptions;
    }

    public void setLiabilityAccountOptions(AccountOptions liabilityAccountOptions) {
        this.liabilityAccountOptions = liabilityAccountOptions;
    }

    public List<AccountOptions> getAssetAccountOptions() {
        return assetAccountOptions;
    }

    public void setAssetAccountOptions(List<AccountOptions> assetAccountOptions) {
        this.assetAccountOptions = assetAccountOptions;
    }

    public List<AccountOptions> getExpenseAccountOptions() {
        return expenseAccountOptions;
    }

    public void setExpenseAccountOptions(List<AccountOptions> expenseAccountOptions) {
        this.expenseAccountOptions = expenseAccountOptions;
    }

    public List<AccountOptions> getIncomeAccountOptions() {
        return incomeAccountOptions;
    }

    public void setIncomeAccountOptions(List<AccountOptions> incomeAccountOptions) {
        this.incomeAccountOptions = incomeAccountOptions;
    }

    @Override
    public String toString() {
        return "SavingProductsTemplate{" +
                "currency=" + currency +
                ", interestCompoundingPeriodType=" + interestCompoundingPeriodType +
                ", interestPostingPeriodType=" + interestPostingPeriodType +
                ", interestCalculationType=" + interestCalculationType +
                ", interestCalculationDaysInYearType=" + interestCalculationDaysInYearType +
                ", accountingRule=" + accountingRule +
                ", currencyOptions=" + currencyOptions +
                ", interestCompoundingPeriodTypeOptions=" + interestCompoundingPeriodTypeOptions +
                ", interestPostingPeriodTypeOptions=" + interestPostingPeriodTypeOptions +
                ", interestCalculationTypeOptions=" + interestCalculationTypeOptions +
                ", interestCalculationDaysInYearTypeOptions=" + interestCalculationDaysInYearTypeOptions +
                ", lockinPeriodFrequencyTypeOptions=" + lockinPeriodFrequencyTypeOptions +
                ", withdrawalFeeTypeOptions=" + withdrawalFeeTypeOptions +
                ", paymentTypeOptions=" + paymentTypeOptions +
                ", accountingRuleOptions=" + accountingRuleOptions +
                ", liabilityAccountOptions=" + liabilityAccountOptions +
                ", assetAccountOptions=" + assetAccountOptions +
                ", expenseAccountOptions=" + expenseAccountOptions +
                ", incomeAccountOptions=" + incomeAccountOptions +
                '}';
    }
}
