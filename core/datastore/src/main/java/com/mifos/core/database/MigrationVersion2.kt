package com.mifos.core.database

import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.group.Center
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