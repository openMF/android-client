package com.mifos.core.data.repository_imp

import com.mifos.core.data.CenterPayload
import com.mifos.core.data.repository.SyncCenterPayloadsRepository
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.objects.response.SaveResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncCenterPayloadsRepositoryImp @Inject constructor(private val dataManagerCenter: DataManagerCenter) :
    SyncCenterPayloadsRepository {

    override fun allDatabaseCenterPayload(): Observable<List<CenterPayload>> {
        return dataManagerCenter.allDatabaseCenterPayload
    }

    override fun createCenter(centerPayload: CenterPayload): Observable<SaveResponse> {
        return dataManagerCenter.createCenter(centerPayload)
    }

    override fun deleteAndUpdateCenterPayloads(id: Int): Observable<List<CenterPayload>> {
        return dataManagerCenter.deleteAndUpdateCenterPayloads(id)
    }

    override fun updateCenterPayload(centerPayload: CenterPayload): Observable<CenterPayload> {
        return dataManagerCenter.updateCenterPayload(centerPayload)
    }
}