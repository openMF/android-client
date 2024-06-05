package com.mifos.core.data.repository_imp

import com.mifos.core.common.network.Dispatcher
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.network.datamanager.DataManagerSearch
import com.mifos.core.objects.SearchedEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class SearchRepositoryImp @Inject constructor(
    private val dataManagerSearch: DataManagerSearch,
    @Dispatcher(MifosDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : SearchRepository {

    override suspend fun searchResources(
        query: String?,
        resources: String?,
        exactMatch: Boolean?
    ): Flow<List<SearchedEntity>> = flow {
        val result = dataManagerSearch.searchResources(query, resources, exactMatch)
        emit(result)
    }.flowOn(ioDispatcher)
        .distinctUntilChanged()
}