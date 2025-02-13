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

import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao
import com.mifos.room.db.MifosDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Provides
    fun providesColumnValueDao(database: MifosDatabase): ColumnValueDao {
        return database.columnValueDao()
    }

    @Provides
    fun providesLoanDao(database: MifosDatabase): LoanDao {
        return database.loanDao()
    }

    @Provides
    fun providesSurveyDao(database: MifosDatabase): SurveyDao {
        return database.surveyDao()
    }

    @Provides
    fun providesStaffDao(database: MifosDatabase): StaffDao {
        return database.staffDao()
    }
}
