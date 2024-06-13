package com.mifos.core.data.repository_imp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.data.paging_source.CenterListPagingSource
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterListRepositoryImp @Inject constructor(private val dataManagerCenter: DataManagerCenter) :
    CenterListRepository {

    override fun getAllCenters(): Flow<PagingData<Center>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ), pagingSourceFactory = {
                CenterListPagingSource(dataManagerCenter)
            }
        ).flow
    }

    override fun getCentersGroupAndMeeting(id: Int): Observable<CenterWithAssociations> {
        return dataManagerCenter.getCentersGroupAndMeeting(id)
    }

    override fun allDatabaseCenters(): Observable<Page<Center>> {
        return dataManagerCenter.allDatabaseCenters
    }
}