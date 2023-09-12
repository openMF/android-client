package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
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