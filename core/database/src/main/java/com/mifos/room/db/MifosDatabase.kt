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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao
import com.mifos.room.entities.PaymentTypeOption
import com.mifos.room.entities.accounts.loans.ActualDisbursementDate
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.accounts.loans.LoanWithAssociations
import com.mifos.room.entities.accounts.loans.Status
import com.mifos.room.entities.accounts.loans.Summary
import com.mifos.room.entities.accounts.loans.Timeline
import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.entities.organisation.Staff
import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.ResponseDatas
import com.mifos.room.entities.survey.Survey
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate
import com.mifos.room.utils.typeconverters.DueDateConverter
import com.mifos.room.utils.typeconverters.ListTypeConverters
import com.mifos.room.utils.typeconverters.LoanTypeConverters
import com.mifos.room.utils.typeconverters.ServerTypesConverters
import com.mifos.room.utils.typeconverters.SurveyTypeConverters

@Database(
    // [TODO -> add other entities ]
    entities = [
        ColumnValue::class,
        // loan
        LoanWithAssociations::class,
        LoanRepaymentRequest::class,
        LoanRepaymentTemplate::class,
        PaymentTypeOption::class,
        ActualDisbursementDate::class,
        Timeline::class,
        Status::class,
        Summary::class,
        // survey
        Survey::class,
        QuestionDatas::class,
        ResponseDatas::class,
        Staff::class,
    ],
    version = MifosDatabase.VERSION,
    exportSchema = true,
    autoMigrations = [],
)
@TypeConverters(
    ListTypeConverters::class,
    ServerTypesConverters::class,
    DueDateConverter::class,
    LoanTypeConverters::class,
    SurveyTypeConverters::class,
)
// ( TODO -> add type converters here )

abstract class MifosDatabase : RoomDatabase() {
    abstract fun columnValueDao(): ColumnValueDao
    abstract fun loanDao(): LoanDao
    abstract fun surveyDao(): SurveyDao
    abstract fun staffDao(): StaffDao

    companion object {
        const val VERSION = 1
    }
}
