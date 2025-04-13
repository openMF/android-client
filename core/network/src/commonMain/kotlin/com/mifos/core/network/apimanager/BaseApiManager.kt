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

interface BaseApiManager {

    companion object {
        fun getInstance(): BaseApiManager {
            return BaseApiManagerImpl()
        }
    }

    fun createService(
        username: String,
        password: String,
        baseUrl: String,
        tenant: String = "default",
        secured: Boolean = true,
    )

    fun getClient(): FineractClient

    fun getCenterApi(): CentersApi

    fun getClientsApi(): ClientApi

    fun getDataTableApi(): DataTablesApi

    fun getGroupApi(): GroupsApi

    fun getOfficeApi(): OfficesApi

    fun getStaffApi(): StaffApi
}
