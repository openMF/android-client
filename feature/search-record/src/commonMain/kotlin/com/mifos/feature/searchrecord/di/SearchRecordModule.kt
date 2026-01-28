/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.searchrecord.di

import com.mifos.core.data.datasource.SearchRecordLocalDataSource
import com.mifos.core.data.datasource.SearchRecordLocalDataSourceImpl
import com.mifos.core.data.repository.SearchRecordRepository
import com.mifos.core.data.repositoryImp.SearchRecordRepositoryImpl
import com.mifos.feature.searchrecord.SearchRecordViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val SearchRecordModule = module {
    viewModelOf(::SearchRecordViewModel)

    singleOf(::SearchRecordRepositoryImpl) {
        bind<SearchRecordRepository>()
    }

    singleOf(::SearchRecordLocalDataSourceImpl) {
        bind<SearchRecordLocalDataSource>()
    }
}
