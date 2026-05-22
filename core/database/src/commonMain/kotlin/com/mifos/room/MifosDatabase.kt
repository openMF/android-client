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
import com.mifos.room.center.dao.CenterDao
import com.mifos.room.center.entity.CenterDateEntity
import com.mifos.room.center.entity.CenterEntity
import com.mifos.room.center.entity.CenterPayloadEntity
import com.mifos.room.charge.dao.ChargeDao
import com.mifos.room.charge.entity.ChargeCalculationTypeEntity
import com.mifos.room.charge.entity.ChargeTimeTypeEntity
import com.mifos.room.charge.entity.ChargesEntity
import com.mifos.room.charge.entity.ClientChargeCurrencyEntity
import com.mifos.room.client.dao.ClientDao
import com.mifos.room.client.entity.ClientAddressEntity
import com.mifos.room.client.entity.ClientDateEntity
import com.mifos.room.client.entity.ClientEntity
import com.mifos.room.client.entity.ClientIdentifierEntity
import com.mifos.room.client.entity.ClientPayloadEntity
import com.mifos.room.client.entity.ClientStatusEntity
import com.mifos.room.client.entity.ClientsTemplateEntity
import com.mifos.room.client.entity.InterestTypeEntity
import com.mifos.room.client.entity.OptionsEntity
import com.mifos.room.datatable.dao.ColumnValueDao
import com.mifos.room.datatable.entity.ColumnHeader
import com.mifos.room.datatable.entity.ColumnValue
import com.mifos.room.datatable.entity.DataTableEntity
import com.mifos.room.datatable.entity.DataTablePayload
import com.mifos.room.group.dao.GroupsDao
import com.mifos.room.group.entity.GroupDateEntity
import com.mifos.room.group.entity.GroupEntity
import com.mifos.room.group.entity.GroupPayloadEntity
import com.mifos.room.infra.dao.BookkeeperDao
import com.mifos.room.infra.dao.DraftDao
import com.mifos.room.infra.dao.FetchedAtDao
import com.mifos.room.infra.entity.BookkeeperEntity
import com.mifos.room.infra.entity.DraftEntity
import com.mifos.room.infra.entity.FetchedAtEntity
import com.mifos.room.loan.dao.LoanDao
import com.mifos.room.loan.entity.ActualDisbursementDateEntity
import com.mifos.room.loan.entity.LoanAccountEntity
import com.mifos.room.loan.entity.LoanRepaymentRequestEntity
import com.mifos.room.loan.entity.LoanRepaymentResponseEntity
import com.mifos.room.loan.entity.LoanRepaymentTemplateEntity
import com.mifos.room.loan.entity.LoanStatusEntity
import com.mifos.room.loan.entity.LoanTimelineEntity
import com.mifos.room.loan.entity.LoanTypeEntity
import com.mifos.room.loan.entity.LoanWithAssociationsEntity
import com.mifos.room.loan.entity.LoansAccountSummaryEntity
import com.mifos.room.note.dao.NoteCacheDao
import com.mifos.room.note.entity.NoteCacheEntity
import com.mifos.room.note.entity.NoteEntity
import com.mifos.room.office.dao.OfficeDao
import com.mifos.room.office.entity.OfficeEntity
import com.mifos.room.office.entity.OfficeOpeningDateEntity
import com.mifos.room.office.entity.OfficeOptionsEntity
import com.mifos.room.pathtracking.dao.PathTrackingCacheDao
import com.mifos.room.pathtracking.entity.PathTrackingCacheEntity
import com.mifos.room.savings.dao.SavingsDao
import com.mifos.room.savings.entity.PaymentTypeOptionEntity
import com.mifos.room.savings.entity.SavingAccountCurrencyEntity
import com.mifos.room.savings.entity.SavingAccountDepositTypeEntity
import com.mifos.room.savings.entity.SavingProductOptionsEntity
import com.mifos.room.savings.entity.SavingsAccountEntity
import com.mifos.room.savings.entity.SavingsAccountStatusEntity
import com.mifos.room.savings.entity.SavingsAccountSummaryEntity
import com.mifos.room.savings.entity.SavingsAccountTransactionEntity
import com.mifos.room.savings.entity.SavingsAccountTransactionRequestEntity
import com.mifos.room.savings.entity.SavingsAccountTransactionTemplateEntity
import com.mifos.room.savings.entity.SavingsAccountWithAssociationsEntity
import com.mifos.room.savings.entity.SavingsTransactionDateEntity
import com.mifos.room.savings.entity.SavingsTransactionTypeEntity
import com.mifos.room.staff.dao.StaffDao
import com.mifos.room.staff.entity.StaffEntity
import com.mifos.room.staff.entity.StaffOptionsEntity
import com.mifos.room.survey.dao.SurveyDao
import com.mifos.room.survey.entity.ComponentDatasEntity
import com.mifos.room.survey.entity.QuestionDatasEntity
import com.mifos.room.survey.entity.ResponseDatasEntity
import com.mifos.room.survey.entity.SurveyEntity
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
expect object MifosDatabaseConstructor : RoomDatabaseConstructor<MifosDatabase> {
    override fun initialize(): MifosDatabase
}

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
        // per-feature Store5 caches (Phase C)
        NoteCacheEntity::class,
        PathTrackingCacheEntity::class,
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

    // Per-feature Store5 cache DAOs
    abstract val noteCacheDao: NoteCacheDao
    abstract val pathTrackingCacheDao: PathTrackingCacheDao

    companion object {
        const val VERSION = 5
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

/**
 * v3 → v4: adds the `note_cache` table — Phase C Wave 7 Store5 cache for notes.
 */
val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `note_cache` (
                `cacheKey` TEXT PRIMARY KEY NOT NULL,
                `resourceType` TEXT NOT NULL,
                `resourceId` INTEGER NOT NULL,
                `noteId` INTEGER NOT NULL,
                `note` TEXT NOT NULL,
                `clientId` INTEGER,
                `createdById` INTEGER,
                `createdByUsername` TEXT,
                `createdOn` TEXT,
                `updatedById` INTEGER,
                `updatedByUsername` TEXT,
                `updatedOn` TEXT
            )
            """.trimIndent(),
        )
    }
}

/**
 * v4 → v5: adds the `path_tracking_cache` table — Phase C Wave 11 Store5 cache for
 * the staff-path-tracking list. `UserLocation` has no server-side id; rows are
 * keyed by `<userId>:<ordinal>` and listed `ORDER BY ordinal ASC` to preserve the
 * on-wire sequence. See [com.mifos.room.pathtracking.entity.PathTrackingCacheEntity].
 */
val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `path_tracking_cache` (
                `cacheKey` TEXT PRIMARY KEY NOT NULL,
                `userId` INTEGER NOT NULL,
                `ordinal` INTEGER NOT NULL,
                `staffId` INTEGER,
                `latLng` TEXT,
                `startTime` TEXT,
                `stopTime` TEXT,
                `date` TEXT,
                `startAddress` TEXT,
                `endAddress` TEXT,
                `dateFormat` TEXT,
                `locale` TEXT
            )
            """.trimIndent(),
        )
    }
}

/** All migrations to apply, in version order. */
val MifosDatabaseMigrations: Array<Migration> = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
)
