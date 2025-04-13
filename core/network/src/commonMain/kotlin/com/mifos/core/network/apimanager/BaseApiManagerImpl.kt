/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.apimanager

import com.mifos.core.network.apis.CentersApi
import com.mifos.core.network.apis.ClientApi
import com.mifos.core.network.apis.DataTablesApi
import com.mifos.core.network.apis.FineractClient
import com.mifos.core.network.apis.GroupsApi
import com.mifos.core.network.apis.OfficesApi
import com.mifos.core.network.apis.StaffApi

class BaseApiManagerImpl : BaseApiManager {

    private lateinit var client: FineractClient

    override fun createService(
        username: String,
        password: String,
        baseUrl: String,
        tenant: String,
        secured: Boolean,
    ) {
        val builder = FineractClient.builder()
            .baseURL(baseUrl)
            .basicAuth(username, password)
            .inSecure(!secured)
            .tenant(tenant)

        client = builder.build()
    }

    override fun getClient(): FineractClient {
        return client
    }

    override fun getCenterApi(): CentersApi = client.centers

    override fun getClientsApi(): ClientApi = client.clients

    override fun getDataTableApi(): DataTablesApi = client.dataTables

    override fun getGroupApi(): GroupsApi = client.groups

    override fun getOfficeApi(): OfficesApi = client.offices

    override fun getStaffApi(): StaffApi = client.staff
}
