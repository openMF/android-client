/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.di

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.common.utils.Constants
import com.mifos.room.MifosDatabase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import template.core.base.database.AppDatabaseFactory
import kotlin.coroutines.CoroutineContext

/**
 * v1 → v2: adds the `framework_submit_drafts` table — see
 * [com.mifos.room.infra.entity.DraftEntity] for the schema.
 */
internal val MIGRATION_1_2 = object : Migration(1, 2) {
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
internal val MIGRATION_2_3 = object : Migration(2, 3) {
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

actual val PlatformSpecificDatabaseModule: Module = module {
    single<MifosDatabase> {
        val ioContext: CoroutineContext = getKoin().get(named(MifosDispatchers.IO.name))

        AppDatabaseFactory()
            .createDatabase<MifosDatabase>(Constants.DATABASE_NAME)
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(ioContext)
            .build()
    }
}
