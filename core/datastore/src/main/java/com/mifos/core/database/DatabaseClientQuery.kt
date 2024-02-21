package com.mifos.core.database

import com.mifos.core.common.utils.Page
import com.mifos.core.model.ClientDb
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 21/02/24.
 */

class DatabaseClientQuery @Inject constructor(private val realm: Realm) {

    fun getClientListFromDb(): Flow<Page<ClientDb>> {
        return realm.query<ClientDb>().sort("accountNo", sortOrder = Sort.ASCENDING).asFlow()
            .map { Page(totalFilteredRecords = it.list.size, pageItems = it.list) }
    }

}