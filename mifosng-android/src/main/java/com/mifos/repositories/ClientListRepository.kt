package com.mifos.repositories

import com.mifos.objects.client.Client
import com.mifos.objects.client.Page
import rx.Observable

interface ClientListRepository {

    fun getAllClients(paged: Boolean, offset: Int, limit: Int): Observable<Page<Client>>

    fun allDatabaseClients(): Observable<Page<Client>>

}