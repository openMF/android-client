package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.core.data.CenterPayload
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewCenterRepositoryImp @Inject constructor(private val dataManagerCenter: DataManagerCenter) :
    CreateNewCenterRepository {

    override fun offices(): Observable<List<Office>> {
        return dataManagerCenter.offices
    }

    override fun createCenter(centerPayload: CenterPayload): Observable<SaveResponse> {
        return dataManagerCenter.createCenter(centerPayload)
    }
}