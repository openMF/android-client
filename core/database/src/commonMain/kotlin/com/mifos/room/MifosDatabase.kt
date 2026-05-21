/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.TypeConverters
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.mifos.room.dao.CenterDao
import com.mifos.room.dao.ChargeDao
import com.mifos.room.dao.ClientDao
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.GroupsDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.OfficeDao
import com.mifos.room.dao.SavingsDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.ActualDisbursementDateEntity
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.accounts.loans.LoanTypeEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
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
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.client.ChargeCalculationTypeEntity
import com.mifos.room.entities.client.ChargeTimeTypeEntity
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.client.ClientAddressEntity
import com.mifos.room.entities.client.ClientChargeCurrencyEntity
import com.mifos.room.entities.client.ClientDateEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientIdentifierEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.client.ClientStatusEntity
import com.mifos.room.entities.group.CenterDateEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.GroupDateEntity
import com.mifos.room.entities.group.GroupEntity
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
import com.mifos.room.infra.dao.BookkeeperDao
import com.mifos.room.infra.dao.DraftDao
import com.mifos.room.infra.dao.FetchedAtDao
import com.mifos.room.infra.entity.BookkeeperEntity
import com.mifos.room.infra.entity.DraftEntity
import com.mifos.room.infra.entity.FetchedAtEntity
import com.mifos.room.typeconverters.CustomTypeConverters

/**
 * KSP-generated constructor bridge for [MifosDatabase].
 *
 * Room 3 requires an `expect object` annotated via [@ConstructedBy][ConstructedBy] so that
 * the KSP compiler plugin can generate a platform-specific `actual object` containing the
 * `MifosDatabase_Impl` instantiation logic. The `@Suppress` is needed because the `actual`
 * is generated code, not hand-written.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MifosDatabaseConstructor : RoomDatabaseConstructor<MifosDatabase>

/**
 * Root Room 3 database for the application.
 *
 * Single commonMain declaration across all KMP targets. Platform-specific creation is
 * handled by [template.core.base.database.AppDatabaseFactory] in each platform's
 * `DatabaseModule.<platform>.kt` — see [com.mifos.room.di.platformModule].
 *
 * Room 3 uses [ConstructedBy] to delegate instance creation to [MifosDatabaseConstructor],
 * whose `actual` implementation is generated by KSP on every target.
 */
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
        LoansAccountSummaryEntity::class,
        LoanTimelineEntity::class,
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
        // group package
        CenterEntity::class,
        CenterDateEntity::class,
        GroupEntity::class,
        GroupDateEntity::class,
        GroupPayloadEntity::class,
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
        // survey package
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
        // framework infra (Phase B2 — `infra/entity/`)
        DraftEntity::class,
        FetchedAtEntity::class,
        BookkeeperEntity::class,
    ],
    version = MifosDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [],
)
@TypeConverters(CustomTypeConverters::class)
@ConstructedBy(MifosDatabaseConstructor::class)
abstract class MifosDatabase : RoomDatabase() {

    abstract val centerDao: CenterDao
    abstract val chargeDao: ChargeDao
    abstract val clientDao: ClientDao
    abstract val columnValueDao: ColumnValueDao
    abstract val groupsDao: GroupsDao
    abstract val loanDao: LoanDao
    abstract val officeDao: OfficeDao
    abstract val savingsDao: SavingsDao
    abstract val staffDao: StaffDao
    abstract val surveyDao: SurveyDao

    // Framework infra DAOs (Phase B2 — `infra/dao/`)
    abstract val bookkeeperDao: BookkeeperDao
    abstract val draftDao: DraftDao
    abstract val fetchedAtDao: FetchedAtDao

    companion object {
        const val VERSION = 3
        const val DATABASE_NAME = "mifos_field_officer.db"
    }
}

/**
 * v1 → v2: adds the `framework_submit_drafts` table — see
 * [com.mifos.room.infra.entity.DraftEntity] for the schema. Added in Phase B.
 */
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `framework_submit_drafts` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `formKey` TEXT NOT NULL,
                `payloadJson` TEXT NOT NULL,
                `status` TEXT NOT NULL,
                `createdAtMs` INTEGER NOT NULL,
                `updatedAtMs` INTEGER NOT NULL,
                `errorMessage` TEXT
            )
            """.trimIndent(),
        )
    }
}

/**
 * v2 → v3: adds the `framework_fetched_at` + `store_bookkeeper` tables —
 * Phase B2 (kmp-project-template parity).
 */
val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `framework_fetched_at` (
                `storeKey` TEXT PRIMARY KEY NOT NULL,
                `lastFetchedMillis` INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `store_bookkeeper` (
                `key` TEXT PRIMARY KEY NOT NULL,
                `lastFailedSync` INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}

/** All migrations to apply, in version order. */
val MifosDatabaseMigrations: Array<Migration> = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
)
