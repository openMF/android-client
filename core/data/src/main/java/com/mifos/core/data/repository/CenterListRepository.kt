package com.mifos.core.data.repository

import androidx.paging.PagingData
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface CenterListRepository {

    fun getAllCenters(): Flow<PagingData<Center>>

    fun getCentersGroupAndMeeting(id: Int): Observable<CenterWithAssociations>

    fun allDatabaseCenters(): Observable<Page<Center>>
}