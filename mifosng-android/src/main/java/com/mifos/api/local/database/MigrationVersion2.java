package com.mifos.api.local.database;

import com.mifos.api.local.MifosDatabase;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.group.Center;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

/**
 * Created by mayankjindal on 17/07/17.
 */

public class MigrationVersion2 {
    @Migration(version = MifosDatabase.VERSION, database = MifosDatabase.class)
    public static class Migration1 extends AlterTableMigration<LoanAccount> {

        public Migration1(Class<LoanAccount> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "centerId");
        }
    }

    @Migration(version = MifosDatabase.VERSION, database = MifosDatabase.class)
    public static class Migration2 extends AlterTableMigration<SavingsAccount> {

        public Migration2(Class<SavingsAccount> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "centerId");
        }
    }

    @Migration(version = MifosDatabase.VERSION, database = MifosDatabase.class)
    public static class Migration3 extends AlterTableMigration<Center> {

        public Migration3(Class<Center> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "sync");
            addColumn(SQLiteType.INTEGER, "centerDate_centerId");
            addColumn(SQLiteType.INTEGER, "centerDate_chargeId");
            addColumn(SQLiteType.INTEGER, "centerDate_day");
            addColumn(SQLiteType.INTEGER, "centerDate_month");
            addColumn(SQLiteType.INTEGER, "centerDate_year");
        }
    }
}
