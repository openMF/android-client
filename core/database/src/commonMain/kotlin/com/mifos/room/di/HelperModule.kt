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

import com.mifos.core.common.network.MifosDispatchers
import com.mifos.room.helper.CenterDaoHelper
import com.mifos.room.helper.ChargeDaoHelper
import com.mifos.room.helper.ClientDaoHelper
import com.mifos.room.helper.GroupsDaoHelper
import com.mifos.room.helper.LoanDaoHelper
import com.mifos.room.helper.OfficeDaoHelper
import com.mifos.room.helper.SavingsDaoHelper
import com.mifos.room.helper.StaffDaoHelper
import com.mifos.room.helper.SurveyDaoHelper
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val ioDispatcher = named(MifosDispatchers.IO.name)

val HelperModule = module {
    single { CenterDaoHelper(get(), get(ioDispatcher)) }
    single { ChargeDaoHelper(get(), get(ioDispatcher)) }
    single { ClientDaoHelper(get(), get(ioDispatcher)) }
    single { GroupsDaoHelper(get(), get(ioDispatcher)) }
    single { LoanDaoHelper(get()) }
    single { OfficeDaoHelper(get()) }
    single { SavingsDaoHelper(get(), get(ioDispatcher)) }
    single { StaffDaoHelper(get()) }
    single { SurveyDaoHelper(get()) }
}
