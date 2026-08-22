/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.savings

import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.core.model.objects.account.saving.FieldOfficerOptions
import com.mifos.core.model.objects.commonfiles.InterestType
import com.mifos.core.model.objects.template.client.ChargeOptions
import com.mifos.core.model.objects.template.saving.AccountOptions
import com.mifos.room.entities.PaymentTypeOptionEntity
import kotlinx.serialization.Serializable

/**
 * Created by rajan on 13/3/16.
 */
@Serializable
class SavingProductsTemplate(
    val currency: Currency? = null,

    val interestCompoundingPeriodType: InterestType? = null,

    val interestPostingPeriodType: InterestType? = null,

    val interestCalculationType: InterestType? = null,

    val interestCalculationDaysInYearType: InterestType? = null,

    val accountingRule: InterestType? = null,

    val currencyOptions: List<Currency>? = null,

    val interestCompoundingPeriodTypeOptions: List<InterestType>? = null,

    val interestPostingPeriodTypeOptions: List<InterestType>? = null,

    val interestCalculationTypeOptions: List<InterestType>? = null,

    val interestCalculationDaysInYearTypeOptions: List<InterestType>? = null,

    val lockinPeriodFrequencyTypeOptions: List<InterestType>? = null,

    val withdrawalFeeTypeOptions: List<InterestType>? = null,

    val paymentTypeOptions: List<PaymentTypeOptionEntity>? = null,

    val accountingRuleOptions: List<InterestType>? = null,

    val liabilityAccountOptions: AccountOptions? = null,

    val assetAccountOptions: List<AccountOptions>? = null,

    val expenseAccountOptions: List<AccountOptions>? = null,

    val incomeAccountOptions: List<AccountOptions>? = null,

    val fieldOfficerOptions: List<FieldOfficerOptions>? = null,

    val chargeOptions: List<ChargeOptions>? = null,
)
