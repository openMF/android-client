/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.datasource.SearchRecordLocalDataSource
import com.mifos.core.data.repository.SearchRecordRepository
import com.mifos.core.model.objects.searchrecord.GenericSearchRecord
import com.mifos.core.model.objects.searchrecord.RecordType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRecordRepositoryImpl(
    private val localDataSource: SearchRecordLocalDataSource,
) : SearchRecordRepository {

    override fun searchRecords(
        recordType: RecordType,
        query: String,
    ): Flow<Result<List<GenericSearchRecord>>> = flow {
        try {
            localDataSource.searchRecords(recordType, query)
                .collect { records ->
                    emit(Result.success(records))
                }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
