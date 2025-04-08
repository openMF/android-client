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

import com.mifos.room.MifosDatabase
import org.koin.dsl.module

val DaoModule = module {
    single { get<MifosDatabase>().centerDao }
    single { get<MifosDatabase>().chargeDao }
    single { get<MifosDatabase>().clientDao }
    single { get<MifosDatabase>().columnValueDao }
    single { get<MifosDatabase>().groupsDao }
    single { get<MifosDatabase>().loanDao }
    single { get<MifosDatabase>().officeDao }
    single { get<MifosDatabase>().savingsDao }
    single { get<MifosDatabase>().staffDao }
    single { get<MifosDatabase>().surveyDao }
}
