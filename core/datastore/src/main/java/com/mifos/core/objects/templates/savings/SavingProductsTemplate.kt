package com.mifos.core.objects.templates.savings

import android.os.Parcelable
import com.mifos.core.objects.accounts.savings.Currency
import com.mifos.core.objects.accounts.savings.FieldOfficerOptions
import com.mifos.core.objects.common.InterestType
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
/**
 * Created by rajan on 13/3/16.
 */
@Parcelize
class SavingProductsTemplate(
    var currency: Currency? = null,

    var interestCompoundingPeriodType: InterestType? = null,

    var interestPostingPeriodType: InterestType? = null,

    var interestCalculationType: InterestType? = null,

    var interestCalculationDaysInYearType: InterestType? = null,

    var accountingRule: InterestType? = null,

    var currencyOptions: List<Currency>? = null,

    var interestCompoundingPeriodTypeOptions: List<InterestType>? = null,

    var interestPostingPeriodTypeOptions: List<InterestType>? = null,

    var interestCalculationTypeOptions: List<InterestType>? = null,

    var interestCalculationDaysInYearTypeOptions: List<InterestType>? = null,

    var lockinPeriodFrequencyTypeOptions: List<InterestType>? = null,

    var withdrawalFeeTypeOptions: List<InterestType>? = null,

    var paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>? = null,

    var accountingRuleOptions: List<InterestType>? = null,

    var liabilityAccountOptions: AccountOptions? = null,

    var assetAccountOptions: List<AccountOptions>? = null,

    var expenseAccountOptions: List<AccountOptions>? = null,

    var incomeAccountOptions: List<AccountOptions>? = null,

    var fieldOfficerOptions: List<FieldOfficerOptions>? = null,
) : Parcelable