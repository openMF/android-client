package com.mifos.mifosxdroid.online.clientdetails

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.client.Client
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class ClientDetailsRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    ClientDetailsRepository {
    override fun uploadClientImage(id: Int, file: MultipartBody.Part?): Observable<ResponseBody> {
        return dataManagerClient.uploadClientImage(id, file)
    }

    override fun deleteClientImage(clientId: Int): Observable<ResponseBody> {
        return dataManagerClient.deleteClientImage(clientId)
    }

    override fun getClientAccounts(clientId: Int): Observable<ClientAccounts> {
        return dataManagerClient.getClientAccounts(clientId)
    }

    override fun getClient(clientId: Int): Observable<Client> {
        return dataManagerClient.getClient(clientId)
    }

}