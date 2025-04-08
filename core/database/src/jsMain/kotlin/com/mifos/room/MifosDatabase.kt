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

actual abstract class MifosDatabase {
    actual abstract val centerDao: CenterDao
    actual abstract val chargeDao: ChargeDao
    actual abstract val clientDao: ClientDao
    actual abstract val columnValueDao: ColumnValueDao
    actual abstract val groupsDao: GroupsDao
    actual abstract val loanDao: LoanDao
    actual abstract val officeDao: OfficeDao
    actual abstract val savingsDao: SavingsDao
    actual abstract val staffDao: StaffDao
    actual abstract val surveyDao: SurveyDao
}
