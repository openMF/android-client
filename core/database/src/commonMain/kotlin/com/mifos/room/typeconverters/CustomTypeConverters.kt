/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.typeconverters

import com.mifos.core.model.objects.Changes
import com.mifos.core.model.objects.account.loan.AmortizationType
import com.mifos.core.model.objects.account.loan.Currency
import com.mifos.core.model.objects.account.loan.InterestCalculationPeriodType
import com.mifos.core.model.objects.account.loan.InterestRateFrequencyType
import com.mifos.core.model.objects.account.loan.InterestType
import com.mifos.core.model.objects.account.loan.Period
import com.mifos.core.model.objects.account.loan.RepaymentFrequencyType
import com.mifos.core.model.objects.account.loan.RepaymentSchedule
import com.mifos.core.model.objects.account.loan.TermPeriodFrequencyType
import com.mifos.core.model.objects.account.loan.Transaction
import com.mifos.core.model.objects.account.saving.InterestCalculationDaysInYearType
import com.mifos.core.model.objects.account.saving.InterestCalculationType
import com.mifos.core.model.objects.account.saving.InterestCompoundingPeriodType
import com.mifos.core.model.objects.account.saving.InterestPostingPeriodType
import com.mifos.core.model.objects.account.saving.LockinPeriodFrequencyType
import com.mifos.core.model.objects.clients.Address
import com.mifos.room.savings.entity.PaymentTypeOptionEntity
import com.mifos.core.model.objects.timeline.Timeline
import com.mifos.room.loan.entity.ActualDisbursementDateEntity
import com.mifos.room.loan.entity.LoanStatusEntity
import com.mifos.room.loan.entity.LoanTimelineEntity
import com.mifos.room.loan.entity.LoanTypeEntity
import com.mifos.room.loan.entity.LoansAccountSummaryEntity
import com.mifos.room.charge.entity.SavingsCharge
import com.mifos.room.savings.entity.SavingAccountCurrencyEntity
import com.mifos.room.savings.entity.SavingAccountDepositTypeEntity
import com.mifos.room.savings.entity.SavingAccountDepositTypeEntity.ServerTypes
import com.mifos.room.savings.entity.SavingsAccountStatusEntity
import com.mifos.room.savings.entity.SavingsAccountSummaryEntity
import com.mifos.room.savings.entity.SavingsAccountTransactionEntity
import com.mifos.room.savings.entity.SavingsTransactionDateEntity
import com.mifos.room.savings.entity.SavingsTransactionTypeEntity
import com.mifos.room.charge.entity.ChargeCalculationTypeEntity
import com.mifos.room.charge.entity.ChargeTimeTypeEntity
import com.mifos.room.charge.entity.ClientChargeCurrencyEntity
import com.mifos.room.client.entity.ClientClassificationEntity
import com.mifos.room.client.entity.ClientDateEntity
import com.mifos.room.client.entity.ClientGenderEntity
import com.mifos.room.client.entity.ClientStatusEntity
import com.mifos.room.client.entity.ClientTypeEntity
import com.mifos.room.center.entity.CenterDateEntity
import com.mifos.room.center.entity.CenterEntity
import com.mifos.room.group.entity.GroupDateEntity
import com.mifos.room.group.entity.GroupEntity
import com.mifos.room.datatable.entity.ColumnHeader
import com.mifos.room.datatable.entity.ColumnValue
import com.mifos.room.datatable.entity.DataTableEntity
import com.mifos.room.datatable.entity.DataTablePayload
import com.mifos.room.office.entity.OfficeOpeningDateEntity
import com.mifos.room.survey.entity.ComponentDatasEntity
import com.mifos.room.survey.entity.QuestionDatasEntity
import com.mifos.room.survey.entity.ResponseDatasEntity
import com.mifos.room.client.entity.InterestTypeEntity
import com.mifos.room.office.entity.OfficeOptionsEntity
import com.mifos.room.client.entity.OptionsEntity
import com.mifos.room.savings.entity.SavingProductOptionsEntity
import com.mifos.room.staff.entity.StaffOptionsEntity
import com.mifos.room.loan.entity.LoanType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import template.core.base.database.TypeConverter

@Suppress("TooManyFunctions")
class CustomTypeConverters {

    @TypeConverter
    fun fromCurrency(type: Currency?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCurrency(json: String?): Currency? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromCenterDate(centerDate: CenterDateEntity?): String? {
        return centerDate?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCenterDate(json: String?): CenterDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromDepositType(type: SavingAccountDepositTypeEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toDepositType(json: String?): SavingAccountDepositTypeEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromSavingAccountStatus(type: SavingsAccountStatusEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSavingAccountStatus(json: String?): SavingsAccountStatusEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromSavingAccountSummary(type: SavingsAccountSummaryEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSavingAccountSummary(json: String?): SavingsAccountSummaryEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromGroupDate(groupDate: GroupDateEntity?): String? {
        return groupDate?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGroupDate(json: String?): GroupDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromChargeTimeType(type: ChargeTimeTypeEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toChargeTimeType(json: String?): ChargeTimeTypeEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromClientDate(date: ClientDateEntity?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientDate(json: String?): ClientDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromDataTable(date: DataTableEntity?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toDataTable(json: String?): DataTableEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromChargeCalculationType(type: ChargeCalculationTypeEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toChargeCalculationType(json: String?): ChargeCalculationTypeEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromClientChargeCurrency(currency: ClientChargeCurrencyEntity?): String? {
        return currency?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientChargeCurrency(json: String?): ClientChargeCurrencyEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromClientStatus(status: ClientStatusEntity?): String? {
        return status?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientStatus(json: String?): ClientStatusEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromClientGenderEntity(gender: ClientGenderEntity?): String? {
        return gender?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientGenderEntity(json: String?): ClientGenderEntity? {
        return json?.let { Json.decodeFromString<ClientGenderEntity>(it) }
    }

    @TypeConverter
    fun fromClientTypeEntity(clientType: ClientTypeEntity?): String? {
        return clientType?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientTypeEntity(json: String?): ClientTypeEntity? {
        return json?.let { Json.decodeFromString<ClientTypeEntity>(it) }
    }

    @TypeConverter
    fun fromClientClassificationEntity(clientClassification: ClientClassificationEntity?): String? {
        return clientClassification?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientClassificationEntity(json: String?): ClientClassificationEntity? {
        return json?.let { Json.decodeFromString<ClientClassificationEntity>(it) }
    }

    @TypeConverter
    fun fromOfficeOpeningDate(status: OfficeOpeningDateEntity?): String? {
        return status?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toOfficeOpeningDate(json: String?): OfficeOpeningDateEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromGroupActivationDateListInt(date: List<Int>?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toGroupActivationDateListInt(json: String?): List<Int>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListGroup(date: List<GroupEntity>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListGroup(json: String): List<GroupEntity> {
        return json.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListOfficeOptions(date: List<OfficeOptionsEntity>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListOfficeOptions(json: String): List<OfficeOptionsEntity> {
        return json.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromListStaffOptions(date: List<StaffOptionsEntity>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListStaffOptions(json: String): List<StaffOptionsEntity> {
        return json.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromListDataTable(date: List<DataTableEntity>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListDataTable(json: String): List<DataTableEntity> {
        return json.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromListOptions(date: List<OptionsEntity>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListOptions(json: String): List<OptionsEntity> {
        return json.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromListInterestType(date: List<InterestTypeEntity>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListInterestType(json: String): List<InterestTypeEntity> {
        return json.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListSavingProductOptions(date: List<SavingProductOptionsEntity>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListSavingProductOptions(json: String): List<SavingProductOptionsEntity> {
        return json.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListColumnValue(date: List<ColumnValue>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListColumnValue(json: String?): List<ColumnValue> {
        return json?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromListColumnHeader(date: List<ColumnHeader>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListColumnHeader(json: String?): List<ColumnHeader> {
        return json?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromListDataTablePayload(date: List<DataTablePayload>): String {
        return date.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListDataTablePayload(json: String?): List<DataTablePayload> {
        return json?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromMap(map: Map<String, Any>?): String? {
        return map?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toMap(json: String?): Map<String, Any>? {
        return json?.let {
            Json.decodeFromString<JsonObject>(it)
                .mapValues { entry -> entry.value }
        }
    }

    @TypeConverter
    fun fromListToString(dueDate: List<Int>): String {
        return Json.encodeToString(dueDate)
    }

    @TypeConverter
    fun fromStringToList(dueDateString: String): List<Int> {
        return Json.decodeFromString(dueDateString)
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
    fun fromCenterList(centers: List<CenterEntity?>): String {
        return centers.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toCenterList(json: String): List<CenterEntity?> {
        return json.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListOfTransactions(list: List<SavingsAccountTransactionEntity>?): String? {
        return list?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListOfTransactions(json: String?): List<SavingsAccountTransactionEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromListOfCharges(list: List<SavingsCharge?>?): String? {
        return list?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListOfCharges(json: String?): List<SavingsCharge?>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromStatus(status: LoanStatusEntity?): String? {
        return status?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toStatus(json: String?): LoanStatusEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromLoanType(type: LoanTypeEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toLoanType(json: String?): LoanTypeEntity? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromSavingAccountCurrency(currency: SavingAccountCurrencyEntity?): String? {
        return currency?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSavingAccountCurrency(json: String?): SavingAccountCurrencyEntity? {
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
    fun toInterestRateFrequencyType(json: String?): InterestRateFrequencyType? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromSummary(summary: LoansAccountSummaryEntity?): String? {
        return summary?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSummary(json: String?): LoansAccountSummaryEntity? {
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
    fun fromLoanTimeline(timeline: LoanTimelineEntity?): String? {
        return timeline?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toLoanTimeline(json: String?): LoanTimelineEntity? {
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
    fun fromType(type: LoanType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toType(json: String?): LoanType? {
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
    fun fromActualDisbursementDate(date: ActualDisbursementDateEntity?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toActualDisbursementDate(json: String?): ActualDisbursementDateEntity? {
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
    fun fromListPaymentTypeOptions(type: List<PaymentTypeOptionEntity>): String {
        return type.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toListPaymentTypeOptions(json: String): List<PaymentTypeOptionEntity> {
        return json.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromPeriodList(json: String?): List<Period>? {
        return json?.let { Json.decodeFromString<List<Period>>(it) }
    }

    @TypeConverter
    fun toPeriodList(periodList: List<Period>?): String? {
        return periodList?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toChanges(changes: Changes?): String {
        return Json.encodeToString(changes)
    }

    @TypeConverter
    fun fromChanges(changes: String?): Changes? {
        return changes?.let { Json.decodeFromString(changes) }
    }

    @TypeConverter
    fun fromTransactionType(type: SavingsTransactionTypeEntity?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toTransactionType(json: String?): SavingsTransactionTypeEntity? {
        return json?.let { Json.decodeFromString<SavingsTransactionTypeEntity>(it) }
    }

    @TypeConverter
    fun fromSavingsTransactionDate(date: SavingsTransactionDateEntity?): String? {
        return date?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toSavingsTransactionDate(json: String?): SavingsTransactionDateEntity? {
        return json?.let { Json.decodeFromString<SavingsTransactionDateEntity>(it) }
    }

    @TypeConverter
    fun fromInterestCalculationDaysInYearType(
        type: InterestCalculationDaysInYearType?,
    ): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestCalculationDaysInYearType(json: String?): InterestCalculationDaysInYearType? {
        return json?.let { Json.decodeFromString<InterestCalculationDaysInYearType>(it) }
    }

    @TypeConverter
    fun fromInterestCalculationType(type: InterestCalculationType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestCalculationType(json: String?): InterestCalculationType? {
        return json?.let { Json.decodeFromString<InterestCalculationType>(it) }
    }

    @TypeConverter
    fun fromInterestCompoundingPeriodType(
        type: InterestCompoundingPeriodType?,
    ): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestCompoundingPeriodType(
        json: String?,
    ): InterestCompoundingPeriodType? {
        return json?.let { Json.decodeFromString<InterestCompoundingPeriodType>(it) }
    }

    @TypeConverter
    fun fromInterestPostingPeriodType(
        type: InterestPostingPeriodType?,
    ): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toInterestPostingPeriodType(
        json: String?,
    ): InterestPostingPeriodType? {
        return json?.let { Json.decodeFromString<InterestPostingPeriodType>(it) }
    }

    @TypeConverter
    fun fromLockinPeriodFrequencyType(type: LockinPeriodFrequencyType?): String? {
        return type?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toLockinPeriodFrequencyType(json: String?): LockinPeriodFrequencyType? {
        return json?.let { Json.decodeFromString<LockinPeriodFrequencyType>(it) }
    }

    @TypeConverter
    fun fromPaymentTypeOption(paymentTypeOption: PaymentTypeOptionEntity?): String? {
        return paymentTypeOption?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toPaymentTypeOption(json: String?): PaymentTypeOptionEntity? {
        return json?.let { Json.decodeFromString<PaymentTypeOptionEntity>(it) }
    }

    @TypeConverter
    fun toServerTypes(id: Int?): ServerTypes? {
        return id?.let { ServerTypes.fromId(it) }
    }

    @TypeConverter
    fun fromServerTypes(serverTypes: ServerTypes?): Int? {
        return serverTypes?.id
    }

    @TypeConverter
    fun fromQuestionDatasList(questionDatas: List<QuestionDatasEntity>?): String? {
        return questionDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toQuestionDatasList(json: String?): List<QuestionDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromComponentDatasList(componentDatas: List<ComponentDatasEntity>?): String? {
        return componentDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toComponentDatasList(json: String?): List<ComponentDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromResponseDatasList(responseDatas: List<ResponseDatasEntity>?): String? {
        return responseDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toResponseDatasList(json: String?): List<ResponseDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromAddressList(addressList: List<Address>?): String? =
        addressList?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toAddressList(json: String?): List<Address>? =
        json?.let { Json.decodeFromString(it) }
}
