package com.mifos.core.data.repository

import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.core.objects.templates.clients.ClientsTemplate
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewClientRepository {

    fun clientTemplate(): Observable<ClientsTemplate>

    suspend fun offices(): List<Office>

    suspend fun getStaffInOffice(officeId: Int): List<Staff>

    fun createClient(clientPayload: ClientPayload): Observable<Client>

    fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody>
}