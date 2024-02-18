package com.mifos.core.database

import com.mifos.core.common.utils.Page
import com.mifos.core.data.model.client.Client
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable

class DatabaseHelperClient {


    /**
     * Reading All Clients from table of Client and return the ClientList
     *
     * @return List Of Client
     */
    //TODO Implement Observable Transaction to load Client List
    fun readAllClients(): Observable<Page<Client>> {
        return Observable.create { subscriber ->
            val clientPage = Page<Client>()
            clientPage.pageItems = SQLite.select()
                .from(Client::class.java)
                .queryList()
            subscriber.onNext(clientPage)
            subscriber.onCompleted()
        }
    }
}