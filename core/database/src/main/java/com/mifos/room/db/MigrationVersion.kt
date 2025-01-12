/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE LoanAccount ADD COLUMN centerId INTEGER")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE SavingsAccount ADD COLUMN centerId INTEGER")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Center ADD COLUMN sync INTEGER")
        db.execSQL("ALTER TABLE Center ADD COLUMN centerDate_centerId INTEGER")
        db.execSQL("ALTER TABLE Center ADD COLUMN centerDate_chargeId INTEGER")
        db.execSQL("ALTER TABLE Center ADD COLUMN centerDate_day INTEGER")
        db.execSQL("ALTER TABLE Center ADD COLUMN centerDate_month INTEGER")
        db.execSQL("ALTER TABLE Center ADD COLUMN centerDate_year INTEGER")
    }
}
