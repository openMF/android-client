package com.mifos.core.data.repository

import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.response.SaveResponse
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