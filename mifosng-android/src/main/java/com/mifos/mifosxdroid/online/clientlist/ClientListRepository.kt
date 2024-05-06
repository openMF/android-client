package com.mifos.mifosxdroid.online.clientlist

import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientListRepository {

    fun getAllClients(offset: Int, limit: Int): Observable<Page<Client>>

    fun allDatabaseClients(): Observable<Page<Client>>

}