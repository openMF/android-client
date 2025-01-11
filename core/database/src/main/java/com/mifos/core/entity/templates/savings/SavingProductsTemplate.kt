/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.templates.savings

import android.os.Parcelable
import com.mifos.core.entity.accounts.savings.Currency
import com.mifos.core.objects.account.saving.FieldOfficerOptions
import com.mifos.core.objects.commonfiles.InterestType
import com.mifos.core.objects.template.saving.AccountOptions
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

    var paymentTypeOptions: List<com.mifos.core.entity.PaymentTypeOption>? = null,

    var accountingRuleOptions: List<InterestType>? = null,

    var liabilityAccountOptions: AccountOptions? = null,

    var assetAccountOptions: List<AccountOptions>? = null,

    var expenseAccountOptions: List<AccountOptions>? = null,

    var incomeAccountOptions: List<AccountOptions>? = null,

    var fieldOfficerOptions: List<FieldOfficerOptions>? = null,
) : Parcelable
