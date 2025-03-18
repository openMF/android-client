/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.savings

import android.os.Parcelable
import com.mifos.core.entity.accounts.savings.Currency
import com.mifos.core.model.objects.account.saving.FieldOfficerOptions
import com.mifos.core.model.objects.commonfiles.InterestType
import com.mifos.core.model.objects.template.saving.AccountOptions
import com.mifos.room.entities.PaymentTypeOption
import kotlinx.parcelize.IgnoredOnParcel
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

    @IgnoredOnParcel
    var interestCompoundingPeriodType: InterestType? = null,

    @IgnoredOnParcel
    var interestPostingPeriodType: InterestType? = null,

    @IgnoredOnParcel
    var interestCalculationType: InterestType? = null,

    @IgnoredOnParcel
    var interestCalculationDaysInYearType: InterestType? = null,

    @IgnoredOnParcel
    var accountingRule: InterestType? = null,

    var currencyOptions: List<Currency>? = null,

    @IgnoredOnParcel
    var interestCompoundingPeriodTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    var interestPostingPeriodTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    var interestCalculationTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    var interestCalculationDaysInYearTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    var lockinPeriodFrequencyTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    var withdrawalFeeTypeOptions: List<InterestType>? = null,

    var paymentTypeOptions: List<PaymentTypeOption>? = null,

    @IgnoredOnParcel
    var accountingRuleOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    var liabilityAccountOptions: AccountOptions? = null,

    @IgnoredOnParcel
    var assetAccountOptions: List<AccountOptions>? = null,

    @IgnoredOnParcel
    var expenseAccountOptions: List<AccountOptions>? = null,

    @IgnoredOnParcel
    var incomeAccountOptions: List<AccountOptions>? = null,

    @IgnoredOnParcel
    var fieldOfficerOptions: List<FieldOfficerOptions>? = null,
) : Parcelable
