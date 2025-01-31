/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.utils.typeconverters

import androidx.room.TypeConverter
import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.model.objects.account.loan.RepaymentFrequencyType
import com.mifos.core.objects.account.loan.AmortizationType
import com.mifos.core.objects.account.loan.InterestCalculationPeriodType
import com.mifos.core.objects.account.loan.InterestRateFrequencyType
import com.mifos.core.objects.account.loan.InterestType
import com.mifos.core.objects.account.loan.RepaymentSchedule
import com.mifos.core.objects.account.loan.TermPeriodFrequencyType
import com.mifos.core.objects.account.loan.Transaction
import com.mifos.core.objects.template.loan.Currency
import com.mifos.core.objects.template.loan.Type
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.loans.ActualDisbursementDate
import com.mifos.room.entities.accounts.loans.LoanType
import com.mifos.room.entities.accounts.loans.Status
import com.mifos.room.entities.accounts.loans.Summary
import com.mifos.room.entities.accounts.loans.Timeline
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by Pronay Sarker on 24/01/2025 (3:07 PM)
 */
// TODO remove unused converters
class LoanTypeConverters {

    @TypeConverter
    fun fromStatus(status: Status?): String? {
        return status?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toStatus(json: String?): Status? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromLoanType(type: LoanType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toLoanType(json: String?): LoanType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromCurrency(currency: Currency?): String? {
        return currency?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCurrency(json: String?): Currency? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromTermPeriodFrequencyType(type: TermPeriodFrequencyType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toTermPeriodFrequencyType(json: String?): TermPeriodFrequencyType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromRepaymentFrequencyType(type: RepaymentFrequencyType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toRepaymentFrequencyType(json: String?): RepaymentFrequencyType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromInterestRateFrequencyType(type: InterestRateFrequencyType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun fromSummary(summary: Summary?): String? {
        return summary?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSummary(json: String?): Summary? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun toInterestRateFrequencyType(json: String?): InterestRateFrequencyType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromAmortizationType(type: AmortizationType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toAmortizationType(json: String?): AmortizationType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromInterestType(type: InterestType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestType(json: String?): InterestType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromInterestCalculationPeriodType(type: InterestCalculationPeriodType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestCalculationPeriodType(json: String?): InterestCalculationPeriodType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromTimeline(timeline: Timeline?): String? {
        return timeline?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toTimeline(json: String?): Timeline? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromRepaymentSchedule(schedule: RepaymentSchedule?): String? {
        return schedule?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toRepaymentSchedule(json: String?): RepaymentSchedule? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromTransactionList(transactions: List<Transaction>?): String? {
        return transactions?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toTransactionList(json: String?): List<Transaction>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromType(type: Type?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toType(json: String?): Type? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListInt(date: List<Int?>?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListInt(json: String?): List<Int?>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromActualDisbursementDate(date: ActualDisbursementDate?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toActualDisbursementDate(json: String?): ActualDisbursementDate? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromMutableListInt(date: MutableList<Int>?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toMutableListInt(json: String?): MutableList<Int>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromMutableListPaymentTypeOptions(type: MutableList<PaymentTypeOption>?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toMutableListPaymentTypeOptions(json: String?): MutableList<PaymentTypeOption>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromPeriodList(json: String?): List<Period>? {
        return json?.let { Json.decodeFromString<List<Period>>(it) }
    }

    @TypeConverter
    fun toPeriodList(periodList: List<Period>?): String? {
        return periodList?.let { Json.encodeToString(it) }
    }
}
