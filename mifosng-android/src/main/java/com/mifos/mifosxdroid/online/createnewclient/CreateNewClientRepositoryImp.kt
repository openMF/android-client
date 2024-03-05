package com.mifos.mifosxdroid.online.createnewclient

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.core.network.datamanager.DataManagerStaff
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.templates.clients.ClientsTemplate
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewClientRepositoryImp @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerOffices: DataManagerOffices,
    private val dataManagerStaff: DataManagerStaff
) : CreateNewClientRepository {

    override fun clientTemplate(): Observable<ClientsTemplate> {
        return dataManagerClient.clientTemplate
    }

    override fun offices(): Observable<List<Office>> {
        return dataManagerOffices.offices
    }

    override fun getStaffInOffice(officeId: Int): Observable<List<Staff>> {
        return dataManagerStaff.getStaffInOffice(officeId)
    }

    override fun createClient(clientPayload: ClientPayload): Observable<Client> {
        return dataManagerClient.createClient(clientPayload)
    }

    override fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody> {
        return dataManagerClient.uploadClientImage(id, file)
    }

}