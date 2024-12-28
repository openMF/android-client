/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.database

import com.mifos.core.dbobjects.accounts.loan.LoanAccount
import com.mifos.core.dbobjects.accounts.savings.SavingsAccount
import com.mifos.core.dbobjects.group.Center
import com.raizlabs.android.dbflow.annotation.Migration
import com.raizlabs.android.dbflow.sql.SQLiteType
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration

/**
 * Created by mayankjindal on 17/07/17.
 */
class MigrationVersion2 {
    @Migration(version = MifosDatabase.VERSION, database = MifosDatabase::class)
    class Migration1(table: Class<LoanAccount>) : AlterTableMigration<LoanAccount>(table) {
        override fun onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "centerId")
        }
    }

    @Migration(version = MifosDatabase.VERSION, database = MifosDatabase::class)
    class Migration2(table: Class<SavingsAccount>) : AlterTableMigration<SavingsAccount>(table) {
        override fun onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "centerId")
        }
    }

    @Migration(version = MifosDatabase.VERSION, database = MifosDatabase::class)
    class Migration3(table: Class<Center>) : AlterTableMigration<Center>(table) {
        override fun onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "sync")
            addColumn(SQLiteType.INTEGER, "centerDate_centerId")
            addColumn(SQLiteType.INTEGER, "centerDate_chargeId")
            addColumn(SQLiteType.INTEGER, "centerDate_day")
            addColumn(SQLiteType.INTEGER, "centerDate_month")
            addColumn(SQLiteType.INTEGER, "centerDate_year")
        }
    }
}
