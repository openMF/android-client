package com.mifos.repositories

import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncCenterPayloadsRepository {

    fun allDatabaseCenterPayload(): Observable<List<CenterPayload>>

    fun createCenter(centerPayload: CenterPayload): Observable<SaveResponse>

    fun deleteAndUpdateCenterPayloads(id: Int): Observable<List<CenterPayload>>

    fun updateCenterPayload(centerPayload: CenterPayload): Observable<CenterPayload>
}