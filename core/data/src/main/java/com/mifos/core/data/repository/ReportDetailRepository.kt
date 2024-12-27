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

import com.mifos.core.modelobjects.runreport.FullParameterListResponse

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface ReportDetailRepository {

    suspend fun getReportFullParameterList(
        reportName: String,
        parameterType: Boolean,
    ): FullParameterListResponse

    suspend fun getReportParameterDetails(
        parameterName: String,
        parameterType: Boolean,
    ): FullParameterListResponse

    suspend fun getRunReportOffices(
        parameterName: String,
        officeId: Int,
        parameterType: Boolean,
    ): FullParameterListResponse

    suspend fun getRunReportProduct(
        parameterName: String,
        currency: String,
        parameterType: Boolean,
    ): FullParameterListResponse

    suspend fun getRunReportWithQuery(
        reportName: String,
        options: Map<String, String>,
    ): FullParameterListResponse
}
