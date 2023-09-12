package com.mifos.mifosxdroid.online.createnewclient

import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.objects.templates.clients.ClientsTemplate
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.apache.fineract.client.models.RetrieveOneResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewClientRepository {

    fun clientTemplate(): Observable<ClientsTemplate>

    fun offices(): Observable<List<Office>>

    fun getStaffInOffice(officeId: Int): Observable<List<Staff>>

    fun createClient(clientPayload: ClientPayload): Observable<Client>

    fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody>
}