package com.mifos.objects.templates.savings;

import com.google.gson.annotations.SerializedName;
import com.mifos.objects.Currency;
import com.mifos.objects.InterestType;
import com.mifos.objects.PaymentTypeOption;
import java.util.List;


/**
 * Created by rajan on 13/3/16.
 */
public class SavingProductsTemplate {

	private Currency currency;
	private InterestType interestCompoundingPeriodType ;
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





}
