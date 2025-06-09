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

import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.core.model.objects.account.saving.FieldOfficerOptions
import com.mifos.core.model.objects.commonfiles.InterestType
import com.mifos.core.model.objects.template.saving.AccountOptions
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.PaymentTypeOptionEntity
import kotlinx.serialization.Serializable

/**
 * Created by rajan on 13/3/16.
 */
@Parcelize
@Serializable
class SavingProductsTemplate(
    val currency: Currency? = null,

    @IgnoredOnParcel
    val interestCompoundingPeriodType: InterestType? = null,

    @IgnoredOnParcel
    val interestPostingPeriodType: InterestType? = null,

    @IgnoredOnParcel
    val interestCalculationType: InterestType? = null,

    @IgnoredOnParcel
    val interestCalculationDaysInYearType: InterestType? = null,

    @IgnoredOnParcel
    val accountingRule: InterestType? = null,

    @IgnoredOnParcel
    val currencyOptions: List<Currency>? = null,

    @IgnoredOnParcel
    val interestCompoundingPeriodTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    val interestPostingPeriodTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    val interestCalculationTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    val interestCalculationDaysInYearTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    val lockinPeriodFrequencyTypeOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    val withdrawalFeeTypeOptions: List<InterestType>? = null,

    val paymentTypeOptions: List<PaymentTypeOptionEntity>? = null,

    @IgnoredOnParcel
    val accountingRuleOptions: List<InterestType>? = null,

    @IgnoredOnParcel
    val liabilityAccountOptions: AccountOptions? = null,

    @IgnoredOnParcel
    val assetAccountOptions: List<AccountOptions>? = null,

    @IgnoredOnParcel
    val expenseAccountOptions: List<AccountOptions>? = null,

    @IgnoredOnParcel
    val incomeAccountOptions: List<AccountOptions>? = null,

    @IgnoredOnParcel
    val fieldOfficerOptions: List<FieldOfficerOptions>? = null,
) : Parcelable
