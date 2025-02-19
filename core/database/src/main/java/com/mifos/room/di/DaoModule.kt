/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.di

import com.mifos.room.dao.ChargeDao
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.GroupsDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.OfficeDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao
import com.mifos.room.db.MifosDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    @Singleton
    fun providesColumnValueDao(database: MifosDatabase): ColumnValueDao {
        return database.columnValueDao()
    }

    @Provides
    @Singleton
    fun providesLoanDao(database: MifosDatabase): LoanDao {
        return database.loanDao()
    }

    @Provides
    @Singleton
    fun providesSurveyDao(database: MifosDatabase): SurveyDao {
        return database.surveyDao()
    }

    @Provides
    @Singleton
    fun providesStaffDao(database: MifosDatabase): StaffDao {
        return database.staffDao()
    }

    @Provides
    @Singleton
    fun providesGroupDao(database: MifosDatabase): GroupsDao {
        return database.groupsDao()
    }

    @Provides
    @Singleton
    fun providesOfficeDao(database: MifosDatabase): OfficeDao {
        return database.officeDao()
    }

    @Provides
    @Singleton
    fun providesClientDao(database: MifosDatabase): ChargeDao {
        return database.chargeDao()
    }
}
