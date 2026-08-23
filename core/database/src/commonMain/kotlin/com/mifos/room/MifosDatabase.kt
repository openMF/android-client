/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room

import com.mifos.room.dao.CenterDao
import com.mifos.room.dao.CenterListCacheDao
import com.mifos.room.dao.ChargeDao
import com.mifos.room.dao.ClientDao
import com.mifos.room.dao.ClientListCacheDao
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.GroupListCacheDao
import com.mifos.room.dao.GroupsDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.CheckerTaskDao
import com.mifos.room.dao.DocumentDao
import com.mifos.room.dao.LoanTransactionDao
import com.mifos.room.dao.NoteDao
import com.mifos.room.dao.ReportCategoryDao
import com.mifos.room.dao.OfficeDao
import com.mifos.room.dao.SavingsAccountTransactionDao
import com.mifos.room.dao.SavingsDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class MifosDatabase {
    abstract val centerDao: CenterDao
    abstract val centerListCacheDao: CenterListCacheDao
    abstract val chargeDao: ChargeDao
    abstract val clientDao: ClientDao
    abstract val clientListCacheDao: ClientListCacheDao
    abstract val columnValueDao: ColumnValueDao
    abstract val groupsDao: GroupsDao
    abstract val groupListCacheDao: GroupListCacheDao
    abstract val loanDao: LoanDao
    abstract val loanTransactionDao: LoanTransactionDao
    abstract val officeDao: OfficeDao
    abstract val savingsDao: SavingsDao
    abstract val savingsAccountTransactionDao: SavingsAccountTransactionDao
    abstract val checkerTaskDao: CheckerTaskDao
    abstract val documentDao: DocumentDao
    abstract val reportCategoryDao: ReportCategoryDao
    abstract val noteDao: NoteDao
    abstract val staffDao: StaffDao
    abstract val surveyDao: SurveyDao
}
