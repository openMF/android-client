/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.di

import com.mifos.room.MifosDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module providing the [MifosDatabase] singleton + all DAO accessors.
 *
 * Delegates platform-specific database construction to [platformModule], which each
 * source set (`androidMain`, `desktopMain`, `nativeMain`) implements via
 * [template.core.base.database.AppDatabaseFactory] + the appropriate SQLite driver
 * + coroutine dispatcher.
 */
val DatabaseModule: Module = module {
    includes(platformModule)

    // Feature DAOs
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

    // Framework infra DAOs (Phase B2)
    single { get<MifosDatabase>().bookkeeperDao }
    single { get<MifosDatabase>().draftDao }
    single { get<MifosDatabase>().fetchedAtDao }
}

/**
 * Per-platform Koin module — provides the [MifosDatabase] singleton. Each platform's
 * `actual val platformModule` lives in `<platform>Main/.../di/DatabaseModule.<platform>.kt`.
 */
expect val platformModule: Module
