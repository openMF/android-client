/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room

import androidx.room3.AutoMigration
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.ColumnTypeConverters
import com.mifos.room.dao.CenterDao
import com.mifos.room.dao.CenterListCacheDao
import com.mifos.room.dao.ChargeDao
import com.mifos.room.dao.ClientDao
import com.mifos.room.dao.ClientListCacheDao
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.GroupListCacheDao
import com.mifos.room.dao.GroupsDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.LoanTransactionDao
import com.mifos.room.dao.OfficeDao
import com.mifos.room.dao.SavingsAccountTransactionDao
import com.mifos.room.dao.SavingsDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.ActualDisbursementDateEntity
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.loans.LoanAccountSummaryEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.accounts.loans.LoanTransactionEntity
import com.mifos.room.entities.accounts.loans.LoanTypeEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountStatusEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountSummaryEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsTransactionDateEntity
import com.mifos.room.entities.accounts.savings.SavingsTransactionTypeEntity
import com.mifos.room.dao.CheckerTaskDao
import com.mifos.room.entities.checkerinbox.CheckerTaskEntity
import com.mifos.room.dao.DocumentDao
import com.mifos.room.dao.NoteDao
import com.mifos.room.dao.ReportCategoryDao
import com.mifos.room.entities.document.DocumentEntity
import com.mifos.room.entities.report.ReportCategoryEntity
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.client.ChargeCalculationTypeEntity
import com.mifos.room.entities.client.ChargeTimeTypeEntity
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.client.ClientAddressEntity
import com.mifos.room.entities.client.ClientChargeCurrencyEntity
import com.mifos.room.entities.client.ClientDateEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientIdentifierEntity
import com.mifos.room.entities.client.ClientListCacheEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.client.ClientStatusEntity
import com.mifos.room.entities.group.CenterDateEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterListCacheEntity
import com.mifos.room.entities.group.GroupDateEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupListCacheEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import com.mifos.room.entities.noncore.ColumnHeader
import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.noncore.DataTablePayload
import com.mifos.room.entities.noncore.NoteEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.OfficeOpeningDateEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.survey.ComponentDatasEntity
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.entities.templates.clients.InterestTypeEntity
import com.mifos.room.entities.templates.clients.OfficeOptionsEntity
import com.mifos.room.entities.templates.clients.OptionsEntity
import com.mifos.room.entities.templates.clients.SavingProductOptionsEntity
import com.mifos.room.entities.templates.clients.StaffOptionsEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import com.mifos.room.typeconverters.CustomTypeConverters

@Database(
    entities = [
        // loans package
        ActualDisbursementDateEntity::class,
        LoanAccountEntity::class,
        LoanRepaymentRequestEntity::class,
        LoanRepaymentResponseEntity::class,
        LoanStatusEntity::class,
        LoanTypeEntity::class,
        LoanWithAssociationsEntity::class,
        LoanAccountSummaryEntity::class,
        LoanTimelineEntity::class,
        LoanTransactionEntity::class,
        CheckerTaskEntity::class,
        DocumentEntity::class,
        ReportCategoryEntity::class,
        // savings package
        SavingAccountDepositTypeEntity::class,
        SavingsAccountEntity::class,
        SavingAccountCurrencyEntity::class,
        SavingsAccountTransactionEntity::class,
        SavingsAccountWithAssociationsEntity::class,
        SavingsAccountTransactionRequestEntity::class,
        SavingsAccountTransactionTemplateEntity::class,
        SavingsTransactionDateEntity::class,
        SavingsAccountStatusEntity::class,
        SavingsAccountSummaryEntity::class,
        SavingsTransactionTypeEntity::class,
        // center package
        CenterPayloadEntity::class,
        // client package
        ChargeCalculationTypeEntity::class,
        ChargesEntity::class,
        ChargeTimeTypeEntity::class,
        ClientEntity::class,
        ClientPayloadEntity::class,
        ClientChargeCurrencyEntity::class,
        ClientDateEntity::class,
        ClientStatusEntity::class,
        ClientAddressEntity::class,
        ClientIdentifierEntity::class,
        ClientListCacheEntity::class,
        // group package
        CenterEntity::class,
        CenterDateEntity::class,
        CenterListCacheEntity::class,
        GroupEntity::class,
        GroupDateEntity::class,
        GroupPayloadEntity::class,
        GroupListCacheEntity::class,
        // non-core package
        ColumnHeader::class,
        ColumnValue::class,
        DataTableEntity::class,
        DataTablePayload::class,
        NoteEntity::class,
        // organisation package
        OfficeEntity::class,
        OfficeOpeningDateEntity::class,
        StaffEntity::class,
        // servey package
        ComponentDatasEntity::class,
        QuestionDatasEntity::class,
        ResponseDatasEntity::class,
        SurveyEntity::class,
        // templates package
        ClientsTemplateEntity::class,
        InterestTypeEntity::class,
        OfficeOptionsEntity::class,
        OptionsEntity::class,
        SavingProductOptionsEntity::class,
        StaffOptionsEntity::class,
        LoanRepaymentTemplateEntity::class,
        // zip models package
        PaymentTypeOptionEntity::class,
    ],
    version = MifosDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        // v4: purely-additive `loan_transactions` table (new entity, no destructive
        // column edits) — Room3 auto-migration handles a brand-new table with no spec.
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        // v7: purely-additive `client_list_cache` table (new FK-free entity, no destructive
        // column edits) — Room3 auto-migration handles a brand-new table with no spec.
        AutoMigration(from = 6, to = 7),
        // v8: purely-additive `group_list_cache` table (new FK-free entity, no destructive
        // column edits) — Room3 auto-migration handles a brand-new table with no spec.
        AutoMigration(from = 7, to = 8),
        // v9: purely-additive `center_list_cache` table (new FK-free entity, no destructive
        // column edits) — Room3 auto-migration handles a brand-new table with no spec.
    ],
)
@ColumnTypeConverters(
    CustomTypeConverters::class,
)
@ConstructedBy(MifosDatabaseConstructor::class)
actual abstract class MifosDatabase : RoomDatabase() {
    actual abstract val centerDao: CenterDao
    actual abstract val centerListCacheDao: CenterListCacheDao
    actual abstract val chargeDao: ChargeDao
    actual abstract val clientDao: ClientDao
    actual abstract val clientListCacheDao: ClientListCacheDao
    actual abstract val columnValueDao: ColumnValueDao
    actual abstract val groupsDao: GroupsDao
    actual abstract val groupListCacheDao: GroupListCacheDao
    actual abstract val loanDao: LoanDao
    actual abstract val loanTransactionDao: LoanTransactionDao
    actual abstract val officeDao: OfficeDao
    actual abstract val savingsDao: SavingsDao
    actual abstract val savingsAccountTransactionDao: SavingsAccountTransactionDao
    actual abstract val checkerTaskDao: CheckerTaskDao
    actual abstract val documentDao: DocumentDao
    actual abstract val reportCategoryDao: ReportCategoryDao
    actual abstract val noteDao: NoteDao
    actual abstract val staffDao: StaffDao
    actual abstract val surveyDao: SurveyDao

    companion object {
        const val VERSION = 8
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MifosDatabaseConstructor : RoomDatabaseConstructor<MifosDatabase> {
    override fun initialize(): MifosDatabase
}
