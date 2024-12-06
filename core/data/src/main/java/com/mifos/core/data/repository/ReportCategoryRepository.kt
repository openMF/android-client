/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.objects.runreports.client.ClientReportTypeItem

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface ReportCategoryRepository {

    suspend fun getReportCategories(
        reportCategory: String,
        genericResultSet: Boolean,
        parameterType: Boolean,
    ): List<ClientReportTypeItem>
}
