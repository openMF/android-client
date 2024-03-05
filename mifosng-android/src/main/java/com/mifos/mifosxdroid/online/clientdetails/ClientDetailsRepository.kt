package com.mifos.mifosxdroid.online.clientdetails

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.Client
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface ClientDetailsRepository {

    fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody>

    fun deleteClientImage(clientId: Int): Observable<ResponseBody>

    fun getClientAccounts(clientId: Int): Observable<ClientAccounts>

    fun getClient(clientId: Int): Observable<Client>

}