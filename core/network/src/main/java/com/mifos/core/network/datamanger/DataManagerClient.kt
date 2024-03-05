package com.mifos.core.network.datamanger

import com.mifos.core.database.DatabaseClientQuery
import com.mifos.core.model.ClientDb
import com.mifos.core.network.mappers.clients.GetClientResponseMapper
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.Page
import kotlinx.coroutines.flow.Flow
import org.mifos.core.apimanager.BaseApiManager
import rx.Observable
import javax.inject.Inject

class DataManagerClient @Inject constructor(
    private val baseApiManager: BaseApiManager,
    private val databaseClientQuery: DatabaseClientQuery
) {

    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the clients. The response is pass to the DatabaseHelperClient
     * that save the response in Database different thread and next pass the response to
     * Presenter to show in the view
     *
     *
     * If the offset is zero and UserStatus is 1 then fetch all clients list and show on the view.
     * else if offset is not zero and UserStatus is 1 then return default empty response to
     * presenter
     *
     * @param paged  True Enable the Pagination of the client list REST API
     * @param offset Value give from which position Fetch ClientList
     * @param limit  Maximum Number of clients will come in response
     * @return Client List from offset to max Limit
     */

    fun getAllClients(offset: Int, limit: Int): Observable<Page<Client>> {
        return baseApiManager.getClientsApi().retrieveAll21(
            null, null, null,
            null, null, null,
            null, null, offset,
            limit, null, null, null
        ).map(GetClientResponseMapper::mapFromEntity)
    }

    /**
     * This Method Request to the DatabaseHelperClient and DatabaseHelperClient Read the All
     * clients from Client_Table and give the response Page of List of Client
     *
     * @return Page of Client List
     */
    fun allDatabaseClients(): Flow<Page<ClientDb>> {
        return databaseClientQuery.getClientListFromDb()
    }

}