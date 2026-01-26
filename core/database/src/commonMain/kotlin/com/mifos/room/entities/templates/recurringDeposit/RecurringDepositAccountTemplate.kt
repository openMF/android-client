/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.recurringDeposit

import com.mifos.core.model.objects.template.recurring.AccountChart
import com.mifos.core.model.objects.template.recurring.Currency
import com.mifos.core.model.objects.template.recurring.FieldOfficerOption
import com.mifos.core.model.objects.template.recurring.Timeline
import com.mifos.core.model.objects.template.recurring.WithdrawalFeeTypeOption
import com.mifos.core.model.objects.template.recurring.charge.ChargeOption
import com.mifos.core.model.objects.template.recurring.deposit.DepositType
import com.mifos.core.model.objects.template.recurring.deposit.InMultiplesOfDepositTermType
import com.mifos.core.model.objects.template.recurring.deposit.MaxDepositTermType
import com.mifos.core.model.objects.template.recurring.deposit.MinDepositTermType
import com.mifos.core.model.objects.template.recurring.interest.InterestCalculationDaysInYearType
import com.mifos.core.model.objects.template.recurring.interest.InterestCalculationDaysInYearTypeOption
import com.mifos.core.model.objects.template.recurring.interest.InterestCalculationType
import com.mifos.core.model.objects.template.recurring.interest.InterestCalculationTypeOption
import com.mifos.core.model.objects.template.recurring.interest.InterestCompoundingPeriodType
import com.mifos.core.model.objects.template.recurring.interest.InterestCompoundingPeriodTypeOption
import com.mifos.core.model.objects.template.recurring.interest.InterestPostingPeriodType
import com.mifos.core.model.objects.template.recurring.interest.InterestPostingPeriodTypeOption
import com.mifos.core.model.objects.template.recurring.interest.PreClosurePenalInterestOnTypeOption
import com.mifos.core.model.objects.template.recurring.period.LockinPeriodFrequencyType
import com.mifos.core.model.objects.template.recurring.period.LockinPeriodFrequencyTypeOption
import com.mifos.core.model.objects.template.recurring.period.PeriodFrequencyTypeOption
import com.mifos.core.model.objects.template.recurring.period.ProductOption
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RecurringDepositAccountTemplate(
    @IgnoredOnParcel val accountChart: AccountChart? = null,
    val adjustAdvanceTowardsFuturePayments: Boolean? = null,
    val allowWithdrawal: Boolean? = null,
    @IgnoredOnParcel val chargeOptions: List<ChargeOption>? = null,
    val clientId: Int? = null,
    val clientName: String? = null,
    @IgnoredOnParcel val currency: Currency? = null,
    val depositProductId: Int? = null,
    val depositProductName: String? = null,
    @IgnoredOnParcel val depositType: DepositType? = null,
    @IgnoredOnParcel val fieldOfficerOptions: List<FieldOfficerOption>? = null,
    @IgnoredOnParcel val inMultiplesOfDepositTermType: InMultiplesOfDepositTermType? = null,
    @IgnoredOnParcel val interestCalculationDaysInYearType: InterestCalculationDaysInYearType? = null,
    @IgnoredOnParcel val interestCalculationDaysInYearTypeOptions: List<InterestCalculationDaysInYearTypeOption>? = null,
    @IgnoredOnParcel val interestCalculationType: InterestCalculationType? = null,
    @IgnoredOnParcel val interestCalculationTypeOptions: List<InterestCalculationTypeOption>? = null,
    @IgnoredOnParcel val interestCompoundingPeriodType: InterestCompoundingPeriodType? = null,
    @IgnoredOnParcel val interestCompoundingPeriodTypeOptions: List<InterestCompoundingPeriodTypeOption>? = null,
    @IgnoredOnParcel val interestPostingPeriodType: InterestPostingPeriodType? = null,
    @IgnoredOnParcel val interestPostingPeriodTypeOptions: List<InterestPostingPeriodTypeOption>? = null,
    val isCalendarInherited: Boolean? = null,
    val isMandatoryDeposit: Boolean? = null,
    val lockinPeriodFrequency: Int? = null,
    @IgnoredOnParcel val lockinPeriodFrequencyType: LockinPeriodFrequencyType? = null,
    @IgnoredOnParcel val lockinPeriodFrequencyTypeOptions: List<LockinPeriodFrequencyTypeOption>? = null,
    @IgnoredOnParcel val maxDepositTermType: MaxDepositTermType? = null,
    val minDepositTerm: Int? = null,
    @IgnoredOnParcel val minDepositTermType: MinDepositTermType? = null,
    val nominalAnnualInterestRate: Double? = null,
    @IgnoredOnParcel val periodFrequencyTypeOptions: List<PeriodFrequencyTypeOption>? = null,
    val preClosurePenalApplicable: Boolean? = null,
    @IgnoredOnParcel val preClosurePenalInterestOnTypeOptions: List<PreClosurePenalInterestOnTypeOption>? = null,
    @IgnoredOnParcel val productOptions: List<ProductOption>? = null,
    @IgnoredOnParcel val timeline: Timeline? = null,
    val withHoldTax: Boolean? = null,
    val withdrawalFeeForTransfers: Boolean? = null,
    @IgnoredOnParcel val withdrawalFeeTypeOptions: List<WithdrawalFeeTypeOption>? = null,
) : Parcelable
