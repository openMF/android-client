/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room

import com.mifos.room.dao.CenterDao
import com.mifos.room.dao.ChargeDao
import com.mifos.room.dao.ClientDao
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.GroupsDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.OfficeDao
import com.mifos.room.dao.SavingsDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao

expect abstract class MifosDatabase {
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
}
